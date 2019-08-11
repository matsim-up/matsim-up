/* *********************************************************************** *
 * project: org.matsim.*
 * MyZone.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Identifiable;


@Deprecated
public class MyZone extends MultiPolygon implements Identifiable<MyZone>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Id<MyZone> id;

	MyZone(Polygon[] polygons, GeometryFactory factory, Id<MyZone> id) {
		super(polygons, factory);
		this.id = id;
	}
	
	@Override
	public Id<MyZone> getId() {
		return this.id;
	}

}

