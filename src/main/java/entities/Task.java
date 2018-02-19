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
import javax.persistence.UniqueConstraint;

/**
 * Task generated by hbm2java
 */
@Entity
@Table(name = "task", catalog = "rilasci_db", uniqueConstraints = @UniqueConstraint(columnNames = "id_polarion"))
public class Task implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Priority priority;
	private Resolution resolution;
	private Status status;
	private User user;
	private String idPolarion;
	private String titolo;
	private Date dataCreazione;
	private Date dataUpdate;
	private Date dataInizio;
	private Date dataFine;
	private Integer codTipo;
	private String timespent;
	private Set<TaskHistory> taskHistories = new HashSet<TaskHistory>(0);

	public Task() {
	}

	public Task(User user) {
		this.user = user;
	}

	public Task(Priority priority, Resolution resolution, Status status, User user, String idPolarion, String titolo,
			Date dataCreazione, Date dataUpdate, Date dataInizio, Date dataFine, Integer codTipo, String timespent,
			Set<TaskHistory> taskHistories) {
		this.priority = priority;
		this.resolution = resolution;
		this.status = status;
		this.user = user;
		this.idPolarion = idPolarion;
		this.titolo = titolo;
		this.dataCreazione = dataCreazione;
		this.dataUpdate = dataUpdate;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.codTipo = codTipo;
		this.timespent = timespent;
		this.taskHistories = taskHistories;
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
	@JoinColumn(name = "cod_resolution")
	public Resolution getResolution() {
		return this.resolution;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_status")
	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_author")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "id_polarion", unique = true, length = 255)
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inizio", length = 10)
	public Date getDataInizio() {
		return this.dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fine", length = 10)
	public Date getDataFine() {
		return this.dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	@Column(name = "cod_tipo")
	public Integer getCodTipo() {
		return this.codTipo;
	}

	public void setCodTipo(Integer codTipo) {
		this.codTipo = codTipo;
	}

	@Column(name = "timespent", length = 10)
	public String getTimespent() {
		return this.timespent;
	}

	public void setTimespent(String timespent) {
		this.timespent = timespent;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
	public Set<TaskHistory> getTaskHistories() {
		return this.taskHistories;
	}

	public void setTaskHistories(Set<TaskHistory> taskHistories) {
		this.taskHistories = taskHistories;
	}

}
