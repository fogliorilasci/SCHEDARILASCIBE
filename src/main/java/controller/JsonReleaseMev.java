package controller;

import java.util.List;

import org.json.simple.JSONArray;

public class JsonReleaseMev {

public static JSONArray getReleaseMevInfo(String param){
		
		JSONArray objArray = new JSONArray();

		int columnIndex = 0;
		List<Object[]> allMev = QueryInfoRelease.getMevFromId(param);
		for (Object[] mev : allMev) {
			objArray.add(columnIndex++, mev[0]);
			objArray.add(columnIndex++, mev[1]);
			objArray.add(columnIndex++, mev[2]);
			
//			System.out.println(mev[0]+" "+mev[1]+" "+mev[2]);
			
		}
		
		return objArray;
	}

}
