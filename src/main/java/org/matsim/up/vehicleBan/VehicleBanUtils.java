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
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.controler.Controler;
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


    public static VehicleBanType createVehicleBanType(VehicleBanType.Type type, double probabilityGettingCaught, double fineWhenCaught) {
        return new VehicleBanType(type, probabilityGettingCaught, fineWhenCaught);
    }

    /*FIXME Should not use the Controler anymore.*/
    @Deprecated
    public static Controler createVehicleBanControler(Scenario sc, final VehicleBanType type, final VehicleBanChecker checker) {
        VehicleBanType.Type theType = type.getType();
        VehicleBanController vbc = new VehicleBanController(sc, type.getFineWhenCaught(), type.getProbabilityGettingCaught(), theType.equals(VehicleBanType.Type.FINE_AND_STUCK) ? true : false, checker);
        return vbc.getController();
    }

    public static double getFineFromConfig(Scenario sc) {
        double fine = 0.0;
        ConfigGroup group = sc.getConfig().getModules().get(VehicleBanConfigGroup.NAME);
        if (group == null) {
            throw new IllegalArgumentException("There is no ConfigGroup 'vehicleBan' in the given scenario.");
        }
        return Double.parseDouble(group.getParams().get(VehicleBanConfigGroup.FINE));
    }

    public static VehicleBanModule createModule() {
        return new VehicleBanModule();
    }

    public static VehicleBanModule createModule(double probability, double fine, boolean stuck) {
        VehicleBanModule vehicleBanModule = new VehicleBanModule();
        vehicleBanModule.setVehicleBanParameters(probability, fine, stuck);
        return vehicleBanModule;
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
