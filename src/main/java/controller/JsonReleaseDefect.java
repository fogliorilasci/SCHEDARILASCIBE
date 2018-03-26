package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;

import entities.Defect;
import entities.DefectHistory;
import entities.ReleaseHistory;
import entities.ReleaseIt;

public class JsonReleaseDefect {
	
	private static int totaleDefect = 0;

	public static JSONArray getReleaseDefectInfo(String param){

		JSONArray objArray = new JSONArray();

		Object result = QueryInfoRelease.getCountRows(param);
		
		String resultInteger = String.valueOf(result);

		int sevCritica = 0;
		int sevAlta = 0;
		int sevMedia = 0;
		int sevBassa = 0;
		int prioHighest = 0;
		int prioHigh = 0;
		int prioMedium = 0;
		int prioLow = 0;
		int prioLowest = 0;

		int riaperti = 0;
		int invalidi = 0;
		int limitinoti = 0;
		int risoltiinit = 0;

		int tester = 0;
		int deploy = 0;
		int dba = 0;
		int cm = 0;

		Date dateReleaseInEse = null;
		List<Object> rh = QueryInfoRelease.getDataInizioFineRelease(param);
		if(!rh.isEmpty()){
		Date dateReleaseInIt = (Date) rh.get(0);
		dateReleaseInEse = (Date) rh.get(1);
		}
//		System.out.println("Data in IT: "+dateReleaseInIt);
//		System.out.println("Data in ESE: "+dateReleaseInEse);

		List<Object[]> detailsDefect = QueryInfoRelease.getDetailDefect(param);

		List<Defect> allDefect = QueryInfoRelease.getDefectFromId(param);
		for (Defect defect : allDefect) {

			if(defect.getSeverity() != null){
				if(defect.getSeverity().getPolarionName().equals("1")) sevCritica++;
				else if(defect.getSeverity().getPolarionName().equals("2")) sevAlta++;
				else if(defect.getSeverity().getPolarionName().equals("3")) sevMedia++;
				else if(defect.getSeverity().getPolarionName().equals("4")) sevBassa++;
			}
			else sevMedia++;

			if(defect.getPriority() != null){
				if(defect.getPriority().getPolarionName().equals("highest")) prioHighest++;
				else if(defect.getPriority().getPolarionName().equals("high")) prioHigh++;
				else if(defect.getPriority().getPolarionName().equals("medium")) prioMedium++;
				else if(defect.getPriority().getPolarionName().equals("low")) prioLow++;
				else if(defect.getPriority().getPolarionName().equals("lowest")) prioLowest++;
			}
			else prioMedium++;

			boolean flag = true;
			for (Object[] detailDefect : detailsDefect) {
				if(defect.getIdPolarion().equals(detailDefect[0])){
					if(detailDefect[1] != null){
						if(detailDefect[1].equals(42)) riaperti++;
						if(detailDefect[1].equals(44)) risoltiinit++;

						if(detailDefect[1].equals(11)){
							if(dateReleaseInEse != null)
							if(dateReleaseInEse.before((Date) detailDefect[4]) && flag == true){
								flag = false;
								limitinoti++;
							}
						}
					}
					if(detailDefect[2] != null){
						if(detailDefect[2].equals(10) || detailDefect[2].equals(11)) invalidi++; 
					}
				}
			}
		}
		
		Properties prop = new Properties();
		InputStream input = null;

		input = JsonReleaseDefect.class.getResourceAsStream("../users.properties");

		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<DefectHistory> allDefectAuthor = QueryInfoRelease.getAuthorDefectFromId(param);

		for (DefectHistory defectHistory : allDefectAuthor) {
			if(defectHistory.getUser() != null){
				if(prop.getProperty("tester").contains(defectHistory.getUser().getIdPolarion())) tester++;
				else if(prop.getProperty("deploy").contains(defectHistory.getUser().getIdPolarion())) deploy++;
				else if(prop.getProperty("dba").contains(defectHistory.getUser().getIdPolarion())) dba++;
				else if(prop.getProperty("cm").contains(defectHistory.getUser().getIdPolarion())) cm++;
			}
			else tester++;
		}

//		System.out.println("Limiti noti: "+limitinoti);
//		System.out.println("Invalidi: "+invalidi);
//		System.out.println("Riaperti: "+riaperti);
//		System.out.println("Risolti in It: "+risoltiinit);

		//		List<ReleaseIt> result = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);
		//
		//		int sevCritica = 0;
		//		int sevAlta = 0;
		//		int sevMedia = 0;
		//		int sevBassa = 0;
		//		int prioHighest = 0;
		//		int prioHigh = 0;
		//		int prioMedium = 0;
		//		int prioLow = 0;
		//		int prioLowest = 0;
		//
		//		List<Defect> allDefect = QueryInfoRelease.getDefectFromId(param);
		//		for (Defect defect : allDefect) {
		//
		//			if(defect.getSeverity().getPolarionName() != null){
		//				if(defect.getSeverity().getPolarionName().equals("1")) sevCritica++;
		//				else if(defect.getSeverity().getPolarionName().equals("2")) sevAlta++;
		//				else if(defect.getSeverity().getPolarionName().equals("3")) sevMedia++;
		//				else if(defect.getSeverity().getPolarionName().equals("4")) sevBassa++;
		//			}
		//			else sevMedia++;
		//
		//			if(defect.getPriority().getPolarionName() != null){
		//				if(defect.getPriority().getPolarionName().equals("highest")) prioHighest++;
		//				else if(defect.getPriority().getPolarionName().equals("high")) prioHigh++;
		//				else if(defect.getPriority().getPolarionName().equals("medium")) prioMedium++;
		//				else if(defect.getPriority().getPolarionName().equals("low")) prioLow++;
		//				else if(defect.getPriority().getPolarionName().equals("lowest")) prioLowest++;
		//			}
		//			else prioMedium++;
		//
		//		}
		//
		//		int tester = 0;
		//		int deploy = 0;
		//		int dba = 0;
		//		int cm = 0;
		//
		//		Properties prop = new Properties();
		//		InputStream input = null;
		//
		//		input = JsonReleaseDefect.class.getResourceAsStream("../users.properties");
		//
		//		try {
		//			prop.load(input);
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//
		//		List<DefectHistory> allDefectAuthor = QueryInfoRelease.getAuthorDefectFromId(param);
		//
		//		for (DefectHistory defectHistory : allDefectAuthor) {
		//			if(defectHistory.getUser() != null){
		//				if(prop.getProperty("tester").contains(defectHistory.getUser().getIdPolarion())) tester++;
		//				else if(prop.getProperty("deploy").contains(defectHistory.getUser().getIdPolarion())) deploy++;
		//				else if(prop.getProperty("dba").contains(defectHistory.getUser().getIdPolarion())) dba++;
		//				else if(prop.getProperty("cm").contains(defectHistory.getUser().getIdPolarion())) cm++;
		//			}
		//			else tester++;
		//		}
		//
		//		int limitiNoti = 0; 
		//		int risoltiInIt = 0;
		//		int riaperti = 0;
		//		int invalidi = 0;
		//
		// Se size = 1
		// objArray : [sevCritica, sevAlta, sevMedia, sevBassa, prioHighest, prioHigh, prioMedium, prioLow, prioLowest, limitiNoti, risoltiInIt, riaperti, invalidi, tester, deploy, dba, cm]
		if (resultInteger.equals("1")) {
			int columnIndex = 0;
			objArray.add(columnIndex++, sevCritica);
			objArray.add(columnIndex++, sevAlta);
			objArray.add(columnIndex++, sevMedia);
			objArray.add(columnIndex++, sevBassa);
			objArray.add(columnIndex++, prioHighest);
			objArray.add(columnIndex++, prioHigh);
			objArray.add(columnIndex++, prioMedium);
			objArray.add(columnIndex++, prioLow);
			objArray.add(columnIndex++, prioLowest);
			objArray.add(columnIndex++, limitinoti);
			objArray.add(columnIndex++, risoltiinit);
			objArray.add(columnIndex++, riaperti);
			objArray.add(columnIndex++, invalidi);
			objArray.add(columnIndex++, tester);
			objArray.add(columnIndex++, deploy);
			objArray.add(columnIndex++, dba);
			objArray.add(columnIndex++, cm);
		}
		// Se size > 1
		else{
			int columnIndex = 0;
			objArray.add(columnIndex++, sevCritica);
			objArray.add(columnIndex++, sevAlta);
			objArray.add(columnIndex++, sevMedia);
			objArray.add(columnIndex++, sevBassa);
			objArray.add(columnIndex++, prioHighest);
			objArray.add(columnIndex++, prioHigh);
			objArray.add(columnIndex++, prioMedium);
			objArray.add(columnIndex++, prioLow);
			objArray.add(columnIndex++, prioLowest);
			objArray.add(columnIndex++, tester);
			objArray.add(columnIndex++, deploy);
			objArray.add(columnIndex++, dba);
			objArray.add(columnIndex++, cm);
		}
		
		totaleDefect = allDefect.size();
		setTotaleDefect(totaleDefect);
		
		return objArray;
	}
	
	public static int getTotaleDefect() {
		return totaleDefect;
	}

	public static void setTotaleDefect(int totaleDefect) {
		JsonReleaseDefect.totaleDefect = totaleDefect;
	}

	public static Object getReleaseDefectInfoTotal(String param) {
		List<Defect> allDefect = QueryInfoRelease.getDefectFromId(param);
		return allDefect.size();
	}

}
