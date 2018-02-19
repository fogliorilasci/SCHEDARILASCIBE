package controller;

import java.util.List;

import org.json.simple.JSONArray;

import entities.Anomalia;

public class JsonReleaseAnomalia {

	public static JSONArray getReleaseDefectInfo(String param){

		JSONArray objArray = new JSONArray();

		int sevCritica = 0;
		int sevAlta = 0;
		int sevMedia = 0;
		int sevBassa = 0;
		int prioHighest = 0;
		int prioHigh = 0;
		int prioMedium = 0;
		int prioLow = 0;
		int prioLowest = 0;

		List<Anomalia> allAnomalia = QueryInfoRelease.getAnomalieFromId(param);
		for (Anomalia anomalia : allAnomalia) {

			if(anomalia.getSeverity().getPolarionName() != null){
				if(anomalia.getSeverity().getPolarionName().equals("1")) sevCritica++;
				else if(anomalia.getSeverity().getPolarionName().equals("2")) sevAlta++;
				else if(anomalia.getSeverity().getPolarionName().equals("3")) sevMedia++;
				else if(anomalia.getSeverity().getPolarionName().equals("4")) sevBassa++;
				else sevMedia++;
			}
			else sevMedia++;

			if(anomalia.getPriority().getPolarionName() != null){
				if(anomalia.getPriority().getPolarionName().equals("highest")) prioHighest++;
				else if(anomalia.getPriority().getPolarionName().equals("high")) prioHigh++;
				else if(anomalia.getPriority().getPolarionName().equals("medium")) prioMedium++;
				else if(anomalia.getPriority().getPolarionName().equals("low")) prioLow++;
				else if(anomalia.getPriority().getPolarionName().equals("lowest")) prioLowest++;
				else prioMedium++;
			}
			else prioMedium++;

		}

		// objArray : [sevCritica, sevAlta, sevMedia, sevBassa, prioHighest, prioHigh, prioMedium, prioLow, prioLowest]
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

		return objArray;
	}

}
