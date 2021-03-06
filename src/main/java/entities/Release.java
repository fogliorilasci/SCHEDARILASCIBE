package entities;
// Generated Nov 28, 2017 11:11:12 AM by Hibernate Tools 5.1.0.Alpha1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Release generated by hbm2java
 */
@Entity
@Table(name = "release", catalog = "rilasci_db")
public class Release implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Priority priority;
	private Project project;
	private Severity severity;
	private String idPolarion;
	private String titolo;
	private Date dataCreazione;
	private Date dataUpdate;
	private String versione;
	private String link;
	private String type;
	private String repository;
	private Set<Assignee> assignees = new HashSet<Assignee>(0);
	private Set<ReleaseHistory> releaseHistories = new HashSet<ReleaseHistory>(0);

	public Release() {
	}

	public Release(Priority priority, Project project, Severity severity, String idPolarion, String titolo,
			Date dataCreazione, Date dataUpdate, String versione, String link, String type, String repository,
			Set<Assignee> assignees, Set<ReleaseHistory> releaseHistories) {
		this.priority = priority;
		this.project = project;
		this.severity = severity;
		this.idPolarion = idPolarion;
		this.titolo = titolo;
		this.dataCreazione = dataCreazione;
		this.dataUpdate = dataUpdate;
		this.versione = versione;
		this.link = link;
		this.type = type;
		this.repository = repository;
		this.assignees = assignees;
		this.releaseHistories = releaseHistories;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_priority")
	public Priority getPriority() {
		return this.priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_id_progetto")
	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_severity")
	public Severity getSeverity() {
		return this.severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Column(name = "id_polarion", length = 65535)
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_update", length = 19)
	public Date getDataUpdate() {
		return this.dataUpdate;
	}

	public void setDataUpdate(Date dataUpdate) {
		this.dataUpdate = dataUpdate;
	}

	@Column(name = "versione", length = 45)
	public String getVersione() {
		return this.versione;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}

	@Column(name = "link", length = 2000)
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(name = "type", length = 45)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "repository", length = 45)
	public String getRepository() {
		return this.repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "releaseIt")
	public Set<Assignee> getAssignees() {
		return this.assignees;
	}

	public void setAssignees(Set<Assignee> assignees) {
		this.assignees = assignees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "release")
	public Set<ReleaseHistory> getReleaseHistories() {
		return this.releaseHistories;
	}

	public void setReleaseHistories(Set<ReleaseHistory> releaseHistories) {
		this.releaseHistories = releaseHistories;
	}

}
