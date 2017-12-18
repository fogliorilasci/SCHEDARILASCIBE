package controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalculateTime {

	public static List<Object> listFiltrata = new ArrayList<Object>();

	public CalculateTime(){

	}

	public static String getTimeInStatus(List<Object[]> list, String status, String statusName) {
		long diff = 0;
		for (int i = 0; i < list.size(); i++) {
			if (("" + list.get(i)[0]).equals(status)) {
				if(!list.get(i)[0].equals(list.get(i+1)[0])){
					listFiltrata.add(list.get(i)[1]);
					listFiltrata.add(list.get(i + 1)[1]);
					diff += getDifferenceLong((Date) list.get(i)[1], (Date) list.get(i + 1)[1]);
				}
			}
		}
		return getDifferenceTime(diff, statusName);
	}

	public static long getDifferenceLong(Date dt1, Date dt2) {
		return dt2.getTime() - dt1.getTime();

	}

	public static String getDifferenceTime(long diff, String statusName) {
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		int diffInDays = (int) ((diff) / (1000 * 60 * 60 * 24));

		if (diffInDays > 0) {
			diffHours = diffHours % 24;
			if (diffHours > 0) {
				System.out.println("Time in status "+statusName+" : " + diffInDays + " days, " + diffHours
						+ " hours and " + diffMinutes + " minutes.");
				return "Time in status  "+statusName+" : " + diffInDays + " days, " + diffHours + " hours and "
				+ diffMinutes + " minutes.";
			} else
				System.out.println("Time in status  "+statusName+" : " + diffMinutes + " minutes.");
			return "Time in status "+statusName+" : " + diffMinutes + " minutes.";
		} else{
			if (diffHours > 0) {
				System.out.println("Time in status "+statusName+" : " + diffInDays + " days, " + diffHours
						+ " hours and " + diffMinutes + " minutes.");
				return "Time in status "+statusName+" : " + diffInDays + " days, " + diffHours + " hours and "
				+ diffMinutes + " minutes.";
			} else
				System.out.println("Time in status  "+statusName+" : " + diffMinutes + " minutes.");
			return "Time in status "+statusName+" : " + diffMinutes + " minutes.";
		}
	}

	// CALCOLA IL TEMPO EFFETTIVO DEL TIMESPENT
	public static Double getHours(String str) {
		Integer day = new Integer(0);
		if(str.indexOf(('d')) > 0) {
			day = Integer.parseInt(str.substring(0,str.indexOf('d')));
		}

		Double hours = new Double(0);
		int inizio = 0;
		int fine = 0;
		if(str.indexOf((' ')) > 0) {
			inizio = str.indexOf((' ')) + 1;
		}
		if(str.indexOf(('h')) > 0) {
			fine = str.indexOf(('h'));
			str = str.substring(inizio,fine);
			if(str.indexOf(('/')) > 0) {
				int slash = str.indexOf(('/'));
				int numeratore = Integer.parseInt(str.substring(0,slash));
				int denominatore = Integer.parseInt(str.substring(slash+1));
				hours = (Double) (new Double(numeratore)/new Double(denominatore));
			} else {
				hours = (Double) (new Double(str));
			}
		}
		hours = hours+(day*8);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.parseDouble(df.format(hours).replace(',', '.'));
	}

	// RETURN PERCENTAGE FROM MINUTES
	public static Double getPercentageFromMinute(String hours) {
		Double ore = Double.parseDouble(hours.substring(0, hours.indexOf(':')));
		Double minuti = Double.parseDouble(hours.substring(hours.indexOf(':')+1));
		return Double.parseDouble((new DecimalFormat(".##").format(((ore*60) + minuti)/(new Double(60))).replace(',', '.')));
	}

	// CONVERTE I MINUTI IN PERCENTUALE
	public static String convertFromMinutesToPercentage(String str) {
		String[] numbers = str.split(":");
		return numbers[0] + "." + ((int) (Integer.parseInt(numbers[1]) * 1.666));
	}

	public static List<Object> getListFiltrata() {
		return listFiltrata;
	}

	public static void setListFiltrata(List<Object> listFiltrata) {
		CalculateTime.listFiltrata = listFiltrata;
	}

}
