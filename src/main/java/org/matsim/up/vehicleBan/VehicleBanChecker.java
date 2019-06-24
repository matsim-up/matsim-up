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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.vehicles.Vehicle;

/**
 * Generic class to deal with checking if a vehicle is considered <i>banned</i>
 * on a specific link at a specific time. 
 *  
 * @author jwjoubert
 */

public interface VehicleBanChecker {

	boolean isBanned(Id<Vehicle> vehicleId, Id<Link> linkId, double time);

	boolean isBannedVehicle(Id<Vehicle> vehicleId);

	boolean isBannedLink(Id<Link> linkId);

	boolean isBannedTime(double time);

	/**
	 * This method is necessary as the {@link Vehicle} may be from one of many
	 * possible sources, for example the main {@link org.matsim.api.core.v01.Scenario},
	 * the freight carriers, etc.
	 *
	 * @param vehicleId the vehicle entering the (possibly banned) link.
	 * @return the selected {@link Plan} of the agent associated with the
	 * 		   given vehicle.
	 */
	Plan getSelectedPlan(Id<Vehicle> vehicleId);
}
