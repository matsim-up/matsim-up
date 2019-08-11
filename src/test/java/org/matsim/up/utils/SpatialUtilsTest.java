/* *********************************************************************** *
 * project: org.matsim.*
 * TestSpatialUtils.java
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
import org.locationtech.jts.geom.*;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.gbl.MatsimRandom;


public class SpatialUtilsTest {

	@Test
	public void testSampleRandomInteriorPoint() {
		MatsimRandom.reset(12345);
		Point p = SpatialUtils.sampleRandomInteriorPoint(buildSquare());
		Assert.assertEquals("Wrong x-coordinate", 36.1803107, p.getX(), 1e-6);
		Assert.assertEquals("Wrong y-coordinate", 93.2993485, p.getY(), 1e-6);
	}
	
	
	@Test
	public void testSampleRandomInteriorCoord() {
		MatsimRandom.reset(12345);
		Coord c = SpatialUtils.sampleRandomInteriorCoord(buildSquare());
		Assert.assertEquals("Wrong x-coordinate", 36.1803107, c.getX(), 1e-6);
		Assert.assertEquals("Wrong y-coordinate", 93.2993485, c.getY(), 1e-6);
	}
	
	
	private Geometry buildSquare() {
		Coordinate c1 = new Coordinate(0.0, 0.0);
		Coordinate c2 = new Coordinate(100.0, 0.0);
		Coordinate c3 = new Coordinate(100.0, 100.0);
		Coordinate c4 = new Coordinate(0.0, 100.0);
		Coordinate[] ca = {c1, c2, c3, c4, c1};
		Polygon p = new GeometryFactory().createPolygon(ca);
		return p;
	}

}
