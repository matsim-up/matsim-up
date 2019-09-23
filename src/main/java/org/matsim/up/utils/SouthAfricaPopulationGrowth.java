/* *********************************************************************** *
 * project: org.matsim.*
 * SAPopulationGrowthConverter.java
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

package org.matsim.up.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Class to account for estimated population growth in South Africa. The values used in this class is taken
 * from the latest release of the <a href=http://www.statssa.gov.za/?page_id=1854&PPN=P0302>P0302 -
 * Mid-year population estimates</a> report from Statistics South Africa. More specifically, we use the
 * accompanying summary document <i>District Council projection by sex and age (2002-2017)</i>.<br><br>
 *
 * <b><i>Note:</i></b> All growth figures are provided relative to the base year of 2011, the latest census
 * year in South Africa.
 *
 * @author jwjoubert
 */
public class SouthAfricaPopulationGrowth {
	private static Map<StudyArea, Map<Integer, Double>> growthMap;
	private static final String DEFAULT_YEAR = "2019";
	private static final String DEFAULT_AREA = "All";

	/**
	 * Quick run to report the cumulative (since 2011) growth for a given area.
	 *
	 * @param args two (optional) arguments in the following order:
	 *             <ol>
	 *             		<li> the year (> 2011) for which the growth must be calculated; and
	 *             		<li> {@link StudyArea} description, or "All" to report for all areas.
	 *             </ol>
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{DEFAULT_YEAR, DEFAULT_AREA};
		}

		Header.printHeader(SouthAfricaPopulationGrowth.class, args);
		int year = Integer.parseInt(args[0]);
		if (args[1].equalsIgnoreCase("All")) {
			for (StudyArea area : StudyArea.values()) {
				String growth = String.format("Cumulative growth for the period 2011-%d for '%s': %.5f",
						year,
						area.toString(),
						SouthAfricaPopulationGrowth.getGrowthFactor(area, year));
				Logger.getLogger(SouthAfricaPopulationGrowth.class).info(growth);
			}
		} else {
			StudyArea area = StudyArea.valueOf(args[1]);
			String growth = String.format("Cumulative growth for the period 2011-%d for '%s': %.5f",
					year,
					area.toString(),
					SouthAfricaPopulationGrowth.getGrowthFactor(area, year));
			Logger.getLogger(SouthAfricaPopulationGrowth.class).info(growth);
		}

		Header.printFooter();
	}

	/**
	 * Get the cumulative growth factor for a given study area, relative to 2011 (the last census). For
	 * example, if there was a 5% growth, the value 1.05 is returned.
	 *
	 * @param area the study area that must be one of {@link StudyArea}; and
	 * @param year the year (>2011) for which the growth factor is returned.
	 * @return the cumulative growth factor (since 2011).
	 */
	static double getGrowthFactor(StudyArea area, int year) {
		populateGrowthMap();
		if (year < 2012) {
			throw new IllegalArgumentException(
					"Can only provide growth for 2012 onwards as 2011 is the base year.");
		}
		double factor = 1.0;

		int dummyYear = 2012;
		while (dummyYear <= year) {
			factor *= growthMap.get(area).get(dummyYear);
			dummyYear++;
		}

		return factor;
	}


	/**
	 * Manually adding the annual growth factor for each study area. This should be updated as and when new
	 * mid-year population estimates are released by Statistics South Africa. The values captured here are
	 * year-on-year growth figures.
	 */
	private static void populateGrowthMap() {
		growthMap = new HashMap<>();

		/* Buffalo City */
		Map<Integer, Double> buffaloCityMap = new TreeMap<>();
		buffaloCityMap.put(2012, 1.00256);
		buffaloCityMap.put(2013, 1.00198);
		buffaloCityMap.put(2014, 1.00130);
		buffaloCityMap.put(2015, 1.00080);
		buffaloCityMap.put(2016, 1.00064);
		buffaloCityMap.put(2017, 1.00037);
		buffaloCityMap.put(2018, 1.00205); /* Provincial data, 20180802 */
		buffaloCityMap.put(2019, 1.00124); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.BuffaloCity, buffaloCityMap);

