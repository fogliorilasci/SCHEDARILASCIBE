package controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import entities.ReleaseIt;

public class JsonReleaseStatus {

	private static SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public static JSONArray getReleaseStatusInfo(String param) throws ParseException{

		Object result = QueryInfoRelease.getCountRows(param);
		
		String resultInteger = String.valueOf(result);

		if (resultInteger.equals("1")) {
			JSONObject obj = new JSONObject();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Map<Object, Object> map = new LinkedHashMap<Object, Object>();
			map.put("data creazione", QueryInfoRelease.getSingleFieldFromTable("Release", "id_polarion", param, "dataCreazione"));
			List<Object[]> listStatusDate = QueryInfoRelease.getStatusNameAndDataUpdateFromReleaseHistory(param);
			List<Timestamp> listDateTimestamp = new ArrayList<Timestamp>();

			listDateTimestamp.add((Timestamp) QueryInfoRelease.getSingleFieldFromTable("Release", "id_polarion", param, "dataCreazione"));
			for(Object[] o : listStatusDate){
				listDateTimestamp.add((Timestamp) o[1]);
			}

			List<String> differenceTime = new ArrayList<String>();
			for(int i = 0; i < listDateTimestamp.size() - 1; i=i+1){
				differenceTime.add(CalculateTime.getTimeInDayHourMin(CalculateTime.getDifferenceLong(listDateTimestamp.get(i), listDateTimestamp.get(i+1))));
			}
			
			List<String> differenceTimeWorking = new ArrayList<String>();
			for(int i = 0; i < listDateTimestamp.size() - 1; i=i+1){
				differenceTimeWorking.add(CalculateTime.getTotalTimeWorking(listDateTimestamp.get(i), listDateTimestamp.get(i+1)));
			}

			JSONArray objArray = new JSONArray();
			int columnIndex = 0;

			Object[] firstElement = {"data creazione", QueryInfoRelease.getSingleFieldFromTable("Release", "id_polarion", param, "dataCreazione")};
			listStatusDate.add(0, firstElement);

			differenceTime.add(0, "0");
			differenceTimeWorking.add(0, "0");

			for(int i = 1; i<listStatusDate.size(); i++){
				objArray.add(columnIndex++, listStatusDate.get(i)[0]);
				objArray.add(columnIndex++, sdf2.format(new Date(((Timestamp)listStatusDate.get(i-1)[1]).getTime())));
				objArray.add(columnIndex++, sdf2.format(new Date(((Timestamp)listStatusDate.get(i)[1]).getTime())));
				objArray.add(columnIndex++, differenceTime.get(i));
				objArray.add(columnIndex++, differenceTimeWorking.get(i));
			}

			return objArray;
		}
		else{
			return null;
		}
	}
}
