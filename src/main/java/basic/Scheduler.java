package basic;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.ReversedLinesFileReader;

import entities.Anomalia;
import entities.AnomaliaHistory;
import entities.Checklist;
import entities.ChecklistTestcase;
import entities.Csv;
import entities.Defect;
import entities.DefectHistory;
import entities.Documento;
import entities.Esito;
import entities.LinkedItem;
import entities.LinkedItemId;
import entities.Mev;
import entities.Priority;
import entities.ProgettoSviluppo;
import entities.Project;
import entities.Release;
import entities.ReleaseHistory;
import entities.ReleaseIt;
import entities.ReleaseitHistory;
import entities.Status;
import entities.Support;
import entities.SupportHistory;
import entities.TaskItHistory;
import entities.Taskit;
import entities.Testcase;
import entities.TestcaseHistory;
import entities.User;
import entities.Workrecords;

public class Scheduler extends Thread {
	public final static int LOOP_ON_DELAY = 0, LOOP_CERTAIN_TIME = 1, ONE_SHOT = 2;
	public final static int TRUNCATE_DATA = -1, APPEND_DATA = 1, UPDATE_DATA = 2, ARRANGE_DATA = 3;

	private GregorianCalendar start;
	private long time;
	private int mode, operationStatus;
	private boolean status, DEBUG = true;
	private Date startDate;
	private String user;
	private Timer t;
	private final long delay24h = 3600 * 24 * 1000;
	private ResourceBundle rb = ResourceBundle.getBundle("config");
	private String uniqueID;

	public Scheduler(int mode, int operationStatus, long time, String user) {
		this.mode = mode;
		this.setOperationStatus(operationStatus);
		this.time = time;
		this.user = user;
		this.status = false;

		uniqueID = System.currentTimeMillis() + user;
	}

