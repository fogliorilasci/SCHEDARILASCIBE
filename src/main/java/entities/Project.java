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
 * Project generated by hbm2java
 */
@Entity
@Table(name = "project", catalog = "rilasci_db")
public class Project implements java.io.Serializable {

	private Integer id;
	private String nome;
	private Set<Release> releases = new HashSet<Release>(0);

	public Project() {
	}

	public Project(String nome, Set<Release> releases) {
		this.nome = nome;
		this.releases = releases;
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

	@Column(name = "nome", length = 65535)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	public Set<Release> getReleases() {
		return this.releases;
	}

	public void setReleases(Set<Release> releases) {
		this.releases = releases;
	}

}
