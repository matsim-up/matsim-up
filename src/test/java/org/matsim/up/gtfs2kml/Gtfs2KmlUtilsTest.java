package org.matsim.up.gtfs2kml;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Gtfs2KmlUtilsTest {

    @Test
    public void convertStringToCalendar() {
        String dateString = "19750918";
        int year = 1975;
        int month = Calendar.SEPTEMBER;
        int dayOfMonth = 18;
        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        calendar.setTimeZone(Gtfs2KmlUtils.TIME_ZONE);
        Assert.assertEquals("Wrong calendar object", calendar, Gtfs2KmlUtils.convertStringToCalendar(dateString));
    }

    @Test
    public void isInDateRange() {
        GregorianCalendar dayIn = Gtfs2KmlUtils.convertStringToCalendar("19750918");
        GregorianCalendar dayOut = Gtfs2KmlUtils.convertStringToCalendar("20200610");
        GregorianCalendar start = Gtfs2KmlUtils.convertStringToCalendar("19750101");
        GregorianCalendar end = Gtfs2KmlUtils.convertStringToCalendar("19751231");
        Assert.assertTrue("Date should be in range", Gtfs2KmlUtils.isInDateRange(dayIn, start, end));
        Assert.assertFalse("Date should not be in range", Gtfs2KmlUtils.isInDateRange(dayOut, start, end));
    }

    @Test
    public void isDayOfWeekServiced() {
        CSVRecord fullWeek = null;
        try {
            fullWeek = getFullWeekRecord();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Should parse the 'full week' test record without an exception.");
        }

        CSVRecord noDays = null;
        try {
            noDays = getNoDaysRecord();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Should parse the 'no days' test record without an exception.");
        }
        
        GregorianCalendar sunday = Gtfs2KmlUtils.convertStringToCalendar("20200607");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(sunday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(sunday, noDays));
        GregorianCalendar monday = Gtfs2KmlUtils.convertStringToCalendar("20200608");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(monday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(monday, noDays));
        GregorianCalendar tueday = Gtfs2KmlUtils.convertStringToCalendar("20200609");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(tueday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(tueday, noDays));
        GregorianCalendar wednesday = Gtfs2KmlUtils.convertStringToCalendar("20200610");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(wednesday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(wednesday, noDays));
        GregorianCalendar thursday = Gtfs2KmlUtils.convertStringToCalendar("20200611");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(thursday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(thursday, noDays));
        GregorianCalendar friday = Gtfs2KmlUtils.convertStringToCalendar("20200612");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(friday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(friday, noDays));
        GregorianCalendar saturday = Gtfs2KmlUtils.convertStringToCalendar("20200613");
        Assert.assertTrue("Should service day", Gtfs2KmlUtils.isDayOfWeekServiced(saturday, fullWeek));
        Assert.assertFalse("Should not service day", Gtfs2KmlUtils.isDayOfWeekServiced(saturday, noDays));
    }

    @Test
    public void convertGregorianCalendarToShortString() {
        GregorianCalendar calendar = new GregorianCalendar(1975, Calendar.SEPTEMBER, 18);
        calendar.setTimeZone(Gtfs2KmlUtils.TIME_ZONE);
        Assert.assertEquals("Wrong date conversion", "19750918", Gtfs2KmlUtils.convertGregorianCalendarToShortString(calendar));
    }

    @Test
    public void getCorrectMonth() {
        Assert.assertEquals("Wrong month.", Calendar.JANUARY, Gtfs2KmlUtils.getCorrectMonth(1));
        Assert.assertEquals("Wrong month.", Calendar.DECEMBER, Gtfs2KmlUtils.getCorrectMonth(12));
    }

    @Test
    public void convertDateStringToGregorianCalendar() {
        GregorianCalendar calendar = new GregorianCalendar(Gtfs2KmlUtils.TIME_ZONE);
        calendar.set(1975, Calendar.SEPTEMBER, 18, 8, 12, 34);
        Assert.assertEquals("Wrong calendar",
                calendar.getTimeInMillis(),
                Gtfs2KmlUtils.convertDateStringToGregorianCalendar("1975/09/18 08:12:34").getTimeInMillis());
    }

    private CSVRecord getFullWeekRecord() throws IOException {
        String fullWeek = "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\ntmp,1,1,1,1,1,1,1,20200608,20200614";
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        return CSVParser.parse(fullWeek, csvFormat).getRecords().get(0);
    }

    private CSVRecord getNoDaysRecord() throws IOException {
        String fullWeek = "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\ntmp,0,0,0,0,0,0,0,20200608,20200614";
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        return CSVParser.parse(fullWeek, csvFormat).getRecords().get(0);
    }

}
