/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
package org.matsim.up.vehicleBan;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.up.utils.Header;

/**
 * Example of how to implement and execute the vehicle ban module. For a more
 * complete example, with a population, see the 'equil' folder.
 */
public class RunVehicleBan {

    public static void main(String[] args){
        Header.printHeader(RunVehicleBan.class, args);

        Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Controler controler = new Controler(sc);

//        VehicleBanModule module = VehicleBanUtils.createModule(0.25, 1000.0, true);
        VehicleBanModule module = VehicleBanUtils.createModule();
        module.setVehicleBanChecker( VehicleBanUtils.createVehicleBanCheckerThatAllowsAllLinks());
        module.setFineOnSpot(true);

        controler.addOverridingModule(module);

        controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        controler.getConfig().controler().setLastIteration(2);
        controler.run();

        Header.printFooter();
    }

}
