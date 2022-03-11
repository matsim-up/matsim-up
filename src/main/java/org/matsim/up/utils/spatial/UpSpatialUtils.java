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
import org.geotools.data.shapefile.index.quadtree.QuadTree;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.net.MalformedURLException;
import java.util.Collection;

/**
 * Various spatial utilities used in the University of Pretoria (UP) code.
 */
public class UpSpatialUtils {
    final private static Logger LOG = Logger.getLogger(UpSpatialUtils.class);

    /**
     * Creates and returns a coordinate that is covered by the given geometry.
     * this is only practical if the geometry is a (multi)polygon.
     *
     * @param g a given geometry.
     * @return a coordinate that is covered by the given geometry.
     */
    public static Coord findRandomInternalCoord(Geometry g) {
        int coords = g.getCoordinates().length;
        if (coords < 4) {
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

    /**
     * A reusable method to calculate the extent of a collection of geometry
     * features. This is useful for {@link QuadTree}s, for example.
     *
     * @param features the collection of features.
     * @return the extent of the collection, given in the format [minX, minY, maxX, maxY]
     */
    public static double[] getQuadTreeExtentFromFeatures(Collection<SimpleFeature> features) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (SimpleFeature feature : features) {
            Object o = feature.getDefaultGeometry();
            if (o instanceof Geometry) {
                Geometry g = (Geometry) o;
                Coordinate[] envelope = g.getEnvelope().getCoordinates();
                minX = Math.min(minX, envelope[0].x);
                maxX = Math.max(maxX, envelope[2].x);
                minY = Math.min(minY, envelope[0].y);
                maxY = Math.max(maxY, envelope[2].y);
            } else {
                LOG.warn("SimpleFeature not of type 'MultiPolygon', but " + o.getClass().toString() + ". Ignoring");
            }
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    public static Collection<SimpleFeature> getShapefileFeatures(UpShapefiles area) {
        try {
            return ShapeFileReader.getAllFeatures(area.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            LOG.error("Could not get the shapefile. Returning NULL.");
        }
        return null;
    }

}
