/* *********************************************************************** *
 * project: org.matsim.*
 * Header.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package org.matsim.up.utils;

import org.apache.log4j.Logger;
import org.matsim.core.gbl.Gbl;

public class Header {
    private final static Logger LOG = Logger.getLogger(Header.class);
    private static int length = 10;

    public static void printHeader(Class<?> theClass, String[] args) {
        length = theClass.toString().length();
        LOG.info(getLineString("=", length));
        LOG.info(theClass.toString());
        LOG.info(getLineString("-", length));
        if (args != null) {
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    LOG.info("args[" + i + "]: " + args[i]);
                }
            } else {
                LOG.info("  NO ARGUMENTS !!");
            }
        }
        LOG.info(getLineString("-", length));
        Gbl.printSystemInfo();
        Gbl.startMeasurement();
    }

    public static void printFooter() {

        Gbl.printElapsedTime();
        LOG.info(getLineString("-", length));
        String s = "";
        for (int i = 0; i < length / 2 - 2; i++) {
            s += " ";
        }
        LOG.info(s + "Done");
        LOG.info(getLineString("=", length));
    }

    private static String getLineString(String string, int length) {
        String s = "";
        for (int i = 0; i <= length; i++) {
            s += string;
        }
        return s;
    }
}

