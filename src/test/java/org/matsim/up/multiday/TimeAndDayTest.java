package org.matsim.up.multiday;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.core.utils.misc.Time;
import org.matsim.testcases.MatsimTestUtils;

import static org.junit.Assert.*;

public class TimeAndDayTest {

    @Test
    public void parseDay() {
        Assert.assertEquals("Wrong day", 0, TimeAndDay.parseDay(Time.parseTime("12:00:00")));
        Assert.assertEquals("Wrong day", 1, TimeAndDay.parseDay(Time.parseTime("36:00:00")));
    }

    @Test
    public void parseDayOfWeek() {
        Assert.assertTrue("Wrong day", TimeAndDay.parseDayOfWeek(0).equalsIgnoreCase("Monday"));
        Assert.assertTrue("Wrong day", TimeAndDay.parseDayOfWeek(4).equalsIgnoreCase("Friday"));

        /* Test days than run over one week. */
        Assert.assertTrue("Wrong day", TimeAndDay.parseDayOfWeek(8).equalsIgnoreCase("Tuesday"));
    }

    @Test
    public void convertDayAndTimeToTimeOnly() {
        Assert.assertEquals("Wrong time", Time.parseTime("30:00:00"),
                TimeAndDay.convertDayAndTimeToTimeOnly(1, Time.parseTime("06:00:00")), MatsimTestUtils.EPSILON);
    }
}