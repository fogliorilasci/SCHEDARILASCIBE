package entities;
// Generated Nov 28, 2017 11:11:12 AM by Hibernate Tools 5.1.0.Alpha1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Support generated by hbm2java
 */
@Entity
@Table(name = "support", catalog = "rilasci_db", uniqueConstraints = @UniqueConstraint(columnNames = "id_polarion"))
public class Support implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private Priority priority;
	private Resolution resolution;
	private Severity severity;
	private String idPolarion;
	private String titolo;
	private Date dataCreazione;
	private Set<SupportHistory> supportHistories = new HashSet<SupportHistory>(0);

	public Support() {
	}

	public Support(int id) {
		this.id = id;
	}

	public Support(int id, Priority priority, Resolution resolution, Severity severity, String idPolarion,
			String titolo, Date dataCreazione, Set<SupportHistory> supportHistories) {
		this.id = id;
		this.priority = priority;
		this.resolution = resolution;
		this.severity = severity;
		this.idPolarion = idPolarion;
		this.titolo = titolo;
		this.dataCreazione = dataCreazione;
		this.supportHistories = supportHistories;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_priority")
	public Priority getPriority() {
		return this.priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_resolution")
	public Resolution getResolution() {
		return this.resolution;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_severity")
	public Severity getSeverity() {
		return this.severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Column(name = "id_polarion", length = 255)
	public String getIdPolarion() {
		return this.idPolarion;
	}

	public void setIdPolarion(String idPolarion) {
		this.idPolarion = idPolarion;
	}

	@Column(name = "titolo", length = 65535)
	public String getTitolo() {
		return this.titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_creazione", length = 19)
	public Date getDataCreazione() {
		return this.dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "support")
	public Set<SupportHistory> getSupportHistories() {
		return this.supportHistories;
	}

	public void setSupportHistories(Set<SupportHistory> supportHistories) {
		this.supportHistories = supportHistories;
	}

}
