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

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.misc.Time;
import org.matsim.up.vehicleBan.VehicleBanChecker;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleImpl;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleTypeImpl;

public class TestEquilVehicleBanCheckerEquil {

	@Test
	public void testIsBannedVehicle() {
		VehicleBanChecker checker = new EquilVehicleBanChecker();
		Vehicle v1 = new VehicleImpl(Id.createVehicleId("test"), new VehicleTypeImpl(Id.create("experiment", VehicleType.class)));
		Assert.assertTrue("Vehicle should be banned.", checker.isBannedVehicle(v1));

		Vehicle v2 = new VehicleImpl(Id.createVehicleId("test"), new VehicleTypeImpl(Id.create("control", VehicleType.class)));
		Assert.assertFalse("Vehicle should not be banned.", checker.isBannedVehicle(v2));
	}


	@Test
	public void testIsBannedLink() {
		VehicleBanChecker checker = new EquilVehicleBanChecker();
		Id<Link> l1 = Id.createLinkId("2");
		Assert.assertFalse("Link should not be banned.", checker.isBannedLink(l1));

		Id<Link> l2 = Id.createLinkId("5");
		Assert.assertTrue("Link should be banned.", checker.isBannedLink(l2));
	}


	@Test
	public void testIsBannedTime() {
		VehicleBanChecker checker = new EquilVehicleBanChecker();
		double t1 = Time.parseTime("06:00:00");
		Assert.assertFalse("Time should not be banned.", checker.isBannedTime(t1));

		double t2 = Time.parseTime("07:15:00");
		Assert.assertTrue("Time should not be banned.", checker.isBannedTime(t2));
	}


	@Test
	public void testIsBanned() {
		VehicleBanChecker checker = new EquilVehicleBanChecker();
		Vehicle v1 = new VehicleImpl(Id.createVehicleId("test"), new VehicleTypeImpl(Id.create("control", VehicleType.class)));
		Vehicle v2 = new VehicleImpl(Id.createVehicleId("test"), new VehicleTypeImpl(Id.create("experiment", VehicleType.class)));

		Id<Link> l1 = Id.createLinkId("2");
		Id<Link> l2 = Id.createLinkId("5");
		
		double t1 = Time.parseTime("06:00:00");
		double t2 = Time.parseTime("07:15:00");
		
		Assert.assertFalse("Should not be banned.", checker.isBanned(v1, l1, t1));
		Assert.assertFalse("Should not be banned.", checker.isBanned(v1, l1, t2));
		Assert.assertFalse("Should not be banned.", checker.isBanned(v1, l2, t1));
		Assert.assertFalse("Should not be banned.", checker.isBanned(v1, l2, t2));
		
		Assert.assertFalse("Should not be banned.", checker.isBanned(v2, l1, t1));
		Assert.assertFalse("Should not be banned.", checker.isBanned(v2, l1, t2));
		Assert.assertFalse("Should not be banned.", checker.isBanned(v2, l2, t1));
	
		Assert.assertTrue("Should be banned.", checker.isBanned(v2, l2, t2));
	}
}