		/* City of Cape Town */
		Map<Integer, Double> capeTownMap = new TreeMap<>();
		capeTownMap.put(2012, 1.02053);
		capeTownMap.put(2013, 1.02006);
		capeTownMap.put(2014, 1.01950);
		capeTownMap.put(2015, 1.01895);
		capeTownMap.put(2016, 1.01851);
		capeTownMap.put(2017, 1.01867);
		capeTownMap.put(2018, 1.01883); /* Provincial data, 20180802 */
		capeTownMap.put(2019, 1.01831); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.CapeTownFunctional, capeTownMap);

		/* eThekwini */
		Map<Integer, Double> eThekwiniMap = new TreeMap<>();
		eThekwiniMap.put(2012, 1.00670);
		eThekwiniMap.put(2013, 1.00714);
		eThekwiniMap.put(2014, 1.00761);
		eThekwiniMap.put(2015, 1.00813);
		eThekwiniMap.put(2016, 1.00874);
		eThekwiniMap.put(2017, 1.01118);
		eThekwiniMap.put(2018, 1.01139); /* Provincial data, 20180802 */
		eThekwiniMap.put(2019, 1.01138); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.eThekwini, eThekwiniMap);

		/* Gauteng */
		Map<Integer, Double> gautengMap = new TreeMap<>();
		gautengMap.put(2012, 1.02620);
		gautengMap.put(2013, 1.02620);
		gautengMap.put(2014, 1.02609);
		gautengMap.put(2015, 1.02616);
		gautengMap.put(2016, 1.02640);
		gautengMap.put(2017, 1.02675);
		gautengMap.put(2018, 1.02650); /* Provincial data, 20180802 */
		gautengMap.put(2019, 1.02463); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.Gauteng, gautengMap);

		/* Mangaung */
		Map<Integer, Double> mangaungMap = new TreeMap<>();
		mangaungMap.put(2012, 1.00605);
		mangaungMap.put(2013, 1.00618);
		mangaungMap.put(2014, 1.00626);
		mangaungMap.put(2015, 1.00644);
		mangaungMap.put(2016, 1.00679);
		mangaungMap.put(2017, 1.00666);
		mangaungMap.put(2018, 1.00631); /* Provincial data, 20180802 */
		mangaungMap.put(2019, 1.00405); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.Mangaung, mangaungMap);

		/* Mbombela */
		Map<Integer, Double> mbombelaMap = new TreeMap<>();
		mbombelaMap.put(2012, 1.00814);
		mbombelaMap.put(2013, 1.00888);
		mbombelaMap.put(2014, 1.00972);
		mbombelaMap.put(2015, 1.01042);
		mbombelaMap.put(2016, 1.01093);
		mbombelaMap.put(2017, 1.01198);
		mbombelaMap.put(2018, 1.01710); /* Provincial data, 20180802 */
		mbombelaMap.put(2019, 1.01590); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.Mbombela, mbombelaMap);

		/* Nelson Mandela Bay Metropolitan */
		Map<Integer, Double> nmbmMap = new TreeMap<>();
		nmbmMap.put(2012, 1.00893);
		nmbmMap.put(2013, 1.00930);
		nmbmMap.put(2014, 1.00955);
		nmbmMap.put(2015, 1.00998);
		nmbmMap.put(2016, 1.01073);
		nmbmMap.put(2017, 1.01009);
		nmbmMap.put(2018, 1.00205); /* Provincial data, 20180802 */
		nmbmMap.put(2019, 1.00124); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.NelsonMandelaBay, nmbmMap);

		/* Polokwane */
		Map<Integer, Double> polokwaneMap = new TreeMap<>();
		polokwaneMap.put(2012, 1.00584);
		polokwaneMap.put(2013, 1.00596);
		polokwaneMap.put(2014, 1.00636);
		polokwaneMap.put(2015, 1.00661);
		polokwaneMap.put(2016, 1.00665);
		polokwaneMap.put(2017, 1.00734);
		polokwaneMap.put(2018, 1.01001); /* Provincial data, 20180802 */
		polokwaneMap.put(2019, 1.00831); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.Polokwane, polokwaneMap);

		/* Rustenburg */
		Map<Integer, Double> rustenburgMap = new TreeMap<>();
		rustenburgMap.put(2012, 1.02248);
		rustenburgMap.put(2013, 1.02321);
		rustenburgMap.put(2014, 1.02375);
		rustenburgMap.put(2015, 1.02416);
		rustenburgMap.put(2016, 1.02453);
		rustenburgMap.put(2017, 1.02446);
		rustenburgMap.put(2018, 1.01744); /* Provincial data, 20180802 */
		rustenburgMap.put(2019, 1.01626); /* Provincial data, 20190729 */
		growthMap.put(StudyArea.Rustenburg, rustenburgMap);
	}


	public enum StudyArea {
		BuffaloCity, CapeTownFunctional, eThekwini, Gauteng, Mangaung, Mbombela, NelsonMandelaBay,
		Polokwane, Rustenburg
	}

}
