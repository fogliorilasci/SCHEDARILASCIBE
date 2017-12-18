package servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.hibernate.Session;

import com.google.gson.Gson;

import basic.HibernateUtil;
import entities.User;
import entities.Csv;

@Path("/")
public class ServiceProva {

	@GET
	@Path("/user")
	@Produces("application/json")
	public List<String> getProductInJSON() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		CriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
		cq.from(User.class);
		List<User> listUser = session.createQuery(cq).getResultList();
		List<String> a = new ArrayList<String>();
		
		for (User utente : listUser) {
			a.add(utente.getIdPolarion());
		}
		return a;
	}

	@GET
	@Path("/csv")
	@Produces("application/json")
	public List<Csv> getCsv() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		CriteriaQuery<Csv> cq = session.getCriteriaBuilder().createQuery(Csv.class);
		cq.from(Csv.class);
		List<Csv> listCsv = session.createQuery(cq).getResultList();
		List<String> a = new ArrayList<String>();
	
		return listCsv;
	}
	
	@GET
	@Path("/csvRelease")
	@Produces("application/json")
	public Csv getCsvRelease(@QueryParam("idPolarion") String idPolarion) {
		Csv csv = new Csv();
		csv.setIdPolarion(idPolarion);
		return csv;
	}
	
//	@GET
//	@Path("/csvRelease")
//	@Produces("application/json")
//	public Response getCsvRelease(@QueryParam("idPolarion") String idPolarion) {
//
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		CriteriaQuery<Csv> cq = session.getCriteriaBuilder().createQuery(Csv.class);
//		cq.from(Csv.class);
//		
//		Set<Parameter<?>> listParam = session.createQuery(cq).getParameters();
//		for(Parameter<?> p : listParam){
//			System.out.println("A: "+p);
//		}
//		
//		List<Csv> listCsv = session.createQuery(cq).setParameter("idPolarion", idPolarion).getResultList();
//		for(Csv csv : listCsv){
//			System.out.println(csv.getColonnaA());
//			System.out.println(csv.getColonnaB());
//			System.out.println(csv.getColonnaC());
//			System.out.println(csv.getColonnaD());
//			System.out.println(csv.getColonnaE());
//			System.out.println(csv.getColonnaF());
//			System.out.println(csv.getColonnaG());
//		}
//		return Response.status(201).entity(listCsv).build();
//	}

	//	@POST
	//	@Path("/post")
	//	@Consumes("application/json")
	//	public Response createProductInJSON(Product product) {
	//
	//		String result = "Product created : " + product;
	//		return Response.status(201).entity(result).build();
	//		
	//	}
	//	
	
	
	@GET
	@Path("/getuser")
	@Produces("application/json")
	public List<String> getUser() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		CriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
		cq.from(User.class);
		List<User> listUser = session.createQuery(cq).getResultList();
		List<String> a = new ArrayList<String>();
		
		for (User utente : listUser) {
			a.add(utente.getIdPolarion());
		}
		
		
		 Gson g = new Gson();
		 String jsonString = g.toJson(a); 
		
		
		return a;
	}	  
	
}