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
	private static final String DEFAULT_YEAR = "2022";
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
					area,
					SouthAfricaPopulationGrowth.getGrowthFactor(area, year));
			Logger.getLogger(SouthAfricaPopulationGrowth.class).info(growth);
		}

		Header.printFooter();
	}

	/**
	 * Hide the constructor.
	 */
	private SouthAfricaPopulationGrowth(){}

	/**
	 * Get the cumulative growth factor for a given study area, relative to 2011 (the last census). For
	 * example, if there was a 5% growth, the value 1.05 is returned.
	 *
	 * @param area the study area that must be one of {@link StudyArea}; and
	 * @param year the year (>2011) for which the growth factor is returned.
	 * @return the cumulative growth factor (since 2011).
	 */
	public static double getGrowthFactor(StudyArea area, int year) {
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
		buffaloCityMap.put(2020, 1.00050); /* Provincial data, 20200709 */
		buffaloCityMap.put(2021, 0.99367); /* District data, 20200719 */
		buffaloCityMap.put(2022, 0.99872); /* Provincial data, 20200728 */
		growthMap.put(StudyArea.BuffaloCity, buffaloCityMap);

		/* Bushbuckridge (Mpumalanga, Ehlanzeni District) */
		Map<Integer, Double> bushbuckMap = new TreeMap<>();
		bushbuckMap.put(2012, 1.00898); /* District data, 20210719 */
		bushbuckMap.put(2013, 1.00851); /* District data, 20210719 */
		bushbuckMap.put(2014, 1.00805); /* District data, 20210719 */
		bushbuckMap.put(2015, 1.00710); /* District data, 20210719 */
		bushbuckMap.put(2016, 1.00602); /* District data, 20210719 */
		bushbuckMap.put(2017, 1.00857); /* District data, 20210719 */
		bushbuckMap.put(2018, 1.00966); /* District data, 20210719 */
		bushbuckMap.put(2019, 1.00929); /* District data, 20210719 */
		bushbuckMap.put(2020, 1.00779); /* District data, 20210719 */
		bushbuckMap.put(2021, 1.00376); /* District data, 20210719 */
		bushbuckMap.put(2022, 1.01116); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Bushbuckridge, bushbuckMap);

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
		capeTownMap.put(2020, 1.01839); /* Provincial data, 20200709 */
		capeTownMap.put(2021, 1.01547); /* District data, 20210719 */
		capeTownMap.put(2022, 1.01541); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.CapeTownFunctional, capeTownMap);

		/* Eastern Cape */
		Map<Integer, Double> easternCapeMap = new TreeMap<>();
		easternCapeMap.put(2012,1.00226); /* Provincial data, 20220728 */
		easternCapeMap.put(2013,1.00244); /* Provincial data, 20220728 */
		easternCapeMap.put(2014,1.00257); /* Provincial data, 20220728 */
		easternCapeMap.put(2015,1.00208); /* Provincial data, 20220728 */
		easternCapeMap.put(2016,1.00132); /* Provincial data, 20220728 */
		easternCapeMap.put(2017,1.00174); /* Provincial data, 20220728 */
		easternCapeMap.put(2018,1.00193); /* Provincial data, 20220728 */
		easternCapeMap.put(2019,1.00241); /* Provincial data, 20220728 */
		easternCapeMap.put(2020,1.00113); /* Provincial data, 20220728 */
		easternCapeMap.put(2021,0.99805); /* Provincial data, 20220728 */
		easternCapeMap.put(2022,0.99872); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.EasternCape, easternCapeMap);

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
		eThekwiniMap.put(2020, 1.01199); /* Provincial data, 20200709 */
		eThekwiniMap.put(2021, 1.00869); /* District data, 20210719 */
		eThekwiniMap.put(2022, 1.00801); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.eThekwini, eThekwiniMap);

		/* Free State */
		Map<Integer, Double> freeStateMap = new TreeMap<>();
		freeStateMap.put(2012, 1.00543); /* Provincial data, 20220728 */
		freeStateMap.put(2013, 1.00537); /* Provincial data, 20220728 */
		freeStateMap.put(2014, 1.00539); /* Provincial data, 20220728 */
		freeStateMap.put(2015, 1.00486); /* Provincial data, 20220728 */
		freeStateMap.put(2016, 1.00426); /* Provincial data, 20220728 */
		freeStateMap.put(2017, 1.00533); /* Provincial data, 20220728 */
		freeStateMap.put(2018, 1.00539); /* Provincial data, 20220728 */
		freeStateMap.put(2019, 1.00567); /* Provincial data, 20220728 */
		freeStateMap.put(2020, 1.00489); /* Provincial data, 20220728 */
		freeStateMap.put(2021, 1.00113); /* Provincial data, 20220728 */
		freeStateMap.put(2022, 1.00264); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.FreeState, freeStateMap);

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
		gautengMap.put(2020, 1.02352); /* Provincial data, 20200709 */
		gautengMap.put(2021, 1.01916); /* District data, 20210719 */
		gautengMap.put(2022, 1.01837); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Gauteng, gautengMap);

		/* KwaZulu Natal */
		Map<Integer, Double> kznMap = new TreeMap<>();
		kznMap.put(2012, 1.01150); /* Provincial data, 20220728 */
		kznMap.put(2013, 1.01166); /* Provincial data, 20220728 */
		kznMap.put(2014, 1.01186); /* Provincial data, 20220728 */
		kznMap.put(2015, 1.01147); /* Provincial data, 20220728 */
		kznMap.put(2016, 1.01087); /* Provincial data, 20220728 */
		kznMap.put(2017, 1.01068); /* Provincial data, 20220728 */
		kznMap.put(2018, 1.01080); /* Provincial data, 20220728 */
		kznMap.put(2019, 1.01122); /* Provincial data, 20220728 */
		kznMap.put(2020, 1.01013); /* Provincial data, 20220728 */
		kznMap.put(2021, 1.00708); /* Provincial data, 20220728 */
		kznMap.put(2022, 1.00801); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.KwaZuluNatal, kznMap);

		/* Limpopo */
		Map<Integer, Double> limpopoMap = new TreeMap<>();
		limpopoMap.put(2012, 1.00912); /* Provincial data, 20220728 */
		limpopoMap.put(2013, 1.00965); /* Provincial data, 20220728 */
		limpopoMap.put(2014, 1.01017); /* Provincial data, 20220728 */
		limpopoMap.put(2015, 1.01007); /* Provincial data, 20220728 */
		limpopoMap.put(2016, 1.00964); /* Provincial data, 20220728 */
		limpopoMap.put(2017, 1.00926); /* Provincial data, 20220728 */
		limpopoMap.put(2018, 1.00917); /* Provincial data, 20220728 */
		limpopoMap.put(2019, 1.00954); /* Provincial data, 20220728 */
		limpopoMap.put(2020, 1.00830); /* Provincial data, 20220728 */
		limpopoMap.put(2021, 1.00548); /* Provincial data, 20220728 */
		limpopoMap.put(2022, 1.00581); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Limpopo, limpopoMap);

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
		mangaungMap.put(2020, 1.00512); /* Provincial data, 20200709 */
		mangaungMap.put(2021, 1.00571); /* District data, 20210719 */
		mangaungMap.put(2022, 1.00264); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Mangaung, mangaungMap);

		/* Maruleng (Limpopo, Mopani District) */
		Map<Integer, Double> marulengMap = new TreeMap<>();
		marulengMap.put(2012, 1.01015); /* District data, 20210719 */
		marulengMap.put(2013, 1.00953); /* District data, 20210719 */
		marulengMap.put(2014, 1.00910); /* District data, 20210719 */
		marulengMap.put(2015, 1.00832); /* District data, 20210719 */
		marulengMap.put(2016, 1.00754); /* District data, 20210719 */
		marulengMap.put(2017, 1.00777); /* District data, 20210719 */
		marulengMap.put(2018, 1.00843); /* District data, 20210719 */
		marulengMap.put(2019, 1.00874); /* District data, 20210719 */
		marulengMap.put(2020, 1.00767); /* District data, 20210719 */
		marulengMap.put(2021, 1.00502); /* District data, 20210719 */
		marulengMap.put(2022, 1.00581); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Maruleng, marulengMap);

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
		mbombelaMap.put(2020, 1.01531); /* Provincial data, 20200709 */
		mbombelaMap.put(2021, 1.00376); /* District data, 20210719 */
		mbombelaMap.put(2022, 1.01116); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Mbombela, mbombelaMap);

		/* Mpumalanga */
		Map<Integer, Double> mpumalangaMap = new TreeMap<>();
		mpumalangaMap.put(2012, 1.01688); /* Provincial data, 20220728 */
		mpumalangaMap.put(2013, 1.01604); /* Provincial data, 20220728 */
		mpumalangaMap.put(2014, 1.01534); /* Provincial data, 20220728 */
		mpumalangaMap.put(2015, 1.01416); /* Provincial data, 20220728 */
		mpumalangaMap.put(2016, 1.01298); /* Provincial data, 20220728 */
		mpumalangaMap.put(2017, 1.01379); /* Provincial data, 20220728 */
		mpumalangaMap.put(2018, 1.01469); /* Provincial data, 20220728 */
		mpumalangaMap.put(2019, 1.01522); /* Provincial data, 20220728 */
		mpumalangaMap.put(2020, 1.01438); /* Provincial data, 20220728 */
		mpumalangaMap.put(2021, 1.01066); /* Provincial data, 20220728 */
		mpumalangaMap.put(2022, 1.01116); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Mpumalanga, mpumalangaMap);

		/* Namakwa (Northern Cape) including Calvinia */
		Map<Integer, Double> hantamMap = new TreeMap<>();
		hantamMap.put(2012, 0.99875);
		hantamMap.put(2013, 0.99854);
		hantamMap.put(2014, 0.99856);
		hantamMap.put(2015, 0.99833);
		hantamMap.put(2016, 0.99825);
		hantamMap.put(2017, 1.00173);
		hantamMap.put(2018, 1.00210);
		hantamMap.put(2019, 1.00215);
		hantamMap.put(2020, 1.00209);
		hantamMap.put(2021, 0.99876); /* District data, 20210719 */
		hantamMap.put(2022, 1.00864); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Namakwa, hantamMap);

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
		nmbmMap.put(2020, 1.00050); /* Provincial data, 20200709 */
		nmbmMap.put(2021, 0.99825); /* District data, 20210719 */
		nmbmMap.put(2022, 0.99872); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.NelsonMandelaBay, nmbmMap);

		/* Northern Cape */
		Map<Integer, Double> northernCapeMap = new TreeMap<>();
		northernCapeMap.put(2012, 1.01202); /* Provincial data, 20220728 */
		northernCapeMap.put(2013, 1.01256); /* Provincial data, 20220728 */
		northernCapeMap.put(2014, 1.01316); /* Provincial data, 20220728 */
		northernCapeMap.put(2015, 1.01314); /* Provincial data, 20220728 */
		northernCapeMap.put(2016, 1.01293); /* Provincial data, 20220728 */
		northernCapeMap.put(2017, 1.01279); /* Provincial data, 20220728 */
		northernCapeMap.put(2018, 1.01229); /* Provincial data, 20220728 */
		northernCapeMap.put(2019, 1.01232); /* Provincial data, 20220728 */
		northernCapeMap.put(2020, 1.01186); /* Provincial data, 20220728 */
		northernCapeMap.put(2021, 1.00778); /* Provincial data, 20220728 */
		northernCapeMap.put(2022, 1.00864); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.NorthernCape, northernCapeMap);

		/* North West */
		Map<Integer, Double> northWestMap = new TreeMap<>();
		northWestMap.put(2012, 1.01731); /* Provincial data, 20220728 */
		northWestMap.put(2013, 1.01745); /* Provincial data, 20220728 */
		northWestMap.put(2014, 1.01769); /* Provincial data, 20220728 */
		northWestMap.put(2015, 1.01735); /* Provincial data, 20220728 */
		northWestMap.put(2016, 1.01689); /* Provincial data, 20220728 */
		northWestMap.put(2017, 1.01671); /* Provincial data, 20220728 */
		northWestMap.put(2018, 1.01657); /* Provincial data, 20220728 */
		northWestMap.put(2019, 1.01661); /* Provincial data, 20220728 */
		northWestMap.put(2020, 1.01603); /* Provincial data, 20220728 */
		northWestMap.put(2021, 1.01174); /* Provincial data, 20220728 */
		northWestMap.put(2022, 1.01251); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.NorthWest, northWestMap);

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
		polokwaneMap.put(2020, 1.00651); /* Provincial data, 20200709 */
		polokwaneMap.put(2021, 1.00204); /* District data, 20210719 */
		polokwaneMap.put(2022, 1.00581); /* Provincial data, 20220728 */
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
		rustenburgMap.put(2020, 1.01678); /* Provincial data, 20200709 */
		rustenburgMap.put(2021, 1.01848); /* District data, 20210719 */
		rustenburgMap.put(2022, 1.01251); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Rustenburg, rustenburgMap);

		/* Xariep (Free State) including Philipolis */
		Map<Integer, Double> xariepMap = new TreeMap<>();
		xariepMap.put(2012, 1.00326);
		xariepMap.put(2013, 1.00354);
		xariepMap.put(2014, 1.00384);
		xariepMap.put(2015, 1.00388);
		xariepMap.put(2016, 1.00387);
		xariepMap.put(2017, 1.00550);
		xariepMap.put(2018, 1.00544);
		xariepMap.put(2019, 1.00523);
		xariepMap.put(2020, 1.00445);
		xariepMap.put(2021, 0.99984); /* District data, 20210719 */
		xariepMap.put(2022, 1.00264); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.Xariep, xariepMap);

		/* Western Cape */
		Map<Integer, Double> wcMap = new TreeMap<>();
		wcMap.put(2012, 1.02120); /* Provincial data, 20220728 */
		wcMap.put(2013, 1.02099); /* Provincial data, 20220728 */
		wcMap.put(2014, 1.02093); /* Provincial data, 20220728 */
		wcMap.put(2015, 1.02032); /* Provincial data, 20220728 */
		wcMap.put(2016, 1.01973); /* Provincial data, 20220728 */
		wcMap.put(2017, 1.01961); /* Provincial data, 20220728 */
		wcMap.put(2018, 1.01973); /* Provincial data, 20220728 */
		wcMap.put(2019, 1.01982); /* Provincial data, 20220728 */
		wcMap.put(2020, 1.01914); /* Provincial data, 20220728 */
		wcMap.put(2021, 1.01461); /* Provincial data, 20220728 */
		wcMap.put(2022, 1.01541); /* Provincial data, 20220728 */
		growthMap.put(StudyArea.WesternCape, wcMap);
	}


	public enum StudyArea {
		BuffaloCity, Bushbuckridge, CapeTownFunctional, EasternCape, eThekwini, FreeState, Gauteng, KwaZuluNatal, Limpopo, Mangaung,
		Maruleng, Mbombela, Mpumalanga, NelsonMandelaBay, NorthernCape, NorthWest, Polokwane, Rustenburg, Xariep, Namakwa, WesternCape
	}

}
