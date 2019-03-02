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
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.ConfigUtils;
import org.matsim.vehicles.Vehicle;

/**
 * General utilities, independent of use case, to deal with modelling
 * modal bans.
 *
 * @author jwjoubert
 */
public class VehicleBanUtils {
    final public static String ATTRIBUTE_BANNED_ROUTE_TRAVELLED = "bannedRouteTravelled";
    final static String ATTRIBUTE_BANNED_ROUTE_FINED = "bannedRouteFined";


    public static VehicleBanModule createModule() {
        return new VehicleBanModule();
    }

    public static VehicleBanModule createModule(double probability, double fine, boolean stuck) {
        VehicleBanModule vehicleBanModule = new VehicleBanModule();
        vehicleBanModule.setVehicleBanParameters(probability, fine, stuck);
        return vehicleBanModule;
    }

    public static VehicleBanConfigGroup getConfigGroup(Scenario sc){
        if(!sc.getConfig().getModules().containsKey( VehicleBanConfigGroup.NAME )){
            throw new IllegalArgumentException("Cannot find 'vehicleBan' ConfigGroup.");
        }
        return ConfigUtils.addOrGetModule(sc.getConfig(), VehicleBanConfigGroup.NAME, VehicleBanConfigGroup.class);
    }


    public static VehicleBanChecker createVehicleBanCheckerThatAllowsAllLinks() {
        return new VehicleBanChecker() {
            @Override
            public boolean isBanned(Vehicle vehicle, Id<Link> linkId, double time) {
                return false;
            }

            @Override
            public boolean isBannedVehicle(Vehicle vehicle) {
                return false;
            }

            @Override
            public boolean isBannedLink(Id<Link> linkId) {
                return false;
            }

            @Override
            public boolean isBannedTime(double time) {
                return false;
            }
        };
    }

}
