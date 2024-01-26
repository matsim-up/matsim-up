/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,     *
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

package org.matsim.up.utils.grid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.locationtech.jts.geom.*;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.up.utils.grid.GeneralGrid.GridType;
import org.matsim.up.utils.grid.KernelDensityEstimator.KdeType;

import java.util.Iterator;


public class KernelDensityEstimatorTest {

	@RegisterExtension
	public MatsimTestUtils utils = new MatsimTestUtils();


	@Test
	public void testConstructor() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.SQUARE);
		grid.generateGrid(p);

		KernelDensityEstimator kde;

		try {
			new KernelDensityEstimator(grid, KdeType.UNIFORM, 5.0);
			Assertions.fail("Schould have caught exception. Only KdeType.CELL allowed");
		} catch (IllegalArgumentException e) {
			/* Correctly caught exception. */
		}

		try {
			kde = new KernelDensityEstimator(grid, KdeType.CELL, 0.0);
			Assertions.assertEquals(KdeType.CELL, kde.getKdeType(), "Wrong KdeType.");
			Assertions.assertEquals(grid, kde.getGrid(), "Wrong GeneralGrid");
			Assertions.assertEquals(0.0, kde.getRadius(), MatsimTestUtils.EPSILON, "Wrong radius.");
		} catch (IllegalArgumentException e) {
			Assertions.fail("Should not have caused an exception. KdeType and width combination is correct");
		}
	}

	@Test
	public void testProcessPoint_CELL() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.SQUARE);
		grid.generateGrid(p);

		KernelDensityEstimator kde = new KernelDensityEstimator(grid, KdeType.CELL, 0.0);

		Point pc = p.getFactory().createPoint(new Coordinate(0.0, 0.0));

		Point p1 = p.getFactory().createPoint(new Coordinate(1.0, 1.0));
		kde.processPoint(p1, 5);
		Assertions.assertEquals(5.0, kde.getWeight(pc), MatsimTestUtils.EPSILON, "Wrong weight.");
		kde.processPoint(p1, 5);
		Assertions.assertEquals(10.0, kde.getWeight(pc), MatsimTestUtils.EPSILON, "Wrong weight.");
	}

	/**
	 * Check the weight distribution if a uniform kernel density is used around
	 * a point.
	 */
	@Test
	public void testProcessPoint_UNIFORM() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.SQUARE);
		grid.generateGrid(p);

		/* Try a point in the lower-left corner. */
		KernelDensityEstimator kde = new KernelDensityEstimator(grid, KdeType.UNIFORM, 15.0);

		Point pc1 = p.getFactory().createPoint(new Coordinate(0.0, 0.0));
		kde.processPoint(pc1, 20);
		Point pc2 = p.getFactory().createPoint(new Coordinate(10.0, 0.0));
		Point pc3 = p.getFactory().createPoint(new Coordinate(10.0, 10.0));
		Point pc4 = p.getFactory().createPoint(new Coordinate(0.0, 10.0));

		Assertions.assertEquals(5.0, kde.getWeight(pc1), MatsimTestUtils.EPSILON, "Wrong weight for cell (0, 0)");
		Assertions.assertEquals(5.0, kde.getWeight(pc2), MatsimTestUtils.EPSILON, "Wrong weight for cell (1, 0)");
		Assertions.assertEquals(5.0, kde.getWeight(pc3), MatsimTestUtils.EPSILON, "Wrong weight for cell (1, 1)");
		Assertions.assertEquals(5.0, kde.getWeight(pc4), MatsimTestUtils.EPSILON, "Wrong weight for cell (0, 1)");

		/* Try another point on the boundary of four cells. */
		Point target = p.getFactory().createPoint(new Coordinate(15.0, 15.0));
		kde.processPoint(target, 20);
		Point pc5 = p.getFactory().createPoint(new Coordinate(10.0, 20.0));
		Point pc6 = p.getFactory().createPoint(new Coordinate(20.0, 10.0));
		Point pc7 = p.getFactory().createPoint(new Coordinate(20.0, 20.0));

		Assertions.assertEquals(10.0, kde.getWeight(pc3), MatsimTestUtils.EPSILON, "Wrong weight for cell (1, 1)");
		Assertions.assertEquals(5.0, kde.getWeight(pc5), MatsimTestUtils.EPSILON, "Wrong weight for cell (1, 2)");
		Assertions.assertEquals(5.0, kde.getWeight(pc6), MatsimTestUtils.EPSILON, "Wrong weight for cell (2, 1)");
		Assertions.assertEquals(5.0, kde.getWeight(pc7), MatsimTestUtils.EPSILON, "Wrong weight for cell (2, 2)");
	}

	@Test
	public void testProcessPoint_TRIANGULAR() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.SQUARE);
		grid.generateGrid(p);

		/* Try a point in the lower-left corner. */
		KernelDensityEstimator kde = new KernelDensityEstimator(grid, KdeType.TRIANGULAR, 20.0);
		Point target = p.getFactory().createPoint(new Coordinate(0.0, 0.0));
		kde.processPoint(target, 20);

		Point pc11 = p.getFactory().createPoint(new Coordinate(0.0, 0.0));
		Point pc21 = p.getFactory().createPoint(new Coordinate(10.0, 0.0));
		Point pc31 = p.getFactory().createPoint(new Coordinate(20.0, 0.0));
		Point pc12 = p.getFactory().createPoint(new Coordinate(0.0, 10.0));
		Point pc22 = p.getFactory().createPoint(new Coordinate(10.0, 10.0));
		Point pc13 = p.getFactory().createPoint(new Coordinate(0.0, 20.0));

		double w11 = 20.0;
		double w21 = 10.0;
		double w12 = 10.0;
		double w22 = (20 - Math.sqrt(Math.pow(10.0, 2.0) + Math.pow(10.0, 2.0)));
		double sum = w11 + w21 + w12 + w22;
		Assertions.assertEquals(w11 / sum * 20.0, kde.getWeight(pc11), MatsimTestUtils.EPSILON, "Wrong weight for cell (1,  1)");
		Assertions.assertEquals(w21 / sum * 20.0, kde.getWeight(pc21), MatsimTestUtils.EPSILON, "Wrong weight for cell (2,  1)");
		Assertions.assertEquals(0.0, kde.getWeight(pc31), MatsimTestUtils.EPSILON, "Wrong weight for cell (3,  1)");
		Assertions.assertEquals(w12 / sum * 20.0, kde.getWeight(pc12), MatsimTestUtils.EPSILON, "Wrong weight for cell (1,  2)");
		Assertions.assertEquals(w22 / sum * 20.0, kde.getWeight(pc22), MatsimTestUtils.EPSILON, "Wrong weight for cell (2,  2)");
		Assertions.assertEquals(0.0, kde.getWeight(pc13), MatsimTestUtils.EPSILON, "Wrong weight for cell (1,  3)");
	}

	@Test
	public void testProcessLine_CELL() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.SQUARE);
		grid.generateGrid(p);

		KernelDensityEstimator kde1 = new KernelDensityEstimator(grid, KdeType.CELL, 0.0);
		Coordinate c0 = new Coordinate(0.0, 0.0);
		Coordinate c1 = new Coordinate(20.0, 0.0);
		Coordinate[] ca1 = {c0, c1};
		LineString l1 = p.getFactory().createLineString(ca1);
		kde1.processLine(l1, 20);
		Point pc11 = p.getFactory().createPoint(new Coordinate(0.0, 0.0));
		Point pc21 = p.getFactory().createPoint(new Coordinate(10.0, 0.0));
		Point pc31 = p.getFactory().createPoint(new Coordinate(20.0, 0.0));
		Assertions.assertEquals(5.0, kde1.getWeight(pc11), MatsimTestUtils.EPSILON, "Wrong weight to cell (0, 0)");
		Assertions.assertEquals(10.0, kde1.getWeight(pc21), MatsimTestUtils.EPSILON, "Wrong weight to cell (1, 0)");
		Assertions.assertEquals(5.0, kde1.getWeight(pc31), MatsimTestUtils.EPSILON, "Wrong weight to cell (2, 0)");

		KernelDensityEstimator kde2 = new KernelDensityEstimator(grid, KdeType.CELL, 0.0);
		Coordinate c2 = new Coordinate(0.0, 5.0);
		Coordinate c3 = new Coordinate(20.0, 5.0);
		Coordinate[] ca2 = {c2, c3};
		LineString l2 = p.getFactory().createLineString(ca2);
		kde2.processLine(l2, 20);
		Point pc12 = p.getFactory().createPoint(new Coordinate(0.0, 10.0));
		Point pc22 = p.getFactory().createPoint(new Coordinate(10.0, 10.0));
		Point pc32 = p.getFactory().createPoint(new Coordinate(20.0, 10.0));
		Assertions.assertEquals(2.5, kde2.getWeight(pc11), MatsimTestUtils.EPSILON, "Wrong weight to cell (0, 0)");
		Assertions.assertEquals(5.0, kde2.getWeight(pc21), MatsimTestUtils.EPSILON, "Wrong weight to cell (1, 0)");
		Assertions.assertEquals(2.5, kde2.getWeight(pc31), MatsimTestUtils.EPSILON, "Wrong weight to cell (2, 0)");
		Assertions.assertEquals(2.5, kde2.getWeight(pc12), MatsimTestUtils.EPSILON, "Wrong weight to cell (0, 1)");
		Assertions.assertEquals(5.0, kde2.getWeight(pc22), MatsimTestUtils.EPSILON, "Wrong weight to cell (1, 1)");
		Assertions.assertEquals(2.5, kde2.getWeight(pc32), MatsimTestUtils.EPSILON, "Wrong weight to cell (2, 1)");
	}

	@Test
	public void testMakeEmptyCopy() {
		Polygon p = buildDummyPolygon();
		GeneralGrid grid = new GeneralGrid(10.0, GridType.HEX);
		grid.generateGrid(p);

		KernelDensityEstimator kde = new KernelDensityEstimator(grid, KdeType.CELL, 0.0);
		Point point = new GeometryFactory().createPoint(new Coordinate(10.0, 10.0));
		kde.processPoint(point, 1.0);
		/* Find the point */
		Point pointCell = null;
		Iterator<Point> gridPointIterator = kde.getGrid().getGrid().values().iterator();
		while (pointCell == null && gridPointIterator.hasNext()) {
			Point gridPoint = gridPointIterator.next();
			if(kde.getWeight(gridPoint) > 0.0){
				pointCell = gridPoint;
			}
		}
		Assertions.assertEquals(1.0, kde.getWeight(pointCell), MatsimTestUtils.EPSILON, "Incorrect weight.");

		KernelDensityEstimator kdeCopy = kde.makeEmptyCopy();
		Assertions.assertNotEquals(kde, kdeCopy, "Should not be the same object.");
		Assertions.assertEquals(0.0, kdeCopy.getWeight(pointCell), MatsimTestUtils.EPSILON, "Copy should have no weights.");
	}


	/**
	 * Generating a polygon of 100 x 100.
	 */
	private Polygon buildDummyPolygon() {
		GeometryFactory gf = new GeometryFactory();
		Coordinate c1 = new Coordinate(0.0, 0.0);
		Coordinate c2 = new Coordinate(0.0, 100);
		Coordinate c3 = new Coordinate(100, 100);
		Coordinate c4 = new Coordinate(100, 0.0);
		Coordinate[] ca = {c1, c2, c3, c4, c1};
		return gf.createPolygon(ca);
	}

}
