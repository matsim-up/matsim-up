package org.matsim.up.utils.attributeConverter;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.utils.objectattributes.AttributeConverter;
import org.matsim.utils.objectattributes.attributeconverters.CoordConverter;

import java.util.Locale;

/**
 * This class is an alternative for the built-in attribute converter 
 * {@link CoordConverter}, but differs in that it rounds the x and y-values
 * not to only two decimal places, but six. This is necessary when working 
 * with decimal degrees (WGS84) instead of a projected coordinate reference
 * system where the units of measure is meters.
 *
 * @author jwjoubert
 */
public class Wgs84CoordConverter implements AttributeConverter<Coord> {
	private final Logger log = Logger.getLogger(Wgs84CoordConverter.class);

	@Override
	public Coord convert(String value) {
		String s = value.replace("(", "");
		s = s.replace(")", "");
		String[] sa = s.split(";");
		return new Coord(Double.parseDouble(sa[0]), Double.parseDouble(sa[1]));
	}

	@Override
	public String convertToString(Object o) {
		if(!(o instanceof Coord)){
			log.error("Object is not of type Coord: " + o.getClass().toString());
			return null;
		}
		Coord c = (Coord)o;
		
		return String.format(Locale.US, "(%.6f;%.6f)", c.getX(), c.getY());
	}

}
