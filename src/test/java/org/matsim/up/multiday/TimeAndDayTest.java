package org.matsim.up.multiday;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matsim.core.utils.misc.Time;
import org.matsim.testcases.MatsimTestUtils;

public class TimeAndDayTest {

    @Test
    public void parseDay() {
        Assertions.assertEquals(0, TimeAndDay.parseDay(Time.parseTime("12:00:00")), "Wrong day");
        Assertions.assertEquals(1, TimeAndDay.parseDay(Time.parseTime("36:00:00")), "Wrong day");
    }

    @Test
    public void parseDayOfWeek() {
        Assertions.assertTrue(TimeAndDay.parseDayOfWeek(0).equalsIgnoreCase("Monday"), "Wrong day");
        Assertions.assertTrue(TimeAndDay.parseDayOfWeek(4).equalsIgnoreCase("Friday"), "Wrong day");

        /* Test days than run over one week. */
        Assertions.assertTrue(TimeAndDay.parseDayOfWeek(8).equalsIgnoreCase("Tuesday"), "Wrong day");
    }

    @Test
    public void convertDayAndTimeToTimeOnly() {
        Assertions.assertEquals(Time.parseTime("30:00:00"), TimeAndDay.convertDayAndTimeToTimeOnly(1, Time.parseTime("06:00:00")), MatsimTestUtils.EPSILON, "Wrong time");
    }
}