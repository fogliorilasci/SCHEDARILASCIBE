package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Time {

	private static ArrayList<LocalDate> holidays = new ArrayList<LocalDate>();

	static {
		for (int year = 2015; year < 2020; year++) {
			holidays.add(LocalDate.of(year, 1, 1)); // Capodanno
			holidays.add(LocalDate.of(year, 1, 6)); // Epifania
			holidays.add(LocalDate.of(year, 4, 25)); // Liberazione
			holidays.add(LocalDate.of(year, 5, 1)); // Festa del Lavoro
			holidays.add(LocalDate.of(year, 6, 2)); // Festa della Repubblica
			holidays.add(LocalDate.of(year, 8, 15)); // Ferragosto
			holidays.add(LocalDate.of(year, 11, 1)); // Ognissanti
			holidays.add(LocalDate.of(year, 12, 8)); // Immacolata Concezione
			holidays.add(LocalDate.of(year, 12, 25)); // Natale
			holidays.add(LocalDate.of(year, 12, 26)); // Santo Stefano
		}

		// Pasqua e Pasquetta
		holidays.add(LocalDate.of(2016, 3, 27)); // Pasqua
		holidays.add(LocalDate.of(2016, 3, 28)); // Pasquetta
		holidays.add(LocalDate.of(2017, 4, 16)); // Pasqua
		holidays.add(LocalDate.of(2017, 4, 17)); // Pasquetta
		holidays.add(LocalDate.of(2018, 4, 1)); // Pasqua
		holidays.add(LocalDate.of(2018, 4, 2)); // Pasquetta
	}

	public Time(){

	}

	static private boolean isHoliday(LocalDate date) {
		for (LocalDate holiday: Time.holidays) {
			if (date.equals(holiday))
				return true;
		}

		return false;
	}

	static private boolean isWeekendDay(LocalDate date) {
		if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
			return true;
		return false;
	}

	static private boolean isWorkingDay(LocalDate date) {
		return !Time.isWeekendDay(date);
	}

	/* Static methods */
	public static int numberOfDaysInPeriod(LocalDate startDate, LocalDate endDate) {
		int count = 0;
		for (LocalDate date = startDate;  date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)){
			count++;
		}

		return count;
	}

	public static int numberOfWeekendDaysInPeriod(LocalDate startDate, LocalDate endDate) {
		int count = 0;
		for (LocalDate date = startDate;  date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)){
			if (Time.isWeekendDay(date))
				count++;
		}

		return count;
	}

	public static int numberOfWorkingDaysInPeriod(LocalDate startDate, LocalDate endDate) {
		int count = 0;
		for (LocalDate date = startDate;  date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) { // inclusive period
			if (Time.isWorkingDay(date) || Time.isHoliday(date))
				count++;
		}

		return count;
	}

	public static int numberOfWorkingDaysInPeriod(Date startDate, Date endDate) {
		return Time.numberOfWorkingDaysInPeriod(Time.getLocalDateFromDate(startDate), Time.getLocalDateFromDate(endDate));
	}

	public static Double getAverage(List<Double> listDouble){

		Double average = new Double(0);
		for(Double d : listDouble){
			average = average + d;
		}

		average = average / listDouble.size();

		return average;

	}

	public static String numberOfWorkingHoursInPeriod(Date startDate, Date endDate) {
		Calendar calendar = Calendar.getInstance();

		// ** Get time of full working day **
		calendar.setTime(startDate);
		LocalDate start = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
		calendar.setTime(endDate);
		LocalDate end = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

		int hours = 0, minutes = 0;

		// Same day
		if (start.isEqual(end)) {
			// Check if start time is between 13:00 and 14:00
			calendar.setTime(startDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) == 13) {
				calendar.setTime(endDate);
				if (calendar.get(Calendar.HOUR_OF_DAY) == 13) {
					long m = ((endDate.getTime() - startDate.getTime()) / (60 * 1000)) % 60;
					if (m < 10) {
						return "0:10";
					} else {
						return "0:" + m;
					}
				}
				calendar.setTime(startDate);

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 

				String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
				if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
					day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

				String month = "" + (calendar.get(Calendar.MONTH) + 1);
				if ((calendar.get(Calendar.MONTH) + 1) < 10)
					month = "0" + (calendar.get(Calendar.MONTH) + 1);	

				String year = "" + calendar.get(Calendar.YEAR);

				try {
					startDate = sdf.parse(day + "/" + month + "/" + year + " 14:00");
				} catch (ParseException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}

			long diff = endDate.getTime() - startDate.getTime();
			long h = diff / (60 * 60 * 1000);
			long m = diff / (60 * 1000) % 60;

			// Remove 1 hour for lunch break
			calendar.setTime(startDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) < 13) {
				calendar.setTime(endDate);
				if (calendar.get(Calendar.HOUR_OF_DAY) >= 14) {
					h--;
				}
			}

			if (h == 0 && m < 10)
				return "0:10";

			return h + ":" + m;
		}

		// 	** Consecutive days **
		if (start.plusDays(1).isEqual(end)) {

			// Get first day time
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
			calendar.setTime(startDate);

			// Check if start time is between 13:00 and 14:00
			calendar.setTime(startDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) == 13) {
				String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
				if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
					day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

				String month = "" + (calendar.get(Calendar.MONTH) + 1);
				if ((calendar.get(Calendar.MONTH) + 1) < 10)
					month = "0" + (calendar.get(Calendar.MONTH) + 1);	

				String year = "" + calendar.get(Calendar.YEAR);

				try {
					startDate = sdf.parse(day + "/" + month + "/" + year + " 14:00");
				} catch (ParseException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}

			String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
			if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
				day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

			String month = "" + (calendar.get(Calendar.MONTH) + 1);
			if ((calendar.get(Calendar.MONTH) + 1) < 10)
				month = "0" + (calendar.get(Calendar.MONTH) + 1);	

			String year = "" + calendar.get(Calendar.YEAR);

			try {
				Date endDay = sdf.parse(day + "/" + month + "/" + year + " 18:00");

				long diff = endDay.getTime() - startDate.getTime();
				hours = (int) (diff / (60 * 60 * 1000));
				minutes = (int) (diff / (60 * 1000) % 60);

				// Remove 1 hour for lunch break
				calendar.setTime(startDate);
				if (calendar.get(Calendar.HOUR_OF_DAY) < 13) {
					calendar.setTime(endDate);
					if (calendar.get(Calendar.HOUR_OF_DAY) >= 14) {
						hours--;
					}
				}

				// Check if starting date is after 18:00
				if (calendar.get(Calendar.HOUR_OF_DAY) >= 18) {
					hours = minutes = 0;
				}
			} catch (ParseException e) {
				System.err.println(e);
				e.printStackTrace();
			}

			// Get second day time 

			// Check if end time is before 9:00
			calendar.setTime(endDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) < 9) {
				day = "" + calendar.get(Calendar.DAY_OF_MONTH);
				if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
					day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

				month = "" + (calendar.get(Calendar.MONTH) + 1);
				if ((calendar.get(Calendar.MONTH) + 1) < 10)
					month = "0" + (calendar.get(Calendar.MONTH) + 1);	

				year = "" + calendar.get(Calendar.YEAR);

				try {
					endDate = sdf.parse(day + "/" + month + "/" + year + " 9:00");
				} catch (ParseException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}

			calendar.setTime(endDate);

			day = "" + calendar.get(Calendar.DAY_OF_MONTH);
			if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
				day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

			month = "" + (calendar.get(Calendar.MONTH) + 1);
			if ((calendar.get(Calendar.MONTH) + 1) < 10)
				month = "0" + (calendar.get(Calendar.MONTH) + 1);	

			year = "" + calendar.get(Calendar.YEAR);

			try {
				Date startDay = sdf.parse(day + "/" + month + "/" + year + " 9:00");

				long diff = endDate.getTime() - startDay.getTime();
				hours += (int) (diff / (60 * 60 * 1000));
				minutes += (int) (diff / (60 * 1000) % 60);

				// Remove 1 hour for lunch break
				calendar.setTime(startDay);
				if (calendar.get(Calendar.HOUR_OF_DAY) < 13) {
					calendar.setTime(endDate);
					if (calendar.get(Calendar.HOUR_OF_DAY) >= 14) {
						hours--;
					}
				}
			} catch (ParseException e) {
				System.err.println(e);
				e.printStackTrace();
			}

			return hours + ":" + minutes;
		}

		// ** General case **
		int totalHours = (Time.numberOfDaysInPeriod(start, end) - 2) * 8;

		// Get first day time
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
		calendar.setTime(startDate);

		// Check if start time is between 13:00 and 14:00
		calendar.setTime(startDate);
		if (calendar.get(Calendar.HOUR_OF_DAY) == 13) {
			String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
			if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
				day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

			String month = "" + (calendar.get(Calendar.MONTH) + 1);
			if ((calendar.get(Calendar.MONTH) + 1) < 10)
				month = "0" + (calendar.get(Calendar.MONTH) + 1);	

			String year = "" + calendar.get(Calendar.YEAR);

			try {
				startDate = sdf.parse(day + "/" + month + "/" + year + " 14:00");
			} catch (ParseException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}

		String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

		String month = "" + (calendar.get(Calendar.MONTH) + 1);
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			month = "0" + (calendar.get(Calendar.MONTH) + 1);	

		String year = "" + calendar.get(Calendar.YEAR);

		try {
			Date endDay = sdf.parse(day + "/" + month + "/" + year + " 18:00");

			long diff = endDay.getTime() - startDate.getTime();
			hours = (int) (diff / (60 * 60 * 1000));
			minutes = (int) (diff / (60 * 1000) % 60);

			// Remove 1 hour for lunch break
			calendar.setTime(startDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) < 13) {
				calendar.setTime(endDate);
				if (calendar.get(Calendar.HOUR_OF_DAY) >= 14) {
					hours--;
				}
			}

			// Check if starting date is after 18:00
			if (calendar.get(Calendar.HOUR_OF_DAY) >= 18) {
				hours = minutes = 0;
			}
		} catch (ParseException e) {
			System.err.println(e);
			e.printStackTrace();
		}

		// Get second day time 
		calendar.setTime(endDate);

		day = "" + calendar.get(Calendar.DAY_OF_MONTH);
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			day = "0" + calendar.get(Calendar.DAY_OF_MONTH);	

		month = "" + (calendar.get(Calendar.MONTH) + 1);
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			month = "0" + (calendar.get(Calendar.MONTH) + 1);	

		year = "" + calendar.get(Calendar.YEAR);

		try {
			Date startDay = sdf.parse(day + "/" + month + "/" + year + " 9:00");

			long diff = endDate.getTime() - startDay.getTime();
			if (diff > 0) {
				hours += (int) (diff / (60 * 60 * 1000));
				minutes += (int) (diff / (60 * 1000) % 60);

				// Remove 1 hour for lunch break
				calendar.setTime(startDay);
				if (calendar.get(Calendar.HOUR_OF_DAY) < 13) {
					calendar.setTime(endDate);
					if (calendar.get(Calendar.HOUR_OF_DAY) >= 14) {
						hours--;
					}
				}
			}
		} catch (ParseException e) {
			System.err.println(e);
			e.printStackTrace();
		}

		totalHours += hours;

		if (totalHours < 0 || minutes < 0) {
			System.err.println("[ERROR] Time.numberOfWorkingHoursInPeriod with starting date <" + startDate + "> and ending date <" + endDate + ">");
		}

		return totalHours + ":" + minutes;
	}

	/* Utility Static Methods */
	public static LocalDate getLocalDateFromDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static Date getDateFromLocalDate(LocalDate localDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Date date = null;
		try {
			String month = ""; 
			int mm = localDate.getMonthValue();
			if (mm < 10)
				month = "0" + mm;
			else
				month = "" + mm;

			String day = "";
			int dd = localDate.getDayOfMonth();
			if (dd < 10)
				day = "0" + dd;
			else
				day = "" + dd;


			date = sdf.parse(day + "/" + month + "/" + localDate.getDayOfYear());
		} catch (Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}

		return date;
	}

}
