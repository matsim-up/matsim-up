package org.matsim.up.utils.spatial;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.locationtech.jts.geom.*;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.testcases.MatsimTestUtils;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Collection;

public class UpSpatialUtilsTest {

	@RegisterExtension
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void findRandomInternalCoord() {
		Geometry geometry = createSquareOfSizeTwo();
		Coord c1 = UpSpatialUtils.findRandomInternalCoord(geometry);
		Assert.assertTrue("Square should cover coordinate.", geometry.covers(convertCoordToPoint(c1)));

		Geometry diamond = createDiamond();
		Coord c2 = UpSpatialUtils.findRandomInternalCoord(diamond);
		Assert.assertTrue("Diamond should cover coordinate.", geometry.covers(convertCoordToPoint(c2)));
	}

	@Test
	public void getQuadTreeExtentFromFeatures(){
		double[] array = UpSpatialUtils.getQuadTreeExtentFromFeatures(readSimpleFeatures());
		Assert.assertEquals("Wrong array length.", 4, array.length);
	}

	private Point convertCoordToPoint(Coord c){
		GeometryFactory gf = new GeometryFactory();
		return gf.createPoint(new Coordinate(c.getX(), c.getY()));
	}

	private Polygon createSquareOfSizeTwo(){
		GeometryFactory gf = new GeometryFactory();

		Coordinate c1 = new Coordinate(0.0, 0.0);
		Coordinate c2 = new Coordinate(0.0, 2.0);
		Coordinate c3 = new Coordinate(2.0, 2.0);
		Coordinate c4 = new Coordinate(2.0, 0.0);
		Coordinate[] ca = new Coordinate[]{c1, c2, c3, c4, c1};
		return gf.createPolygon(ca);
	}

	private Polygon createDiamond(){
		GeometryFactory gf = new GeometryFactory();

		Coordinate c1 = new Coordinate(2.0, 0.0);
		Coordinate c2 = new Coordinate(0.0, 2.0);
		Coordinate c3 = new Coordinate(2.0, 4.0);
		Coordinate c4 = new Coordinate(4.0, 2.0);
		Coordinate[] ca = new Coordinate[]{c1, c2, c3, c4, c1};
		return gf.createPolygon(ca);
	}

	private Collection<SimpleFeature> readSimpleFeatures(){
		String test = utils.getClassInputDirectory() + "TestDiamond.shp";
		return ShapeFileReader.getAllFeatures(test);
	}

	/**
	 * Test the shapefiles.
	 */
	@Test
	public void getShapefileFeatures(){
		Collection<SimpleFeature> features = null;
		try{
			features = UpSpatialUtils.getShapefileFeatures(UpShapefiles.CAPETOWN_FUNCTIONAL_WGS84);
		} catch(Exception e){
			e.printStackTrace();
			Assert.fail("Should read shapefile from URL without exception.");
		}
		Assert.assertNotNull("Collection should not be null", features);
	}


}