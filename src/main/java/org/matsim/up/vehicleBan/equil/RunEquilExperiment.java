/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.examples.ExamplesUtils;
import org.matsim.up.utils.FileUtils;
import org.matsim.up.utils.Header;
import org.matsim.up.vehicleBan.VehicleBanChecker;
import org.matsim.up.vehicleBan.VehicleBanType;
import org.matsim.up.vehicleBan.VehicleBanUtils;
import org.matsim.vehicles.VehicleCapacity;
import org.matsim.vehicles.VehicleCapacityImpl;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleTypeImpl;

import java.io.File;


/**
 * Class to run the equil network with a modal ban imposed.
 * 
 * @author jwjoubert
 */
class RunEquilExperiment {

	public static void main(String[] args) {
		Header.printHeader(RunEquilExperiment.class, args);

		String[] fullArgs = new String[5];
		fullArgs[0] = args[0];
		fullArgs[1] = args[1];
		fullArgs[2] = args[2];

		if(args.length == 3) {
			fullArgs[3] = ExamplesUtils.getTestScenarioURL("equil").getFile() + "network.xml";
			fullArgs[4] = "./output/";
		} else {
			fullArgs[3] = args[3];
			fullArgs[4] = args[4];
		}
		run(fullArgs);
		Header.printFooter();
	}
	
	
	private static void run(final String[] args) {
		VehicleBanType experiment = VehicleBanUtils.createVehicleBanType(
				VehicleBanType.Type.valueOf(args[0]),
				Double.parseDouble(args[1]),
				Double.parseDouble(args[2]));
		
		String equilPath = args[3];
		String dumpLocation = args[4];
		
		Scenario sc = EquilBanScenario.Builder.newInstance()
				.setEquilPath(equilPath)
				.setExperiment(experiment)
				.setNumberOfIterations(200)
				.setNumberOfPersons(10000)
				.setScenarioDumpLocation(dumpLocation)
				.setSeed(20181213L)
				.setVehicleTypeExperiment(buildEquilExperimentVehicleType())
				.build();
		
		VehicleBanChecker checker = new EquilVehicleBanChecker();
		Controler controler = VehicleBanUtils.createVehicleBanControler(sc, experiment, checker);
		/* All experiments must have the following set up so that we can write
		 * the route choices to file. */
		controler.addOverridingModule(new AbstractModule() {			
			@Override
			public void install() {
				this.addControlerListenerBinding().to(EquilBanControlerListener.class);
				this.bind(EquilBanLinkEnterEventHandler.class);
				this.addPlanStrategyBinding(EquilBanScenario.STRATEGY_RANDOM_REROUTE).toProvider(EquilBanRandomRouterFactory.class);
			}
		});

		controler.run();
		FileUtils.delete(new File(controler.getConfig().controler().getOutputDirectory() + "ITERS/"));
	}
	
	
	private static VehicleType buildEquilExperimentVehicleType() {
		VehicleType type = new VehicleTypeImpl(Id.create("experiment", VehicleType.class));
		type.setLength(15.0);
		type.setPcuEquivalents(2.0);
		type.setMaximumVelocity(80.0/3.6);
		VehicleCapacity capacity = new VehicleCapacityImpl();
		capacity.setSeats(4);
		type.setCapacity(capacity);
		return type;
	}
	
}