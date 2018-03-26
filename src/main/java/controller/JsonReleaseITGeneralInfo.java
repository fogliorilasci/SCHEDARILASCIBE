package controller;

import java.rmi.activation.UnknownObjectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.cj.x.json.JsonArray;

import basic.HibernateUtil;
import basic.Util;
import entities.Checklist;
import entities.Defect;
import entities.ReleaseHistory;
import entities.ReleaseIt;
import entities.ReleaseitHistory;
import entities.TaskItHistory;
import entities.Taskit;
import entities.Testcase;
import entities.User;
import entities.Workrecords;

public class JsonReleaseITGeneralInfo {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static ResourceBundle usersTypes = ResourceBundle.getBundle("users");

	/**
	 * It retrieves release IT's general info. Param must be the releaseIT's
	 * id_polarion.
	 * 
	 * @param List<ReleaseHistory>
	 *            list, String status
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getReleaseITGeneralInfo(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<ReleaseIt> result = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);

		long totDocumenti = 0;
		long totDefect = 0;
		long totAnomalie = 0;
		long totProgettoSviluppo = 0;
		long totSupporto = 0;
		long totMev = 0;

		for (ReleaseIt r : result) {
			if (result.size() == 1) {
				JSONArray objArray = new JSONArray();
				int columnIndex = 0;
				objArray.add(columnIndex++, param);
				String project = QueryReleaseIT.getProjectByReleaseIT(r.getIdPolarion());
				if (project != null) {
					objArray.add(columnIndex++, project);
					objArray.add(columnIndex++, project.substring(project.indexOf("{") + 1, project.indexOf("}")));
				} else {
					objArray.add(columnIndex++, "");
					objArray.add(columnIndex++, "");
				}
				objArray.add(columnIndex++, r.getRepository());
				objArray.add(columnIndex++, r.getTitolo());
				objArray.add(columnIndex++, r.getPriority().getNome());
				objArray.add(columnIndex++, r.getSeverity().getNome());
				if (r.getDataInizio() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataInizio()));
				else
					objArray.add(columnIndex++, "");

				if (r.getDataFine() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataFine()));
				else
					objArray.add(columnIndex++, "");

				if (r.getDataCreazione() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataCreazione()));
				else
					objArray.add(columnIndex++, "");

				objArray.add(columnIndex++, QueryReleaseIT.getReleaseByReleaseIT(r.getIdPolarion()).getIdPolarion());
				objArray.add(columnIndex++,
						QueryReleaseIT.getAllRelaseStatusByRelaseIT(r.getIdPolarion(), "quickfix").size());
				List<ReleaseHistory> listRH = QueryReleaseIT.getAllRelaseStatusByRelaseIT(r.getIdPolarion(), null);
				objArray.add(columnIndex++,
						CalculateTime.getTimeInDayHourMin(getTimeInAStatusOfRelease(listRH, "quickfix")));

				totDocumenti = QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "documento");
				totDefect = QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "defect");
				totAnomalie = QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "anomalia");
				totProgettoSviluppo += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"progetto_sviluppo");
				totSupporto += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"support");
				totMev += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "mev");

				objArray.add(columnIndex++, (Object) totDocumenti);
				objArray.add(columnIndex++, (Object) totDefect);
				objArray.add(columnIndex++, (Object) totAnomalie);
				objArray.add(columnIndex++, (Object) totProgettoSviluppo);
				objArray.add(columnIndex++, (Object) totSupporto);
				objArray.add(columnIndex++, (Object) totMev);

				obj.put("infoGenerali", objArray);
			} else {
				JSONArray objArray = new JSONArray();
				int columnIndex = 0;

				totDocumenti += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "documento");
				totDefect += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "defect");
				totAnomalie += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "anomalia");
				totProgettoSviluppo += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"progetto_sviluppo");
				totSupporto += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"support");
				totMev += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "mev");

				objArray.add(columnIndex++, (Object) totDocumenti);
				objArray.add(columnIndex++, (Object) totDefect);
				objArray.add(columnIndex++, (Object) totAnomalie);
				objArray.add(columnIndex++, (Object) totProgettoSviluppo);
				objArray.add(columnIndex++, (Object) totSupporto);
				objArray.add(columnIndex++, (Object) totMev);
				obj.put("infoGenerali", objArray);
			}
		}

		return obj;
	}

	/**
	 * It calculates how much time a release IT stays in a specific status.
	 * 
	 * @param List<ReleaseHistory>
	 *            list, String status
	 * @return JSONObject
	 */
	private static Long getTimeInAStatusOfRelease(List<ReleaseHistory> list, String status) {
		if (list == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("JsonReleaseITGeneralInfo.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE,
					"JsonReleaseITGeneralInfo.java throws exception", npe);
			return null;
		}

		Date startDate = null, endDate = null;

		for (int i = 0; i < list.size(); i++) {
			ReleaseHistory current = list.get(i);
			if (current.getStatus() != null && current.getStatus().getPolarionName().equals(status)) {
				if (i == 0)
					startDate = current.getDataUpdate();
				else
					startDate = list.get(i - 1).getDataUpdate();
				if (endDate == null || current.getDataUpdate().after(endDate))
					endDate = current.getDataUpdate();
			}
		}
		if (startDate == null)
			return 0L;

		return endDate.getTime() - startDate.getTime();
	}

