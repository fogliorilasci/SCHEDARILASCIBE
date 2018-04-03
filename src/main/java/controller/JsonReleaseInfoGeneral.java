package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import entities.Release;

public class JsonReleaseInfoGeneral {
	
	private static int size = 0;

	public static JSONArray getReleaseGeneralInfo(String param) {
		JSONObject obj = new JSONObject();
		JSONArray objArray = new JSONArray();
		List<Release> result = QueryInfoRelease.getInfoReleaseByIDPolarion(param);
		
		size = result.size();
		setSize(size);
		
		// obj.put("numRows", result.get(0));
		// obj.put("countDoc", UtilityInfoRelease.getCountFromTable("Testcase",
		// "cod_status", "13"));

		long totDocumenti = 0;
		long totDefect = 0;
		long totAnomalie = 0;
		long totProgettoSviluppo = 0;
		long totSupporto = 0;
		long totMev = 0;

		ClassLoader classLoader = JsonReleaseInfoGeneral.class.getClassLoader();
		File file = new File(classLoader.getResource("AreeApplicativi.csv").getFile());
		String CSV_FILE_PATH = file.getAbsolutePath();

		List<String> lines = new ArrayList<String>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new java.io.FileReader(CSV_FILE_PATH));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String,String> areeApplicativi=new HashMap<String, String>();
		for (String string : lines) {
			String[] aP=string.split(";");
			areeApplicativi.put(aP[0], aP[1]);
		}

		String sigla = null;
		String area = null;
		if(param.contains("-")){
			sigla = param.substring(0, param.indexOf("-")).toUpperCase();
			area = areeApplicativi.get(sigla);
		}
		else area = areeApplicativi.get(sigla);

		for (Release r : result) {
			if (result.size() == 1) {
				int columnIndex = 0;
				objArray.add(columnIndex++, param);
				if (r.getProject() != null) {
					objArray.add(columnIndex++, r.getProject().getNome());
					objArray.add(columnIndex++, r.getProject().getNome().substring(
							r.getProject().getNome().indexOf("{") + 1, r.getProject().getNome().indexOf("}")));
				} else {
					objArray.add(columnIndex++, "");
					objArray.add(columnIndex++, "");
				}
				objArray.add(columnIndex++, r.getVersione());
				objArray.add(columnIndex++, r.getLink());
				objArray.add(columnIndex++, r.getRepository());
				objArray.add(columnIndex++, area);
				
				objArray.add(columnIndex++, JsonReleaseProgettoSviluppo.getTotaleProgettoSviluppo());
				objArray.add(columnIndex++, JsonReleaseAnomalia.getTotaleAnomalie());
				objArray.add(columnIndex++, JsonReleaseDefect.getTotaleDefect());		
				objArray.add(columnIndex++, JsonReleaseDocumenti.getTotaleDocumenti());
				
//				obj.put("infoGenerali", objArray);
			}
			else{
				int columnIndex = 0;

				totDocumenti += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "Documento");
				totDefect += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "Defect");
				totAnomalie += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "Anomalia");
				totProgettoSviluppo += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "ProgettoSviluppo");
				totSupporto += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "Support");
				totMev += (Long) QueryInfoRelease.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "Mev");

				objArray.add(columnIndex++, (Object) totDocumenti);
				objArray.add(columnIndex++, (Object) totDefect);
				objArray.add(columnIndex++, (Object) totAnomalie);
				objArray.add(columnIndex++, (Object) totProgettoSviluppo);
				objArray.add(columnIndex++, (Object) totSupporto);
				objArray.add(columnIndex++, (Object) totMev);
//				obj.put("infoGenerali", objArray);
			}

		}
		return objArray;
	}

	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		JsonReleaseInfoGeneral.size = size;
	}

	public static Object getIdReleaseIt(String idPolarion) {
		// TODO Auto-generated method stub
		return QueryInfoRelease.getIdReleaseIt(idPolarion);
	}


	//	public static String getCodArea(String codProdotto) {
	//
	//		ClassLoader classLoader = getClass().getClassLoader();
	//		File file = new File(classLoader.getResource("../AreeApplicativi.csv").getFile());
	//		String CSV_FILE_PATH = file.getAbsolutePath();
	//
	//		List<String> lines = new ArrayList<String>();
	//		BufferedReader br = null;
	//		String line = "";
	//		try {
	//			br = new BufferedReader(new java.io.FileReader(CSV_FILE_PATH));
	//			while ((line = br.readLine()) != null) {
	//				lines.add(line);
	//			}
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} finally {
	//			if (br != null) {
	//				try {
	//					br.close();
	//				} catch (IOException e) {
	//					e.printStackTrace();
	//				}
	//			}
	//		}
	//		Map<String,String> areeProdotti=new HashMap<String, String>();
	//		for (String string : lines) {
	//			String[] aP=string.split(";");
	//			areeProdotti.put(aP[0], aP[1]);
	//		}
	//		return null;
	//	}
}
