package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import entities.Release;

public class JsonReleaseFiltri {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static JSONArray getReleaseFiltri(String area, String contesto, String project){

		// Get area from AreeApplicativi.csv (seleziono da colonna B e restituisce colonna A)

		// Get project from project.csv (seleziono tutto e faccio il substring tra {} )

		JSONArray objArray = new JSONArray();
		List<Release> releaseFiltri = QueryInfoRelease.getReleaseFromFiltri(area, contesto, project);

		return objArray;
	}
	
	public static List<String> getArea(String areaColonnaB){
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
		List<String> aree = new ArrayList<String>();
		for (String string : lines) {
			String[] aP=string.split(";");
			if(aP[1].equals(areaColonnaB)) aree.add(aP[0]);
		}
		
		return aree;
	}

	public static JSONArray getRilasciApplicativo(String applicativo) {
		
		JSONArray objArray = new JSONArray();
		List<Release> releaseFiltri = QueryInfoRelease.getReleaseFromApplicativo(applicativo);
		List<String> result = new ArrayList<String>();
		for (Release release : releaseFiltri) {
//			result.add(release.getIdPolarion()+" - "+release.getTitolo());
			objArray.add(release.getIdPolarion());
			objArray.add(release.getTitolo());
			objArray.add(sdf.format(release.getDataCreazione()));
			objArray.add(sdf.format(release.getDataUpdate()));
		}
		
//		objArray.add(result);

		return objArray;
	}

	public static Object getRilasciContesto(String contesto) {
		JSONArray objArray = new JSONArray();
		List<Release> releaseFiltri = QueryInfoRelease.getReleaseFromContesto(contesto);
		List<String> result = new ArrayList<String>();
		for (Release release : releaseFiltri) {
//			result.add(release.getIdPolarion()+" - "+release.getTitolo());
			objArray.add(release.getIdPolarion());
			objArray.add(release.getTitolo());
			objArray.add(sdf.format(release.getDataCreazione()));
			objArray.add(sdf.format(release.getDataUpdate()));
		}
		
//		objArray.add(result);

		return objArray;
	}

}
