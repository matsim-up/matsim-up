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

import org.apache.log4j.Logger;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;

public final class VehicleBanModule extends AbstractModule {


    final private static Logger LOG = Logger.getLogger(VehicleBanModule.class);

    private Double probability;
    private Double fine;
    private Boolean stuck;
    private VehicleBanChecker checker;
    private Boolean spotFined = false;

    private boolean reportParameters = false;


    VehicleBanModule() {
        LOG.info("VehicleBanModule instantiated");
    }


    public void setVehicleBanParameters(final double probability, final double fine, final boolean stuck) {
        this.probability = probability;
        this.fine = fine;
        this.stuck = stuck;
    }


    public void setVehicleBanChecker(VehicleBanChecker checker) {
        this.checker = checker;
    }

    public void setFineOnSpot(boolean fineOnTheSpot) {
        this.spotFined = fineOnTheSpot;
    }


    @Override
    public void install() {
        /* Set up the ConfigGroup */
        VehicleBanConfigGroup configGroup;
        if (!getConfig().getModules().containsKey(VehicleBanConfigGroup.NAME)) {
            configGroup = new VehicleBanConfigGroup();
            getConfig().addModule(configGroup);
        }
        configGroup = ConfigUtils.addOrGetModule(getConfig(), VehicleBanConfigGroup.NAME, VehicleBanConfigGroup.class);
        if (!reportParameters) {
            if (this.probability != null) {
                LOG.warn("   Overwriting VehicleBan probability");
                configGroup.setProbability(this.probability);
            } else {
                this.probability = configGroup.getProbability();
            }
            if (this.fine != null) {
                LOG.warn("   Overwriting VehicleBan fine");
                configGroup.setFine(this.fine);
            } else {
                this.fine = configGroup.getFine();
            }
            if (this.stuck != null) {
                LOG.warn("   Overwriting VehicleBan stuck");
                configGroup.setStuck(this.stuck);
            } else {
                this.stuck = configGroup.isStuck();
            }
            if (this.spotFined != null) {
                LOG.warn("   Overwriting VehicleBan spotFined");
                configGroup.setSpotFined(this.spotFined);
            }
            LOG.warn(this.toString());
            reportParameters = true;
        }

        addControlerListenerBinding().to(VehicleBanControlerListener.class);

        if (this.checker == null) {
            throw new RuntimeException("Must provide a VehicleBanChecker instance.");
        }
        addEventHandlerBinding().toInstance(new VehicleBanEventHandler(checker));

        bindScoringFunctionFactory().toInstance(new VehicleBanScoringFunctionFactory());
    }


    public String toString() {
        return "VehicleBanModule: " +
                "Fine=" +
                this.fine +
                "; Probability=" +
                this.probability +
                "; with AgentStuckEvent=" +
                this.stuck +
                "; spotFined=" +
                this.spotFined;
    }
}
