package servlets;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import basic.HibernateUtil;
import basic.Scheduler;
import controller.CalculateTime;
import controller.JsonReleaseAnomalia;
import controller.JsonReleaseDefect;
import controller.JsonReleaseDocumenti;
import controller.JsonReleaseFiltri;
import controller.JsonReleaseITGeneralInfo;
import controller.JsonReleaseInfoGeneral;
import controller.JsonReleaseMev;
import controller.JsonReleaseProgettoSviluppo;
import controller.JsonReleaseStatus;
import controller.QueryReleaseIT;
import entities.Csv;
import entities.Release;
import entities.ReleaseHistory;
import entities.ReleaseIt;
import entities.ReleaseitHistory;
import entities.Status;

@Path("/")
public class ServicesGet {

	private static LinkedList<Scheduler> schedulersList = new LinkedList<Scheduler>();

	private JsonObject jsonObject = new JsonObject();

	static ClassLoader classLoader = ServicesGet.class.getClassLoader();
	private static File file = new File(classLoader.getResource("db.xml").getFile());

	@GET
	@Path("/getAllStatus")
	@Produces("application/json")
	public Response getAllStatus() {
		List<String> statusName = new ArrayList<String>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query query = session
				.createQuery("FROM Status");
		List<Status> result = query.getResultList();

		session.getTransaction().commit();

		for (Status s : result) {
			statusName.add(s.getPolarionName());
		}

		return Response.accepted().entity(statusName).build();
	}

