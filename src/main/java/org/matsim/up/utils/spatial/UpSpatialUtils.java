/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2020 by the members listed in the COPYING,        *
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
package org.matsim.up.utils.spatial;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.utils.geometry.CoordUtils;

/**
 * Various spatial utilities used in the University of Pretoria (UP) code.
 */
public class UpSpatialUtils {
	final private static Logger LOG = Logger.getLogger(UpSpatialUtils.class);

	/**
	 * Creates and returns a coordinate that is covered by the given geometry.
	 * this is only practical if the geometry is a (multi)polygon.
	 * @param g a given geometry.
	 * @return a coordinate that is covered by the given geometry.
	 */
	public static Coord findRandomInternalCoord(Geometry g) {
		int coords = g.getCoordinates().length;
		if(coords < 4){
			LOG.warn("It seems the geometry is not a (multi)polygon as it has " + coords + " coordinates.");
			LOG.warn("Not very practical (computationally efficient) to search for a point covered by the given geometry");
		}
		GeometryFactory gf = new GeometryFactory();
		Coordinate[] envelope = g.getEnvelope().getCoordinates();
		Point p = null;
		while (p == null) {
			double xGap = envelope[2].x - envelope[0].x;
			double yGap = envelope[2].y - envelope[0].y;
			double x = envelope[0].x + MatsimRandom.getRandom().nextDouble() * (xGap);
			double y = envelope[0].y + MatsimRandom.getRandom().nextDouble() * (yGap);
			Point pTemp = gf.createPoint(new Coordinate(x, y));
			if (g.covers(pTemp)) {
				p = pTemp;
			}
		}
		return CoordUtils.createCoord(p.getX(), p.getY());
	}

}
