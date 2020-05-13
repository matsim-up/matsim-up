/* *********************************************************************** *
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
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.ConfigWriter.Verbosity;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.TypicalDurationScoreComputation;
import org.matsim.core.config.groups.QSimConfigGroup.LinkDynamics;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.examples.ExamplesUtils;
import org.matsim.vehicles.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Building an adapted equil network for testing modal ban.
 *
 * @author jwjoubert
 */
class EquilBanScenario {
    final static String VEHICLE_TYPE_CONTROL = "control";
    final static String VEHICLE_TYPE_EXPERIMENT = "experiment";
    final static String STRATEGY_RANDOM_REROUTE = "randomReroute";

    final static String OUTPUT_FILENAME_STATS = "output_vehicleBanStatistics.csv";
    final static String OUTPUT_FILENAME_ROUTE_CHOICE_PEAK = "routeChoice_peak.csv";
    final static String OUTPUT_FILENAME_ROUTE_CHOICE_OFFPEAK = "routeChoice_offpeak.csv";

    private EquilBanScenario() {
    }

    static class Builder {
        private Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        private int numberOfPersons = 1000;
        private int numberOfIterations = 100;
        private String equilPath = ExamplesUtils.getTestScenarioURL("equil").getFile() + "network.xml";
        private VehicleType controlVehicleType = VehicleUtils.createVehicleType(Id.create(VEHICLE_TYPE_CONTROL, VehicleType.class));
        private VehicleType experimentVehicleType = VehicleUtils.createVehicleType(Id.create(VEHICLE_TYPE_EXPERIMENT, VehicleType.class));
        private long seed = 1234L;
        private String dumpLocation = null;
        private double probability = 0.0;
        private double fine = 0.0;
        private boolean stuck = false;

        private Builder() {
        }

        static Builder newInstance() {
            return new Builder();
        }

        Builder setEquilPath(String path) {
            if (path != null) {
                this.equilPath = path;
            }
            return this;
        }

        Builder setProbabilityBeingCaught(double probability){
            this.probability = probability;
            return this;
        }

        Builder setFineWhenCaught(double fine){
            this.fine = fine;
            return this;
        }

        Builder setStuck(boolean stuck){
            this.stuck = stuck;
            return this;
        }

        Builder setNumberOfIterations(int iterations) {
            this.numberOfIterations = iterations;
            return this;
        }

        Builder setNumberOfPersons(int number) {
            this.numberOfPersons = number;
            return this;
        }

        Builder setScenarioDumpLocation(String folder) {
            if (folder != null) {
                folder += folder.endsWith("/") ? "" : "/";
                this.dumpLocation = folder;
            }
            return this;
        }

        Builder setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        Builder setVehicleTypeExperiment(VehicleType type) {
            this.experimentVehicleType = type;
            return this;
        }

        Scenario build() {
            setupConfig();
            sc.getVehicles().addVehicleType(controlVehicleType);
            sc.getVehicles().addVehicleType(experimentVehicleType);
            sc.getConfig().qsim().setVehiclesSource(VehiclesSource.fromVehiclesData);

            /* Read the network */
            File equilNetworkFile = new File(equilPath);
            new MatsimNetworkReader(sc.getNetwork()).readFile(equilNetworkFile.getAbsolutePath());

            /* Create the population */
            VehiclesFactory vf = sc.getVehicles().getFactory();
            PopulationFactory pf = sc.getPopulation().getFactory();
            for (int i = 0; i < numberOfPersons; i++) {
                Plan plan = PopulationUtils.createPlan();

                Activity a = pf.createActivityFromLinkId("a", Id.createLinkId("1"));
                a.setEndTime(Time.parseTime("06:30:00") + MatsimRandom.getRandom().nextDouble() * Time.parseTime("02:00:00"));
                plan.addActivity(a);

                Leg la = pf.createLeg("car");
                la.setRoute(sampleEquilRoute(sc.getNetwork()));
                plan.addLeg(la);


                Activity b = pf.createActivityFromLinkId("b", Id.createLinkId("21"));
                b.setMaximumDuration(Time.parseTime("08:00:00"));
                plan.addActivity(b);

                Leg lb = pf.createLeg("car");
                lb.setRoute(getReturnRoute(sc.getNetwork()));
                plan.addLeg(lb);

                Activity c = pf.createActivityFromLinkId("a", Id.createLinkId("1"));
                plan.addActivity(c);

                Person person = pf.createPerson(Id.createPersonId(String.valueOf(i)));
                person.addPlan(plan);
                sc.getPopulation().addPerson(person);

                VehicleType vehicleType = sampleVehicleType();
                person.getAttributes().putAttribute("vehicleType", vehicleType.getId().toString());
                Vehicle vehicle = vf.createVehicle(Id.createVehicleId(i), vehicleType);
                sc.getVehicles().addVehicle(vehicle);

                /* Link the vehicle to the person. */
                VehicleUtils.insertVehicleIdIntoAttributes(person, "car", vehicle.getId());
            }


            if (this.dumpLocation != null) {
                File file = new File(this.dumpLocation);
                if (!file.exists()) {
                    boolean success = file.mkdirs();
                    if (!success) {
                        throw new RuntimeException("Could not create the output location " + this.dumpLocation);
                    }
                }

                new PopulationWriter(sc.getPopulation()).write(dumpLocation + "population.xml.gz");
                new VehicleWriterV1(sc.getVehicles()).writeFile(dumpLocation + "vehicles.xml.gz");
                new NetworkWriter(sc.getNetwork()).write(dumpLocation + "network.xml.gz");
                new ConfigWriter(sc.getConfig(), Verbosity.all).write(dumpLocation + "config.xml");
            }

            return this.sc;
        }


