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
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.controler.events.BeforeMobsimEvent;
import org.matsim.core.controler.listener.BeforeMobsimListener;

/**
 * Resets the banned flags for each person's selected plan.
 * 
 * @author jwjoubert
 */
public final class VehicleBanControlerListener implements BeforeMobsimListener {

	@Inject private Scenario sc;

	@Override
	public void notifyBeforeMobsim(BeforeMobsimEvent event) {
		/* Clear all the banned travel attributes. */
		for(Person person : sc.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			plan.getAttributes().removeAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_TRAVELLED);
			plan.getAttributes().removeAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED);
		}
	}

}
