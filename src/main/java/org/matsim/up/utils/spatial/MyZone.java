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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Identifiable;
import org.matsim.utils.objectattributes.ObjectAttributes;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

@Deprecated
public class MyZone extends MultiPolygon implements Identifiable<MyZone>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Id<MyZone> id;
	private ObjectAttributes attr;

	public MyZone(Polygon[] polygons, GeometryFactory factory, Id<MyZone> id) {
		super(polygons, factory);
		this.id = id;
		this.attr = new ObjectAttributes();
	}
	
	@Override
	public Id<MyZone> getId() {
		return this.id;
	}
	
	
	public ObjectAttributes getObjectAttributes(){
		return this.attr;
	}
	
}

