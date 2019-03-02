/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
package org.matsim.up.multiday;

import org.apache.log4j.Logger;
import org.matsim.core.utils.misc.Time;

import java.util.Calendar;

public class TimeAndDay {
    final private static Logger LOG = Logger.getLogger(TimeAndDay.class);

    private TimeAndDay() {
    }

    public static int parseDay(double timeOnly) {
        int day = 0;
        while (timeOnly > Time.parseTime("24:00:00")) {
            timeOnly -= Time.parseTime("24:00:00");
            day++;
        }
        return day;
    }


    /**
     * @param day       the day number; start counting from ONE (1), so Monday, the first day, will be day 1.
     * @param timeOfDay the clock-time of the day given in the previous parameter.
     * @return
     */
    public static double convertDayAndTimeToTimeOnly(int day, double timeOfDay) {
        return day * Time.parseTime("24:00:00") + timeOfDay;
    }


    /**
     * Since MATSim typically models weekdays, we return the first day,
     * day zero, as Monday. Day 1 is Tuesday, etc.
     *
     * @param day
     * @return
     */
    public static String parseDayOfWeek(int day) {
        switch (day) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                LOG.warn("Given day '" + day + "' runs into multiple weeks.");
                return parseDayOfWeek(day - 7);
        }
    }

}
