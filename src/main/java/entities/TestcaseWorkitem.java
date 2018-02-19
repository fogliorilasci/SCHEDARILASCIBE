package entities;
// Generated Nov 28, 2017 11:11:12 AM by Hibernate Tools 5.1.0.Alpha1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TestcaseWorkitem generated by hbm2java
 */
@Entity
@Table(name = "testcase_workitem", catalog = "rilasci_db")
public class TestcaseWorkitem implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Testcase testcase;
	private User user;
	private Integer codType;
	private Date dataCreazione;
	private Date timespent;

	public TestcaseWorkitem() {
	}

	public TestcaseWorkitem(Testcase testcase, Date dataCreazione, Date timespent) {
		this.testcase = testcase;
		this.dataCreazione = dataCreazione;
		this.timespent = timespent;
	}

	public TestcaseWorkitem(Testcase testcase, User user, Integer codType, Date dataCreazione, Date timespent) {
		this.testcase = testcase;
		this.user = user;
		this.codType = codType;
		this.dataCreazione = dataCreazione;
		this.timespent = timespent;
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
	@JoinColumn(name = "cod_testcase")
	public Testcase getTestcase() {
		return this.testcase;
	}

	public void setTestcase(Testcase testcase) {
		this.testcase = testcase;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_user")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "cod_type")
	public Integer getCodType() {
		return this.codType;
	}

	public void setCodType(Integer codType) {
		this.codType = codType;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_creazione", length = 10)
	public Date getDataCreazione() {
		return this.dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "timespent", length = 8)
	public Date getTimespent() {
		return this.timespent;
	}

	public void setTimespent(Date timespent) {
		this.timespent = timespent;
	}

}
