package entities;
// Generated Nov 28, 2017 11:11:12 AM by Hibernate Tools 5.1.0.Alpha1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Severity generated by hbm2java
 */
@Entity
@Table(name = "severity", catalog = "rilasci_db")
public class Severity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nome;
	private String polarionName;
	private Set<Release> releases = new HashSet<Release>(0);
	private Set<ReleaseIt> releaseIts = new HashSet<ReleaseIt>(0);
	private Set<Defect> defects = new HashSet<Defect>(0);
	private Set<ProgettoSviluppo> progettoSviluppos = new HashSet<ProgettoSviluppo>(0);
	private Set<AnomaliaAssistenza> anomaliaAssistenzas = new HashSet<AnomaliaAssistenza>(0);
	private Set<Mev> mevs = new HashSet<Mev>(0);
	private Set<Anomalia> anomalias = new HashSet<Anomalia>(0);
	private Set<Documento> documentos = new HashSet<Documento>(0);
	private Set<Support> supports = new HashSet<Support>(0);

	public Severity() {
	}

	public Severity(String nome, String polarionName, Set<Release> releases, Set<ReleaseIt> releaseIts,
			Set<Defect> defects, Set<ProgettoSviluppo> progettoSviluppos, Set<AnomaliaAssistenza> anomaliaAssistenzas,
			Set<Mev> mevs, Set<Anomalia> anomalias, Set<Documento> documentos, Set<Support> supports) {
		this.nome = nome;
		this.polarionName = polarionName;
		this.releases = releases;
		this.releaseIts = releaseIts;
		this.defects = defects;
		this.progettoSviluppos = progettoSviluppos;
		this.anomaliaAssistenzas = anomaliaAssistenzas;
		this.mevs = mevs;
		this.anomalias = anomalias;
		this.documentos = documentos;
		this.supports = supports;
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

	@Column(name = "nome", length = 45)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "polarion_name", length = 45)
	public String getPolarionName() {
		return this.polarionName;
	}

	public void setPolarionName(String polarionName) {
		this.polarionName = polarionName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Release> getReleases() {
		return this.releases;
	}

	public void setReleases(Set<Release> releases) {
		this.releases = releases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<ReleaseIt> getReleaseIts() {
		return this.releaseIts;
	}

	public void setReleaseIts(Set<ReleaseIt> releaseIts) {
		this.releaseIts = releaseIts;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Defect> getDefects() {
		return this.defects;
	}

	public void setDefects(Set<Defect> defects) {
		this.defects = defects;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<ProgettoSviluppo> getProgettoSviluppos() {
		return this.progettoSviluppos;
	}

	public void setProgettoSviluppos(Set<ProgettoSviluppo> progettoSviluppos) {
		this.progettoSviluppos = progettoSviluppos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<AnomaliaAssistenza> getAnomaliaAssistenzas() {
		return this.anomaliaAssistenzas;
	}

	public void setAnomaliaAssistenzas(Set<AnomaliaAssistenza> anomaliaAssistenzas) {
		this.anomaliaAssistenzas = anomaliaAssistenzas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Mev> getMevs() {
		return this.mevs;
	}

	public void setMevs(Set<Mev> mevs) {
		this.mevs = mevs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Anomalia> getAnomalias() {
		return this.anomalias;
	}

	public void setAnomalias(Set<Anomalia> anomalias) {
		this.anomalias = anomalias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Documento> getDocumentos() {
		return this.documentos;
	}

	public void setDocumentos(Set<Documento> documentos) {
		this.documentos = documentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "severity")
	public Set<Support> getSupports() {
		return this.supports;
	}

	public void setSupports(Set<Support> supports) {
		this.supports = supports;
	}

}
