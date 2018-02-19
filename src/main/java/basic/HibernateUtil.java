package basic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import entities.Anomalia;
import entities.Checklist;
import entities.ChecklistTestcase;
import entities.Csv;
import entities.Defect;
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
import entities.Resolution;
import entities.Severity;
import entities.Status;
import entities.Support;
import entities.SupportHistory;
import entities.TaskItHistory;
import entities.Taskit;
import entities.Testcase;
import entities.TestcaseHistory;
import entities.TipoItem;
import entities.User;
import entities.Workrecords;

public class HibernateUtil {
	/*
	 * Hibernate 5 issue with registry
	 * 
	 * https://stackoverflow.com/questions/32405031/hibernate-5-org-hibernate-
	 * mappingexception-unknown-entity
	 */
	private static SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", (Exception) ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null)
			sessionFactory = buildSessionFactory();
		return sessionFactory;
	}

	public static List<Csv> readAllCsv() {
		List<Csv> list = null;
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Csv> criteriaQuery = criteriaBuilder.createQuery(Csv.class);
			Root<Csv> root = criteriaQuery.from(Csv.class);

			// Query
			criteriaQuery.select(root);

			Query<Csv> query = session.createQuery(criteriaQuery);
			list = query.getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return list;
	}
	
	public static List<Documento> readAllDocuments() {
		List<Documento> list = null;
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Documento> criteriaQuery = criteriaBuilder.createQuery(Documento.class);
			Root<Documento> root = criteriaQuery.from(Documento.class);

			// Query
			criteriaQuery.select(root);

			Query<Documento> query = session.createQuery(criteriaQuery);
			list = query.getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return list;
	}
	
	public static List<Testcase> readAllTestcase() {
		List<Testcase> list = null;
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Testcase> criteriaQuery = criteriaBuilder.createQuery(Testcase.class);
			Root<Testcase> root = criteriaQuery.from(Testcase.class);

			// Query
			criteriaQuery.select(root);

			Query<Testcase> query = session.createQuery(criteriaQuery);
			list = query.getResultList();

			session.getTransaction().commit();
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return list;
	}
	
	public static List<TestcaseHistory> readTestcaseHistory(Testcase tc) {

		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<TestcaseHistory> cq = builder.createQuery(TestcaseHistory.class);
		Root<TestcaseHistory> root = cq.from(TestcaseHistory.class);

		// Query
		cq.select(root).where(builder.equal(root.get("testcase"), tc));
		cq.orderBy(builder.asc(root.get("data")));
		
		Query<TestcaseHistory> q = session.createQuery(cq);

		List<TestcaseHistory> list = null;
		
		try {
			list = q.getResultList();
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		
		session.getTransaction().commit();
		
		return list;
	}
	public static Project readProjectForName(String name) {

		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Project> cq = builder.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);

		// Query
		cq.select(root).where(builder.equal(root.get("nome"), name));

		Query<Project> q = session.createQuery(cq);

		Project p = null;
		try {
			p = q.getSingleResult();
		} catch (NoResultException ex) {
			p = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			p = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return p;
	}

	public static Priority readPriority(float d) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Priority> cq = builder.createQuery(Priority.class);
		Root<Priority> root = cq.from(Priority.class);

		// Query
		cq.select(root).where(builder.equal(root.get("valore"), d));

		Query<Priority> q = session.createQuery(cq);

		Priority p = null;

		try {
			p = q.getSingleResult();
		} catch (NoResultException ex) {
			p = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			p = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return p;
	}

	public static Severity readSeverity(String name) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Severity> cq = builder.createQuery(Severity.class);
		Root<Severity> root = cq.from(Severity.class);

		// Query
		cq.select(root).where(builder.equal(root.get("polarionName"), name));

		Query<Severity> q = session.createQuery(cq);

		Severity s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static List<Status> readAllStatus() {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Status> cq = builder.createQuery(Status.class);
		Root<Status> root = cq.from(Status.class);

		// Query
		cq.select(root);
		Query<Status> q = session.createQuery(cq);
		List<Status> s = null;

		try {
			s = q.getResultList();
		} catch (NoResultException ex) {
			s = null;
		}
		session.getTransaction().commit();
		return s;
	}

	public static Status readStatus(String name) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Status> cq = builder.createQuery(Status.class);
		Root<Status> root = cq.from(Status.class);

		// Query
		cq.select(root).where(builder.equal(root.get("polarionName"), name));

		Query<Status> q = session.createQuery(cq);
		Status s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Csv readCsvByIdPolarion(String idPolarion) {
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Csv> cq = builder.createQuery(Csv.class);
			Root<Csv> root = cq.from(Csv.class);

			// Query
			cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

			Query<Csv> q = session.createQuery(cq);
			Csv csv = q.getSingleResult();

			session.getTransaction().commit();
			if (csv != null)
				return csv;
		} catch (NoResultException ex) {
			return null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return null;
	}

	public static User readUser(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<User> cq = builder.createQuery(User.class);
		Root<User> root = cq.from(User.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<User> q = session.createQuery(cq);
		User s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Release readRelease(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Release> cq = builder.createQuery(Release.class);
		Root<Release> root = cq.from(Release.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Release> q = session.createQuery(cq);
		Release s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static ReleaseIt readReleaseIT(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ReleaseIt> cq = builder.createQuery(ReleaseIt.class);
		Root<ReleaseIt> root = cq.from(ReleaseIt.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<ReleaseIt> q = session.createQuery(cq);
		ReleaseIt s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static ProgettoSviluppo readProgettoSviluppo(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ProgettoSviluppo> cq = builder.createQuery(ProgettoSviluppo.class);
		Root<ProgettoSviluppo> root = cq.from(ProgettoSviluppo.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<ProgettoSviluppo> q = session.createQuery(cq);
		ProgettoSviluppo s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Mev readMev(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Mev> cq = builder.createQuery(Mev.class);
		Root<Mev> root = cq.from(Mev.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Mev> q = session.createQuery(cq);
		Mev s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Documento readDocument(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Documento> cq = builder.createQuery(Documento.class);
		Root<Documento> root = cq.from(Documento.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Documento> q = session.createQuery(cq);
		Documento s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Support readRichiestaDiSupporto(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Support> cq = builder.createQuery(Support.class);
		Root<Support> root = cq.from(Support.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Support> q = session.createQuery(cq);
		Support s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Defect readDefect(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Defect> cq = builder.createQuery(Defect.class);
		Root<Defect> root = cq.from(Defect.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Defect> q = session.createQuery(cq);
		Defect s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Anomalia readAnomalia(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Anomalia> cq = builder.createQuery(Anomalia.class);
		Root<Anomalia> root = cq.from(Anomalia.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Anomalia> q = session.createQuery(cq);
		Anomalia s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Taskit readTaskIT(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Taskit> cq = builder.createQuery(Taskit.class);
		Root<Taskit> root = cq.from(Taskit.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Taskit> q = session.createQuery(cq);
		Taskit s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Resolution readResolution(String polarionName) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Resolution> cq = builder.createQuery(Resolution.class);
		Root<Resolution> root = cq.from(Resolution.class);

		// Query
		cq.select(root).where(builder.equal(root.get("polarionName"), polarionName));

		Query<Resolution> q = session.createQuery(cq);
		Resolution s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Testcase readTestCase(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Testcase> cq = builder.createQuery(Testcase.class);
		Root<Testcase> root = cq.from(Testcase.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Testcase> q = session.createQuery(cq);
		Testcase s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Checklist readCheckList(String idPolarion) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Checklist> cq = builder.createQuery(Checklist.class);
		Root<Checklist> root = cq.from(Checklist.class);

		// Query
		cq.select(root).where(builder.equal(root.get("idPolarion"), idPolarion));

		Query<Checklist> q = session.createQuery(cq);
		Checklist s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Esito readEsito(String nome) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Esito> cq = builder.createQuery(Esito.class);
		Root<Esito> root = cq.from(Esito.class);

		// Query
		cq.select(root).where(builder.equal(root.get("nome"), nome));

		Query<Esito> q = session.createQuery(cq);
		Esito s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static TipoItem readTipoItem(String nome) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<TipoItem> cq = builder.createQuery(TipoItem.class);
		Root<TipoItem> root = cq.from(TipoItem.class);

		// Query
		cq.select(root).where(builder.equal(root.get("nome"), nome));

		Query<TipoItem> q = session.createQuery(cq);
		TipoItem s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static Workrecords readWorkRecord(String typeLink, String idPolarion, User user, Date dataUpdate) {
		if (typeLink == null || idPolarion == null || user == null || dataUpdate == null)
			return null;

		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Workrecords> cq = builder.createQuery(Workrecords.class);
		Root<Workrecords> root = cq.from(Workrecords.class);

		// Query
		cq.select(root)
				.where(builder.and(builder.equal(root.get("typeLink"), typeLink),
						builder.equal(root.get("idPolarion"), idPolarion), builder.equal(root.get("user"), user),
						builder.equal(root.get("dateUpdate"), dataUpdate)));

		Query<Workrecords> q = session.createQuery(cq);
		Workrecords s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static LinkedItem readLinkedItem(LinkedItemId lIId) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			// Query
			LinkedItem li = session.get(LinkedItem.class, lIId);

			session.getTransaction().commit();
			return li;
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return null;
	}

	public static ReleaseHistory readReleaseHistory(Release release, Status status, Date dataUpdate, User user) {
		if (release == null)
			return null;
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ReleaseHistory> cq = builder.createQuery(ReleaseHistory.class);
		Root<ReleaseHistory> root = cq.from(ReleaseHistory.class);

		// Query
		if (status != null && dataUpdate != null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate),
					builder.equal(root.get("user"), user.getId())));
		else if (status != null && dataUpdate != null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate)));
		else if (status != null && dataUpdate == null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("user"), user.getId())));
		else if (status == null && dataUpdate != null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("dataUpdate"), dataUpdate), builder.equal(root.get("user"), user.getId())));
		else if (status != null && dataUpdate == null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("status"), status)));
		else if (status == null && dataUpdate != null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("dataUpdate"), dataUpdate)));
		else if (status == null && dataUpdate == null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release),
					builder.equal(root.get("user"), user.getId())));
		else if (status == null && dataUpdate == null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), release)));

		Query<ReleaseHistory> q = session.createQuery(cq);
		ReleaseHistory s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static ReleaseitHistory readReleaseItHistory(ReleaseIt release, Status status, Date dataUpdate, User user) {
		if (release == null)
			return null;

		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ReleaseitHistory> cq = builder.createQuery(ReleaseitHistory.class);
		Root<ReleaseitHistory> root = cq.from(ReleaseitHistory.class);

		// Query
		if (status != null && dataUpdate != null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate),
					builder.equal(root.get("user"), user.getId())));
		else if (status != null && dataUpdate != null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate)));
		else if (status != null && dataUpdate == null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("status"), status), builder.equal(root.get("user"), user.getId())));
		else if (status == null && dataUpdate != null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("dataUpdate"), dataUpdate), builder.equal(root.get("user"), user.getId())));
		else if (status != null && dataUpdate == null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("status"), status)));
		else if (status == null && dataUpdate != null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("dataUpdate"), dataUpdate)));
		else if (status == null && dataUpdate == null && user != null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release),
					builder.equal(root.get("user"), user.getId())));
		else if (status == null && dataUpdate == null && user == null)
			cq.select(root).where(builder.and(builder.equal(root.get("releaseIt"), release)));

		Query<ReleaseitHistory> q = session.createQuery(cq);
		ReleaseitHistory s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static TaskItHistory readTaskITHistory(Taskit taskit, Status status, Date dataUpdate, User user) {
		if (taskit == null || user == null || dataUpdate == null)
			return null;

		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<TaskItHistory> cq = builder.createQuery(TaskItHistory.class);
		Root<TaskItHistory> root = cq.from(TaskItHistory.class);

		// Query
		if (status != null)
			cq.select(root).where(builder.and(builder.equal(root.get("taskit"), taskit),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate),
					builder.equal(root.get("user"), user.getId())));
		else
			cq.select(root).where(builder.and(builder.equal(root.get("taskit"), taskit),
					builder.equal(root.get("dataUpdate"), dataUpdate), builder.equal(root.get("user"), user.getId())));

		Query<TaskItHistory> q = session.createQuery(cq);
		TaskItHistory s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static SupportHistory readRichiestaDiSupportoHistory(Support richiestaDiSupporto, Status status,
			Date dataUpdate) {
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<SupportHistory> cq = builder.createQuery(SupportHistory.class);
		Root<SupportHistory> root = cq.from(SupportHistory.class);

		// Query
		if (status != null && dataUpdate != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), richiestaDiSupporto),
					builder.equal(root.get("status"), status), builder.equal(root.get("dataUpdate"), dataUpdate)));
		else if (status != null && dataUpdate == null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), richiestaDiSupporto),
					builder.equal(root.get("status"), status)));
		else if (status == null && dataUpdate != null)
			cq.select(root).where(builder.and(builder.equal(root.get("release"), richiestaDiSupporto),
					builder.equal(root.get("dataUpdate"), dataUpdate)));

		Query<SupportHistory> q = session.createQuery(cq);
		SupportHistory s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	public static ChecklistTestcase readCheckListTestCase(Checklist checkList, Testcase tc, User user,
			Date dataUpdate) {
		if (checkList == null || tc == null || user == null || dataUpdate == null)
			return null;
		Session session = getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ChecklistTestcase> cq = builder.createQuery(ChecklistTestcase.class);
		Root<ChecklistTestcase> root = cq.from(ChecklistTestcase.class);

		// Query
		cq.select(root)
				.where(builder.and(builder.equal(root.get("checklist"), checkList),
						builder.equal(root.get("testcase"), tc), builder.equal(root.get("dataEsecuzione"), dataUpdate),
						builder.equal(root.get("user"), user)));

		Query<ChecklistTestcase> q = session.createQuery(cq);
		ChecklistTestcase s = null;

		try {
			s = q.getSingleResult();
		} catch (NoResultException ex) {
			s = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			s = q.getResultList().get(0);
		}
		session.getTransaction().commit();
		return s;
	}

	/**
	 * 
	 * Generics methods
	 * 
	 */

	public static <T> boolean update(Class<T> clazz, Serializable entity) {
		if (entity == null || clazz == null || !clazz.isInstance(entity))
			return false;
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();
			// session.setHibernateFlushMode(FlushMode.ALWAYS);

			// Query
			session.update(entity);

			session.getTransaction().commit();
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return false;
	}

	public static <T> T loadObject(Class<T> clazz, Serializable key) {
		T dbObject = null;
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();
			dbObject = clazz.cast(session.get(clazz, key));
			session.getTransaction().commit();
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
		}
		return dbObject;
	}

	public static boolean save(Object o) {
		try {
			if (!(o instanceof Serializable)) {
				ClassNotFoundException ex = new ClassNotFoundException("Object o is not instance of Serializable");
				throw ex;
			}
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();
			// session.setHibernateFlushMode(FlushMode.ALWAYS);

			// Query
			session.save(o);

			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			return false;
		}
	}

	public static boolean rawQuery(String query) {
		try {
			Session session = getSessionFactory().getCurrentSession();
			if (!session.getTransaction().isActive())
				session.beginTransaction();

			// Query
			session.createNativeQuery(query).executeUpdate();

			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (Util.DEBUG)
				Util.writeLog("HibernateUtil.java throws exception", e);
			Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "HibernateUtil.java throws exception", e);
			return false;
		}
	}
}