	/**
	 * It retrieves all status of a release IT and also counts the rejects one.
	 * Param must be the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getReleaseITStatus(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<ReleaseIt> result = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);
		if (result == null || result.size() > 1)
			return null;

		JSONArray objArray = new JSONArray();
		int columnIndex = 0, numRifiutati = 0;

		List<ReleaseitHistory> releaseItHistoryList = QueryReleaseIT.getAllReleaseItStatus(param, null);
		String lastDate = null;
		boolean isFirstHistory = true;
		for (int i = 0; i < releaseItHistoryList.size(); i++) {
			ReleaseitHistory rh = releaseItHistoryList.get(i);

			if (rh.getStatus().getPolarionName().equals("rifiutato"))
				numRifiutati++;
			
			if (i + 1 < releaseItHistoryList.size() && releaseItHistoryList.get(i + 1).getStatus().equals(rh.getStatus()))
				continue;
			objArray.add(columnIndex++, rh.getStatus().getPolarionName());
			
			if (isFirstHistory) {
				lastDate = sdf.format(rh.getDataUpdate());
				objArray.add(columnIndex++, sdf.format(rh.getReleaseIt().getDataCreazione()));
				objArray.add(columnIndex++, lastDate);
				objArray.add(columnIndex++, CalculateTime.getTimeInDayHourMin(
						rh.getDataUpdate().getTime() - rh.getReleaseIt().getDataCreazione().getTime()));
				objArray.add(columnIndex++,
						CalculateTime.getTotalTimeWorking(rh.getReleaseIt().getDataCreazione(), rh.getDataUpdate()));
				isFirstHistory = false;
			} else {
				objArray.add(columnIndex++, lastDate);
				lastDate = sdf.format(rh.getDataUpdate());
				objArray.add(columnIndex++, lastDate);
				objArray.add(columnIndex++, CalculateTime.getTimeInDayHourMin(
						rh.getDataUpdate().getTime() - releaseItHistoryList.get(i - 1).getDataUpdate().getTime()));
				objArray.add(columnIndex++, CalculateTime
						.getTotalTimeWorking(releaseItHistoryList.get(i - 1).getDataUpdate(), rh.getDataUpdate()));
			}
		}
		obj.put("stati", objArray);
		JSONArray objArrayRifiutati = new JSONArray();
		objArrayRifiutati.add(0, numRifiutati);
		obj.put("rifiutati", objArrayRifiutati);
		return obj;

	}

	/**
	 * It retrieves working time of every user's typology for each TaskIt
	 * associated with a release IT. Param must be the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getTaskIt(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<TaskItHistory> taskItHistoryList = QueryReleaseIT.getTastItHistoryByReleaseIT(param);
		
		List<String> listIdTask = new ArrayList<String>();

		JSONArray objArrayDeploy = new JSONArray(), objArrayDBA = new JSONArray(), objArrayTest = new JSONArray(),
				objArrayContrDoc = new JSONArray(), objArrayDataPrep = new JSONArray(), objTaskId = new JSONArray();
		int columnIndexDeploy = 0, columnIndexDBA = 0, columnIndexTest = 0, columnIndexContrDoc = 0,
				columnIndexDataPrep = 0, columnIndexTaskID = 0;
		Date lastDateDeploy = null, lastDateDBA = null, lastDateTest = null, lastDateContrDoc = null,
				lastDateDataPrep = null;
		for (int i = 0; i < taskItHistoryList.size(); i++) {
			TaskItHistory tItH = taskItHistoryList.get(i);
			if (tItH.getTaskit().getDataCreazione() == null) {
				tItH.getTaskit().setDataCreazione(tItH.getDataUpdate());
				HibernateUtil.update(Taskit.class, tItH.getTaskit());
			}
			if (tItH.getUser() == null || tItH.getStatus() == null || tItH.getTaskit() == null
					|| tItH.getDataUpdate() == null || tItH.getUser() == null)
				continue;
			switch (getUserType(tItH.getUser().getIdPolarion())) {
			case "deploy":
				objArrayDeploy.add(columnIndexDeploy++, tItH.getStatus().getPolarionName());
				
				if(!listIdTask.contains(tItH.getTaskit().getIdPolarion())){
					String titolo = tItH.getTaskit().getTitolo();
					titolo = titolo.substring(titolo.indexOf("(") + 1, titolo.indexOf(")"));
					objTaskId.add(columnIndexTaskID++, "ID Task "+titolo+" : "+tItH.getTaskit().getIdPolarion());
					listIdTask.add(tItH.getTaskit().getIdPolarion());
				}
				
				if (lastDateDeploy == null) {
					objArrayDeploy.add(columnIndexDeploy++, sdf.format(tItH.getTaskit().getDataCreazione()));
					lastDateDeploy = tItH.getDataUpdate();
					objArrayDeploy.add(columnIndexDeploy++, sdf.format(lastDateDeploy));
					objArrayDeploy.add(columnIndexDeploy++, CalculateTime
							.getTotalTimeWorking(tItH.getTaskit().getDataCreazione(), tItH.getDataUpdate()));
					objArrayDeploy.add(columnIndexDeploy++, tItH.getUser().getIdPolarion());
					break;
				}
				objArrayDeploy.add(columnIndexDeploy++, sdf.format(lastDateDeploy));
				objArrayDeploy.add(columnIndexDeploy++, sdf.format(tItH.getDataUpdate()));
				objArrayDeploy.add(columnIndexDeploy++,
						CalculateTime.getTotalTimeWorking(lastDateDeploy, tItH.getDataUpdate()));
				objArrayDeploy.add(columnIndexDeploy++, tItH.getUser().getIdPolarion());
				lastDateDeploy = tItH.getDataUpdate();
				break;
			case "dba":
				objArrayDBA.add(columnIndexDBA++, tItH.getStatus().getPolarionName());
				
				if(!listIdTask.contains(tItH.getTaskit().getIdPolarion())){
					String titolo = tItH.getTaskit().getTitolo();
					titolo = titolo.substring(titolo.indexOf("(") + 1, titolo.indexOf(")"));
					objTaskId.add(columnIndexTaskID++, "ID Task "+titolo+" : "+tItH.getTaskit().getIdPolarion());
					listIdTask.add(tItH.getTaskit().getIdPolarion());
				}
				
				if (lastDateDBA == null) {
					objArrayDBA.add(columnIndexDBA++, sdf.format(tItH.getTaskit().getDataCreazione()));
					lastDateDBA = tItH.getDataUpdate();
					objArrayDBA.add(columnIndexDBA++, sdf.format(lastDateDBA));
					objArrayDBA.add(columnIndexDBA++, CalculateTime
							.getTotalTimeWorking(tItH.getTaskit().getDataCreazione(), tItH.getDataUpdate()));
					objArrayDBA.add(columnIndexDBA++, tItH.getUser().getIdPolarion());
					break;
				}
				objArrayDBA.add(columnIndexDBA++, sdf.format(lastDateDBA));

				objArrayDBA.add(columnIndexDBA++, sdf.format(tItH.getDataUpdate()));
				objArrayDBA.add(columnIndexDBA++, CalculateTime.getTotalTimeWorking(lastDateDBA, tItH.getDataUpdate()));
				objArrayDBA.add(columnIndexDBA++, tItH.getUser().getIdPolarion());
				lastDateDBA = tItH.getDataUpdate();
				break;
			case "test":
				objArrayTest.add(columnIndexTest++, tItH.getStatus().getPolarionName());
				
				if(!listIdTask.contains(tItH.getTaskit().getIdPolarion())){
					String titolo = tItH.getTaskit().getTitolo();
					titolo = titolo.substring(titolo.indexOf("(") + 1, titolo.indexOf(")"));
					objTaskId.add(columnIndexTaskID++, "ID Task "+titolo+" : "+tItH.getTaskit().getIdPolarion());
					listIdTask.add(tItH.getTaskit().getIdPolarion());
				}
				
				if (lastDateTest == null) {
					objArrayTest.add(columnIndexTest++, sdf.format(tItH.getTaskit().getDataCreazione()));
					lastDateTest = tItH.getDataUpdate();
					objArrayTest.add(columnIndexTest++, sdf.format(lastDateTest));
					objArrayTest.add(columnIndexTest++, CalculateTime
							.getTotalTimeWorking(tItH.getTaskit().getDataCreazione(), tItH.getDataUpdate()));
					objArrayTest.add(columnIndexTest++, tItH.getUser().getIdPolarion());
					break;
				}
				objArrayTest.add(columnIndexTest++, sdf.format(lastDateTest));
				objArrayTest.add(columnIndexTest++, sdf.format(tItH.getDataUpdate()));
				objArrayTest.add(columnIndexTest++,
						CalculateTime.getTotalTimeWorking(lastDateTest, tItH.getDataUpdate()));
				objArrayTest.add(columnIndexTest++, tItH.getUser().getIdPolarion());
				lastDateTest = tItH.getDataUpdate();
				break;
			case "cm":
				objArrayContrDoc.add(columnIndexContrDoc++, tItH.getStatus().getPolarionName());
				
				if(!listIdTask.contains(tItH.getTaskit().getIdPolarion())){
					String titolo = tItH.getTaskit().getTitolo();
					titolo = titolo.substring(titolo.indexOf("(") + 1, titolo.indexOf(")"));
					objTaskId.add(columnIndexTaskID++, "ID Task "+titolo+" : "+tItH.getTaskit().getIdPolarion());
					listIdTask.add(tItH.getTaskit().getIdPolarion());
				}
				
				if (lastDateContrDoc == null) {
					objArrayContrDoc.add(columnIndexContrDoc++, sdf.format(tItH.getTaskit().getDataCreazione()));
					lastDateContrDoc = tItH.getDataUpdate();
					objArrayContrDoc.add(columnIndexContrDoc++, sdf.format(lastDateContrDoc));
					objArrayContrDoc.add(columnIndexContrDoc++, CalculateTime
							.getTotalTimeWorking(tItH.getTaskit().getDataCreazione(), tItH.getDataUpdate()));
					objArrayContrDoc.add(columnIndexContrDoc++, tItH.getUser().getIdPolarion());
					break;
				}
				objArrayContrDoc.add(columnIndexContrDoc++, sdf.format(lastDateContrDoc));

				objArrayContrDoc.add(columnIndexContrDoc++, sdf.format(tItH.getDataUpdate()));
				objArrayContrDoc.add(columnIndexContrDoc++,
						CalculateTime.getTotalTimeWorking(lastDateContrDoc, tItH.getDataUpdate()));
				objArrayContrDoc.add(columnIndexContrDoc++, tItH.getUser().getIdPolarion());
				lastDateContrDoc = tItH.getDataUpdate();
				break;
			case "dataprep":
				objArrayDataPrep.add(columnIndexDataPrep++, tItH.getStatus().getPolarionName());
				
				if(!listIdTask.contains(tItH.getTaskit().getIdPolarion())){
					String titolo = tItH.getTaskit().getTitolo();
					titolo = titolo.substring(titolo.indexOf("(") + 1, titolo.indexOf(")"));
					objTaskId.add(columnIndexTaskID++, "ID Task "+titolo+" : "+tItH.getTaskit().getIdPolarion());
					listIdTask.add(tItH.getTaskit().getIdPolarion());
				}
				
				if (lastDateDataPrep == null) {
					objArrayDataPrep.add(columnIndexDataPrep++, sdf.format(tItH.getTaskit().getDataCreazione()));
					lastDateDataPrep = tItH.getDataUpdate();
					objArrayDataPrep.add(columnIndexDataPrep++, sdf.format(lastDateDataPrep));
					objArrayDataPrep.add(columnIndexDataPrep++, CalculateTime
							.getTotalTimeWorking(tItH.getTaskit().getDataCreazione(), tItH.getDataUpdate()));
					objArrayDataPrep.add(columnIndexDataPrep++, tItH.getUser().getIdPolarion());
					break;
				}
				objArrayDataPrep.add(columnIndexDataPrep++, sdf.format(lastDateDataPrep));
				objArrayDataPrep.add(columnIndexDataPrep++, sdf.format(tItH.getDataUpdate()));
				objArrayDataPrep.add(columnIndexDataPrep++,
						CalculateTime.getTotalTimeWorking(lastDateDataPrep, tItH.getDataUpdate()));
				objArrayDataPrep.add(columnIndexDataPrep++, tItH.getUser().getIdPolarion());
				lastDateDataPrep = tItH.getDataUpdate();
				break;
			default:
				if (Util.DEBUG) {
					UnknownObjectException uee = new UnknownObjectException(
							"Unknow user type for " + tItH.getUser().getIdPolarion());
					Util.writeLog("JsonReleaseITGeneralInfo.java throws exception " + "[Tipo utente non valido per "
							+ tItH.getUser().getIdPolarion() + "]", uee);
					Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.WARNING,
							"JsonReleaseITGeneralInfo.java throws exception", uee);
				}
				break;
			}// switch
		} // for
		obj.put("infoTaskDeploy", objArrayDeploy);
		obj.put("infoTaskDBA", objArrayDBA);
		obj.put("infoTaskTest", objArrayTest);
		obj.put("infoTaskContrDoc", objArrayContrDoc);
		obj.put("infoTaskDataPrep", objArrayDataPrep);
		obj.put("infoTaskId", objTaskId);
		
		return obj;
	}

	/**
	 * It retrieves user's typology. Param must be the user's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	private static String getUserType(String idPolarionUser) {
		if (usersTypes.getString("deploy").contains(idPolarionUser))
			return "deploy";
		if (usersTypes.getString("dba").contains(idPolarionUser))
			return "dba";
		if (usersTypes.getString("tester").contains(idPolarionUser))
			return "test";
		if (usersTypes.getString("cm").contains(idPolarionUser))
			return "cm";
		if (usersTypes.getString("dataprep").contains(idPolarionUser))
			return "dataprep";

		return "";
	}

	/**
	 * It counts all Testcase's types of a release IT and also retrieve defect's
	 * list for each Testcase. Param must be the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getTestCaseByReleaseIT(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<ReleaseIt> releaseItList = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);

		if (releaseItList == null || releaseItList.size() != 1)
			return null;

		ReleaseIt r = releaseItList.get(0);

		if (r.getDataInizio() == null || r.getDataFine() == null)
			return null;

		JSONArray objArray = new JSONArray();

		objArray.add(0, QueryReleaseIT.getBaseTestcaseCountByReleaseIT(r));
		objArray.add(1, QueryReleaseIT.getNewTestcaseCountByReleaseIT(r));
		objArray.add(2, QueryReleaseIT.getUpdatedTestcaseCountByReleaseIT(r));
		objArray.add(3, QueryReleaseIT.getDeprecatedTestcaseCountByReleaseIT(r));
		objArray.add(4, QueryReleaseIT.getAutomaticTestcaseCountByReleaseIT(r));
		objArray.add(5, QueryReleaseIT.getManualTestcaseCountByReleaseIT(r));

		obj.put("testcase", objArray);

		List<Testcase> list = QueryReleaseIT.getTestcaseByReleaseIT(r.getIdPolarion());
		for (Testcase tc : list) {
			List<Defect> defectList = QueryReleaseIT.getDefectByTestcase(tc.getIdPolarion());
			if (defectList.isEmpty())
				continue;
			JSONArray objArrayTestcaseDefect = new JSONArray();
			int index = 0;
			for (Defect d : defectList)
				objArrayTestcaseDefect.add(index++, d.getIdPolarion());
			obj.put(tc.getIdPolarion(), objArrayTestcaseDefect);
		}
		return obj;
	}

	/**
	 * It retrieves all hours of executed TaskIt of a release IT. Param must be
	 * the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getTimingByReleaseIT(String param) {
		if (param == null)
			return null;
		JSONObject obj = new JSONObject();

		List<ReleaseIt> releaseItList = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);

		if (releaseItList == null || releaseItList.size() != 1)
			return null;

		ReleaseIt r = releaseItList.get(0);
		if (r.getDataInizio() == null || r.getDataFine() == null)
			return null;

		final long hour = 3600 * 1000;

		JSONArray objArrayReleaseIt = new JSONArray();
		objArrayReleaseIt.add(0, sdf.format(r.getDataInizio()));
		objArrayReleaseIt.add(1, sdf.format(r.getDataFine()));
		objArrayReleaseIt.add(2, "" + Math.floorDiv(r.getDataFine().getTime() - r.getDataInizio().getTime(), hour));
		String timeRIT = Time.numberOfWorkingHoursInPeriod(r.getDataInizio(), r.getDataFine());
		objArrayReleaseIt.add(3, timeRIT.substring(0, timeRIT.indexOf(":")));
		obj.put("releaseit", objArrayReleaseIt);

		JSONArray objArray = new JSONArray();
		int columnIndex = 0;
		List<Long> deployDBAlist = calculateTimeByTaskitHistory(QueryReleaseIT.getTimingDeployDBATaskit(r));
		objArray.add(columnIndex++, "" + deployDBAlist.get(0));
		objArray.add(columnIndex++, "" + deployDBAlist.get(1));

		List<ReleaseHistory> list = QueryReleaseIT.getReleseHistoryByStatusQuickfixAndInIntegrazione(r);

		/*
		 * NB: quando viene effettuata una qualsiasi modifica come ad esempio
		 * l'inserimento di un commento viene riportata la modifica nello
		 * storico, di conseguenza si avranno più status consecutivi dello
		 * stesso tipo.
		 * 
		 */
		long qfTime = 0, qfWorkingTime = 0;
		Date startDate = null;
		for (int i = 0; i < list.size(); i++) {
			ReleaseHistory rh = list.get(i);
			if (startDate == null || rh.getStatus().getPolarionName().equals("in_integrazione")) {
				if (rh.getStatus().getPolarionName().equals("quickfix")) {
					IllegalStateException ise = new IllegalStateException();
					if (Util.DEBUG)
						Util.writeLog("JsonReleaseITGeneralInfo.java throws exception", ise);
					Logger.getLogger(JsonReleaseITGeneralInfo.class.getName()).log(Level.SEVERE,
							"JsonReleaseITGeneralInfo.java throws exception", ise);
					return null;
				}
				startDate = rh.getDataUpdate();
				continue;
			}
			qfTime += rh.getDataUpdate().getTime() - startDate.getTime();
			String time = Time.numberOfWorkingHoursInPeriod(startDate, rh.getDataUpdate());
			qfWorkingTime += Long.parseLong(time.substring(0, time.indexOf(":")));
			/*
			 * Faccio questo aggiornamento della startDate a causa della
			 * situazione espressa nel NB riportato sopra
			 */
			startDate = rh.getDataUpdate();
		}
		objArray.add(columnIndex++, "" + Math.floorDiv(qfTime, hour));
		objArray.add(columnIndex++, "" + Math.floorDiv(qfWorkingTime, hour));

