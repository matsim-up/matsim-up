package org.matsim.up.utils.spatial;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public enum UpShapefiles {

    CAPETOWN_FUNCTIONAL_WGS84("CapeTown", "https://ie-repo.up.ac.za/data/shapefiles/-/raw/master/capeTown/zones/functional/CapeTown_Functional_SP2011_WGS84.shp");
    private final String name;
    private final String url;

    UpShapefiles(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL( String.format(Locale.US, "%s", url) );
    }

    public String getName(){
        return this.name;
    }

}
