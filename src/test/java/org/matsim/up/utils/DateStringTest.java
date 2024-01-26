package org.matsim.up.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

public class DateStringTest {

	@Test
	public void toPrettyString() {
		DateString ds = createDateString();
		Assertions.assertEquals("1975/09/18 12:00:00", ds.toPrettyString(), "Wrong pretty string.");
	}

	@Test
	public void testToString() {
		DateString ds = createDateString();
		Assertions.assertEquals("19750918120000.000", ds.toString(), "Wrong string.");
	}

	@Test
	public void getDateFields() {
		DateString ds = createDateString();
		Assertions.assertEquals(7, ds.getDateFields().length, "Wrong number of fields.");
		Assertions.assertEquals(1975, ds.getDateFields()[0], "Wrong year");
		Assertions.assertEquals(9, ds.getDateFields()[1], "Wrong month");
		Assertions.assertEquals(18, ds.getDateFields()[2], "Wrong day");
		Assertions.assertEquals(12, ds.getDateFields()[3], "Wrong hours");
		Assertions.assertEquals(0, ds.getDateFields()[4], "Wrong minutes");
		Assertions.assertEquals(0, ds.getDateFields()[5], "Wrong seconds");
		Assertions.assertEquals(0, ds.getDateFields()[6], "Wrong millisecond");
	}

	private DateString createDateString(){
		DateString ds = new DateString();
		ds.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
		ds.setTimeInMillis(180266400000L);
		return ds;
	}
}