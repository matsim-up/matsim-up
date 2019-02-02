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

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.scoring.SumScoringFunction.BasicScoring;

/**
 * 
 * @author jwjoubert
 */
public class VehicleBanScoringFunction implements BasicScoring {
	private Person person;
	private final VehicleBanType type;
	private final double marginalUtilityOfMoney;

	VehicleBanScoringFunction(
			Person person, 
			VehicleBanType type,
			double marginalUtilityOfMoney) {
		this.person = person;
		this.type = type;
		this.marginalUtilityOfMoney = marginalUtilityOfMoney;
	}
	
	@Override
	public void finish() {
	}

	@Override
	public double getScore() {
		Object o = person.getSelectedPlan().getAttributes().getAttribute( VehicleBanUtils.ATTRIBUTE_BANNED_ROUTE_FINED );
		if(o != null) {
			boolean fined = (boolean) o;
			if(fined) {
				return -type.getFineWhenCaught()*marginalUtilityOfMoney;
			}
		}
		
		return 0.0;
	}

}
