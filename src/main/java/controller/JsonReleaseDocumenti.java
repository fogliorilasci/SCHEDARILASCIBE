package controller;

import org.json.simple.JSONArray;

public class JsonReleaseDocumenti {

	public static JSONArray getReleaseDocumentiInfo(String param){

		// infoDocumenti=SIS, RdD, SFSW, PTS, STSW, CKS, RNS, RTS, RDU

		JSONArray objArray = new JSONArray();

		Object totDocumentiA = QueryInfoRelease.getCountFromDocument(param, "6");
		Object totDocumentiB = QueryInfoRelease.getCountFromDocument(param, "11");
		Object totDocumentiC = QueryInfoRelease.getCountFromDocument(param, "4");
		Object totDocumentiD = QueryInfoRelease.getCountFromDocument(param, "2");
		Object totDocumentiE = QueryInfoRelease.getCountFromDocument(param, "10");
		Object totDocumentiF = QueryInfoRelease.getCountFromDocument(param, "8");
		Object totDocumentiG = QueryInfoRelease.getCountFromDocument(param, "12");
		Object totDocumentiH = QueryInfoRelease.getCountFromDocument(param, "1");
		Object totDocumentiI = QueryInfoRelease.getCountFromDocument(param, "7");

		int columnIndex = 0;
		objArray.add(columnIndex++, totDocumentiA);
		objArray.add(columnIndex++, totDocumentiB);
		objArray.add(columnIndex++, totDocumentiC);
		objArray.add(columnIndex++, totDocumentiD);
		objArray.add(columnIndex++, totDocumentiE);
		objArray.add(columnIndex++, totDocumentiF);
		objArray.add(columnIndex++, totDocumentiG);
		objArray.add(columnIndex++, totDocumentiH);
		objArray.add(columnIndex++, totDocumentiI);

		return objArray;
	}
}
