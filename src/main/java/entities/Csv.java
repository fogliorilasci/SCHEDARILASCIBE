package entities;
// Generated Nov 28, 2017 11:11:12 AM by Hibernate Tools 5.1.0.Alpha1

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Csv generated by hbm2java
 */
@Entity
@Table(name = "csv", catalog = "rilasci_db")
public class Csv implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String fileName;
	private Date fileDate;
	private Date inizioEstrazione;
	private Date fineEstrazione;
	private String idPolarion;
	private String progettoPolarion;
	private String repository;
	private String colonnaA;
	private String colonnaB;
	private String colonnaC;
	private String colonnaD;
	private String colonnaE;
	private String colonnaF;
	private String colonnaG;
	private String colonnaH;
	private String colonnaI;
	private String colonnaJ;
	private String colonnaK;
	private String colonnaL;
	private String colonnaM;
	private String colonnaN;

	public Csv() {
	}

	public Csv(String fileName, Date fileDate, Date inizioEstrazione, Date fineEstrazione, String idPolarion,
			String progettoPolarion, String repository, String colonnaA, String colonnaB, String colonnaC,
			String colonnaD, String colonnaE, String colonnaF, String colonnaG, String colonnaH, String colonnaI,
			String colonnaJ, String colonnaK, String colonnaL, String colonnaM, String colonnaN) {
		this.fileName = fileName;
		this.fileDate = fileDate;
		this.inizioEstrazione = inizioEstrazione;
		this.fineEstrazione = fineEstrazione;
		this.idPolarion = idPolarion;
		this.progettoPolarion = progettoPolarion;
		this.repository = repository;
		this.colonnaA = colonnaA;
		this.colonnaB = colonnaB;
		this.colonnaC = colonnaC;
		this.colonnaD = colonnaD;
		this.colonnaE = colonnaE;
		this.colonnaF = colonnaF;
		this.colonnaG = colonnaG;
		this.colonnaH = colonnaH;
		this.colonnaI = colonnaI;
		this.colonnaJ = colonnaJ;
		this.colonnaK = colonnaK;
		this.colonnaL = colonnaL;
		this.colonnaM = colonnaM;
		this.colonnaN = colonnaN;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "fileName", length = 400)
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fileDate", length = 19)
	public Date getFileDate() {
		return this.fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inizioEstrazione", length = 19)
	public Date getInizioEstrazione() {
		return this.inizioEstrazione;
	}

	public void setInizioEstrazione(Date inizioEstrazione) {
		this.inizioEstrazione = inizioEstrazione;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fineEstrazione", length = 19)
	public Date getFineEstrazione() {
		return this.fineEstrazione;
	}

	public void setFineEstrazione(Date fineEstrazione) {
		this.fineEstrazione = fineEstrazione;
	}

	@Column(name = "idPolarion", length = 65535)
	public String getIdPolarion() {
		return this.idPolarion;
	}

	public void setIdPolarion(String idPolarion) {
		this.idPolarion = idPolarion;
	}

	@Column(name = "progettoPolarion", length = 16777215)
	public String getProgettoPolarion() {
		return this.progettoPolarion;
	}

	public void setProgettoPolarion(String progettoPolarion) {
		this.progettoPolarion = progettoPolarion;
	}

	@Column(name = "repository")
	public String getRepository() {
		return this.repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	@Column(name = "colonnaA")
	public String getColonnaA() {
		return this.colonnaA;
	}

	public void setColonnaA(String colonnaA) {
		this.colonnaA = colonnaA;
	}

	@Column(name = "colonnaB")
	public String getColonnaB() {
		return this.colonnaB;
	}

	public void setColonnaB(String colonnaB) {
		this.colonnaB = colonnaB;
	}

	@Column(name = "colonnaC")
	public String getColonnaC() {
		return this.colonnaC;
	}

	public void setColonnaC(String colonnaC) {
		this.colonnaC = colonnaC;
	}

	@Column(name = "colonnaD")
	public String getColonnaD() {
		return this.colonnaD;
	}

	public void setColonnaD(String colonnaD) {
		this.colonnaD = colonnaD;
	}

	@Column(name = "colonnaE")
	public String getColonnaE() {
		return this.colonnaE;
	}

	public void setColonnaE(String colonnaE) {
		this.colonnaE = colonnaE;
	}

	@Column(name = "colonnaF")
	public String getColonnaF() {
		return this.colonnaF;
	}

	public void setColonnaF(String colonnaF) {
		this.colonnaF = colonnaF;
	}

	@Column(name = "colonnaG")
	public String getColonnaG() {
		return this.colonnaG;
	}

	public void setColonnaG(String colonnaG) {
		this.colonnaG = colonnaG;
	}

	@Column(name = "colonnaH")
	public String getColonnaH() {
		return this.colonnaH;
	}

	public void setColonnaH(String colonnaH) {
		this.colonnaH = colonnaH;
	}

	@Column(name = "colonnaI")
	public String getColonnaI() {
		return this.colonnaI;
	}

	public void setColonnaI(String colonnaI) {
		this.colonnaI = colonnaI;
	}

	@Column(name = "colonnaJ")
	public String getColonnaJ() {
		return this.colonnaJ;
	}

	public void setColonnaJ(String colonnaJ) {
		this.colonnaJ = colonnaJ;
	}

	@Column(name = "colonnaK")
	public String getColonnaK() {
		return this.colonnaK;
	}

	public void setColonnaK(String colonnaK) {
		this.colonnaK = colonnaK;
	}

	@Column(name = "colonnaL")
	public String getColonnaL() {
		return this.colonnaL;
	}

	public void setColonnaL(String colonnaL) {
		this.colonnaL = colonnaL;
	}

	@Column(name = "colonnaM")
	public String getColonnaM() {
		return this.colonnaM;
	}

	public void setColonnaM(String colonnaM) {
		this.colonnaM = colonnaM;
	}

	@Column(name = "colonnaN")
	public String getColonnaN() {
		return this.colonnaN;
	}

	public void setColonnaN(String colonnaN) {
		this.colonnaN = colonnaN;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return "current CSV: [id: " + id + ", fileName: " + fileName + ", fileDate: "
				+ (fileDate != null ? sdf.format(fileDate) + " or " + fileDate.getTime() : null)
				+ ", inizioEstrazione: " + (inizioEstrazione != null ? sdf.format(inizioEstrazione) : null)
				+ ", fineEstrazione: " + (inizioEstrazione != null ? sdf.format(inizioEstrazione) : null)
				+ ",\n idPolarion: " + idPolarion + ", progettoPolarion: " + progettoPolarion + ", repository: "
				+ repository + ",\n ColonnaA: " + colonnaA + ",\n ColonnaB: " + colonnaB + ",\n ColonnaC: " + colonnaC
				+ ",\n ColonnaD: " + colonnaD + ",\n ColonnaE: " + colonnaE + ",\n ColonnaF: " + colonnaF
				+ ",\n ColonnaG: " + colonnaG + ", ColonnaH: " + colonnaH + ", ColonnaI: " + colonnaI + ", ColonnaJ: "
				+ colonnaJ + ",\n ColonnaK: " + colonnaK + ",\n ColonnaL: " + colonnaL + ",\n ColonnaM: " + colonnaM
				+ ",\n ColonnaN: " + colonnaN + "]";
	}
}