		List<Long> testList = calculateTimeByTaskitHistory(QueryReleaseIT.getTimingTestTaskit(r));
		objArray.add(columnIndex++, "" + testList.get(0));
		objArray.add(columnIndex++, "" + testList.get(1));

		obj.put("deploydba_qf_test", objArray);

		return obj;
	}

	/**
	 * It calculates two values: 1st is the real time between two different
	 * TaskIt, 2nd is the working time between two different TaskIt.
	 * 
	 * Complexity: (Worst case) O(2n - 2)
	 * 
	 * @param List<TaskItHistory>
	 *            list
	 * @return List<Long> list, there are always two elements in this list.
	 */
	private static List<Long> calculateTimeByTaskitHistory(List<TaskItHistory> list) {
		if (list == null)
			return null;
		List<Long> returnList = new ArrayList<Long>();
		final long hour = 3600 * 1000;
		long time = 0, workingTime = 0;
		Date currentEndDate = null;
		Taskit currentTaskIt = null;
		for (int i = 0; i < list.size(); i++) {
			TaskItHistory th = list.get(i);
			if (currentEndDate == null) {
				currentTaskIt = th.getTaskit();
				currentEndDate = th.getDataUpdate();
				if (i == list.size() - 1) {
					time += currentEndDate.getTime() - currentTaskIt.getDataCreazione().getTime();
					String timeS = Time.numberOfWorkingHoursInPeriod(currentTaskIt.getDataCreazione(), currentEndDate);
					workingTime += Long.parseLong(timeS.substring(0, timeS.indexOf(":")));
				}
				continue;
			}

			if (!th.getTaskit().equals(currentTaskIt)) {
				time += currentEndDate.getTime() - currentTaskIt.getDataCreazione().getTime();
				String timeS = Time.numberOfWorkingHoursInPeriod(currentTaskIt.getDataCreazione(), currentEndDate);
				workingTime += Long.parseLong(timeS.substring(0, timeS.indexOf(":")));
				currentEndDate = null;
				currentTaskIt = null;
				i--;
				continue;
			}

			if (currentEndDate.before(th.getDataUpdate())) {
				currentEndDate = th.getDataUpdate();
				continue;
			}

			if (i == list.size() - 1) {
				time += currentEndDate.getTime() - currentTaskIt.getDataCreazione().getTime();
				String timeS = Time.numberOfWorkingHoursInPeriod(currentTaskIt.getDataCreazione(), currentEndDate);
				workingTime += Long.parseLong(timeS.substring(0, timeS.indexOf(":")));

				currentEndDate = null;
				currentTaskIt = null;
			}
		}

		returnList.add(Math.floorDiv(time, hour));
		returnList.add(workingTime);
		return returnList;
	}

	/**
	 * It retrieves all users that executed checklist's testcases of a release
	 * IT. Param must be the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getAuthorsByReleaseIT(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<ReleaseIt> releaseItList = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);

		if (releaseItList == null || releaseItList.size() != 1)
			return null;

		ReleaseIt r = releaseItList.get(0);

		List<Workrecords> workrecordsList = QueryReleaseIT.getAuthorsTaskitWorkrecordsByReleaseIT(r);
		List<User> userList = new LinkedList<User>();

		JSONArray objArray = new JSONArray();
		int columnIndex = 0;
		for (Workrecords w : workrecordsList) {
			if (!userList.contains(w.getUser()))
				userList.add(w.getUser());
			objArray.add(columnIndex++, w.getUser().getIdPolarion());
			objArray.add(columnIndex++, w.getWorkTime());
			objArray.add(columnIndex++, w.getWorkType());
			objArray.add(columnIndex++, w.getNote());
		}
		obj.put("author", objArray);

		JSONArray objArrayAuthorNumber = new JSONArray();

		objArrayAuthorNumber.add(0, userList.size());
		obj.put("author_size", objArrayAuthorNumber);

		return obj;
	}

	/**
	 * It counts all executed checklist's testcases of a release IT. Param must
	 * be the releaseIT's id_polarion.
	 * 
	 * @param String
	 *            param
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getCheckListByReleaseIT(String param) {
		if (param == null)
			return null;

		JSONObject obj = new JSONObject();
		List<ReleaseIt> releaseItList = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);

		if (releaseItList == null || releaseItList.size() != 1)
			return null;

		ReleaseIt r = releaseItList.get(0);

		List<Checklist> checklistList = QueryReleaseIT.getChecklistByReleaseIT(r);

		JSONArray objArrayClSize = new JSONArray();
		objArrayClSize.add(0, checklistList.size());

		obj.put("cl_size", objArrayClSize);

		JSONArray objArrayCl = new JSONArray();
		int columnIndex = 0;
		for (Checklist cl : checklistList) {
			int totalTC = QueryReleaseIT.getTestcaseCountByChecklist(cl);
			int passed = QueryReleaseIT.getChecklistTestcaseCountByChecklist(cl, "passed");
			int notApplicable = QueryReleaseIT.getChecklistTestcaseCountByChecklist(cl, "not_applicable");
			int failed = QueryReleaseIT.getChecklistTestcaseCountByChecklist(cl, "failed");
			int invalid = QueryReleaseIT.getChecklistTestcaseCountByChecklist(cl, "Invalid");

			objArrayCl.add(columnIndex++, cl.getIdPolarion());
			objArrayCl.add(columnIndex++, totalTC);
			objArrayCl.add(columnIndex++, (passed + notApplicable + failed + invalid));
			objArrayCl.add(columnIndex++, passed);
			objArrayCl.add(columnIndex++, failed);
			objArrayCl.add(columnIndex++, notApplicable);
			objArrayCl.add(columnIndex++, invalid);
			objArrayCl.add(columnIndex++, totalTC - (passed + notApplicable + failed + invalid));
		}
		obj.put("cl_tc", objArrayCl);

		JSONArray objArrayUsers = new JSONArray();
		objArrayUsers.addAll(QueryReleaseIT.getDistinctUserInChecklistTestcaseByReleaseIT(r));
		obj.put("cl_users", objArrayUsers);

		return obj;
	}
}
