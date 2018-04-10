package controller;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;

import basic.HibernateUtil;
import basic.Util;
import entities.Checklist;
import entities.Defect;
import entities.Release;
import entities.ReleaseHistory;
import entities.ReleaseIt;
import entities.ReleaseitHistory;
import entities.Status;
import entities.TaskItHistory;
import entities.Testcase;
import entities.Workrecords;

public class QueryReleaseIT {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String DEPRECATED = "DEPRECATO", OBSOLETE = "OBSOLETO";

	public static List<ReleaseIt> getInfoReleaseITByIDPolarion(String param) {
		if (param == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ReleaseIt> cq = builder.createQuery(ReleaseIt.class);
		Root<ReleaseIt> root = cq.from(ReleaseIt.class);

		// Query
		if ((param.length() - param.replace("-", "").length()) == 1)
			cq.select(root).where(builder.like(root.get("idPolarion"), "%" + param + "%"));
		else
			cq.select(root).where(builder.equal(root.get("idPolarion"), param));

		Query<ReleaseIt> q = session.createQuery(cq);

		List<ReleaseIt> releaseList = q.getResultList();

		session.getTransaction().commit();

		return releaseList;
	}

	public static String getProjectByReleaseIT(String idPolarionReleaseIT) {
		if (idPolarionReleaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT nome FROM project WHERE id IN (SELECT distinct r.cod_id_progetto FROM linked_item AS li INNER JOIN rilasci_db.release AS r WHERE li.id_polarion_padre = '"
				+ idPolarionReleaseIT + "' AND li.id_polarion_figlio = r.id_polarion)";

		@SuppressWarnings("unchecked")
		Query<String> q = session.createNativeQuery(query);

		String result = null;

		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		} catch (NonUniqueResultException e) {
			StringBuffer sb = new StringBuffer();
			for (String s : q.getResultList())
				sb.append(s);
			result = sb.toString();
		}
		session.getTransaction().commit();
		return result;
	}

	public static Release getReleaseByReleaseIT(String idPolarionReleaseIT) {
		if (idPolarionReleaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT DISTINCT r.* FROM linked_item AS li INNER JOIN rilasci_db.release AS r WHERE li.id_polarion_padre = '"
				+ idPolarionReleaseIT + "' AND li.id_polarion_figlio = r.id_polarion";

		Query<Release> q = session.createNativeQuery(query, Release.class);

		Release result = null;

		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE,
					"QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			result = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE,
					"QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			result = null;
		}
		session.getTransaction().commit();
		return result;
	}

	public static Long getCountFromLinkedItemInnerJoinATable(String param, String table) {
		if (param == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}
		Release r = getReleaseByReleaseIT(param);

		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT COUNT(*) FROM linked_item INNER JOIN " + table
				+ " WHERE id_polarion_figlio = id_polarion AND id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND id_polarion LIKE '%" + r.getIdPolarion().substring(0, r.getIdPolarion().indexOf("-")) + "%'";

		@SuppressWarnings("unchecked")
		Query<BigInteger> q = session.createNativeQuery(query);

		Long result = q.getSingleResult().longValue();

		session.getTransaction().commit();
		return result;
	}

	public static List<ReleaseHistory> getAllRelaseStatusByRelaseIT(String idPolarionRelaseIT, String status) {
		if (idPolarionRelaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Release r = getReleaseByReleaseIT(idPolarionRelaseIT);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT rh.* FROM release_history AS rh INNER JOIN rilasci_db.release AS r ";
		if (status != null)
			query += " INNER JOIN rilasci_db.status AS s ";

		query += "WHERE rh.cod_id_release = r.id AND r.id_polarion = '" + r.getIdPolarion() + "'";

		if (status != null)
			query += "AND rh.cod_status = s.id AND s.polarion_name='" + status + "'";

		query += " ORDER BY rh.data_update ASC";

		Query<ReleaseHistory> q = session.createNativeQuery(query, ReleaseHistory.class);

		List<ReleaseHistory> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<ReleaseitHistory> getAllReleaseItStatus(String idPolarionReleaseIT, Status status) {
		if (idPolarionReleaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		ReleaseIt r = HibernateUtil.readReleaseIT(idPolarionReleaseIT);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT * FROM releaseit_history WHERE cod_releaseit = '" + r.getId()
				+ "' AND cod_status IS NOT NULL";
		if (status != null)
			query += " AND cod_status=" + status.getId();

		query += " ORDER BY data_update ASC";

		Query<ReleaseitHistory> q = session.createNativeQuery(query, ReleaseitHistory.class);

		List<ReleaseitHistory> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<TaskItHistory> getTastItHistoryByReleaseIT(String idPolarionReleaseIT) {
		if (idPolarionReleaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT th.* FROM taskit AS t INNER JOIN task_it_history AS th INNER JOIN linked_item AS li"
				+ " WHERE t.id = th.cod_task_it AND li.id_polarion_figlio = t.id_polarion AND li.id_polarion_padre = '"
				+ idPolarionReleaseIT + "' ORDER BY th.data_update ASC";

		Query<TaskItHistory> q = session.createNativeQuery(query, TaskItHistory.class);

		List<TaskItHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<Testcase> getTestcaseByReleaseIT(String idPolarionReleaseIT) {
		if (idPolarionReleaseIT == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT tc.* FROM testcase AS tc INNER JOIN linked_item AS li WHERE "
				+ "tc.id_polarion = li.id_polarion_figlio AND li.id_polarion_padre = '" + idPolarionReleaseIT + "'";
		Query<Testcase> q = session.createNativeQuery(query, Testcase.class);

		List<Testcase> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Integer getBaseTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT Count(*) FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND tc.data_creazione < '" + sdf.format(r.getDataInizio()) + "'";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Integer getNewTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null || r.getDataFine() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT Count(*) FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND '" + sdf.format(r.getDataInizio()) + "' < tc.data_creazione AND tc.data_creazione < '"
				+ sdf.format(r.getDataFine()) + "'";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	/***
	 * Verifica che nello storico del tc esista almeno una data_update compresa
	 * tra data_inizio e data_fine della release_it
	 * 
	 * @param ReleaseIt
	 *            r
	 * @return Integer
	 */
	@SuppressWarnings("unchecked")
	public static Integer getUpdatedTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null || r.getDataFine() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT COUNT(*) FROM (";
		String queryUpdatedTC = "SELECT DISTINCT tc.id_polarion FROM testcase AS tc INNER JOIN testcase_history AS th INNER JOIN linked_item AS li WHERE"
				+ " tc.id = th.id_testcase AND th.data != tc.data_creazione AND " + "(tc.titolo NOT LIKE '%"
				+ DEPRECATED + "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND '" + sdf.format(r.getDataInizio())
				+ "' < th.data AND '" + sdf.format(r.getDataFine())
				+ "' > th.data AND li.id_polarion_figlio = tc.id_polarion AND li.id_polarion_padre = '"
				+ r.getIdPolarion() + "'";
		String queryNewTC = "SELECT DISTINCT tc.id_polarion FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND '" + sdf.format(r.getDataInizio()) + "' < tc.data_creazione AND tc.data_creazione < '"
				+ sdf.format(r.getDataFine()) + "'";

		query += queryUpdatedTC + " AND tc.id_polarion NOT IN (" + queryNewTC + ")) AS q";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Integer getDeprecatedTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT Count(*) FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo LIKE '%" + OBSOLETE + "%' OR tc.tag LIKE '%" + DEPRECATED + "%' OR tc.tag LIKE '%"
				+ OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion() + "'";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Integer getAutomaticTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT Count(*) FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND tc.tipo_tc = 'automatico'";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Integer getManualTestcaseCountByReleaseIT(ReleaseIt r) {
		if (r == null || r.getDataInizio() == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT Count(*) FROM testcase AS tc INNER JOIN linked_item AS li WHERE"
				+ " tc.id_polarion = li.id_polarion_figlio AND " + "(tc.titolo NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.titolo NOT LIKE '%" + OBSOLETE + "%' OR tc.tag NOT LIKE '%" + DEPRECATED
				+ "%' OR tc.tag NOT LIKE '%" + OBSOLETE + "%')" + " AND li.id_polarion_padre = '" + r.getIdPolarion()
				+ "' AND (tc.tipo_tc = 'manuale' OR tc.tipo_tc IS NULL)";

		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	public static List<TaskItHistory> getTimingDeployDBATaskit(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT th.* FROM task_it_history AS th INNER JOIN rilasci_db.status AS s INNER JOIN taskit AS t  INNER JOIN tipo_item AS ti INNER JOIN linked_item AS li_r_t"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_r_t.id_polarion_padre AND t.id_polarion = li_r_t.id_polarion_figlio AND t.cod_tipo = ti.id AND"
				+ " (ti.nome = 'deploy' or ti.nome = 'dba') AND t.id = th.cod_task_it AND th.cod_status = s.id AND s.polarion_name = 'chiuso'"
				+ " ORDER BY th.cod_task_it ASC, th.data_update DESC";

		Query<TaskItHistory> q = session.createNativeQuery(query, TaskItHistory.class);

		List<TaskItHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<TaskItHistory> getTimingTestTaskit(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT th.* FROM task_it_history AS th INNER JOIN rilasci_db.status AS s INNER JOIN taskit AS t  INNER JOIN tipo_item AS ti INNER JOIN linked_item AS li_r_t"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_r_t.id_polarion_padre AND t.id_polarion = li_r_t.id_polarion_figlio AND t.cod_tipo = ti.id AND"
				+ " (ti.nome = 'test') AND t.id = th.cod_task_it AND th.cod_status = s.id AND s.polarion_name = 'chiuso'"
				+ " ORDER BY th.cod_task_it ASC, th.data_update DESC";

		Query<TaskItHistory> q = session.createNativeQuery(query, TaskItHistory.class);

		List<TaskItHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<Workrecords> getAuthorsTaskitWorkrecordsByReleaseIT(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT w.* "
				+ "FROM workrecords AS w INNER JOIN taskit AS t INNER JOIN linked_item AS li_r_t INNER JOIN linked_item AS li_t_w"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_r_t.id_polarion_padre AND t.id_polarion = li_r_t.id_polarion_figlio AND t.id_polarion = li_t_w.id_polarion_padre AND "
				+ "w.id_polarion = li_t_w.id_polarion_figlio AND w.cod_author IS NOT NULL";

		Query<Workrecords> q = session.createNativeQuery(query, Workrecords.class);

		List<Workrecords> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<ReleaseHistory> getReleseHistoryByStatusQuickfixAndInIntegrazione(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT rh.* FROM release_history AS rh INNER JOIN rilasci_db.release AS r INNER JOIN linked_item AS li_rit_r INNER JOIN rilasci_db.status AS s"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_rit_r.id_polarion_padre AND r.id_polarion = li_rit_r.id_polarion_figlio AND r.id = rh.cod_id_release AND rh.cod_status = s.id AND"
				+ "(s.polarion_name = 'quickfix' OR s.polarion_name = 'in_integrazione') "
				+ "ORDER BY rh.data_update ASC";
		Query<ReleaseHistory> q = session.createNativeQuery(query, ReleaseHistory.class);

		List<ReleaseHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<ReleaseHistory> getReleseHistoryByStatusSviluppoAndQuickBuild(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT rh.* FROM release_history AS rh INNER JOIN rilasci_db.release AS r INNER JOIN linked_item AS li_rit_r INNER JOIN rilasci_db.status AS s"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_rit_r.id_polarion_padre AND r.id_polarion = li_rit_r.id_polarion_figlio AND r.id = rh.cod_id_release AND rh.cod_status = s.id AND"
				+ "(s.polarion_name = 'in_sviluppo' OR s.polarion_name = 'quickbuild') "
				+ "ORDER BY rh.data_update ASC";
		Query<ReleaseHistory> q = session.createNativeQuery(query, ReleaseHistory.class);

		List<ReleaseHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<ReleaseHistory> getReleseHistoryByStatusCM(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT rh.* FROM release_history AS rh INNER JOIN rilasci_db.release AS r INNER JOIN linked_item AS li_rit_r INNER JOIN rilasci_db.status AS s"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_rit_r.id_polarion_padre AND r.id_polarion = li_rit_r.id_polarion_figlio AND r.id = rh.cod_id_release AND rh.cod_status = s.id AND"
				+ "(s.polarion_name = 'in_compilazione' OR s.polarion_name = 'go_integrazione') "
				+ "ORDER BY rh.data_update ASC";
		Query<ReleaseHistory> q = session.createNativeQuery(query, ReleaseHistory.class);

		List<ReleaseHistory> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<Checklist> getChecklistByReleaseIT(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT cl.* FROM linked_item AS li_rit_cl INNER JOIN checklist AS cl WHERE '"
				+ r.getIdPolarion()
				+ "' = li_rit_cl.id_polarion_padre AND cl.id_polarion = li_rit_cl.id_polarion_figlio";

		Query<Checklist> q = session.createNativeQuery(query, Checklist.class);

		List<Checklist> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static Integer getChecklistTestcaseCountByChecklist(Checklist cl, String esito) {
		if (cl == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT count(*) FROM checklist_testcase AS cl_tc INNER JOIN esito AS e WHERE '" + cl.getId()
				+ "' = cl_tc.cod_checlist AND e.id = cl_tc.cod_esito AND e.nome = '" + esito + "'";

		@SuppressWarnings("unchecked")
		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	public static List<String> getDistinctUserInChecklistTestcaseByReleaseIT(ReleaseIt r) {
		if (r == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT DISTINCT u.id_polarion FROM linked_item AS li_rit_cl INNER JOIN checklist AS cl INNER JOIN checklist_testcase AS ct INNER JOIN rilasci_db.user u"
				+ " WHERE '" + r.getIdPolarion()
				+ "' = li_rit_cl.id_polarion_padre AND cl.id_polarion = li_rit_cl.id_polarion_figlio AND ct.cod_checlist = cl.id AND ct.cod_user = u.id";

		@SuppressWarnings("unchecked")
		Query<String> q = session.createNativeQuery(query);

		List<String> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static Integer getTestcaseCountByChecklist(Checklist cl) {
		if (cl == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT count(*) FROM checklist_testcase WHERE '" + cl.getId() + "' = cod_checlist";

		@SuppressWarnings("unchecked")
		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}

	public static List<Defect> getDefectByTestcase(String idPolarion) {
		if (idPolarion == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT d.* FROM linked_item AS li_tc_d INNER JOIN defect d"
				+ " WHERE	li_tc_d.id_polarion_padre = '" + idPolarion
				+ "' AND li_tc_d.id_polarion_figlio = d.id_polarion GROUP BY d.id_polarion ORDER BY d.data_creazione ASC";

		Query<Defect> q = session.createNativeQuery(query, Defect.class);

		List<Defect> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static Integer getChecklistCountByQuickfixDate(Date startDate, Date endDate, ReleaseIt r) {
		if (endDate == null) {
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception",
					npe);
			return null;
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT COUNT(*) FROM linked_item AS li_rit_cl INNER JOIN checklist AS cl WHERE '"
				+ r.getIdPolarion()
				+ "' = li_rit_cl.id_polarion_padre AND cl.id_polarion = li_rit_cl.id_polarion_figlio AND cl.data_fine IS NOT NULL";
		if (startDate != null)
			query += " AND cl.data_fine > '" + sdf.format(startDate) + "'";

		query += " AND cl.data_fine <= '" + sdf.format(endDate) + "'";

		@SuppressWarnings("unchecked")
		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();

		return result;
	}
}
