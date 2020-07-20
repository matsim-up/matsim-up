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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.DateString;
import org.matsim.up.utils.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class GtfsConverter {
    public static final Logger LOG = Logger.getLogger(GtfsConverter.class);

    private static final Map<String, CSVRecord> CALENDAR_DATA = new TreeMap<>();
    private static final Map<String, CSVRecord> CALENDAR_DATE_DATA = new TreeMap<>();
    private static final Map<String, CSVRecord> STOPS_DATA = new TreeMap<>();
    private static final Map<String, Map<String, List<CSVRecord>>> STOP_TIMES_DATA = new TreeMap<>();
    private static final Map<String, CSVRecord> TRIPS_DATA = new TreeMap<>();
    private static final Map<String, CSVRecord> ROUTES_DATA = new TreeMap<>();

    /**
     * Executes the GTFS-to-Kml conversion.
     *
     * @param args <ol>
     *             <li>a single compulsory argument, namely a valid path to a
     *             folder that contains a validated GTFS feed;</li>
     *             <li>a second, optional date argument in the form
     *             <code>YYYY/MM/DD HH:MM:SS</code>, similar to {@link DateString#toPrettyString()}.</li>
     *             </ol>
     */
    public static void main(String[] args) {
        Header.printHeader(GtfsConverter.class, args);
        if (args.length == 1) {
            LOG.info("Only input folder provided. No dynamic departures.");
        }
        File folder = new File(args[0]);
        if (!folder.isDirectory()) {
            throw new RuntimeException("Need a valid path to folder where GTFS files are.");
        }
        GregorianCalendar thisDay = null;
        if (args.length == 2) {
            LOG.info("Input folder provided, and will report departures for '" + args[1] + "'.");
            thisDay = Gtfs2KmlUtils.convertDateStringToGregorianCalendar(args[1]);
        } else if (args.length > 2) {
            LOG.warn("Third and subsequent arguments are ignored.");
        }
        run(folder, thisDay);
        Header.printFooter();
    }


    static void run(File folder, GregorianCalendar thisDay) {
        parseStopsData(folder);
        parseRoutesData(folder);
        parseTripsData(folder);
        parseStopTimesData(folder);
        parseCalendarData(folder);
        parseCalendarExceptionData(folder);

        Kml kml = doYourMagic(thisDay);
        try {
            kml.marshal(new File(Gtfs2KmlUtils.getOutputFilename(folder)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Kml doYourMagic(GregorianCalendar thisDay) {
        Kml kml = setupKml();
        Document document = getDocumentOrCrash(kml);

        for (String stopId : STOPS_DATA.keySet()) {
            String stopDescription = getStopDescription(stopId);
            stopDescription += stopDescription.length() > 1 ? "<br>" : "";
            StringBuilder builder = new StringBuilder(String.format("\n\t\t\t\t<table width=\"400\"><tr><td>%s<br>\n", stopDescription));
            builder.append("\t\t\t\tBus routes servicing this stop:<br><br>");
            Map<String, String> mapOfRoutes = getAllRoutesServicingStop(stopId);
            for (String routeShort : mapOfRoutes.keySet()) {
                builder.append(String.format("\n\t\t\t\t\t<details>\n\t\t\t\t\t\t<summary>%s: %s</summary>\n",
                        routeShort, mapOfRoutes.get(routeShort)));
                reportOtherStopsServiced(builder, routeShort, stopId);
                if (thisDay != null) {
                    reportSubsequentDepartures(builder, routeShort, stopId, thisDay);
                }

                builder.append("\t\t\t\t\t</details>\t\t\t");
            }
            builder.append("\n\t\t\t\t</td></tr></table>\n\t\t\t");
            document.createAndAddPlacemark()
                    .withName(getStopName(stopId))
                    .withDescription(builder.toString())
                    .withStyleUrl("busStopStyle")
                    .withVisibility(true)
//					.withAddress("")
                    //
                    .createAndSetPoint()
                    .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
                    .addToCoordinates(
                            getCoordFromStopsData(stopId).getX(),
                            getCoordFromStopsData(stopId).getY());
        }
        kml.setFeature(document);

        LOG.info("Done converting KML file");
        return kml;
    }

    static void reportOtherStopsServiced(StringBuilder builder, String routeShortName, String stopId) {
        builder.append("\t\t\t\t\t\t<blockquote><p style=\"color:gray\">Other stops serviced by this route:</p>\n");
        builder.append("\t\t\t\t\t\t\t<ul>\n");
        String routeId = getRouteIdFromShortName(routeShortName);
        for (String stopName : getStopsServicedByRoute(routeId, stopId)) {
            builder.append(String.format("\t\t\t\t\t\t\t\t<li style=\"color:gray\">%s</li>\n", stopName));
        }
        builder.append("\t\t\t\t\t\t\t</ul>\n");
        builder.append("\t\t\t\t\t\t</blockquote>\n");
    }

    static void reportSubsequentDepartures(StringBuilder builder, String routeShortName, String stopId, GregorianCalendar day) {
        builder.append("\t\t\t\t\t\t<blockquote><p style=\"color:gray\">Departures today for this route:</p>\n");
        builder.append("\t\t\t\t\t\t\t<blockquote><p style=\"color:gray\">");
        List<String> departures = getDepartures(getRouteIdFromShortName(routeShortName), stopId, day);
        builder.append("\n\t\t\t\t\t\t\t\t");
        for (int i = 0; i < departures.size() - 1; i++) {
            builder.append(String.format("%s; ", departures.get(i).substring(0, 5)));
            if (i > 0 & (i + 1) % 5 == 0) {
                builder.append("<br>\n\t\t\t\t\t\t\t\t");
            }
        }
        if (!departures.isEmpty()) {
            String lastEntry = departures.get(departures.size() - 1);
            builder.append(String.format("%s</p>\n",
                    lastEntry.equals("None") ? lastEntry : lastEntry.substring(0, 5)));
        }
        builder.append("\t\t\t\t\t\t\t</blockquote>\n");
        builder.append("\t\t\t\t\t\t</blockquote>\n");
    }

    static Document getDocumentOrCrash(Kml kml) {
        Feature feature = kml.getFeature();
        if (feature instanceof Document) {
            return (Document) feature;
        } else {
            throw new RuntimeException("Kml was incorrectly set up with a Feature other than type 'Document'.");
        }
    }


    static Kml setupKml() {
        LOG.info("Coverting GTFS to KML file...");
        DateString ds = new DateString();
        ds.setTimeInMillis(System.currentTimeMillis());
        ds.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));

        final Kml kml = new Kml();
        Document document = kml.createAndSetDocument();
        document.withName("GTFS-to-KML conversion, " + ds.toPrettyString());

        /* Style */
        Style normalStyle = document.createAndAddStyle()
                .withId("normalStyle");
        normalStyle.createAndSetLabelStyle()
                .setScale(0.0);
        normalStyle.createAndSetIconStyle()
                .withIcon(new Icon().withHref(Gtfs2KmlUtils.URL_BUS_ICON))
                .withScale(0.8);

        Style highlightStyle = document.createAndAddStyle()
                .withId("highlightStyle");
        highlightStyle.createAndSetLabelStyle()
                .setScale(1.0);
        highlightStyle.createAndSetIconStyle()
                .withIcon(new Icon().withHref(Gtfs2KmlUtils.URL_BUS_ICON))
                .withScale(1.0);

        StyleMap map = document.createAndAddStyleMap().withId("busStopStyle");
        Pair pairNormal = map.createAndAddPair();
        pairNormal.setKey(StyleState.NORMAL);
        pairNormal.setStyleUrl("#normalStyle");
        Pair pairHighlight = map.createAndAddPair();
        pairHighlight.setKey(StyleState.HIGHLIGHT);
        pairHighlight.setStyleUrl("#highlightStyle");

        kml.setFeature(document);
        return kml;
    }

    static void parseStopsData(File folder) {
        LOG.info("Parsing stop details...");
        String stopFile = folder.getAbsolutePath();
        stopFile += (stopFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_STOPS;
        try (BufferedReader br = IOUtils.getBufferedReader(stopFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for (CSVRecord thisRecord : records) {
                String stopId = thisRecord.get(Gtfs2KmlUtils.FIELD_STOP_ID);

                if (STOPS_DATA.containsKey(stopId)) {
                    LOG.warn("Duplicate stop id '" + stopId + "'.");
                }
                STOPS_DATA.put(stopId, thisRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse stops.");
        }
        LOG.info("Found a total of " + STOPS_DATA.size() + " stops.");
    }

    static void parseRoutesData(File folder) {
        LOG.info("Parsing routes...");
        String routesFile = folder.getAbsolutePath();
        routesFile += (routesFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_ROUTES;
        try (BufferedReader br = IOUtils.getBufferedReader(routesFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for (CSVRecord record : records) {
                String routeId = record.get(Gtfs2KmlUtils.FIELD_ROUTE_ID);

                if (ROUTES_DATA.containsKey(routeId)) {
                    LOG.warn("Duplicate route id '" + routeId + "'.");
                }
                ROUTES_DATA.put(routeId, record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse routes.");
        }
        LOG.info("Found a total of " + ROUTES_DATA.size() + " routes.");
    }

    static void parseTripsData(File folder) {
        LOG.info("Parsing trips...");
        String tripsFile = folder.getAbsolutePath();
        tripsFile += (tripsFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_TRIPS;
        try (BufferedReader br = IOUtils.getBufferedReader(tripsFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for (CSVRecord record : records) {
                String tripId = record.get(Gtfs2KmlUtils.FIELD_TRIP_ID);

                if (TRIPS_DATA.containsKey(tripId)) {
                    LOG.warn("Duplicate trip id '" + tripId + "'.");
                }
                TRIPS_DATA.put(tripId, record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse trips.");
        }
        LOG.info("Found a total of " + TRIPS_DATA.size() + " trips.");
    }

    static void parseStopTimesData(File folder) {
        LOG.info("Parsing stop times...");
        String stopTimesFile = folder.getAbsolutePath();
        stopTimesFile += (stopTimesFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_STOP_TIMES;
        int count = 0;
        try (BufferedReader br = IOUtils.getBufferedReader(stopTimesFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for (CSVRecord record : records) {
                String tripId = record.get(Gtfs2KmlUtils.FIELD_TRIP_ID);
                String stopId = record.get(Gtfs2KmlUtils.FIELD_STOP_ID);
                if (!STOP_TIMES_DATA.containsKey(stopId)) {
                    STOP_TIMES_DATA.put(stopId, new TreeMap<>());
                }
                Map<String, List<CSVRecord>> thisStopTrips = STOP_TIMES_DATA.get(stopId);
                if (!thisStopTrips.containsKey(tripId)) {
                    thisStopTrips.put(tripId, new ArrayList<>());
                }
                thisStopTrips.get(tripId).add(record);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse stop times.");
        }
        LOG.info("Found a total of " + count + " unique stop-time entries.");
    }

    static void parseCalendarData(File folder) {
        LOG.info("Parsing calendars...");
        String calendarFile = folder.getAbsolutePath();
        calendarFile += (calendarFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_CALENDAR;
        try (BufferedReader br = IOUtils.getBufferedReader(calendarFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for(CSVRecord record : records){
                String serviceId = record.get(Gtfs2KmlUtils.FIELD_SERVICE_ID);
                if (!CALENDAR_DATA.containsKey(serviceId)) {
                    CALENDAR_DATA.put(serviceId, record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse stop times.");
        }
        LOG.info("Found a total of " + CALENDAR_DATA.size() + " unique calendar entries.");
    }

    static void parseCalendarExceptionData(File folder) {
        LOG.info("Parsing calendar exceptions...");
        String calendarDatesFile = folder.getAbsolutePath();
        calendarDatesFile += (calendarDatesFile.endsWith("/") ? "" : "/") + Gtfs2KmlUtils.FILE_CALENDAR_DATES;
        try (BufferedReader br = IOUtils.getBufferedReader(calendarDatesFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br).getRecords();
            for(CSVRecord record : records){
                String serviceId = record.get(Gtfs2KmlUtils.FIELD_SERVICE_ID);
                if (!CALENDAR_DATE_DATA.containsKey(serviceId)) {
                    CALENDAR_DATE_DATA.put(serviceId, record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot calendar exceptions.");
        }
        LOG.info("Found a total of " + CALENDAR_DATE_DATA.size() + " unique calendar exception entries.");
    }

    static Map<String, String> getAllRoutesServicingStop(String stopId) {
        Map<String, String> map = new TreeMap<>();
        Set<String> tripsServicingThisStop = STOP_TIMES_DATA.get(stopId).keySet();
        if (!tripsServicingThisStop.isEmpty()) {
            for (String tripId : tripsServicingThisStop) {
                String routeId = getRouteIdFromTripData(tripId);
                String shortName = getShortNameFromRouteData(routeId);
                String longName = getLongNameFromRouteData(routeId);
                if (!map.containsKey(shortName)) {
                    map.put(shortName, longName);
                }
            }
        }
        return map;
    }

    static String getRouteIdFromTripData(String tripId) {
        return TRIPS_DATA.get(tripId).get(Gtfs2KmlUtils.FIELD_ROUTE_ID);
    }

    static String getShortNameFromRouteData(String routeId) {
        return ROUTES_DATA.get(routeId).get(Gtfs2KmlUtils.FIELD_ROUTE_SHORT_NAME);
    }

    static String getLongNameFromRouteData(String routeId) {
        return ROUTES_DATA.get(routeId).get(Gtfs2KmlUtils.FIELD_ROUTE_LONG_NAME);
    }

    static String getStopName(String stopId) {
        return STOPS_DATA.get(stopId).get(Gtfs2KmlUtils.FIELD_STOP_NAME);
    }

    static String getStopDescription(String stopId) {
        return STOPS_DATA.get(stopId).get(Gtfs2KmlUtils.FIELD_STOP_DESCRIPTION);
    }

    static Coord getCoordFromStopsData(String stopId) {
        double lon = Double.parseDouble(STOPS_DATA.get(stopId).get(Gtfs2KmlUtils.FIELD_STOP_LON));
        double lat = Double.parseDouble(STOPS_DATA.get(stopId).get(Gtfs2KmlUtils.FIELD_STOP_LAT));
        return CoordUtils.createCoord(lon, lat);
    }

    static List<String> getStopsServicedByRoute(String routeId, String thisStopId) {
        List<String> listOfStopIds = new ArrayList<>();
        List<String> tripList = getTripsServicingRoute(routeId);
        for (String tripId : tripList) {
            for (String stopId : STOP_TIMES_DATA.keySet()) {
                Set<String> tripsServingThisStop = STOP_TIMES_DATA.get(stopId).keySet();
                if (tripsServingThisStop.contains(tripId)) {
                    if (!listOfStopIds.contains(stopId)) {
                        listOfStopIds.add(stopId);
                    }
                }
            }
        }

        /* Covert Stop Ids to the stop names. */
        List<String> names = new ArrayList<>();
        for (String stopId : listOfStopIds) {
            if (!stopId.equals(thisStopId)) {
                names.add(getStopName(stopId));
            }
        }
        return names;
    }

    static List<String> getTripsServicingRoute(String routeId) {
        List<String> tripList = new ArrayList<>();
        for (String tripId : TRIPS_DATA.keySet()) {
            if (routeId.equals(getRouteIdFromTripData(tripId))) {
                if (!tripList.contains(tripId)) {
                    tripList.add(tripId);
                }
            }
        }
        return tripList;
    }

    static String getRouteIdFromShortName(String shortName) {
        String routeId = null;
        Iterator<String> iterator = ROUTES_DATA.keySet().iterator();
        while (routeId == null & iterator.hasNext()) {
            String id = iterator.next();
            String name = ROUTES_DATA.get(id).get(Gtfs2KmlUtils.FIELD_ROUTE_SHORT_NAME);
            if (name.equals(shortName)) {
                routeId = id;
            }
        }
        return routeId;
    }

    static List<String> getDepartures(String routeId, String stopId, GregorianCalendar day) {
        List<String> list = new ArrayList<>();

        Set<String> tripsServicingThisStop = STOP_TIMES_DATA.get(stopId).keySet();
        for (String tripId : tripsServicingThisStop) {
            String tripServiceId = TRIPS_DATA.get(tripId).get(Gtfs2KmlUtils.FIELD_SERVICE_ID);
            String thisTripRoute = TRIPS_DATA.get(tripId).get(Gtfs2KmlUtils.FIELD_ROUTE_ID);
            if (thisTripRoute.equals(routeId)) {
                if (isDayServiced(tripServiceId, day)) {
                    for (CSVRecord record : STOP_TIMES_DATA.get(stopId).get(tripId)) {
                        String departureTime = record.get(Gtfs2KmlUtils.FIELD_STOP_DEPARTURE_TIME);
                        if (!list.contains(departureTime)) {
                            list.add(departureTime);
                        }
                    }
                }
            }
        }

//
//        for (String tripId : getTripsServicingRoute(routeId)) {
//            List<String>
//            String tripServiceId = TRIPS_DATA.get(tripId).get(Gtfs2KmlUtils.FIELD_SERVICE_ID);
//            if (isDayServiced(tripServiceId, day)) {
//                List<String> tripStops = STOP_TIMES_DATA.get(stop);
//                for (String line : tripStops) {
//                    String[] ssaa = line.split(",");
//                    String thisStopId = ssaa[Gtfs2KmlUtils.FIELD_STOP_TIMES_STOP_ID];
//                    if (thisStopId.equals(stopId)) {
//                        list.add(ssaa[Gtfs2KmlUtils.FIELD_STOP_TIMES_DEPARTURE]);
//                    }
//                }
//            }
//        }

        Collections.sort(list);
        if (list.isEmpty()) {
            list.add("None");
        }
        return list;
    }

    static boolean isDayServiced(String serviceId, GregorianCalendar day) {
        CSVRecord calendarRecord = CALENDAR_DATA.get(serviceId);

        /* Check that the date is within the range */
        GregorianCalendar startDate = Gtfs2KmlUtils.convertStringToCalendar(
                calendarRecord.get(Gtfs2KmlUtils.FIELD_CALENDAR_START_DATE));
        GregorianCalendar endDate = Gtfs2KmlUtils.convertStringToCalendar(
                calendarRecord.get(Gtfs2KmlUtils.FIELD_CALENDAR_END_DATE));
        boolean inRange = Gtfs2KmlUtils.isInDateRange(day, startDate, endDate);

        /* Check that the specific calendar services this day. */
        boolean dayServiced = Gtfs2KmlUtils.isDayOfWeekServiced(day, calendarRecord);

        /* Check that the day is not an exception. */
        boolean dayRemoved = isCalendarRemovedOnDay(serviceId, Gtfs2KmlUtils.convertGregorianCalendarToShortString(day));
        boolean dayAdded = isCalendarAddedOnDay(serviceId, Gtfs2KmlUtils.convertGregorianCalendarToShortString(day));

        /* Put the checks together */
        if (inRange && dayServiced && !dayRemoved) {
            return true;
        } else return inRange && dayServiced && dayAdded;
    }

    static boolean isCalendarAddedOnDay(String serviceId, String date) {
        String exceptionType = getExceptionType(serviceId, date);
        return exceptionType != null && exceptionType.equals("1");
    }

    static boolean isCalendarRemovedOnDay(String serviceId, String date) {
        String exceptionType = getExceptionType(serviceId, date);
        return exceptionType != null && exceptionType.equals("2");
    }

    private static String getExceptionType(String serviceId, String date) {
        if (CALENDAR_DATE_DATA.containsKey(serviceId)) {
            CSVRecord record = CALENDAR_DATE_DATA.get(serviceId);
            String thisDate = record.get(Gtfs2KmlUtils.FIELD_CALENDAR_DATES_DATE);
            if (thisDate.equals(date)) {
                return record.get(Gtfs2KmlUtils.FIELD_CALENDAR_DATES_EXCEPTION_TYPE);
            }
        }
        return null;
    }

}
