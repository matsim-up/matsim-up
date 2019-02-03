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
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scoring.SumScoringFunction.BasicScoring;

/**
 * @author jwjoubert
 */
final class VehicleBanScoringFunction implements BasicScoring {
    private Person person;
    private Scenario sc;

    VehicleBanScoringFunction(Person person, Scenario sc) {
        this.person = person;
        this.sc = sc;
    }

    @Override
    public void finish() {
    }

    @Override
    public double getScore() {

        ConfigGroup configGroup = sc.getConfig().getModules().get(VehicleBanConfigGroup.NAME);
        if (configGroup == null) {
            throw new RuntimeException("Cannot find the 'VehicleBanConfigGroup' for scoring parameters");
        }
        double fineWhenCaught = Double.parseDouble(configGroup.getParams().get(VehicleBanConfigGroup.FINE));

//        double fineWhenCaught = ConfigUtils.addOrGetModule(config, VehicleBanConfigGroup.NAME, VehicleBanConfigGroup.class).getFine();
        Object o = person.getSelectedPlan().getAttributes().getAttribute(VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED);
        if (o != null) {
            boolean fined = (boolean) o;
            if (fined) {
                return -fineWhenCaught * sc.getConfig().planCalcScore().getMarginalUtilityOfMoney();
            }
        }

        return 0.0;
    }

}
