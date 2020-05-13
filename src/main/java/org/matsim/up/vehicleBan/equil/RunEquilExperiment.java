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
import org.matsim.up.utils.Header;
import org.matsim.up.vehicleBan.VehicleBanChecker;
import org.matsim.up.vehicleBan.VehicleBanModule;
import org.matsim.up.vehicleBan.VehicleBanUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;


/**
 * Class to run the 'equil' network with a modal ban imposed on the middle three
 * links.
 *
 * @author jwjoubert
 */
class RunEquilExperiment {

    /**
     * @param args The following arguments are required, and in the following order:
     *             <ol>
     *             <li> probability of being caught, in the range [0.0, 1.0];
     *             <li> fine when caught (in monetary units);
     *             <li> boolean indicating if a {@link org.matsim.api.core.v01.events.PersonStuckEvent}
     *             should be thrown along with fine;
     *             <li> (optional) path to network file; and
     *             <li> (optional) path to output folder.
     *             </ol>
     */
    public static void main(String[] args) {
        Header.printHeader(RunEquilExperiment.class, args);

        String[] fullArgs = new String[5];
        fullArgs[0] = args[0];
        fullArgs[1] = args[1];
        fullArgs[2] = args[2];

        if (args.length == 3) {
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
        double probabilityBeingCaught = Double.parseDouble(args[0]);
        double fineWhenCaught = Double.parseDouble(args[1]);
        boolean stuck = Boolean.parseBoolean(args[2]);

        String equilPath = args[3];
        String dumpLocation = args[4];

        Scenario sc = EquilBanScenario.Builder.newInstance()
                .setEquilPath(equilPath)
                .setNumberOfIterations(10)
                .setNumberOfPersons(10000)
                .setProbabilityBeingCaught(probabilityBeingCaught)
                .setFineWhenCaught(fineWhenCaught)
                .setStuck(stuck)
                .setScenarioDumpLocation(dumpLocation)
                .setSeed(20181213L)
                .setVehicleTypeExperiment(buildEquilExperimentVehicleType())
                .build();

        VehicleBanChecker checker = new EquilVehicleBanChecker(sc);
        VehicleBanModule module = VehicleBanUtils.createModule(probabilityBeingCaught, fineWhenCaught, stuck);
        module.setVehicleBanChecker(checker);

        Controler controler = new Controler(sc);
        controler.addOverridingModule(module);

        /* The following is just so that more output is generated, and you can
         * track how the agents move away from the banned links. These are
         * completely optional, and was just used in Working Paper 086 that was
         * submitted for ABMTRANS 2019. */
        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                this.addControlerListenerBinding().to(EquilBanControlerListener.class);
                this.bind(EquilBanLinkEnterEventHandler.class);
                this.addPlanStrategyBinding(EquilBanScenario.STRATEGY_RANDOM_REROUTE).toProvider(EquilBanRandomRouterFactory.class);
            }
        });

        controler.run();
    }


    private static VehicleType buildEquilExperimentVehicleType() {
        VehicleType type = VehicleUtils.createVehicleType(Id.create("experiment", VehicleType.class));
        type.setLength(15.0);
        type.setPcuEquivalents(2.0);
        type.setMaximumVelocity(80.0 / 3.6);
//        VehicleCapacity capacity = new VehicleCapacity();
//        capacity.setSeats(4);
//        type.setCapacity(capacity);
        return type;
    }

}
