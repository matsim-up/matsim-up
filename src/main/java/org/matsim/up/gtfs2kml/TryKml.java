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
package org.matsim.up.gtfs2kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.xal.AddressDetails;
import net.opengis.kml.v_2_2_0.PointType;
import org.apache.log4j.Logger;
import org.geoserver.config.GeoServer;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.kml.KMLEncoder;
import org.geoserver.kml.KmlEncodingContext;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSMapContent;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.kml.KMLConfiguration;
import org.geotools.kml.v22.KML;
import org.geotools.ows.bindings.WGS84BoundingBoxTypeBinding;
import org.geotools.xsd.Encoder;
import org.geotools.xsd.Parser;
import org.jdom2.CDATA;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.kml.KMLWriter;
import org.matsim.up.utils.Header;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class TryKml {
	private static final Logger LOG = Logger.getLogger(TryKml.class);

	public static void main(String[] args) {
		Header.printHeader(TryKml.class, args);
		run();
		Header.printFooter();
	}

	static void run(String[] args) {
		GeometryFactory gf = new GeometryFactory();
		Kml kml = KmlFactory.createKml();
		Placemark pm1 = kml.createAndSetPlacemark();
		Point p = new Point();
		p.createAndSetCoordinates();
		p.addToCoordinates(28.224855, -25.742612);
		pm1.setGeometry(p);

		pm1.setGeometry(p);
		pm1.setId("1");

		Encoder encoder = new Encoder(new KMLConfiguration());
		encoder.setIndenting(true);

		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
		List<Object> values = new ArrayList<>();
		SimpleFeatureType type = null;
		try {
			type = DataUtilities.createType("location", "geom:Point,name:String");
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		FeatureId id = new FeatureIdImpl("home");
		SimpleFeature sf = new SimpleFeatureImpl(values, type, id);
		sf.setDefaultGeometry(gf.createPoint(new org.locationtech.jts.geom.Coordinate(28.224855, -25.742612)));
		featureCollection.add(sf);
		try {
			encoder.encode(featureCollection, KML.kml, new FileOutputStream("/Users/jwjoubert/Downloads/FirstKml.kml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void run() {
		final Kml kml = new Kml();
		Document document = kml.createAndSetDocument();
		document.withName("Test Kml file.");

		/* Set up icon-only styles */
		document.createAndAddStyle().withId("busStyle")
				.createAndSetIconStyle()
				.withIcon(new Icon().withHref("http://maps.google.com/mapfiles/kml/shapes/bus.png"))
				.withScale(0.8)
				.withColor(Color.RED.toString());
		document.createAndAddStyle()
				.withId("busStyleOther")
				.createAndSetIconStyle()
				.withIcon(new Icon().withHref("https://img.icons8.com/material/100/000000/bus--v2.png"))
				.withScale(0.8)
				.withColor(Color.RED.toString());

		/* Complex baloon style */
		CDATA complexCdata = new CDATA("<h2>Complex style for $[name] </h2>\n" +
				"It has $[a.name/displayName] and $[b.name/displayName].<br><br>$[description]<br><br>" +
				"$[geDirections]");
		Style complexStyle = document.createAndAddStyle()
				.withId("complexStyle");
		complexStyle.createAndSetIconStyle()
				.withIcon(new Icon().withHref("http://maps.google.com/mapfiles/kml/shapes/bus.png"))
				.withScale(0.8);
		complexStyle.createAndSetBalloonStyle()
				.withText(complexCdata.getTextTrim());

		/* Style map. */
		StyleMap map = document.createAndAddStyleMap().withId("normalAndHighlight");
		Pair pairNormal = map.createAndAddPair();
		pairNormal.setKey(StyleState.NORMAL);
		pairNormal.setStyleUrl("#busStyle");
		Pair pairHighlight = map.createAndAddPair();
		pairHighlight.setKey(StyleState.HIGHLIGHT);
		pairHighlight.setStyleUrl("#busStyleOther");

		/* Add home as p1. */
		document.createAndAddPlacemark()
				.withName(" Home")
				.withDescription("")
				.withOpen(Boolean.TRUE)
				.withAddress("Home address")
				.withStyleUrl("busStyleOther")
				.createAndSetPoint().addToCoordinates(28.224855, -25.742612)
				.withTargetId("Some target id");

		CDATA a = new CDATA("<h1>CDATA Tags are useful!</h1>\n" +
				"          <p><font color=\"red\">Text is <i>more readable</i> and \n" +
				"          <b>easier to write</b> when you can avoid using entity \n" +
				"          references.</font></p>");
		ExtendedData ed = new ExtendedData();
		Data dataA = ed.createAndAddData("a.value").withName("a.name").withDisplayName("a's display name");
		Data dataB = ed.createAndAddData("b.value").withId("b.id").withName("b.name").withDisplayName("b's display name");

		CDATA b = new CDATA("## Routes serviced:" +
				"<details>" +
				"<summary>Summary</summary> " +
				"a <br> b <br> c <br><br> " +
				"</details>");


		/* Add work as p2. */
		document.createAndAddPlacemark()
				.withName("work")
				.withDescription(b.getText())
				.withStyleUrl("#normalAndHighlight")
				.withVisibility(true)
				.withExtendedData(ed)
				.withAddress("Work address")
				//
				.createAndSetPoint()
				.withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
				.addToCoordinates(28.227725, -25.753326);

		kml.setFeature(document);
		try {
			kml.marshal(new File("/Users/jwjoubert/Downloads/FirstKml.kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
