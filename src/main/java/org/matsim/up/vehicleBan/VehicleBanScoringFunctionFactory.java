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
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.*;

/**
 * This is mainly a copy-paste version of the standard {@link CharyparNagelScoringFunctionFactory}
 * that I've just adapted slightly for the {@link VehicleBanScoringFunction}.
 *
 * @author jwjoubert
 */
final class VehicleBanScoringFunctionFactory implements ScoringFunctionFactory {
    @Inject
    private Scenario sc;

    VehicleBanScoringFunctionFactory() {
    }

    @Override
    public ScoringFunction createNewScoringFunction(Person person) {
        SumScoringFunction sumScoringFunction = new SumScoringFunction();

        /* Score activities, payments and being stuck with the default MATSim
         * scoring based on utility parameters in the config file. */
        final ScoringParameters params = new ScoringParameters.Builder(sc, person.getId()).build();
        sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params));
        sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, sc.getNetwork()));
        sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params));
        sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));

        /* Add the money scoring of being caught. */
        sumScoringFunction.addScoringFunction(new VehicleBanScoringFunction(person, sc));

        return sumScoringFunction;
    }
}
