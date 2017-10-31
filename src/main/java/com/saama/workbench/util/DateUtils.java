package com.saama.workbench.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Utility class with factory methods for doing common operations with Java
 * dates. This class assumes that you are using a java.util.Calendar.
 */
public class DateUtils {
	private static final Logger log = Logger.getLogger(DateUtils.class);
	private static DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");

	/**
	 * Private constructor enforces non-instantiability.
	 */
	private DateUtils() {
	}

	/**
	 * Convenience method to get a current date
	 * 
	 * @return Calendar The Calendar object with current date-time instance
	 */
	public static Calendar getCurrCalendar() {
		Calendar calendar = Calendar.getInstance();
		return calendar;
	}

	/**
	 * Convenience method to get a current date
	 * 
	 * @return Calendar The Calendar object with current date-time instance
	 */
	public static Calendar getCurrDateInUTC() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		return calendar;
	}

	/**
	 * Converts UTC calendar object to locale specific one.
	 * 
	 * @param calendar
	 *            The date time in form of Calendar object
	 * @param localTimeZone
	 *            the string for the locale time zone e.g. America/Los_Angeles,
	 *            Japan
	 * @return Calendar The calendar object corresponding to the timeZone
	 * 
	 */
	public static Calendar getLocaleCal(Calendar utcCal, String localTimeZone) {
		Calendar localCal = Calendar.getInstance(TimeZone
				.getTimeZone(localTimeZone));
		localCal.setTimeInMillis(utcCal.getTimeInMillis());
		return localCal;
	}

	public static Date parseUTCDate(String dateStr, String pattern)
			throws ParseException {
		TimeZone utcZone = TimeZone.getTimeZone("UTC");
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(utcZone);
		return dateFormat.parse(dateStr);
	}

	public static Calendar getCalendar(String dateStr, String pattern)
			throws ParseException {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(parseUTCDate(dateStr, pattern));
		} catch (Exception e) {
			log.error("Exception in toCalendar method of DateUtils", e);
		}
		return c;
	}

	public static String getUpdateDate() {
		/*String calMon = "";
		int day, month, year;
		GregorianCalendar date = new GregorianCalendar();
		day = date.get(Calendar.DAY_OF_MONTH);
		month = date.get(Calendar.MONTH) + 1;
		year = date.get(Calendar.YEAR);

		calMon = "" + year + month;
		return "" + year + "-" + month + "-" + day;*/
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		java.util.Date date = calendar.getTime();
		return dateFormatter.format(date);
	}

	public static String getLastUpdatedDateTime() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		java.util.Date date = calendar.getTime();
		return dateFormatter.format(date);
	}

	public static Calendar toCalendar(String date) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(df1.parse(date));
		} catch (Exception e) {
			log.error("Exception in toCalendar method of DateUtils", e);
		}
		return c;
	}

	public static String getDateString(Calendar c) {
		if (c != null) {
			return df1.format(c.getTime());
		} else {
			return "Unavailable";
		}

	}

	public static Calendar getUTCToLocaleCal(long milliSeconds) {
		Calendar localCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		localCal.setTimeInMillis(milliSeconds);
		localCal.setTimeZone(TimeZone.getDefault());
		return localCal;
	}

	public static void removeTimestamp(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}

	public static String getCurrentMonth() {

		String calMon = "";
		int day, month, year;

		GregorianCalendar date = new GregorianCalendar();

		day = date.get(Calendar.DAY_OF_MONTH);
		month = date.get(Calendar.MONTH) + 1;
		year = date.get(Calendar.YEAR);

		if (month != 10 || month != 11 || month != 12) {
			calMon = "" + year + "0" + month;
		} else {
			calMon = "" + year + month;
		}

		return calMon;

	}

	public static String getDefaultDateRange(String dateRangeSeparator) {
		GregorianCalendar date = new GregorianCalendar();
		int year = date.get(Calendar.YEAR);
		if (dateRangeSeparator == null || dateRangeSeparator.length() < 1) {
			dateRangeSeparator = AppConstants.DATE_RANGE_SEPARATOR;
		}
		return year + "-01-01"
				+ (dateRangeSeparator != null ? dateRangeSeparator : " - ")
				+ year + "-12-01";
	}

	public static boolean isEmpty(String str) {
		return (str == null || (str.trim().equals("")));
	}

	public static String getDateRangeOfQuarter(String quarter, String year) {
		String result = null;
		if (year == null || year.isEmpty()) {
			year = new GregorianCalendar().get(Calendar.YEAR) + "";
		}
		if (quarter == null || quarter.isEmpty()) {
			result = year + "-01-01" + AppConstants.DATE_RANGE_SEPARATOR + year
					+ "-12-31";
			return result;
		}
		switch (quarter.toUpperCase()) {
		case "Q1":
			result = year + "-01-01" + AppConstants.DATE_RANGE_SEPARATOR + year
					+ "-03-31";
			break;
		case "Q2":
			result = year + "-04-01" + AppConstants.DATE_RANGE_SEPARATOR + year
					+ "-06-30";
			break;
		case "Q3":
			result = year + "-07-01" + AppConstants.DATE_RANGE_SEPARATOR + year
					+ "-09-30";
			break;
		case "Q4":
			result = year + "-10-01" + AppConstants.DATE_RANGE_SEPARATOR + year
					+ "-12-31";
			break;
		}
		return result;
	}

	public static List<String> getYearRange(String yearSelectRange) {
		List<String> yearList = new ArrayList<String>();

		if (yearSelectRange == null) {
			yearSelectRange = "-2:2";
		}
		GregorianCalendar date = new GregorianCalendar();
		int year = date.get(Calendar.YEAR);

		for (int i = Integer.parseInt(yearSelectRange.split(":")[0]); i <= Integer
				.parseInt(yearSelectRange.split(":")[1]); i++) {
			yearList.add((year + i) + "");
		}

		return yearList;
	}

	public static int getCurrentYear() {
		GregorianCalendar date = new GregorianCalendar();
		return date.get(Calendar.YEAR);
	}
	
	public static boolean isDateString(String val, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			if (val != null && val.trim().length() > 0) {
				sdf.parse(val.trim());
			}
			else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static Date convertToDate(String val, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			if (val != null && val.trim().length() > 0) {
				return sdf.parse(val.trim());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static String getCurrentQuarter() {
		GregorianCalendar date = new GregorianCalendar();
		int month = date.get(Calendar.MONTH);
		switch (month) {
		case 1:
		case 2:
		case 3:
			return "Q1";
		case 4:
		case 5:
		case 6:
			return "Q2";
		case 7:
		case 8:
		case 9:
			return "Q3";
		case 10:
		case 11:
		case 12:
			return "Q4";
		default:
			return "Q1";
		}
	}

}
