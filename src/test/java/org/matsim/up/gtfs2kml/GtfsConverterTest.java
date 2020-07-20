package org.matsim.up.gtfs2kml;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.testcases.MatsimTestUtils;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class GtfsConverterTest {
	@Rule public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void checkTestInput(){
		File folder = new File(utils.getClassInputDirectory());
		Assert.assertTrue("Input folder should exist.", folder.exists());
		Assert.assertTrue("Input folder should be a directory.", folder.isDirectory());
	}

	@Test
	public void parseStopData() {

	}

	@Test
	public void convertStringToCalendar(){
	}
}