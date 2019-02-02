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

package org.matsim.up.vehicleBan;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;

/**
 * A class that just allows the normal {@link Controler} to be set up
 * with the correct bindings. The intended use is to construct an
 * instance of this class, and then get the internal {@link Controler}
 * back again using the {@link #getController()} method so that
 * (possibly) additional binding can be performed.
 *
 * @author jwjoubert
 */
public final class VehicleBanController {

    private Controler controller;

    VehicleBanController(Scenario sc, final VehicleBanType type, final VehicleBanChecker checker) {
        controller = new Controler(sc);
        ConfigGroup configGroup = new VehicleBanConfigGroup();
        controller.getConfig().addModule(configGroup);

        /* All experiments must have the following set up so that we can flag a
         * selected plan as using a banned route. */
        controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                this.addControlerListenerBinding().to(VehicleBanControlerListener.class);
                this.addEventHandlerBinding().toInstance(new VehicleBanEventHandler(checker, type, false));
            }
        });
        controller.setScoringFunctionFactory(new VehicleBanScoringFunctionFactory(sc, type));
    }

    /**
     * Returning the controller if one wants to add additional bindings.
     *
     * @return the {@link Controler}.
     */
    public Controler getController() {
        return this.controller;
    }

}
