/* *********************************************************************** *
 * project: org.matsim.*
 * SpatialUtils.java
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

/**
 * 
 */
package org.matsim.up.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.utils.geometry.CoordUtils;


/**
 * Class with specific spatial utilities.
 * 
 * @author jwjoubert
 */
public class SpatialUtils {

	public static Coord sampleRandomInteriorCoord(Geometry g) {
		if(g == null) {
			throw new RuntimeException("Cannot find an interior point for a 'null' geometry.");
		}
		Point p = sampleRandomInteriorPoint(g);

		return CoordUtils.createCoord(p.getX(), p.getY());
	}
	
	public static Point sampleRandomInteriorPoint(Geometry g) {
		if(g == null) {
			throw new RuntimeException("Cannot find an interior point for a 'null' geometry.");
		}
		Point p = null;
		
		Geometry envelope = g.getEnvelope();
		double c1x = envelope.getCoordinates()[0].x;
		double c2x = envelope.getCoordinates()[2].x;
		double c1y = envelope.getCoordinates()[0].y;
		double c2y = envelope.getCoordinates()[2].y;
		double minX = Math.min(c1x, c2x);
		double maxX = Math.max(c1x, c2x);
		double minY = Math.min(c1y, c2y);
		double maxY = Math.max(c1y, c2y);
		
		while(p == null){
			double sampleX = minX + MatsimRandom.getRandom().nextDouble()*(maxX-minX);
			double sampleY = minY + MatsimRandom.getRandom().nextDouble()*(maxY-minY);
			Point pp = g.getFactory().createPoint(new Coordinate(sampleX, sampleY));
			if(g.covers(pp)){
				p = pp;
			}
		}
		
		return p;
	}
	
	
}
