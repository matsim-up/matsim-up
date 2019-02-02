/* *********************************************************************** *
 * project: org.matsim.*
 * ElevationControlerListener.java
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

package org.matsim.up.vehicleBan.equil;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.vehicleBan.VehicleBanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to write the route choice proportions to file for the 'equil' 
 * experiments.
 * 
 * @author jwjoubert
 */
class EquilBanControlerListener implements StartupListener, IterationEndsListener, ShutdownListener {

	@Inject
	Scenario sc;

	@Inject
	EquilBanLinkEnterEventHandler eventhandler;

	@Inject EventsManager events;

	EquilBanControlerListener() {
	}

	@Override
	public void notifyShutdown(ShutdownEvent event) {
		/* Calculate the average executed scores of the control and 
		 * experimental groups, respectively. */
		List<Double> scoresControl = new ArrayList<>();
		List<Double> scoresExperiment = new ArrayList<>();
		List<Id<Person>> listOfOffenders = new ArrayList<>();
		for(Person person : sc.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			double score = plan.getScore();
			String vType = sc.getVehicles().getVehicles().get(Id.createVehicleId(person.getId().toString())).getType().getId().toString();
			if(vType.equalsIgnoreCase(EquilBanScenario.VEHICLE_TYPE_CONTROL)) {
				scoresControl.add(score);
			} else {
				scoresExperiment.add(score);
			}

			if(plan.getAttributes().getAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_TRAVELLED ) != null) {
				listOfOffenders.add(person.getId());
			}
		}

		BufferedWriter bw = IOUtils.getBufferedWriter(event.getServices().getControlerIO().getOutputPath() + "/" + 
				EquilBanScenario.OUTPUT_FILENAME_STATS);
		try {
			try {
				bw.write("avgControl,avgExperiment,violators");
				for(int i = 1; i <= listOfOffenders.size(); i++) {
					bw.write(String.format(",v%05d", i));
				}
				bw.newLine();
				bw.write(String.format("%.4f,%.4f,%d", 
						calculateAverage(scoresControl), 
						calculateAverage(scoresExperiment), 
						listOfOffenders.size()));
				for(Id<Person> pId : listOfOffenders) {
					bw.write(String.format(",%s", pId.toString()));
				}
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not write statistics.");
			}
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not close statistics.");
			}
		}
	}


	private double calculateAverage(List<Double> list) {
		double total = 0.0;
		for(Double d : list) {
			total += d;
		}
		return total / ((double) list.size());
	}

	
	@Override
	public void notifyIterationEnds(IterationEndsEvent event) {
		writeEntry(event.getIteration(), event.getServices().getControlerIO().getOutputPath() + "/" + 
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_PEAK, eventhandler.linkMapPeak);
		writeEntry(event.getIteration(), event.getServices().getControlerIO().getOutputPath() + "/" + 
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_OFFPEAK, eventhandler.linkMapOffPeak);

		/* Report the total number of individuals using banned routes. */
		int total = 0;
		for(Person person : sc.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			if(plan.getAttributes().getAttribute( VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_TRAVELLED ) != null) {
				total++;
			}
		}
		Logger.getLogger(EquilBanControlerListener.class).info("Total number of users on banned routes: " + total);
	}

	
	@Override
	public void notifyStartup(StartupEvent event) {
		writeHeader(event.getServices().getControlerIO().getOutputPath() + "/" + 
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_PEAK);
		writeHeader(event.getServices().getControlerIO().getOutputPath() + "/" + 
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_OFFPEAK);
		this.events.addHandler(this.eventhandler);
	}

	
	private void writeHeader(String filename) {
		BufferedWriter bw = IOUtils.getBufferedWriter(filename);
		try{
			bw.write("iter,c1,e1,c2,e2,c3,e3,c4,e4,c5,e5,c6,e6,c7,e7,c8,e8,c9,e9");
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write route choice at startup");
		} finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close route choice writer(s) at startup.");
			}
		}
	}

	
	private void writeEntry(int iteration, String filename, Map<Id<Link>, Integer[]> counts) {
		List<Id<Link>> linkList = EquilBanLinkEnterEventHandler.getLinkWatchList();
		BufferedWriter bw = IOUtils.getAppendingBufferedWriter(filename);
		try{
			bw.write(String.valueOf(iteration));
			for (Id<Link> lid : linkList) {
				Integer[] ia = counts.get(lid);
				bw.write(String.format(",%d,%d", ia[0], ia[1]));
			}
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write route choice after iteration." + iteration);
		} finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close route choice writer after iteration " + iteration);
			}
		}
	}


}
