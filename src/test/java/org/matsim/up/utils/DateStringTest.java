package org.matsim.up.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateStringTest {

	@Test
	public void toPrettyString() {
		DateString ds = createDateString();
		Assert.assertEquals("Wrong pretty string.", "1975/09/18 12:00:00", ds.toPrettyString());
	}

	@Test
	public void testToString() {
		DateString ds = createDateString();
		Assert.assertEquals("Wrong string.", "19750918120000.000", ds.toString());
	}

	@Test
	public void getDateFields() {
		DateString ds = createDateString();
		Assert.assertEquals("Wrong number of fields.", 7, ds.getDateFields().length);
		Assert.assertEquals("Wrong year", 1975, ds.getDateFields()[0]);
		Assert.assertEquals("Wrong month", 9, ds.getDateFields()[1]);
		Assert.assertEquals("Wrong day", 18, ds.getDateFields()[2]);
		Assert.assertEquals("Wrong hours", 12, ds.getDateFields()[3]);
		Assert.assertEquals("Wrong minutes", 0, ds.getDateFields()[4]);
		Assert.assertEquals("Wrong seconds", 0, ds.getDateFields()[5]);
		Assert.assertEquals("Wrong millisecond", 0, ds.getDateFields()[6]);
	}

	private DateString createDateString(){
		DateString ds = new DateString();
		ds.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
		ds.setTimeInMillis(180266400000L);
		return ds;
	}
}