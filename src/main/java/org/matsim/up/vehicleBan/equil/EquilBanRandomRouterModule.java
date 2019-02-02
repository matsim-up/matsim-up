/* *********************************************************************** *
 * project: org.matsim.*
 * EquilRandomRouter.java
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
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.replanning.PlanStrategyModule;
import org.matsim.core.replanning.ReplanningContext;

/**
 *
 * @author jwjoubert
 */
class EquilBanRandomRouterModule implements PlanStrategyModule {

	private final Scenario sc;
	
	@Inject
	public EquilBanRandomRouterModule(Scenario sc) {
		this.sc = sc;
	}
	
	@Override
	public void prepareReplanning(ReplanningContext replanningContext) {
	}

	@Override
	public void handlePlan(Plan plan) {
		Leg legOut = (Leg) plan.getPlanElements().get(1);
		legOut.setRoute(EquilBanScenario.sampleEquilRoute(sc.getNetwork()));

		Leg legIn = (Leg) plan.getPlanElements().get(3);
		legIn.setRoute(EquilBanScenario.getReturnRoute(sc.getNetwork()));
	}

	@Override
	public void finishReplanning() {
	}

}
