package org.matsim.up.utils;

import org.junit.Assert;
import org.junit.Test;

public class SouthAfricaInflationCorrectorTest {

	@Test
	public void convert(){
		double start = 1.0;
		double end = SouthAfricaInflationCorrector.convert(start, 1981, 2019);
		Assert.assertEquals("Wrong conversion.", 22.815, end, 0.0001);

		try{
			SouthAfricaInflationCorrector.convert(start, 1980, 1990);
			Assert.fail("Should not have years before 1981.");
		} catch(IllegalArgumentException e){
			e.printStackTrace();
		}

		try{
			SouthAfricaInflationCorrector.convert(start, 2000, 2100);
			Assert.fail("Should not have years so far in the future.");
		} catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

}