	/**
	 * Scheduler's run mode.
	 */
	@Override
	public void run() {
		if (status)
			switch (mode) {
			case LOOP_ON_DELAY:
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						if (status) {
							performOperations();
						} else
							stopScheduler();
					}
				}, start.getTime(), time);
				break;
			case LOOP_CERTAIN_TIME:
				start.setTimeInMillis(time);
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						if (status) {
							performOperations();
						} else
							stopScheduler();
					}
				}, start.getTime(), delay24h);
				break;
			case ONE_SHOT:
				performOperations();
				stopScheduler();
				break;
			}
	}

	/**
	 * This is the scheduled method. It switches on selected operation.
	 */
	private synchronized void performOperations() {
		try {
			switch (operationStatus) {
			case TRUNCATE_DATA:
				HibernateUtil.rawQuery("TRUNCATE Table csv");
				if (DEBUG) {
					Util.writeLog(toString());
					Logger.getLogger(Scheduler.class.getName()).log(Level.INFO, toString());
				}
				return;
			case APPEND_DATA:
				parseData(false);
				if (DEBUG) {
					Util.writeLog(toString());
					Logger.getLogger(Scheduler.class.getName()).log(Level.INFO, toString());
				}
				break;
			case UPDATE_DATA:
				parseData(true);
				if (DEBUG) {
					Util.writeLog(toString());
					Logger.getLogger(Scheduler.class.getName()).log(Level.INFO, toString());
				}
				break;
			case ARRANGE_DATA:
				arrangeData();
				if (DEBUG) {
					Util.writeLog(toString());
					Logger.getLogger(Scheduler.class.getName()).log(Level.INFO, toString());
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * It reads csv file with most recently latest modified date
	 */
	private synchronized File readCsvFile() {
		try {
			File lastUpdatedFile = null, directory = new File(rb.getString("file.path"));
			File[] filesList = directory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".csv");
				}
			});

			for (File f : filesList) {
				if (lastUpdatedFile == null) {
					lastUpdatedFile = f;
					continue;
				}
				if (new Date(lastUpdatedFile.lastModified()).before(new Date(f.lastModified())))
					lastUpdatedFile = f;
			}
			if (lastUpdatedFile == null)
				throw new NullPointerException("No csv file in folder " + rb.getString("file.path"));

			return lastUpdatedFile;
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("readCsvFile()", e);
			Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * It appends/updates data in to csv table.
	 * 
	 * complexity: T(n)= Ω( c + (n*m - 3n)) ~ T(n) = O(n^2)
	 */
	private void parseData(boolean isUpdateMode) {
		try {
			File lastUpdatedFile = readCsvFile();
			SimpleDateFormat sdfConverter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			basic.State currentState = new basic.State();
			Csv csvToFill = new Csv();

			Iterable<CSVRecord> records = CSVFormat.newFormat(';').parse(new FileReader(lastUpdatedFile));

			// Reading first extract date
			Date globalFirstExtract = null;
			// Reading last extract date
			ReversedLinesFileReader rlfr = new ReversedLinesFileReader(lastUpdatedFile, Charset.forName("UTF-8"));
			Date globalLastExtract = null;
			while (globalLastExtract == null) {
				String line = rlfr.readLine();
				if (line == null || line.length() == 0 || !line.split(";")[2].trim().equals(currentState.A))
					continue;
				globalLastExtract = sdfConverter.parse(line.split(";")[4].trim());
			}
			rlfr.close();

			/*
			 * Caratteri da rimuovere dovuti a codifica errata durante la
			 * generazione del file csv
			 */
			String regex = "[ยงÂ§]";

			// n
			for (CSVRecord row : records) {

				// Avoid blank rows
				if (row.size() == 0)
					continue;

				// Reading first extract date
				if (globalFirstExtract == null)
					globalFirstExtract = sdfConverter.parse(row.get(3).trim());

				if (csvToFill.getFileName() == null)
					csvToFill.setFileName(lastUpdatedFile.getName());
				if (csvToFill.getFileDate() == null)
					csvToFill.setFileDate(new Date(Long.parseLong(lastUpdatedFile.getName().substring(
							lastUpdatedFile.getName().indexOf("_") - 13, lastUpdatedFile.getName().indexOf("_")))));
				if (csvToFill.getRepository() == null)
					csvToFill.setRepository(
							lastUpdatedFile.getName().substring(0, lastUpdatedFile.getName().indexOf("_") - 14));
				if (csvToFill.getInizioEstrazione() == null)
					csvToFill.setInizioEstrazione(sdfConverter.parse(row.get(3).trim()));
				if (csvToFill.getFineEstrazione() == null)
					csvToFill.setFineEstrazione(sdfConverter.parse(row.get(4).trim()));

				// m - 3
				for (int i = 2; i < row.size() - 1; i++) {
					switch (i) {
					case 2:
						// idRelease
						if (currentState.getCurrentState() == null)
							csvToFill.setIdPolarion(row.get(1).trim().replaceAll(regex, ""));

						// colonna
						String nextNaturalState = currentState.getNextNaturalState();
						currentState.setCurrentState(row.get(i).trim().replaceAll(regex, ""));

						if (nextNaturalState != null && !nextNaturalState.equals(currentState.getCurrentState()))
							Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, "States missmatch expected: ["
									+ nextNaturalState + "] parsed: [" + currentState.getCurrentState() + "]");
						break;
					case 3:
						if (currentState.getCurrentState().equals(currentState.A)) {
							/*
							 * colonnaA
							 * 
							 * dataInizioEstrazioneString;
							 * dataFineEstrazioneString;
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaA() == null)
									csvToFill.setColonnaA(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaA(
											csvToFill.getColonnaA() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.B)) {
							/*
							 * ColonnaB
							 * 
							 * idReleaseDiProgetto; titleReleaseDiProgetto;
							 * priorityReleaseDiProgetto;
							 * severityReleaseDiProgetto; progettoPolarion;
							 * versione; link; release;
							 * dataCreazioneReleaseDiProgetto;
							 * dataUltimoAggiornamentoReleaseDP
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getProgettoPolarion() == null && i == 7)
									csvToFill.setProgettoPolarion(row.get(i).trim().replaceAll(regex, ""));
								if (csvToFill.getColonnaB() == null)
									csvToFill.setColonnaB(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaB(
											csvToFill.getColonnaB() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.C)) {
							/*
							 * ColonnaC
							 * 
							 * progettoRelease
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaC() == null)
									csvToFill.setColonnaC(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaC(
											csvToFill.getColonnaC() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.D)) {
							/*
							 * ColonnaD
							 * 
							 * progettoSviluppo
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaD() == null)
									csvToFill.setColonnaD(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaD(
											csvToFill.getColonnaD() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.E)) {
							/*
							 * ColonnaE
							 * 
							 * progettoMev
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaE() == null)
									csvToFill.setColonnaE(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaE(
											csvToFill.getColonnaE() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.F)) {
							/*
							 * ColonnaF
							 * 
							 * progettoDocumento
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaF() == null)
									csvToFill.setColonnaF(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaF(
											csvToFill.getColonnaF() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.G)) {
							/*
							 * ColonnaG
							 * 
							 * progettoRds
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaG() == null)
									csvToFill.setColonnaG(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaG(
											csvToFill.getColonnaG() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.H)) {
							/*
							 * ColonnaH
							 * 
							 * progettoDefect
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaH() == null)
									csvToFill.setColonnaH(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaH(
											csvToFill.getColonnaH() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.I)) {
							/*
							 * ColonnaI
							 * 
							 * progettoAnomalia
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaI() == null)
									csvToFill.setColonnaI(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaI(
											csvToFill.getColonnaI() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.J)) {
							/*
							 * ColonnaJ
							 * 
							 * progettoReleaseIt
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaJ() == null)
									csvToFill.setColonnaJ(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaJ(
											csvToFill.getColonnaJ() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.K)) {
							/*
							 * ColonnaK
							 * 
							 * taskInfo
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaK() == null)
									csvToFill.setColonnaK(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaK(
											csvToFill.getColonnaK() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.L)) {
							/*
							 * ColonnaL
							 * 
							 * workRecordInfo
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaL() == null)
									csvToFill.setColonnaL(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaL(
											csvToFill.getColonnaL() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.M)) {
							/*
							 * ColonnaM
							 * 
							 * infoTestcase
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaM() == null)
									csvToFill.setColonnaM(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaM(
											csvToFill.getColonnaM() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
						} else if (currentState.getCurrentState().equals(currentState.N)) {
							/*
							 * ColonnaN
							 * 
							 * infoChecklist
							 */
							while (i < row.size() - 1) {
								if (csvToFill.getColonnaN() == null)
									csvToFill.setColonnaN(row.get(i).trim().replaceAll(regex, ""));
								else
									csvToFill.setColonnaN(
											csvToFill.getColonnaN() + ";" + row.get(i).trim().replaceAll(regex, ""));
								i++;
							}
							boolean done = false;
							if (!isUpdateMode) {
								// Append
								done = HibernateUtil.save(csvToFill);
							} else {
								// Update
								Csv existsRow = HibernateUtil.readCsvByIdPolarion(csvToFill.getIdPolarion());
								if (existsRow == null) {
									done = HibernateUtil.save(csvToFill);
								} else {
									csvToFill.setId(existsRow.getId());
									done = HibernateUtil.update(Csv.class, csvToFill);
								}
							}
							if (done) {
								csvToFill = new Csv();
								currentState = new basic.State();
							} else {
								if (Util.DEBUG)
									Util.writeLog("Impossible to persist created object csv: " + csvToFill.toString(),
											new IllegalStateException());
								throw new IllegalStateException(
										"Impossible to persist created object csv: " + csvToFill.toString());
							}
						}
						break;
					} // switch
				} // for
			} // for
			Logger.getLogger(Scheduler.class.getName()).log(Level.INFO, toString());
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("appendData()", e);
			Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * It fills other database' tables starting from csv's one
	 * 
	 * complexity: T(n) = O(n^2)
	 */
	private void arrangeData() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
					literalSdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			List<Csv> list = HibernateUtil.readAllCsv();
			for (Csv csv : list) {
				// Se non esiste il progetto lo creo
				Project pForName = HibernateUtil.readProjectForName(csv.getProgettoPolarion());
				if (pForName == null) {
					pForName = new Project();
					pForName.setNome(csv.getProgettoPolarion());
					HibernateUtil.save(pForName);
				}
				Release releaseDiProgetto = null;
				if (csv.getColonnaB() != null && csv.getColonnaB().length() > 0) {
					/*
					 * ColonnaB
					 * 
					 * idReleaseDiProgetto; titleReleaseDiProgetto;
					 * priorityReleaseDiProgetto; severityReleaseDiProgetto;
					 * progettoPolarion; versione; link; release;
					 * dataCreazioneReleaseDiProgetto;
					 * dataUltimoAggiornamentoReleaseDP
					 */
					String[] bElements = csv.getColonnaB().split(Pattern.quote(";"));
					releaseDiProgetto = HibernateUtil.readRelease(bElements[0].trim());
					boolean toUpdate = false, toSave = false;

					if (releaseDiProgetto == null) {
						releaseDiProgetto = new Release();
						releaseDiProgetto.setIdPolarion(bElements[0].trim());
						toSave = true;
					}
					if (releaseDiProgetto.getTitolo() == null
							|| !releaseDiProgetto.getTitolo().equals(bElements[1].trim())) {
						releaseDiProgetto.setTitolo(bElements[1].trim());
						if (!toSave)
							toUpdate = true;
					}
					if (bElements[2] != null && bElements[2].trim().length() > 0) {
						if (!toSave && releaseDiProgetto.getPriority() != null
								&& releaseDiProgetto.getPriority().getValore() != Float.parseFloat(bElements[2].trim()))
							toUpdate = true;
						if (toSave || toUpdate)
							releaseDiProgetto
									.setPriority(HibernateUtil.readPriority(Float.parseFloat(bElements[2].trim())));
					}
					if (bElements[3] != null && bElements[3].trim().length() > 0) {
						if (!toSave && releaseDiProgetto.getSeverity() != null
								&& !releaseDiProgetto.getSeverity().getNome().equals(bElements[3].trim()))
							toUpdate = true;
						if (toSave || toUpdate)
							releaseDiProgetto.setSeverity(HibernateUtil.readSeverity(bElements[3].trim()));
					}
					releaseDiProgetto.setProject(pForName);
					if (bElements[5] != null && bElements[5].trim().length() > 0) {
						if (!toSave && releaseDiProgetto.getVersione() != null
								&& !releaseDiProgetto.getVersione().equals(bElements[5].trim()))
							toUpdate = true;
						releaseDiProgetto.setVersione(bElements[5].trim());
					}
					if (bElements[6] != null && bElements[6].trim().length() > 0) {
						if (!toSave && releaseDiProgetto.getLink() != null
								&& !releaseDiProgetto.getLink().equals(bElements[6].trim()))
							toUpdate = true;
						releaseDiProgetto.setLink(bElements[6].trim());
						releaseDiProgetto.setRepository(releaseDiProgetto.getLink().substring("http://sgr-".length(),
								releaseDiProgetto.getLink().indexOf(".adlispa")));
					}
					if (bElements[7] != null && bElements[7].trim().length() > 0) {
						if (!toSave && releaseDiProgetto.getType() != null
								&& !releaseDiProgetto.getType().equals(bElements[6].trim()))
							toUpdate = true;
						releaseDiProgetto.setType(bElements[7].trim());
					}
					if (bElements[8] != null && bElements[8].trim().length() > 0) {
						Date dataCreazione = sdf.parse(bElements[8].trim());
						if (!toSave && releaseDiProgetto.getDataCreazione() != null
								&& !releaseDiProgetto.getDataCreazione().equals(dataCreazione))
							toUpdate = true;
						releaseDiProgetto.setDataCreazione(dataCreazione);
					}
					if (bElements[9] != null && bElements[9].trim().length() > 0) {
						Date dataUltimoAggiornamento = sdf.parse(bElements[9].trim());
						if (!toSave && releaseDiProgetto.getDataUpdate() != null
								&& !releaseDiProgetto.getDataUpdate().equals(dataUltimoAggiornamento))
							toUpdate = true;
						releaseDiProgetto.setDataUpdate(dataUltimoAggiornamento);
					}
					if (toSave)
						HibernateUtil.save(releaseDiProgetto);
					else if (toUpdate)
						HibernateUtil.update(Release.class, releaseDiProgetto);

				}
				if (csv.getColonnaC() != null && csv.getColonnaC().length() > 0) {
					/*
					 * ColonnaC
					 * 
					 * progettoRelease
					 */
					String regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaC());
					while (matcher.find()) {
						String[] history = matcher.group(1).split(Pattern.quote("^"));

						Status status = null;
						if (history.length > 0)
							status = HibernateUtil.readStatus(history[0].trim());
						Date dataUpdate = null;
						if (history.length > 1)
							dataUpdate = sdf.parse(history[1]);
						User user = null;
						if (history.length > 2)
							user = HibernateUtil.readUser(history[2].trim());

						ReleaseHistory rHistory = HibernateUtil.readReleaseHistory(releaseDiProgetto, status,
								dataUpdate, user);
						if (rHistory == null) {
							rHistory = new ReleaseHistory();
							rHistory.setRelease(releaseDiProgetto);
							rHistory.setStatus(status);
							rHistory.setDataUpdate(dataUpdate);
							rHistory.setUser(user);
							HibernateUtil.save(rHistory);
						}
					}
				}
				ProgettoSviluppo ps = null;
				if (csv.getColonnaD() != null && csv.getColonnaD().length() > 0) {
					/*
					 * ColonnaD
					 * 
					 * progettoSviluppo
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaD().trim());
					while (matcher.find()) {
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(releaseDiProgetto.getIdPolarion());
						lIId.setIdPolarionFiglio(elements[0].trim());
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						ps = HibernateUtil.readProgettoSviluppo(elements[0].trim());
						if (ps == null) {
							ps = new ProgettoSviluppo();
							ps.setIdPolarion(elements[0].trim());
							ps.setTitolo(elements[1].trim());
						}
					}
				}
				if (csv.getColonnaE() != null && csv.getColonnaE().length() > 0) {
					/*
					 * ColonnaE
					 * 
					 * progettoMev
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaE().trim());
					while (matcher.find()) {
						Mev mev = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(releaseDiProgetto.getIdPolarion());
						lIId.setIdPolarionFiglio(elements[0]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						mev = HibernateUtil.readMev(elements[0].trim());
						if (mev == null) {
							mev = new Mev();
							mev.setIdPolarion(elements[0].trim());
							mev.setTitolo(elements[1].trim());
						}
					}
				}
				if (csv.getColonnaF() != null && csv.getColonnaF().length() > 0) {
					/*
					 * ColonnaF
					 * 
					 * progettoDocumento
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaF().trim());
					while (matcher.find()) {
						Documento doc = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(elements[1].trim());
						lIId.setIdPolarionFiglio(elements[2].trim());
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}

						doc = HibernateUtil.readDocument(elements[2].trim());
						if (doc == null) {
							doc = new Documento();
							doc.setIdPolarion(elements[2].trim());
							doc.setTitolo(elements[3].trim());
						}
					}
				}
				if (csv.getColonnaG() != null && csv.getColonnaG().length() > 0) {
					/*
					 * ColonnaG
					 * 
					 * progettoRds
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaG().trim());
					while (matcher.find()) {
						Support richiestaDiSupporto = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(releaseDiProgetto.getIdPolarion());
						lIId.setIdPolarionFiglio(elements[0]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						richiestaDiSupporto = HibernateUtil.readRichiestaDiSupporto(elements[0].trim());
						boolean toSave = false, toUpdate = false;
						if (richiestaDiSupporto == null
								|| !richiestaDiSupporto.getTitolo().equals(elements[1].trim())) {
							if (richiestaDiSupporto == null) {
								richiestaDiSupporto = new Support();
								richiestaDiSupporto.setIdPolarion(elements[0].trim());
								toSave = true;
							}
							if (richiestaDiSupporto.getTitolo() == null
									|| !richiestaDiSupporto.getTitolo().equals(elements[1].trim())) {
								richiestaDiSupporto.setTitolo(elements[1].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (elements.length > 2 && elements[2] != null && elements[2].trim().length() > 0) {
								if (!toSave && richiestaDiSupporto.getSeverity() != null
										&& !richiestaDiSupporto.getSeverity().getNome().equals(elements[2].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									richiestaDiSupporto.setSeverity(HibernateUtil.readSeverity(elements[2].trim()));
							}
							if (elements.length > 3 && elements[3] != null && elements[3].trim().length() > 0) {
								if (!toSave && richiestaDiSupporto.getPriority() != null && richiestaDiSupporto
										.getPriority().getValore() != Float.parseFloat(elements[3].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									richiestaDiSupporto.setPriority(
											HibernateUtil.readPriority(Float.parseFloat(elements[3].trim())));
							}
							if (elements.length > 4 && elements[4] != null && elements[4].trim().length() > 0) {
								if (!toSave && richiestaDiSupporto.getResolution() != null && richiestaDiSupporto
										.getResolution().getPolarionName().equals(elements[4].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									richiestaDiSupporto.setResolution(HibernateUtil.readResolution(elements[4].trim()));
							}
							if (toSave)
								HibernateUtil.save(richiestaDiSupporto);
							else if (toUpdate)
								HibernateUtil.update(Support.class, richiestaDiSupporto);
						}
						regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
						pattern = Pattern.compile(regex);
						if (elements.length > 5) {
							Matcher matcherHistory = pattern.matcher(elements[5].trim());
							boolean isFirstRow = true;
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Status status = HibernateUtil.readStatus(history[0].trim());

								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[1]);
								}

								if (isFirstRow && richiestaDiSupporto.getDataCreazione() == null) {
									richiestaDiSupporto.setDataCreazione(dataUpdate);
									HibernateUtil.update(Support.class, richiestaDiSupporto);
									isFirstRow = false;
								}

								SupportHistory rdsHistory = null;
								/*
								 * eventuale read su db qualora si introduca
								 * l'estrazione degli utenti
								 * 
								 * rdsHistory = HibernateUtil.readDefectHistory(
								 * richiestaDiSupporto, status, dataUpdate,
								 * user);
								 */
								if (rdsHistory == null) {
									rdsHistory = new SupportHistory();
									rdsHistory.setSupport(richiestaDiSupporto);
									rdsHistory.setStatus(status);
									rdsHistory.setDataUpdate(dataUpdate);
									HibernateUtil.save(rdsHistory);
								}
							}
						} // while
					}
				}
				if (csv.getColonnaH() != null && csv.getColonnaH().length() > 0) {
					/*
					 * ColonnaH
					 * 
					 * progettoDefect
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaH().trim());
					while (matcher.find()) {
						Defect defect = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));

						LinkedItemId lIId = new LinkedItemId();
						try {
							lIId.setIdPolarionPadre(elements[1]);
							lIId.setIdPolarionFiglio(elements[2]);
						} catch (IndexOutOfBoundsException ioobe) {
							if (Util.DEBUG)
								Util.writeLog("IndexOutOfBoundsException while creating linkedItem data: "
										+ Arrays.toString(elements), ioobe);
							continue;
						}

						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						defect = HibernateUtil.readDefect(elements[2].trim());
						boolean toSave = false, toUpdate = false;
						if (defect == null || !defect.getTitolo().equals(elements[3].trim())) {
							if (defect == null) {
								defect = new Defect();
								defect.setIdPolarion(elements[2].trim());
								toSave = true;
							}
							if (defect.getTitolo() == null || !defect.getTitolo().equals(elements[3].trim())) {
								defect.setTitolo(elements[3].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (elements.length > 4 && (elements[4] != null && elements[4].trim().length() > 0)) {
								if (!toSave && defect.getSeverity() != null
										&& !defect.getSeverity().getNome().equals(elements[4].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									defect.setSeverity(HibernateUtil.readSeverity(elements[4].trim()));
							}
							if (elements.length > 5 && (elements[5] != null && elements[5].trim().length() > 0)) {
								if (!toSave && defect.getPriority() != null
										&& defect.getPriority().getValore() != Float.parseFloat(elements[5].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									defect.setPriority(
											HibernateUtil.readPriority(Float.parseFloat(elements[5].trim())));
							}
							if (elements.length > 6 && (elements[6] != null && elements[6].trim().length() > 0)) {
								if (!toSave && defect.getResolution() != null
										&& !defect.getResolution().getPolarionName().equals(elements[6].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									defect.setResolution(HibernateUtil.readResolution(elements[6].trim()));
							}
							if (toSave)
								HibernateUtil.save(defect);
							else if (toUpdate)
								HibernateUtil.update(Support.class, defect);
						}
						if (elements.length > 7) {
							regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
							pattern = Pattern.compile(regex);
							Matcher matcherHistory = pattern.matcher(elements[7].trim());
							boolean isFirstRow = true;
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Status status = HibernateUtil.readStatus(history[0].trim());

								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[1]);
								}

								if (isFirstRow && defect.getDataCreazione() == null) {
									defect.setDataCreazione(dataUpdate);
									HibernateUtil.update(Support.class, defect);
									isFirstRow = false;
								}

								DefectHistory dHistory = null;
								/*
								 * eventuale read su db qualora si introduca
								 * l'estrazione degli utenti
								 * 
								 * dHistory =
								 * HibernateUtil.readDefectHistory(defect,
								 * status, dataUpdate, user);
								 */
								if (dHistory == null) {
									dHistory = new DefectHistory();
									dHistory.setDefect(defect);
									dHistory.setStatus(status);
									dHistory.setDataUpdate(dataUpdate);
									HibernateUtil.save(dHistory);
								}
							}
						} // while
					}
				}
				if (csv.getColonnaI() != null && csv.getColonnaI().length() > 0) {
					/*
					 * ColonnaI
					 * 
					 * progettoAnomalia
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaI().trim());
					while (matcher.find()) {
						Anomalia anomalia = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(releaseDiProgetto.getIdPolarion());
						lIId.setIdPolarionFiglio(elements[0]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						anomalia = HibernateUtil.readAnomalia(elements[0].trim());
						boolean toSave = false, toUpdate = false;
						if (anomalia == null || !anomalia.getTitolo().equals(elements[1].trim())) {
							if (anomalia == null) {
								anomalia = new Anomalia();
								anomalia.setIdPolarion(elements[0].trim());
								toSave = true;
							}
							if (anomalia.getTitolo() == null || !anomalia.getTitolo().equals(elements[1].trim())) {
								anomalia.setTitolo(elements[1].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (elements.length > 2 && elements[2] != null && elements[2].trim().length() > 0) {
								if (!toSave && anomalia.getSeverity() != null
										&& !anomalia.getSeverity().getNome().equals(elements[2].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									anomalia.setSeverity(HibernateUtil.readSeverity(elements[2].trim()));
							}
							if (elements.length > 3 && elements[3] != null && elements[3].trim().length() > 0) {
								if (!toSave && anomalia.getPriority() != null
										&& anomalia.getPriority().getValore() != Float.parseFloat(elements[3].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									anomalia.setPriority(
											HibernateUtil.readPriority(Float.parseFloat(elements[3].trim())));
							}
							if (elements.length > 4 && elements[4] != null && elements[4].trim().length() > 0) {
								if (!toSave && anomalia.getResolution() != null
										&& anomalia.getResolution().getPolarionName().equals(elements[4].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									anomalia.setResolution(HibernateUtil.readResolution(elements[4].trim()));
							}
							if (toSave)
								HibernateUtil.save(anomalia);
							else if (toUpdate)
								HibernateUtil.update(Support.class, anomalia);
						}
						if (elements.length > 5) {
							regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
							pattern = Pattern.compile(regex);
							Matcher matcherHistory = pattern.matcher(elements[5].trim());
							boolean isFirstRow = true;
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Status status = HibernateUtil.readStatus(history[0].trim());

								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[1]);
								}

								if (isFirstRow && anomalia.getDataCreazione() == null) {
									anomalia.setDataCreazione(dataUpdate);
									HibernateUtil.update(Support.class, anomalia);
									isFirstRow = false;
								}

								AnomaliaHistory aHistory = null;
								/*
								 * eventuale read su db qualora si introduca
								 * l'estrazione degli utenti
								 * 
								 * aHistory =
								 * HibernateUtil.readAnomaliaHistory(anomalia,
								 * status, dataUpdate, user);
								 */
								if (aHistory == null) {
									aHistory = new AnomaliaHistory();
									aHistory.setAnomalia(anomalia);
									aHistory.setStatus(status);
									aHistory.setDataUpdate(dataUpdate);
									HibernateUtil.save(aHistory);
								}
							}
						} // while
					}
				}
				ReleaseIt releaseIT = null;
				if (csv.getColonnaJ() != null && csv.getColonnaJ().length() > 0) {
					/*
					 * ColonnaJ
					 * 
					 * progettoReleaseIt
					 */
					boolean hasLinkedItem = false;
					if (csv.getColonnaJ().startsWith("<") && csv.getColonnaJ().endsWith(">")) {
						csv.setColonnaJ(csv.getColonnaJ().substring(1, csv.getColonnaJ().length() - 1));
						hasLinkedItem = true;
					}
					String[] elements = csv.getColonnaJ().split(Pattern.quote("|"));
					if (hasLinkedItem) {
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(elements[1]);
						lIId.setIdPolarionFiglio(releaseDiProgetto.getIdPolarion());
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
					}
					releaseIT = HibernateUtil.readReleaseIT(elements[1].trim());
					boolean toSave = false;
					if (releaseIT == null || !releaseIT.getTitolo().equals(elements[2].trim())) {
						if (releaseIT == null) {
							releaseIT = new ReleaseIt();
							releaseIT.setIdPolarion(elements[1].trim());
							releaseIT.setRepository(releaseDiProgetto.getRepository());
							releaseIT.setSeverity(releaseDiProgetto.getSeverity());
							releaseIT.setPriority(releaseDiProgetto.getPriority());
							toSave = true;
						}
						releaseIT.setTitolo(elements[2].trim());
						if (elements[3] != null && elements[3].trim().length() > 0) {
							Date d = null;
							try {
								d = sdf.parse(elements[3].trim());
							} catch (ParseException pe) {
								d = literalSdf.parse(elements[3].trim());
							}
							releaseIT.setDataInizio(d);
						}
						if (elements[4] != null && elements[4].trim().length() > 0) {
							try {
								releaseIT.setDataFine(sdf.parse(elements[4].trim()));
							} catch (ParseException pe) {
								releaseIT.setDataFine(literalSdf.parse(elements[4].trim()));
							}
						}
						if (toSave)
							HibernateUtil.save(releaseIT);
						else
							HibernateUtil.update(ReleaseIt.class, releaseIT);
					}
					if (elements.length > 5 && elements[5] != null && elements[5].trim().length() > 0) {
						String regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(elements[5].trim());
						boolean isFirstRow = true;
						// formato sdf per Thu Feb 18 00:00:00 CET 2016
						while (matcher.find()) {
							String[] history = matcher.group(1).split(Pattern.quote("^"));
							Status status = HibernateUtil.readStatus(history[0].trim());

							Date dataUpdate = null;
							try {
								dataUpdate = sdf.parse(history[1]);
							} catch (ParseException pe) {
								dataUpdate = literalSdf.parse(history[1]);
							}

							if (isFirstRow && releaseIT.getDataCreazione() == null) {
								releaseIT.setDataCreazione(dataUpdate);
								HibernateUtil.update(ReleaseIt.class, releaseIT);
								isFirstRow = false;
							}

							ReleaseitHistory rItHistory = null;
							if (history.length > 2) {
								User user = HibernateUtil.readUser(history[2].trim());
								rItHistory = HibernateUtil.readReleaseItHistory(releaseIT, status, dataUpdate, user);
							}
							if (rItHistory == null) {
								rItHistory = new ReleaseitHistory();
								rItHistory.setReleaseIt(releaseIT);
								rItHistory.setStatus(status);
								rItHistory.setDataUpdate(dataUpdate);
								HibernateUtil.save(rItHistory);
							}
						}
					}
				}
				if (csv.getColonnaK() != null && csv.getColonnaK().length() > 0) {
					/*
					 * ColonnaK
					 * 
					 * taskInfo
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaK().trim());
					while (matcher.find()) {
						Taskit taskit = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(releaseIT.getIdPolarion());
						lIId.setIdPolarionFiglio(elements[0]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						taskit = HibernateUtil.readTaskIT(elements[0].trim());
						boolean toSave = false, toUpdate = false;
						if (taskit == null || !taskit.getTitolo().equals(elements[1].trim())) {
							if (taskit == null) {
								taskit = new Taskit();
								taskit.setIdPolarion(elements[0].trim());
								toSave = true;
							}
							if (taskit.getTitolo() == null || !taskit.getTitolo().equals(elements[1].trim())) {
								taskit.setTitolo(elements[1].trim());
								if (!toSave)
									toUpdate = true;
							}
							if (elements.length > 2 && elements[2] != null && elements[2].trim().length() > 0) {
								if (!toSave && taskit.getTipoItem() != null
										&& !taskit.getTipoItem().getNome().equals(elements[2].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									taskit.setTipoItem(HibernateUtil.readTipoItem(elements[2].trim()));
							}
							if (elements.length > 3 && elements[3] != null && elements[3].trim().length() > 0) {
								if (!toSave && taskit.getTimespent() != null
										&& !taskit.getTimespent().equals(elements[3].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									taskit.setTimespent(elements[3].trim());
							}

							if (elements.length > 4 && elements[4] != null && elements[4].trim().length() > 0) {
								if (!toSave && taskit.getInitialEstimate() != null
										&& taskit.getInitialEstimate().equals(elements[4].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									taskit.setInitialEstimate(elements[4].trim());
							}

							if (elements.length > 5 && elements[5] != null && elements[5].trim().length() > 0) {
								if (!toSave && taskit.getResolution() != null
										&& taskit.getResolution().equals(elements[5].trim()))
									toUpdate = true;
								if (toSave || toUpdate)
									taskit.setResolution(HibernateUtil.readResolution(elements[5].trim()));
							}
							if (toSave)
								HibernateUtil.save(taskit);
							else if (toUpdate)
								HibernateUtil.update(Taskit.class, taskit);
						}
						if (elements.length > 6) {
							regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
							pattern = Pattern.compile(regex);
							Matcher matcherHistory = pattern.matcher(elements[6].trim());
							boolean isFirstRow = true;
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Status status = HibernateUtil.readStatus(history[0].trim());

								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[1]);
								}

								if (isFirstRow && taskit.getDataCreazione() == null) {
									taskit.setDataCreazione(dataUpdate);
									HibernateUtil.update(Support.class, taskit);
									isFirstRow = false;
								}

								TaskItHistory tItHistory = null;
								/*
								 * eventuale read su db qualora si introduca
								 * l'estrazione degli utenti
								 * 
								 * tItHistory =
								 * HibernateUtil.readTaskITHistory(taskit,
								 * status, dataUpdate, user);
								 */
								if (tItHistory == null) {
									tItHistory = new TaskItHistory();
									tItHistory.setTaskit(taskit);
									tItHistory.setStatus(status);
									tItHistory.setDataUpdate(dataUpdate);
									HibernateUtil.save(tItHistory);
								}
							}
						} // while
					}
				}
				if (csv.getColonnaL() != null && csv.getColonnaL().length() > 0) {
					/*
					 * ColonnaL
					 * 
					 * workRecordInfo
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaL().trim());
					while (matcher.find()) {
						boolean toSave = false, toUpdate = false;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));

						regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
						pattern = Pattern.compile(regex);
						Matcher matcherHistory = pattern.matcher(elements[2].trim());
						while (matcherHistory.find()) {
							String[] elementsHistory = matcherHistory.group(1).split(Pattern.quote("^"));

							User user = null;
							if (elementsHistory[0].trim().length() > 0)
								user = HibernateUtil.readUser(elementsHistory[0]);

							Date dataUpdate = null;
							if (elementsHistory[1].trim().length() > 0) {
								try {
									dataUpdate = sdf.parse(elementsHistory[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(elementsHistory[1].trim());
								}
							}

							Workrecords workrecord = HibernateUtil.readWorkRecord(elements[0].trim(),
									elements[1].trim(), user, dataUpdate);
							if (workrecord == null) {
								workrecord = new Workrecords();
								toSave = true;
							}

							if (toSave || !workrecord.getTypeLink().equals(elements[0].trim())) {
								workrecord.setTypeLink(elements[0].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (toSave || !workrecord.getIdPolarion().equals(elements[1].trim())) {
								workrecord.setIdPolarion(elements[1].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (toSave || (dataUpdate != null && !workrecord.getDateUpdate().equals(dataUpdate))) {
								workrecord.setDateUpdate(dataUpdate);
								if (!toSave)
									toUpdate = true;
							}
							if (toSave || !workrecord.getWorkTime().equals(elementsHistory[2].trim())) {
								workrecord.setWorkTime(elementsHistory[2].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (elementsHistory.length > 3
									&& (toSave || !workrecord.getWorkType().equals(elementsHistory[3].trim()))) {
								workrecord.setWorkType(elementsHistory[3].trim());
								if (!toSave)
									toUpdate = true;
							}

							if (elementsHistory.length > 4
									&& (toSave || !workrecord.getNote().equals(elementsHistory[4].trim()))) {
								workrecord.setNote(elementsHistory[4].trim());
								if (!toSave)
									toUpdate = true;
							}
							if (toSave)
								HibernateUtil.save(workrecord);
							else if (toUpdate)
								HibernateUtil.update(Workrecords.class, workrecord);
						}
					}
				}
				if (csv.getColonnaM() != null && csv.getColonnaM().length() > 0) {
					/*
					 * ColonnaM
					 * 
					 * infoTestcase
					 */
					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaM().trim());
					while (matcher.find()) {
						Testcase testcase = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(elements[1]);
						lIId.setIdPolarionFiglio(elements[2]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						testcase = HibernateUtil.readTestCase(elements[2].trim());
						boolean toSave = false, toUpdate = false;
						if (testcase == null || !testcase.getTitolo().equals(elements[1].trim())) {
							if (testcase == null) {
								testcase = new Testcase();
								testcase.setIdPolarion(elements[2].trim());
								testcase.setUser(null);
								toSave = true;
							}

							if (toSave || !testcase.getTitolo().equals(elements[3].trim())) {
								testcase.setTitolo(elements[3].trim());
								if (!toSave)
									toUpdate = true;
							}

							Status status = null;
							if (elements.length > 4) {
								status = HibernateUtil.readStatus(elements[4].trim());
								if (toSave || (status != null && !testcase.getStatus().equals(status))) {
									testcase.setStatus(status);
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 5 && elements[5].trim().length() > 0) {
								Priority priority = HibernateUtil.readPriority(Float.parseFloat(elements[5].trim()));
								if (toSave || !testcase.getPriority().equals(priority)) {
									testcase.setPriority(priority);
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 6 && elements[6].trim().length() > 0) {
								if (toSave || !testcase.getTag().equals(elements[6].trim())) {
									testcase.setTag(elements[6].trim());
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 7 && elements[7].trim().length() > 0) {
								if (toSave || !testcase.getTipoTC().equals(elements[7].trim())) {
									testcase.setTipoTC(elements[7].trim());
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 8 && elements[8].trim().length() > 0) {
								if (toSave || !testcase.getVersioneReference().equals(elements[8].trim())) {
									testcase.setVersioneReference(elements[8].trim());
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 9 && elements[9].trim().length() > 0) {
								if (toSave || !testcase.getObsoletoda().equals(elements[9].trim())) {
									testcase.setObsoletoda(elements[9].trim());
									if (!toSave)
										toUpdate = true;
								}
							}

							if (elements.length > 10 && elements[10].trim().length() > 0) {
								if (toSave || testcase.isTestato() != Boolean.parseBoolean(elements[10].trim())) {
									testcase.setTestato(Boolean.parseBoolean(elements[10].trim()));
									if (!toSave)
										toUpdate = true;
								}
							}

							if (toSave)
								HibernateUtil.save(testcase);
							else if (toUpdate)
								HibernateUtil.update(Taskit.class, testcase);
						}

						if (elements.length > 11) {
							regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
							pattern = Pattern.compile(regex);
							Matcher matcherHistory = pattern.matcher(elements[11].trim());
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Status status = HibernateUtil.readStatus(history[0].trim());

								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[1]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[1]);
								}

								TestcaseHistory tHistory = null;
								/*
								 * eventuale read su db qualora si introduca
								 * l'estrazione degli utenti
								 * 
								 * testcaseHistory =
								 * HibernateUtil.readTestcaseHistory(testcase,
								 * status, dataUpdate, user);
								 */
								if (tHistory == null) {
									tHistory = new TestcaseHistory();
									tHistory.setTestcase(testcase);
									tHistory.setStatus(status);
									tHistory.setData(dataUpdate);
									HibernateUtil.save(tHistory);
								}
							}
						} // while
					}
				}
				if (csv.getColonnaN() != null && csv.getColonnaN().length() > 0) {
					/*
					 * ColonnaN
					 * 
					 * infoChecklist
					 */
					if (csv.getColonnaN().endsWith(";"))
						csv.setColonnaN(csv.getColonnaN().substring(0, csv.getColonnaN().length() - 1));

					String regex = Pattern.quote("<") + "(.*?)" + Pattern.quote(">");
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(csv.getColonnaN().trim());
					while (matcher.find()) {
						Checklist checkList = null;
						String[] elements = matcher.group(1).split(Pattern.quote("|"));
						LinkedItemId lIId = new LinkedItemId();
						lIId.setIdPolarionPadre(elements[1]);
						lIId.setIdPolarionFiglio(elements[2]);
						LinkedItem li = HibernateUtil.readLinkedItem(lIId);
						if (li == null) {
							li = new LinkedItem();
							li.setId(lIId);
							HibernateUtil.save(li);
						}
						checkList = HibernateUtil.readCheckList(elements[2].trim());
						boolean toSave = false, toUpdate = false;
						if (checkList == null) {
							checkList = new Checklist();
							checkList.setIdPolarion(elements[2].trim());
							toSave = true;
						}

						Status status = HibernateUtil.readStatus(elements[3].trim());
						if (toSave || (status != null && !checkList.getStatus().equals(status))) {
							checkList.setStatus(status);
							if (!toSave)
								toUpdate = true;
						}

						if (elements[4].trim().length() > 0) {
							Status esito = HibernateUtil.readStatus(elements[4].trim());
							if (toSave || (esito != null && !checkList.getEsitoChecklist().equals(esito))) {
								checkList.setEsitoChecklist(esito);
								if (!toSave)
									toUpdate = true;
							}
						}

						if (elements[5].trim().length() > 0) {
							Date dataCreazione = null;
							try {
								dataCreazione = sdf.parse(elements[5].trim());
							} catch (ParseException pe) {
								dataCreazione = literalSdf.parse(elements[5].trim());
							}
							if (toSave || !checkList.getDataCreazione().equals(dataCreazione)) {
								checkList.setDataCreazione(dataCreazione);
								if (!toSave)
									toUpdate = true;
							}
						}

						if (elements[6].trim().length() > 0) {
							Date dataTermine = null;
							try {
								dataTermine = sdf.parse(elements[6].trim());
							} catch (ParseException pe) {
								dataTermine = literalSdf.parse(elements[6].trim());
							}
							if (toSave || !checkList.getDataFine().equals(dataTermine)) {
								checkList.setDataFine(dataTermine);
								if (!toSave)
									toUpdate = true;
							}
						}

						if (elements[7].trim().length() > 0) {
							Date dataUpdate = null;
							try {
								dataUpdate = sdf.parse(elements[7].trim());
							} catch (ParseException pe) {
								dataUpdate = literalSdf.parse(elements[7].trim());
							}
							if (toSave || !checkList.getDataUpdate().equals(dataUpdate)) {
								checkList.setDataUpdate(dataUpdate);
								if (!toSave)
									toUpdate = true;
							}
						}

						if (toSave)
							HibernateUtil.save(checkList);
						else if (toUpdate)
							HibernateUtil.update(Taskit.class, checkList);
						if (elements.length > 8) {
							regex = Pattern.quote("(") + "(.*?)" + Pattern.quote(")");
							pattern = Pattern.compile(regex);
							Matcher matcherHistory = pattern.matcher(elements[8].trim());
							while (matcherHistory.find()) {
								String[] history = matcher.group(1).split(Pattern.quote("^"));
								Testcase tc = null;
								if (history[1].trim().length() > 0)
									tc = HibernateUtil.readTestCase(history[0]);

								User userHistory = null;
								if (history[1].trim().length() > 0)
									userHistory = HibernateUtil.readUser(history[1].trim());
								Date dataUpdate = null;
								try {
									dataUpdate = sdf.parse(history[2]);
								} catch (ParseException pe) {
									dataUpdate = literalSdf.parse(history[2]);
								}

								Esito esitoHistory = null;
								if (history[3].trim().length() > 0)
									esitoHistory = HibernateUtil.readEsito(history[3].trim());

								ChecklistTestcase clTc = HibernateUtil.readCheckListTestCase(checkList, tc, userHistory,
										dataUpdate);
								if (clTc == null) {
									clTc = new ChecklistTestcase();
									clTc.setChecklist(checkList);
									clTc.setTestcase(tc);
									clTc.setEsito(esitoHistory);
									clTc.setDataEsecuzione(dataUpdate);
									clTc.setUser(userHistory);
									HibernateUtil.save(clTc);
								}
							}
						} // while
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * It starts scheduler.
	 */
	@Override
	public synchronized void start() {
		t = new Timer();
		start = new GregorianCalendar();
		startDate = start.getTime();
		status = true;
		run();
	}

	/**
	 * It completely stops the scheduler and clears all scheduled jobs.
	 */
	public synchronized void stopScheduler() {
		status = false;
		start = null;
		if (t != null) {
			t.cancel();
			t.purge();
			t = null;
		}
		startDate = null;
		super.interrupt();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy HH:mm:ss.zzz");
		if (status)
			sb.append("Scheduler is running\n");
		else
			sb.append("Scheduler is not running\n");
		if (startDate != null)
			sb.append("Start date: " + sdf.format(startDate).toUpperCase() + "\n");
		if (user != null)
			sb.append("Owner user: " + user + "\n");

		switch (mode) {
		case LOOP_ON_DELAY:
			sb.append("Current Execution Mode: LOOP ON DELAY\n");
			sb.append("Delay time: " + time + "ms\n");
			break;
		case LOOP_CERTAIN_TIME:
			sb.append("Current Execution Mode: LOOP CERTAIN TIME\n");
			sb.append("Delay time: [24h]\n");
			sb.append("Actoions performing time: " + sdf.format(time) + " \n");
			break;
		case ONE_SHOT:
			sb.append("Current Execution Mode: ONE SHOT\n");
			break;
		}
		switch (operationStatus) {
		case TRUNCATE_DATA:
			sb.append("Current Operation Type: TRUNCATE_DATA\n");
			break;
		case APPEND_DATA:
			sb.append("Current Operation Type: APPEND_DATA\n");
			break;
		case UPDATE_DATA:
			sb.append("Current Operation Type: UPDATE_DATA\n");
			break;
		case ARRANGE_DATA:
			sb.append("Current Operation Type: ARRANGE_DATA\n");
			break;
		}
		if (start != null) {
			long currentTime = System.currentTimeMillis() - startDate.getTime();
			sb.append("Current scheduler elapsed time: " + currentTime / 1000 + " sec");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return current Date, scheduler's time.
	 */
	public Date getCurrentTime() {
		return start.getTime();
	}

	/**
	 * Time is used with the mode variable: If mode is LOOP_ON_DELAY, time is
	 * the delay. If mode is LOOP_CERTAIN_TIME, delay is 24h and time will be
	 * the schedule date. If mode is ONE_SHOT, time is the value when the
	 * scheduler will perform the operations.
	 * 
	 * @return long time.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Time is used with the mode variable: If mode is LOOP_ON_DELAY, time is
	 * the delay. If mode is LOOP_CERTAIN_TIME, delay is 24h and time will be
	 * the schedule date. If mode is ONE_SHOT, time is the value when the
	 * scheduler will perform the operations.
	 * 
	 * @param long
	 *            time.
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 
	 * @return current scheduler's mode.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * LOOP_ON_DELAY: it cycles and performs operations with a certain cadence.
	 * LOOP_CERTAIN_TIME: delay will be always 24h and schedule will executes
	 * operations in a certain time. ONE_SHOT: scheduler will performs
	 * operations only one time.
	 * 
	 * @param mode,
	 *            it sets scheduler's behavior.
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * 
	 * @return running status, true if running false otherwise.
	 */
	public boolean getCurrentStatus() {
		return status;
	}

	/**
	 * 
	 * @return user who create current scheduler's instance.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * 
	 * @return first date when scheduler started
	 */
	public Date getStartDate() {
		return startDate;
	}

	public int getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(int operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * 
	 * @return uniqueID that identify the current scheduler
	 */
	public String getUniqueID() {
		return uniqueID;
	}
}
