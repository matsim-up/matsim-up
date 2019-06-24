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

package org.matsim.up.vehicleBan.equil;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.misc.Time;
import org.matsim.up.vehicleBan.VehicleBanChecker;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation for the standard 'equil' example. The inner three links are
 * off-limits for vehicles of type 'experiment' between 07:00 and 08:00.
 *
 * @author jwjoubert
 */
final class EquilVehicleBanChecker implements VehicleBanChecker {
	final private Scenario sc;

	EquilVehicleBanChecker(Scenario sc) {
		this.sc = sc;
	}

	@Override
	public boolean isBanned(Id<Vehicle> vehicleId, Id<Link> linkId, double time) {
		boolean banned = false;

		boolean bannedVehicle = isBannedVehicle(vehicleId);
		if (bannedVehicle) {
			boolean bannedTime = isBannedTime(time);
			if (bannedTime) {
				boolean bannedLink = isBannedLink(linkId);
				if (bannedLink) {
					banned = true;
				}
			}

		}
		return banned;
	}


	@Override
	public boolean isBannedVehicle(Id<Vehicle> vehicleId) {
		boolean bannedVehicle = false;
		Vehicle vehicle = sc.getVehicles().getVehicles().get(vehicleId);
		if (vehicle.getType().getId().toString().equalsIgnoreCase("experiment")) {
			bannedVehicle = true;
		}
		return bannedVehicle;
	}


	@Override
	public boolean isBannedLink(Id<Link> linkId) {
		boolean bannedLink = false;
		Iterator<Id<Link>> iterator = getBannedLinksEquil().iterator();
		while (!bannedLink && iterator.hasNext()) {
			Id<Link> nextLink = iterator.next();
			if (nextLink.equals(linkId)) {
				bannedLink = true;
			}
		}
		return bannedLink;
	}


	@Override
	public boolean isBannedTime(double time) {
		boolean bannedTime = false;
		Iterator<Tuple<Double, Double>> iterator = getBannedTimeWindowsEquil().iterator();
		while (!bannedTime && iterator.hasNext()) {
			Tuple<Double, Double> nextTimeWindow = iterator.next();
			if (time >= nextTimeWindow.getFirst() && time < nextTimeWindow.getSecond()) {
				bannedTime = true;
			}
		}
		return bannedTime;
	}

	@Override
	public Plan getSelectedPlan(Id<Vehicle> vehicleId) {
		/* Assuming, as is the case for this equil example, that the vehicle
		Id and the Person Id is the same. */
		return this.sc.getPopulation().getPersons().get(Id.createPersonId(vehicleId.toString())).getSelectedPlan();
	}


	private static List<Tuple<Double, Double>> getBannedTimeWindowsEquil() {
		List<Tuple<Double, Double>> list = new ArrayList<>();
		list.add(new Tuple<>(Time.parseTime("07:00:00"), Time.parseTime("08:00:00")));
		return list;
	}


	private static List<Id<Link>> getBannedLinksEquil() {
		List<Id<Link>> list = new ArrayList<>(6);
		list.add(Id.createLinkId("5"));
		list.add(Id.createLinkId("6"));
		list.add(Id.createLinkId("7"));
		list.add(Id.createLinkId("14"));
		list.add(Id.createLinkId("15"));
		list.add(Id.createLinkId("16"));
		return list;
	}

}
