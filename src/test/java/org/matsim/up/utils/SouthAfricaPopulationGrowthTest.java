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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matsim.up.utils.SouthAfricaPopulationGrowth.StudyArea;

public class SouthAfricaPopulationGrowthTest {

	@Test
	public void testGetGrowthFactor() {
		double f1 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2012);
		Assertions.assertEquals(1.00256, f1, 1e-5, "Wrong factor");

		double f2 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2013);
		Assertions.assertEquals(1.00455, f2, 1e-5, "Wrong factor");
		
		double f3 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2014);
		Assertions.assertEquals(1.00585, f3, 1e-5, "Wrong factor");
		
		double f4 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2015);
		Assertions.assertEquals(1.00666, f4, 1e-5, "Wrong factor");
		
		double f5 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2016);
		Assertions.assertEquals(1.00730, f5, 1e-5, "Wrong factor");
		
		double f6 = SouthAfricaPopulationGrowth.getGrowthFactor(StudyArea.BuffaloCity, 2017);
		Assertions.assertEquals(1.00768, f6, 1e-5, "Wrong factor");
	}

}
