package controller;

import java.math.BigInteger;
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
import entities.Release;
import entities.ReleaseIt;

public class QueryReleaseIT {
	public static List<ReleaseIt> getInfoReleaseITByIDPolarion(String param) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ReleaseIt> cq = builder.createQuery(ReleaseIt.class);
		Root<ReleaseIt> root = cq.from(ReleaseIt.class);

		// Query
		cq.select(root).where(builder.like(root.get("idPolarion"), "%" + param + "%"));

		Query<ReleaseIt> q = session.createQuery(cq);

		List<ReleaseIt> releaseList = q.getResultList();

		session.getTransaction().commit();

		return releaseList;
	}

	public static String getProjectByReleaseIT(String idPolarionReleaseIT) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT nome FROM project WHERE id IN (SELECT distinct r.cod_id_progetto FROM linked_item AS li INNER JOIN rilasci_db.release AS r WHERE li.id_polarion_padre LIKE '"
				+ idPolarionReleaseIT + "%' AND li.id_polarion_figlio = r.id_polarion)";

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
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = "SELECT DISTINCT r.* FROM linked_item AS li INNER JOIN rilasci_db.release AS r WHERE li.id_polarion_padre LIKE '"
				+ idPolarionReleaseIT + "%' AND li.id_polarion_figlio = r.id_polarion";

		Query<Release> q = session.createNativeQuery(query, Release.class);

		Release result = null;

		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			result = null;
		} catch (NonUniqueResultException e) {
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception [Param: " + idPolarionReleaseIT + "]\n" + query, e);
			result = null;
		}
		session.getTransaction().commit();
		return result;
	}

	public static Long getCountFromLinkedItemInnerJoinATable(String param, String table) {
		
		Release r = getReleaseByReleaseIT(param);
		
		if(r == null){
			NullPointerException npe = new NullPointerException();
			if (Util.DEBUG)
				Util.writeLog("QueryReleaseIT.java throws exception", npe);
			Logger.getLogger(QueryReleaseIT.class.getName()).log(Level.SEVERE, "QueryReleaseIT.java throws exception", npe);
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

}
