package controller;

import java.util.List;

import org.json.simple.JSONArray;

import entities.Defect;
import entities.ProgettoSviluppo;

public class JsonReleaseProgettoSviluppo {

public static JSONArray getReleaseProgettoSviluppoInfo(String param){
		
		JSONArray objArray = new JSONArray();
		
		int columnIndex = 0;
		List<Object[]> allProgettoSviluppo = QueryInfoRelease.getProgettoSviluppoFromId(param);
		for (Object[] progettoSviluppo : allProgettoSviluppo) {
			objArray.add(columnIndex++, progettoSviluppo[0]);
			objArray.add(columnIndex++, progettoSviluppo[1]);
			objArray.add(columnIndex++, progettoSviluppo[2]);
			
//			System.out.println(progettoSviluppo[0]+" "+progettoSviluppo[1]+" "+progettoSviluppo[2]);
			
		}
		
		return objArray;
	}

}
