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
import org.matsim.core.controler.Controler;

/**
 * General utilities, independent of use case, to deal with modelling
 * modal bans.
 * 
 * @author jwjoubert
 */
public class VehicleBanUtils {
	final public static String ATTRIBUTE_BANNED_ROUTE_TRAVELLED = "bannedRouteTravelled";
	final static String ATTRIBUTE_BANNED_ROUTE_FINED = "bannedRouteFined";


	public static VehicleBanType createVehicleBanType(VehicleBanType.Type type, double probabilityGettingCaught, double fineWhenCaught) {
		return new VehicleBanType(type, probabilityGettingCaught, fineWhenCaught);
	}

	public static Controler createVehicleBanControler(Scenario sc, final VehicleBanType type, final VehicleBanChecker checker){
		VehicleBanController vbc = new VehicleBanController(sc, type, checker);
		return vbc.getController();
	}

}
