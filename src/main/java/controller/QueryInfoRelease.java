package controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.query.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONObject;

import basic.HibernateUtil;
import basic.Util;
import entities.Anomalia;
import entities.Defect;
import entities.DefectHistory;
import entities.Release;
import entities.ReleaseHistory;
import entities.Status;

public class QueryInfoRelease {

	public QueryInfoRelease() {

	}
	
	public static Object getCountRows(String value) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();
		TypedQuery query = session
				.createQuery("SELECT COUNT(*) FROM Release WHERE id_polarion like '%" + value + "%' ");
		Object o = query.getSingleResult();
		session.getTransaction().commit();
		return o;
	}

	public static Object getCountFromTable(String table, String column, String value) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();
		TypedQuery query = session
				.createQuery("SELECT COUNT(*) FROM " + table + " WHERE " + column + " like '%" + value + "%' ");
		Object o = query.getSingleResult();
		session.getTransaction().commit();
		return o;
	}

	public static Object getSingleFieldFromTable(String table, String column, String value, String field) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();
		TypedQuery query = session
				.createQuery("SELECT "+field+" FROM " + table + " WHERE " + column + " like '%" + value + "%' ");
		Object o = query.getSingleResult();
		session.getTransaction().commit();
		return o;
	}

	public static Object getCountFromLinkedItemInnerJoinATable(String param, String table) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		TypedQuery query = session.createQuery(
				"SELECT COUNT(*) FROM LinkedItem, "+ table +" where id_polarion_padre = '" + param + "' AND id_polarion like '%"+ param.substring(0, param.indexOf("-")) +"%' and id_polarion_figlio = id_polarion");

		Object o = query.getSingleResult();
		session.getTransaction().commit();

		return o;
	}

	public static List<Release> getInfoReleaseByIDPolarion(String param) {
		//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//		if (!session.getTransaction().isActive())
		//			session.beginTransaction();
		//
		//		CriteriaBuilder builder = session.getCriteriaBuilder();
		//		CriteriaQuery<Release> cq = builder.createQuery(Release.class);
		//		Root<Release> root = cq.from(Release.class);
		//
		//		// Query
		//		cq.select(root).where(builder.like(root.get("idPolarion"), "%" + param + "%"));
		//
		//		Query<Release> q = session.createQuery(cq);
		//
		//		List<Release> releaseList = q.getResultList();
		//
		//		session.getTransaction().commit();

		String query = "";

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		if(param.contains("-")){
			query = "SELECT * FROM rilasci_db.release where id_polarion = '"+param+"'";
		}
		else query = "SELECT * FROM rilasci_db.release where id_polarion like '%"+param+"%'";

		@SuppressWarnings("unchecked")
		Query<Release> q = session.createNativeQuery(query, Release.class);

		List<Release> result = q.getResultList();

		session.getTransaction().commit();

		return result;
	}

	public static List<Object[]> getStatusNameAndDataUpdateFromReleaseHistory(String param) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		TypedQuery queryGetIdFromParam = null;
		Object id = null;
		if(param.contains("-")){
			queryGetIdFromParam = session.createQuery("SELECT id FROM Release where id_polarion = '"+param+"'");
			id = queryGetIdFromParam.getSingleResult();
		}
		else{
			queryGetIdFromParam = session.createQuery("SELECT id FROM Release where id_polarion like '%"+param+"%'");
			id = queryGetIdFromParam.getSingleResult();
		}

		//s.nome, r.dataUpdate
		TypedQuery query = session.createQuery("SELECT s.nome, r.dataUpdate FROM Status AS s, ReleaseHistory AS r WHERE r.release = "+id+" and s.id = r.status group by r.status order by r.dataUpdate");
		List<Object[]> result = new ArrayList<Object[]>();
		result.addAll(query.getResultList());

		session.getTransaction().commit();

		return result;
	}

	public static Object getCountFromDocument(String idPolarionPadre, String codTipoDocumento) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "SELECT COUNT(*) FROM documento where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre = '"+idPolarionPadre+"')"
					+ " and cod_tipo = "+codTipoDocumento;
		}
		else{
			query = "SELECT COUNT(*) FROM documento where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre like '%"+idPolarionPadre+"%')"
					+ " and cod_tipo = "+codTipoDocumento;
		}

		@SuppressWarnings("unchecked")
		Query<BigInteger> q = session.createNativeQuery(query);

		Integer result = q.getSingleResult().intValue();

		session.getTransaction().commit();
		return result;
	}

	public static List<Defect> getDefectFromId(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "SELECT * FROM defect where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre = '"+idPolarionPadre+"')";
		}
		else{
			query = "SELECT * FROM defect where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre like '%"+idPolarionPadre+"%')";
		}

		@SuppressWarnings("unchecked")
		Query<Defect> q = session.createNativeQuery(query, Defect.class);

		List<Defect> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<DefectHistory> getAuthorDefectFromId(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "select * from rilasci_db.defect_history where cod_defect in "
					+ "(select id from rilasci_db.defect as d inner join linked_item as li where d.id_polarion = "
					+ "li.id_polarion_figlio and li.id_polarion_padre = '"+idPolarionPadre+"') group by cod_defect";
		}
		else{
			query = "select * from rilasci_db.defect_history where cod_defect in "
					+ "(select id from rilasci_db.defect as d inner join linked_item as li where d.id_polarion = "
					+ "li.id_polarion_figlio and li.id_polarion_padre like '%"+idPolarionPadre+"%') group by cod_defect";
		}

		@SuppressWarnings("unchecked")
		Query<DefectHistory> q = session.createNativeQuery(query, DefectHistory.class);

		List<DefectHistory> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<Anomalia> getAnomalieFromId(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "SELECT * FROM anomalia where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre = '"+idPolarionPadre+"')";
		}
		else{
			query = "SELECT * FROM anomalia where id_polarion in "
					+ "(select id_polarion_figlio from linked_item where id_polarion_padre like '%"+idPolarionPadre+"%')";
		}

		@SuppressWarnings("unchecked")
		Query<Anomalia> q = session.createNativeQuery(query, Anomalia.class);

		List<Anomalia> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<Object[]> getProgettoSviluppoFromId(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "select sv.id_polarion as svidpolarion, sv.titolo, d.id_polarion as didpolarion from linked_item as li_REL_SV INNER JOIN linked_item as li_SV_DEF "
					+ "INNER JOIN progetto_sviluppo as sv INNER JOIN defect as d where li_REL_SV.id_polarion_figlio = sv.id_polarion "
					+ "and li_REL_SV.id_polarion_padre = '"+idPolarionPadre+"' and li_SV_DEF.id_polarion_figlio = d.id_polarion "
					+ "and li_SV_DEF.id_polarion_padre = sv.id_polarion group by d.id_polarion";
		}
		else{
			query = "select sv.id_polarion as svidpolarion, sv.titolo, d.id_polarion as didpolarion from linked_item as li_REL_SV INNER JOIN linked_item as li_SV_DEF "
					+ "INNER JOIN progetto_sviluppo as sv INNER JOIN defect as d where li_REL_SV.id_polarion_figlio = sv.id_polarion "
					+ "and li_REL_SV.id_polarion_padre like '%"+idPolarionPadre+"%' and li_SV_DEF.id_polarion_figlio = d.id_polarion "
					+ "and li_SV_DEF.id_polarion_padre = sv.id_polarion group by d.id_polarion";
		}

		@SuppressWarnings("unchecked")
		Query<Object[]> q = session.createNativeQuery(query);

		List<Object[]> result = q.getResultList();

		if(result.size() == 0){
			result.clear();

			String query2 = null;
			if(idPolarionPadre.contains("-")){
				query2  = "select sv.id_polarion as svidpolarion, sv.titolo, 0 as placeholder from linked_item as li_REL_SV INNER JOIN linked_item as li_SV_DEF "
						+ "INNER JOIN progetto_sviluppo as sv where li_REL_SV.id_polarion_figlio = sv.id_polarion "
						+ "and li_REL_SV.id_polarion_padre = '"+idPolarionPadre+"' group by sv.id_polarion";
			}
			else{
				query2  = "select sv.id_polarion as svidpolarion, sv.titolo, 0 as placeholder from linked_item as li_REL_SV INNER JOIN linked_item as li_SV_DEF "
						+ "INNER JOIN progetto_sviluppo as sv where li_REL_SV.id_polarion_figlio = sv.id_polarion "
						+ "and li_REL_SV.id_polarion_padre like '%"+idPolarionPadre+"%' group by sv.id_polarion";
			}

			@SuppressWarnings("unchecked")
			Query<Object[]> q2 = session.createNativeQuery(query2);

			result = q2.getResultList();
		}

		session.getTransaction().commit();
		return result;
	}

	public static List<Object[]> getMevFromId(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "select mev.id_polarion as mevidpolarion, mev.titolo, d.id_polarion as didpolarion from linked_item as li_REL_MEV INNER JOIN linked_item as li_MEV_DEF "
					+ "INNER JOIN mev as mev INNER JOIN defect as d where li_REL_MEV.id_polarion_figlio = mev.id_polarion "
					+ "and li_REL_MEV.id_polarion_padre = '"+idPolarionPadre+"' and li_MEV_DEF.id_polarion_figlio = d.id_polarion "
					+ "and li_MEV_DEF.id_polarion_padre = mev.id_polarion group by d.id_polarion";
		}
		else{
			query = "select mev.id_polarion as mevidpolarion, mev.titolo, d.id_polarion as didpolarion from linked_item as li_REL_MEV INNER JOIN linked_item as li_MEV_DEF "
					+ "INNER JOIN mev as mev INNER JOIN defect as d where li_REL_MEV.id_polarion_figlio = mev.id_polarion "
					+ "and li_REL_MEV.id_polarion_padre like '%"+idPolarionPadre+"%' and li_MEV_DEF.id_polarion_figlio = d.id_polarion "
					+ "and li_MEV_DEF.id_polarion_padre = mev.id_polarion group by d.id_polarion";
		}


		@SuppressWarnings("unchecked")
		Query<Object[]> q = session.createNativeQuery(query);

		List<Object[]> result = q.getResultList();

		if(result.size() == 0){
			result.clear();

			String query2 = null;
			if(idPolarionPadre.contains("-")){
				query2  = "select mev.id_polarion as mevidpolarion, mev.titolo, 0 as placeholder from linked_item as li_REL_MEV INNER JOIN linked_item as li_MEV_DEF "
						+ "INNER JOIN mev as mev where li_REL_MEV.id_polarion_figlio = mev.id_polarion "
						+ "and li_REL_MEV.id_polarion_padre = '"+idPolarionPadre+"' group by mev.id_polarion";
			}
			else{
				query2  = "select mev.id_polarion as mevidpolarion, mev.titolo, 0 as placeholder from linked_item as li_REL_MEV INNER JOIN linked_item as li_MEV_DEF "
						+ "INNER JOIN mev as mev where li_REL_MEV.id_polarion_figlio = mev.id_polarion "
						+ "and li_REL_MEV.id_polarion_padre like '%"+idPolarionPadre+"%' group by mev.id_polarion";
			}

			@SuppressWarnings("unchecked")
			Query<Object[]> q2 = session.createNativeQuery(query2);

			result = q2.getResultList();
		}

		session.getTransaction().commit();
		return result;
	}

	public static List<Object> getDataInizioFineRelease(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "select rh.data_update from rilasci_db.release rel inner join release_history rh where rel.id_polarion = '"+idPolarionPadre+"' and rh.cod_id_release = rel.id and rh.cod_status in (22, 7) group by rh.cod_status";
		}
		else{
			query = "select rh.data_update from rilasci_db.release rel inner join release_history rh where rel.id_polarion like '%"+idPolarionPadre+"%' and rh.cod_id_release = rel.id and rh.cod_status in (22, 7) group by rh.cod_status";
		}

		@SuppressWarnings("unchecked")
		Query<Object> q = session.createNativeQuery(query);

		List<Object> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

	public static List<Object[]> getDetailDefect(String idPolarionPadre) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		String query = null;
		if(idPolarionPadre.contains("-")){
			query = "SELECT distinct def.id_polarion, defh.cod_status, def.cod_resolution, def.data_creazione, defh.data_update "
					+ "FROM rilasci_db.defect as def, rilasci_db.defect_history defh "
					+ "WHERE defh.cod_defect = def.id and defh.cod_status is not null and def.id_polarion in "
					+ "(select id_polarion_figlio from rilasci_db.linked_item where id_polarion_padre = '"+idPolarionPadre+"') order by defh.data_update DESC";
		}
		else{
			query = "SELECT distinct def.id_polarion, defh.cod_status, def.cod_resolution, def.data_creazione, defh.data_update "
					+ "FROM rilasci_db.defect as def, rilasci_db.defect_history defh "
					+ "WHERE defh.cod_defect = def.id and defh.cod_status is not null and def.id_polarion in "
					+ "(select id_polarion_figlio from rilasci_db.linked_item where id_polarion_padre like '%"+idPolarionPadre+"%') order by defh.data_update DESC";
		}

		@SuppressWarnings("unchecked")
		Query<Object[]> q = session.createNativeQuery(query);

		List<Object[]> result = q.getResultList();

		session.getTransaction().commit();
		return result;
	}

}
