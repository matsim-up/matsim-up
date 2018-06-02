/* *********************************************************************** *
 * project: org.matsim.*
 * SouthAfricaPopulationGrowthTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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

package org.matsim.up.utils;


import org.junit.Assert;
import org.junit.Test;
import org.matsim.up.utils.SouthAfricaPopulationGrowth.StudyArea;

public class SouthAfricaPopulationGrowthTest {

	@Test
	public void testGetGrowthFactor() {
		double f1 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2012);
		Assert.assertEquals("Wrong factor", 1.00256, f1, 1e-5);

		double f2 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2013);
		Assert.assertEquals("Wrong factor", 1.00455, f2, 1e-5);
		
		double f3 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2014);
		Assert.assertEquals("Wrong factor", 1.00585, f3, 1e-5);
		
		double f4 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2015);
		Assert.assertEquals("Wrong factor", 1.00666, f4, 1e-5);
		
		double f5 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2016);
		Assert.assertEquals("Wrong factor", 1.00730, f5, 1e-5);
		
		double f6 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2017);
		Assert.assertEquals("Wrong factor", 1.00768, f6, 1e-5);
	}

}
