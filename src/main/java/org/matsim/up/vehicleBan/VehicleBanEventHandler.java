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

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonMoneyEvent;
import org.matsim.api.core.v01.events.PersonStuckEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.vehicles.Vehicle;

/**
 * Flags a person's selected plan when it's vehicle uses a banned link during
 * a banned period. Once flagged, there is a chance that the person is caught
 * and fined.
 *
 * @author jwjoubert
 */
class VehicleBanEventHandler implements LinkEnterEventHandler {

    private final VehicleBanChecker checker;

    @Inject
    Scenario sc;
    @Inject
    EventsManager eventManager;

    VehicleBanEventHandler(VehicleBanChecker checker) {
        this.checker = checker;
    }


    @Override
    public void handleEvent(LinkEnterEvent event) {
        Vehicle vehicle = sc.getVehicles().getVehicles().get(event.getVehicleId());
        Id<Link> linkId = event.getLinkId();
        double time = event.getTime();

        if (checker.isBanned(vehicle, linkId, time)) {
            /* Updated the flag for using a banned link. The following only
             * works if the vehicle and person has the same Id. */
            Person person = sc.getPopulation().getPersons().get(Id.createPersonId(vehicle.getId().toString()));
            Plan plan = person.getSelectedPlan();
            plan.getAttributes().putAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_TRAVELLED, true);

            handleBannedLink(person, linkId, time);
        }
    }


    private void handleBannedLink(Person person, Id<Link> linkId, double time) {
        Object o = person.getSelectedPlan().getAttributes().getAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED);

        boolean caught = false;
        double rnd = MatsimRandom.getRandom().nextDouble();
        if (rnd < VehicleBanUtils.getConfigGroup(sc).getProbability()) {
            caught = true;
        }

        boolean finedInThePast = false;
        if (o != null) {
            finedInThePast = Boolean.parseBoolean(o.toString());
        }

        if (!finedInThePast) {
            if (caught) {
                person.getSelectedPlan().getAttributes().putAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED, true);

                /* Handle fine on the spot */
                if (VehicleBanUtils.getConfigGroup(sc).isSpotFined()) {
                    eventManager.processEvent(new PersonMoneyEvent(time, person.getId(), -VehicleBanUtils.getConfigGroup(sc).getFine()));

                    /* Instead of the link speed calculator, one can just throw a
                     * stuck event here, and let the config deal with the delay. */
                    if (VehicleBanUtils.getConfigGroup(sc).isStuck()) {
                        eventManager.processEvent(new PersonStuckEvent(time, person.getId(), linkId, null));
                    }
                }
            } else {
                person.getSelectedPlan().getAttributes().putAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED, false);
            }
        }  /* else do nothing, already handled. */
    }

}
