/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2020 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.up.gtfs2kml;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Gtfs2KmlUtils {
	private static final Logger LOG = Logger.getLogger(Gtfs2KmlUtils.class);

	static final String FILE_CALENDAR = "calendar.txt";
	static final String FILE_CALENDAR_DATES = "calendar_dates.txt";
	static final String FILE_ROUTES = "routes.txt";
	static final String FILE_STOPS = "stops.txt";
	static final String FILE_STOP_TIMES = "stop_times.txt";
	static final String FILE_TRIPS = "trips.txt";
	static final String FILE_OUTPUT = "gtfs.kml";

//	static final String URL_BUS_ICON = "https://www.mbus.co.za/media/widgetkit/Metrobus-Header-Icons-5-5e20db1a8062f871efe19de79fade529.png";
	static final String URL_BUS_ICON = "http://ie-gtfs.up.ac.za/busIcon.png";


	static final String FIELD_CALENDAR_MON = "monday";
	static final String FIELD_CALENDAR_TUE = "tuesday";
	static final String FIELD_CALENDAR_WED = "wednesday";
	static final String FIELD_CALENDAR_THU = "thursday";
	static final String FIELD_CALENDAR_FRI = "friday";
	static final String FIELD_CALENDAR_SAT = "saturday";
	static final String FIELD_CALENDAR_SUN = "sunday";
	static final String FIELD_CALENDAR_START_DATE = "start_date";
	static final String FIELD_CALENDAR_END_DATE = "end_date";
	static final String FIELD_CALENDAR_DATES_DATE = "date";
	static final String FIELD_CALENDAR_DATES_EXCEPTION_TYPE = "exception_type";
	static final String FIELD_ROUTE_ID = "route_id";
	static final String FIELD_ROUTE_SHORT_NAME = "route_short_name";
	static final String FIELD_ROUTE_LONG_NAME = "route_long_name";
	static final String FIELD_ROUTE_DESCRIPTION = "route_desc";
	static final String FIELD_SERVICE_ID = "service_id";
	static final String FIELD_STOP_ID = "stop_id";
	static final String FIELD_STOP_NAME = "stop_name";
	static final String FIELD_STOP_DESCRIPTION = "stop_desc";
	static final String FIELD_STOP_DEPARTURE_TIME = "departure_time";
	static final String FIELD_STOP_LON = "stop_lon";
	static final String FIELD_STOP_LAT = "stop_lat";
	static final String FIELD_TRIP_ID = "trip_id";

	static final int FIELD_CALENDAR_DATE_SERVICE_ID = 0;
	static final int FIELD_CALENDAR_DATE_DATE = 1;
	static final int FIELD_CALENDAR_DATE_EXCEPTION = 2;
//	static final int FIELD_ROUTES_ID = 1;
//	static final int FIELD_ROUTES_SHORT_NAME = 2;
//	static final int FIELD_ROUTES_LONG_NAME = 3;
//	static final int FIELD_ROUTES_DESCRIPTION = 4;
	static final int FIELD_STOP_TIMES_TRIP_ID = 0;
	static final int FIELD_STOP_TIMES_DEPARTURE = 2;
	static final int FIELD_STOP_TIMES_STOP_ID = 3;
	static final int FIELD_TRIPS_ROUTE_ID = 0;
	static final int FIELD_TRIPS_TRIP_ID = 1;
	static final int FIELD_TRIPS_SERVICE_ID = 9;

	static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Africa/Johannesburg");

	public static String getOutputFilename(File folder) {
		String folderName = folder.getAbsolutePath();
		folderName += folderName.endsWith("/") ? "" : "/";
		return folderName + FILE_OUTPUT;
	}

	static boolean isInDateRange(GregorianCalendar date,
								 GregorianCalendar rangeStart,
								 GregorianCalendar rangeEnd){
		return date.getTimeInMillis() >= rangeStart.getTimeInMillis() &
				date.getTimeInMillis() <= rangeEnd.getTimeInMillis();
	}

	/**
	 * Converts a {@link String} into a {@link GregorianCalendar}.
	 * @param date in the format YYYYMMDD;
	 * @return the calendar object.
	 */
	@SuppressWarnings("MagicConstant")
	static GregorianCalendar convertStringToCalendar(String date){
		int year = Integer.parseInt(date.substring(0,4));
		int month = Integer.parseInt(date.substring(4,6));
		int theMonth = getCorrectMonth(month);
		int dayOfMonth = Integer.parseInt(date.substring(6,8));
		GregorianCalendar calendar = new GregorianCalendar(year, theMonth, dayOfMonth);
		calendar.setTimeZone(Gtfs2KmlUtils.TIME_ZONE);
		return calendar;
	}

	static boolean isDayOfWeekServiced(GregorianCalendar day, CSVRecord record){
		int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
		boolean isMonday = Integer.parseInt(record.get(FIELD_CALENDAR_MON)) == 1;
		boolean isTuesday = Integer.parseInt(record.get(FIELD_CALENDAR_TUE)) == 1;
		boolean isWednesday = Integer.parseInt(record.get(FIELD_CALENDAR_WED)) == 1;
		boolean isThursday = Integer.parseInt(record.get(FIELD_CALENDAR_THU)) == 1;
		boolean isFriday = Integer.parseInt(record.get(FIELD_CALENDAR_FRI)) == 1;
		boolean isSaturday = Integer.parseInt(record.get(FIELD_CALENDAR_SAT)) == 1;
		boolean isSunday = Integer.parseInt(record.get(FIELD_CALENDAR_SUN)) == 1;
		switch (dayOfWeek){
			case 1:
				return isSunday;
			case 2:
				return isMonday;
			case 3:
				return isTuesday;
			case 4:
				return isWednesday;
			case 5:
				return isThursday;
			case 6:
				return isFriday;
			case 7:
				return isSaturday;
			default:
				throw new IllegalArgumentException("Don't know what to do with day '" + dayOfWeek + "'");
		}
	}

	static String convertGregorianCalendarToShortString(GregorianCalendar calendar){
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return String.format("%04d%02d%02d", year, month, day);
	}

	@SuppressWarnings("MagicConstant")
	static GregorianCalendar convertDateStringToGregorianCalendar(String dateString){
		GregorianCalendar calendar = new GregorianCalendar(TIME_ZONE);
		if(dateString.length() != 19){
			LOG.warn("Expected date string of length 19, in format 'YYYY/MM/DD HH:MM:SS'. " +
					"This may crash or have unintended consequences.");
		}

		calendar.set(
				Integer.parseInt(dateString.substring(0,4)),
				getCorrectMonth(Integer.parseInt(dateString.substring(5,7))),
				Integer.parseInt(dateString.substring(8,10)),
				Integer.parseInt(dateString.substring(11,13)),
				Integer.parseInt(dateString.substring(14,16)),
				Integer.parseInt(dateString.substring(17,19))
				);
		return calendar;
	}

	static int getCorrectMonth(int month){
		switch (month){
			case 1:
				return Calendar.JANUARY;
			case 2:
				return Calendar.FEBRUARY;
			case 3:
				return Calendar.MARCH;
			case 4:
				return Calendar.APRIL;
			case 5:
				return Calendar.MAY;
			case 6:
				return Calendar.JUNE;
			case 7:
				return Calendar.JULY;
			case 8:
				return Calendar.AUGUST;
			case 9:
				return Calendar.SEPTEMBER;
			case 10:
				return Calendar.OCTOBER;
			case 11:
				return Calendar.NOVEMBER;
			case 12:
				return Calendar.DECEMBER;
			default:
				throw new IllegalStateException("Unexpected value: " + month);
		}
	}

}
