/* *********************************************************************** *
 * project: org.matsim.*
 * ElevationEventHandler.java
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
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.up.vehicleBan.VehicleBanChecker;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is used to report link use statistics for the 'equil' experiments.
 * 
 * @author jwjoubert
 */
class EquilBanLinkEnterEventHandler implements LinkEnterEventHandler {
	Map<Id<Link>, Integer[]> linkMapPeak;
	Map<Id<Link>, Integer[]> linkMapOffPeak;
	private final VehicleBanChecker checker;

	@Inject private Scenario sc;

	public EquilBanLinkEnterEventHandler() {
		this.checker = new EquilVehicleBanChecker();
		this.linkMapPeak = setupLinkMap();
		this.linkMapOffPeak = setupLinkMap();
	}

	@Override
	public void reset(int iteration) {
		this.linkMapPeak = setupLinkMap();
		this.linkMapOffPeak = setupLinkMap();
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		Vehicle vehicle = sc.getVehicles().getVehicles().get( event.getVehicleId() );
		Id<Link> linkId = event.getLinkId();
		double time = event.getTime();

		if(getLinkWatchList().contains(linkId)) {
			Map<Id<Link>, Integer[]> linkMap;
			
			boolean bannedTime = checker.isBannedTime(time);
			if(bannedTime) {
				linkMap = linkMapPeak;
			} else {
				linkMap = linkMapOffPeak;
			}

			String vehicleType = vehicle.getType().getId().toString();
			int index;
			if(vehicleType.equalsIgnoreCase(EquilBanScenario.VEHICLE_TYPE_CONTROL)){
				index = 0;
			} else if(vehicleType.equalsIgnoreCase(EquilBanScenario.VEHICLE_TYPE_EXPERIMENT)){
				index = 1;
			} else{
				throw new RuntimeException("Don't know what to do with vehicle of type '" + vehicleType + "'");
			}
			
			Integer[] ia = linkMap.get(linkId);
			int oldValue = ia[index];
			ia[index] = oldValue + 1;
		}
	}


	private Map<Id<Link>, Integer[]> setupLinkMap(){
		Map<Id<Link>, Integer[]> map = new TreeMap<>();
		for(Id<Link> linkId : getLinkWatchList()) {
			map.put(linkId, new Integer[] {0,0});
		}
		return map;
	}


	static List<Id<Link>> getLinkWatchList(){
		List<Id<Link>> list = new ArrayList<>(9);
		list.add(Id.createLinkId("2"));
		list.add(Id.createLinkId("3"));
		list.add(Id.createLinkId("4"));
		list.add(Id.createLinkId("5"));
		list.add(Id.createLinkId("6"));
		list.add(Id.createLinkId("7"));
		list.add(Id.createLinkId("8"));
		list.add(Id.createLinkId("9"));
		list.add(Id.createLinkId("10"));
		return list;
	}

}