	@GET
	@Path("/csvForIdPolarion")
	@Produces("application/json")
	public Response getCsvForIdPolarion(@QueryParam("idPolarion") String idPolarion) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Criteria cr = session.createCriteria(Csv.class);
		cr.add(Restrictions.eq("idPolarion", idPolarion));
		List<Csv> csv = cr.list();
		return Response.accepted().entity(csv).build();
	}

	@GET
	@Path("/graphRelease")
	@Produces("application/json")
	public Response getRelease() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query queryGraph = session
				.createQuery("SELECT COUNT(*),YEAR(data_creazione) FROM Release GROUP BY YEAR(data_creazione)");
		List<Gson> result = queryGraph.getResultList();

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/countRelease")
	@Produces("application/json")
	public Response getCountRelease() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query querySire = session
				.createQuery("SELECT COUNT(*) FROM Release WHERE repository = 'SIRE'");
		Query querySiss = session
				.createQuery("SELECT COUNT(*) FROM Release WHERE repository = 'SISS'");

		List<Gson> result = querySire.getResultList();
		result.addAll(querySiss.getResultList());

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/graphReleaseIt")
	@Produces("application/json")
	public Response getReleaseIt() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query queryGraph = session
				.createQuery("SELECT COUNT(*),YEAR(data_creazione) FROM ReleaseIt GROUP BY YEAR(data_creazione)");
		List<Gson> result = queryGraph.getResultList();

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/countReleaseIt")
	@Produces("application/json")
	public Response getCountReleaseIt() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query querySire = session
				.createQuery("SELECT COUNT(*) FROM ReleaseIt WHERE repository = 'SIRE' AND id_polarion not like '%DEROGHE%'");
		Query querySiss = session
				.createQuery("SELECT COUNT(*) FROM ReleaseIt WHERE repository = 'SISS' AND id_polarion not like '%DEROGHE%'");
		Query querySireDeroghe = session
				.createQuery("SELECT COUNT(*) FROM ReleaseIt WHERE repository = 'SIRE' AND id_polarion like '%DEROGHE%'");
		Query querySissDeroghe = session
				.createQuery("SELECT COUNT(*) FROM ReleaseIt WHERE repository = 'SISS' AND id_polarion like '%DEROGHE%'");


		List<Gson> result = querySire.getResultList();
		result.addAll(querySiss.getResultList());
		result.addAll(querySireDeroghe.getResultList());
		result.addAll(querySissDeroghe.getResultList());

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/numberOfReleaseItForRepository")
	@Produces("application/json")
	public Response getNumberOfReleaseItForRepository(@QueryParam("repository") String repository) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query query = session.createQuery(
				"SELECT COUNT(*),repository FROM ReleaseIt WHERE repository='" + repository + "' group by repository;");
		List<Gson> result = query.getResultList();

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/numberOfAllReleaseIt")
	@Produces("application/json")
	public Response getNumberOfAllReleaseIt() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		Query query = session.createQuery("SELECT COUNT(*) as ALL FROM ReleaseIt group by repository;");
		List<Gson> result = query.getResultList();

		session.getTransaction().commit();

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/getTimeReleaseInStatus")
	@Produces("application/json")
	public Response getTimeReleaseInStatus(@QueryParam("idPolarion") String idPolarion, @QueryParam("status") String status) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		// Query
		Query queryRelease = session.createQuery("select id FROM Release where id_polarion='" + idPolarion + "'");
		Query queryStatus = session.createQuery("FROM Status where polarion_name='" + status + "'");

		Status s = (Status) queryStatus.getSingleResult();
		List<Release> r = new ArrayList<Release>();
		r = queryRelease.getResultList();

		List<String> result = new ArrayList<String>();
		if(!r.isEmpty()){

			Query releaseHistoryQ = session.createQuery("FROM ReleaseHistory where cod_id_release='" + r.get(0) + "'");
			List<ReleaseHistory> rhList = releaseHistoryQ.getResultList();

			List<Object[]> list = new ArrayList<Object[]>();

			for (ReleaseHistory rh : rhList) {
				Object[] o = new Object[2];
				if (rh.getStatus() != null)
					o[0] = rh.getStatus().getId();
				else
					continue;
				if (rh.getDataUpdate() != null)
					o[1] = rh.getDataUpdate();
				else
					continue;

				list.add(o);
				//			System.out.println(Arrays.toString(o));
			}

			String timeInStatus = CalculateTime.getTimeInStatus(list, s.getId() + "", s.getNome());

			List<String> listDate = new ArrayList<String>();

			List<Object> listFiltrata = CalculateTime.getListFiltrata();
			int length = listFiltrata.size() - 1;
			for(int i = 0; i < length; i=i+2){
				System.out.println("Start date: "+listFiltrata.get(i)+"; End date: "+listFiltrata.get(i+1));
				listDate.add("Start date: "+listFiltrata.get(i)+"; End date: "+listFiltrata.get(i+1));
			}

			result.clear();
			result.add(timeInStatus);
			result.addAll(listDate);

			session.getTransaction().commit();
			listDate.clear();
			timeInStatus="";
			listFiltrata.clear();
		}
		else result.add("No release with id Polarion "+idPolarion);

		return Response.accepted().entity(result).build();

	}

	@GET
	@Path("/getTimeReleaseItInStatus")
	@Produces("application/json")
	public Response getTimeReleaseItInStatus(@QueryParam("idPolarion") String idPolarion, @QueryParam("status") String status) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (!session.getTransaction().isActive())
			session.beginTransaction();

		// Query
		Query queryReleaseIt = session.createQuery("select id FROM ReleaseIt where id_polarion='" + idPolarion + "'");
		Query queryStatus = session.createQuery("FROM Status where polarion_name='" + status + "'");

		Status s = (Status) queryStatus.getSingleResult();
		List<ReleaseIt> r = queryReleaseIt.getResultList();

		List<String> result = new ArrayList<String>();
		if(!r.isEmpty()){

			Query releaseItHistoryQ = session.createQuery("FROM ReleaseitHistory where cod_releaseit='" + r.get(0) + "'");
			List<ReleaseitHistory> rhList = releaseItHistoryQ.getResultList();

			List<Object[]> list = new ArrayList<Object[]>();

			for (ReleaseitHistory rh : rhList) {
				Object[] o = new Object[2];
				if (rh.getStatus() != null)
					o[0] = rh.getStatus().getId();
				else
					continue;
				if (rh.getDataUpdate() != null)
					o[1] = rh.getDataUpdate();
				else
					continue;

				list.add(o);
				System.out.println(Arrays.toString(o));
			}

			//		session.getTransaction().commit();
			//
			//		String result = CalculateTime.getTimeInStatus(list, s.getId() + "", s.getNome());
			//		System.out.println(result);
			//		return Response.accepted().entity(result).build();

			String timeInStatus = CalculateTime.getTimeInStatus(list, s.getId() + "", s.getNome());

			List<String> listDate = new ArrayList<String>();

			List<Object> listFiltrata = CalculateTime.getListFiltrata();
			int length = listFiltrata.size() - 1;
			for(int i = 0; i < length; i=i+2){
				System.out.println("Data inizio: "+listFiltrata.get(i)+"; Data fine: "+listFiltrata.get(i+1));
				listDate.add("Data inizio: "+listFiltrata.get(i)+"; Data fine: "+listFiltrata.get(i+1));
			}


			result.add(timeInStatus);
			result.addAll(listDate);

			session.getTransaction().commit();
			listDate.clear();
			timeInStatus="";
			listFiltrata.clear();

		}
		else result.add("No release with id Polarion "+idPolarion);

		return Response.accepted().entity(result).build();

	}

	/// BatchManager

	@GET
	@Path("/scheduler/avvia")
	@Produces("application/json")
	public void avviaScheduler(@QueryParam("tipoEsecuzione") String tipoEsecuzione,
			@QueryParam("data") String data,
			@QueryParam("time") String time,
			@QueryParam("secondi") String secondi,
			@QueryParam("tempoEsecuzione") String tempoEsecuzione) {

		Scheduler s = null;
		if(tipoEsecuzione == null || tempoEsecuzione == null || tipoEsecuzione.equals("undefined") || tempoEsecuzione.equals("undefined")) {

			//response errore
			return;

		}
		int modalita = Integer.parseInt(tempoEsecuzione);
		int operazione = Integer.parseInt(tipoEsecuzione);
		long tempo = 0;
		System.out.println(!data.equals("unefined"));
		System.out.println(time != null);
		System.out.println(secondi != null);
		if((data != null || !data.equals("undefined")) && (time != null || !time.equals("undefined"))) {
			secondi=null;
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			GregorianCalendar gc = new GregorianCalendar(), tmp = new GregorianCalendar();
			try {
				gc.setTime(sdf.parse(data));
				tmp.setTime(sdf.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			gc.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY));
			gc.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE));
			gc.set(Calendar.SECOND, tmp.get(Calendar.SECOND));

			tempo = gc.getTimeInMillis();
		}else if (data == null || data.equals("undefined") && time == null || time.equals("undefined") && secondi != null || !secondi.equals("undefined")) {
			data=null;
			time=null;
			tempo = 1000 * Long.parseLong(secondi);
		}
		else if (data == null || data.equals("undefined") && time == null || time.equals("undefined") && secondi == null || secondi.equals("undefined")) {

			data=null;
			time=null;
			secondi=null;
			tempo = 1000;
		}

		s = new Scheduler(modalita, operazione, tempo, "utente_1");
		schedulersList.add(s);
		s.start();


	}

	//Elenco Batch attivi
	@GET
	@Path("/scheduler/elenco")
	@Produces("application/json")
	public String getElencoScheduler() {
		JsonObject jsonObject = new JsonObject();
		if(schedulersList.size() > 0) {
			int index = 0;
			for(Scheduler s: schedulersList) {
				JsonObject innerObj = new JsonObject();
				innerObj.addProperty("uniqueid", s.getUniqueID());
				innerObj.addProperty("mode", s.getMode());
				innerObj.addProperty("operation", s.getOperationStatus());
				innerObj.addProperty("status", s.getCurrentStatus());
				jsonObject.add(index + "", innerObj);
				index++;
			}

		}

		System.out.println(jsonObject.toString());
		return jsonObject.toString();
	}

	//Stop Batch
	@GET
	@Path("/scheduler/stop")
	@Produces("application/json")
	public void stopScheduler(@QueryParam("idScheduler") String idScheduler) {

		for(Scheduler element:schedulersList)
		{   
			if(element.getUniqueID().equals(idScheduler)) {
				element.stopScheduler();
				schedulersList.remove(element);   
			}
		}
		// this.getElencoScheduler();
	}

	@POST
	@Path("/checklogin")
	public Response getCheckLogin(@FormParam("user") String user,@FormParam("psw") String psw) throws ParserConfigurationException, SAXException, IOException {


		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		if (file.exists()) {
			Document doc = db.parse(file);
			Element docEle = doc.getDocumentElement();

			NodeList utente = docEle.getElementsByTagName("utente");

			if (utente != null && utente.getLength() > 0) {
				for (int i = 0; i < utente.getLength(); i++) {

					Node node = utente.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element e = (Element) node;
						NodeList nodeListUser = e.getElementsByTagName("username");
						NodeList nodeListPassword = e.getElementsByTagName("password");

						if(nodeListUser.item(0).getChildNodes().item(0).getNodeValue().equals(user) &&
								nodeListPassword.item(0).getChildNodes().item(0).getNodeValue().equals(psw)){

							System.out.println(nodeListUser.item(0).getChildNodes().item(0).getNodeValue());
							System.out.println(nodeListPassword.item(0).getChildNodes().item(0).getNodeValue());

							jsonObject.addProperty("found", "true");
							jsonObject.addProperty("username", user);
							return Response.accepted().entity(jsonObject.toString()).build();         
						}
					}
				}
			} 
		}

		jsonObject.addProperty("found", "false");
		jsonObject.addProperty("username", "null");
		jsonObject.addProperty("err","Username o password non validi");

		return Response.status(Response.Status.BAD_REQUEST).entity(jsonObject.toString()).build();
	}

	@GET
	@Path("/infoReleaseProgetto")
	@Produces("application/json")
	public JSONObject getInfoReleaseProgetto(@QueryParam("idPolarion") String idPolarion) throws ParseException {

		JSONObject obj = new JSONObject();
		obj.put("infoMev", JsonReleaseMev.getReleaseMevInfo(idPolarion));
		obj.put("infoProgettoSviluppo", JsonReleaseProgettoSviluppo.getReleaseProgettoSviluppoInfo(idPolarion));
		obj.put("infoAnomalia", JsonReleaseAnomalia.getReleaseDefectInfo(idPolarion));
		obj.put("infoDefect", JsonReleaseDefect.getReleaseDefectInfo(idPolarion));
		obj.put("infoDocumenti", JsonReleaseDocumenti.getReleaseDocumentiInfo(idPolarion));
		obj.put("infoGenerali", JsonReleaseInfoGeneral.getReleaseGeneralInfo(idPolarion));
		obj.put("infoTotDefect", JsonReleaseDefect.getReleaseDefectInfoTotal(idPolarion));
		obj.put("infoTotAnomalie", JsonReleaseAnomalia.getReleaseAnomalieInfoTotal(idPolarion));
		int size = JsonReleaseInfoGeneral.getSize();
		obj.put("numRows", size);

		if(size == 1){
			obj.put("infoStati", JsonReleaseStatus.getReleaseStatus(idPolarion));
		}

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}

	@GET
	@Path("/infoReleaseIT")
	@Produces("application/json")
	public JSONObject getInfoReleaseIt(@QueryParam("idPolarion") String idPolarion) {

		List<ReleaseIt> result = QueryReleaseIT.getInfoReleaseITByIDPolarion(idPolarion);
		JSONObject obj = new JSONObject();
		obj.put("numRows", result.size());
		obj.put("general", JsonReleaseITGeneralInfo.getReleaseITGeneralInfo(idPolarion));
		if (result.size() == 1) {
			obj.put("status", JsonReleaseITGeneralInfo.getReleaseITStatus(idPolarion));
			obj.put("task", JsonReleaseITGeneralInfo.getTaskIt(idPolarion));
			obj.put("testcase", JsonReleaseITGeneralInfo.getTestCaseByReleaseIT(idPolarion));
			obj.put("timing", JsonReleaseITGeneralInfo.getTimingByReleaseIT(idPolarion));
			obj.put("authors", JsonReleaseITGeneralInfo.getAuthorsByReleaseIT(idPolarion));
			obj.put("cl", JsonReleaseITGeneralInfo.getCheckListByReleaseIT(idPolarion));
		}

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}

	@GET
	@Path("/getArea")
	@Produces("application/json")
	public JSONObject getArea(@QueryParam("codiceArea") String codiceArea) {

		JSONObject obj = new JSONObject();
		obj.put("aree", JsonReleaseFiltri.getArea(codiceArea));

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}

	@GET
	@Path("/getRilasciApplicativo")
	@Produces("application/json")
	public JSONObject getRilasciApplicativo(@QueryParam("applicativo") String applicativo) {

		JSONObject obj = new JSONObject();
		obj.put("rilasciApplicativo", JsonReleaseFiltri.getRilasciApplicativo(applicativo));

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}

	@GET
	@Path("/getRilasciContesto")
	@Produces("application/json")
	public JSONObject getRilasciContesto(@QueryParam("contesto") String contesto) {

		JSONObject obj = new JSONObject();
		obj.put("rilasciContesto", JsonReleaseFiltri.getRilasciContesto(contesto));

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}
	
	@GET
	@Path("/getRilasciFromArea")
	@Produces("application/json")
	public JSONObject getRilasciFromArea(@QueryParam("codiceArea") String codiceArea) {

		JSONObject obj = new JSONObject();
		obj.put("rilasciAree", JsonReleaseFiltri.getRilasciFromArea(codiceArea));

		System.out.println("-----------JSON-----------");
		System.out.println(obj);
		System.out.println("--------------------------");

		return obj;
	}

}