        private void setupConfig() {
            Config config = sc.getConfig();
            config.global().setRandomSeed(this.seed);

            config.controler().setFirstIteration(0);
            config.controler().setLastIteration(numberOfIterations);
            config.controler().setWriteEventsInterval(10);
            config.controler().setWritePlansInterval(10);

            config.qsim().setLinkDynamics(LinkDynamics.PassingQ);
            config.qsim().setRemoveStuckVehicles(true);
            config.qsim().setStuckTime(Time.parseTime("01:00:00"));

            config.controler().setOutputDirectory(String.format("%s/output_%s_%.2f_%04.0f/",
                    dumpLocation,
                    this.stuck ? "stuck" : "fine",
                    this.probability,
                    this.fine));
            config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
            config.qsim().setEndTime(Time.parseTime("24:00:00"));

            ActivityParams params_a = new ActivityParams("a");
            params_a.setEarliestEndTime(Time.parseTime("05:00:00"));
            params_a.setTypicalDuration(Time.parseTime("18:00:00"));
            params_a.setTypicalDurationScoreComputation(TypicalDurationScoreComputation.relative);
            config.planCalcScore().addActivityParams(params_a);

            ActivityParams params_b = new ActivityParams("b");
            params_b.setOpeningTime(Time.parseTime("08:00:00"));
            params_b.setClosingTime(Time.parseTime("16:00:00"));
            params_b.setTypicalDuration(Time.parseTime("04:00:00"));
            params_b.setTypicalDurationScoreComputation(TypicalDurationScoreComputation.relative);
            config.planCalcScore().addActivityParams(params_b);

            StrategySettings changeExpBeta = new StrategySettings();
            changeExpBeta.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
            changeExpBeta.setWeight(0.85);
            config.strategy().addStrategySettings(changeExpBeta);

            StrategySettings timing = new StrategySettings();
            timing.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.TimeAllocationMutator);
            timing.setWeight(0.10);
            timing.setDisableAfter((int) Math.round(0.9 * numberOfIterations));
            config.strategy().addStrategySettings(timing);

            StrategySettings reroute = new StrategySettings();
            reroute.setStrategyName(STRATEGY_RANDOM_REROUTE);
            reroute.setWeight(0.10);
            reroute.setDisableAfter((int) Math.round(0.9 * numberOfIterations));
            config.strategy().addStrategySettings(reroute);
        }


        private VehicleType sampleVehicleType() {
            double rnd = MatsimRandom.getRandom().nextDouble();
            if (rnd < 0.5) {
                return this.controlVehicleType;
            } else {
                return this.experimentVehicleType;
            }
        }


    }

    static Route sampleEquilRoute(Network network) {
        List<Id<Link>> links = new ArrayList<>(4);
        links.add(Id.createLinkId("1"));

        int option = MatsimRandom.getRandom().nextInt(9);
        switch (option) {
            case 0:
                links.add(Id.createLinkId("2"));
                links.add(Id.createLinkId("11"));
                break;
            case 1:
                links.add(Id.createLinkId("3"));
                links.add(Id.createLinkId("12"));
                break;
            case 2:
                links.add(Id.createLinkId("4"));
                links.add(Id.createLinkId("13"));
                break;
            case 3:
                links.add(Id.createLinkId("5"));
                links.add(Id.createLinkId("14"));
                break;
            case 4:
                links.add(Id.createLinkId("6"));
                links.add(Id.createLinkId("15"));
                break;
            case 5:
                links.add(Id.createLinkId("7"));
                links.add(Id.createLinkId("16"));
                break;
            case 6:
                links.add(Id.createLinkId("8"));
                links.add(Id.createLinkId("17"));
                break;
            case 7:
                links.add(Id.createLinkId("9"));
                links.add(Id.createLinkId("18"));
                break;
            case 8:
                links.add(Id.createLinkId("10"));
                links.add(Id.createLinkId("19"));
                break;
            default:
                throw new IllegalArgumentException("There is only 9 routes. Cannot interpret option '" + option + "'");
        }
        links.add(Id.createLinkId("20"));
        links.add(Id.createLinkId("21"));

        Route route = RouteUtils.createNetworkRoute(links, network);
        route.setDistance(35000);
        return route;
    }

    static Route getReturnRoute(Network network) {
        List<Id<Link>> links = new ArrayList<>(4);
        links.add(Id.createLinkId("21"));
        links.add(Id.createLinkId("22"));
        links.add(Id.createLinkId("23"));
        links.add(Id.createLinkId("1"));

        Route route = RouteUtils.createNetworkRoute(links, network);
        route.setDistance(55000);
        return route;
    }


}
