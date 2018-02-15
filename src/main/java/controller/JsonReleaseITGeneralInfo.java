package controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import entities.ReleaseIt;

public class JsonReleaseITGeneralInfo {
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	@SuppressWarnings("unchecked")
	public static JSONObject getReleaseGeneralInfo(String param) {
		JSONObject obj = new JSONObject();
		List<ReleaseIt> result = QueryReleaseIT.getInfoReleaseITByIDPolarion(param);
		obj.put("numRows", result.size());

		long totDocumenti = 0;
		long totDefect = 0;
		long totAnomalie = 0;
		long totProgettoSviluppo = 0;
		long totSupporto = 0;
		long totMev = 0;

		for (ReleaseIt r : result) {
			if (result.size() == 1) {
				JSONArray objArray = new JSONArray();
				int columnIndex = 0;
				objArray.add(columnIndex++, param);
				String project = QueryReleaseIT.getProjectByReleaseIT(r.getIdPolarion());
				if (project != null) {
					objArray.add(columnIndex++, project);
					objArray.add(columnIndex++, project.substring(project.indexOf("{") + 1, project.indexOf("}")));
				} else {
					objArray.add(columnIndex++, "");
					objArray.add(columnIndex++, "");
				}
				objArray.add(columnIndex++, r.getRepository());
				objArray.add(columnIndex++, r.getTitolo());
				objArray.add(columnIndex++, r.getPriority().getNome());
				if(r.getDataInizio() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataInizio()));
				else
					objArray.add(columnIndex++, "");
				
				if(r.getDataFine() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataFine()));
				else
					objArray.add(columnIndex++, "");
				
				if(r.getDataCreazione() != null)
					objArray.add(columnIndex++, sdf.format(r.getDataCreazione()));
				else
					objArray.add(columnIndex++, "");
				
				objArray.add(columnIndex++, "AUTORE");
				
				objArray.add(columnIndex++, QueryReleaseIT.getReleaseByReleaseIT(r.getIdPolarion()));
				
				obj.put("infoGenerali", objArray);
			} else {
				JSONArray objArray = new JSONArray();
				int columnIndex = 0;

				totDocumenti += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "documento");
				totDefect += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "defect");
				totAnomalie += QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "anomalia");
				totProgettoSviluppo += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"progetto_sviluppo");
				totSupporto += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(),
						"support");
				totMev += (Long) QueryReleaseIT.getCountFromLinkedItemInnerJoinATable(r.getIdPolarion(), "mev");

				objArray.add(columnIndex++, (Object) totDocumenti);
				objArray.add(columnIndex++, (Object) totDefect);
				objArray.add(columnIndex++, (Object) totAnomalie);
				objArray.add(columnIndex++, (Object) totProgettoSviluppo);
				objArray.add(columnIndex++, (Object) totSupporto);
				objArray.add(columnIndex++, (Object) totMev);
				obj.put("infoGenerali", objArray);
			}
		}

		return obj;
	}

}
