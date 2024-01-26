package org.matsim.up.vehicleBan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;

public class VehicleBanCheckerTest {


    @Test
    public void testBuilder() {
        try {
            VehicleBanChecker checker = buildVehicleBanChecker();
        } catch (Exception e) {
            Assertions.fail("Should build the VehicleBanChecker without exception.");
        }
    }

    @Test
    public void testVehicleBanChecker() {
        VehicleBanChecker checker = buildVehicleBanChecker();

        Id<Vehicle> bannedVehicleId = Id.create("v1", Vehicle.class);
        Id<Vehicle> NonBannedVehicleId = Id.create("v2", Vehicle.class);
        Id<Link> bannedLink = Id.createLinkId("l1");
        Id<Link> NonBannedLink = Id.createLinkId("l2");
        double bannedTime = Time.parseTime("07:00:00");
        double NonBannedTime = Time.parseTime("09:00:00");

        Assertions.assertTrue(checker.isBannedVehicle(bannedVehicleId), "Vehicle should be banned.");
        Assertions.assertFalse(checker.isBannedVehicle(NonBannedVehicleId), "Vehicle should not be banned.");

        Assertions.assertTrue(checker.isBannedLink(bannedLink), "Link should be banned.");
        Assertions.assertFalse(checker.isBannedLink(NonBannedLink), "Link should not be banned.");

        Assertions.assertTrue(checker.isBannedTime(bannedTime), "Time should be banned");
        Assertions.assertFalse(checker.isBannedTime(NonBannedTime), "Time should not be banned");

        Assertions.assertTrue(checker.isBanned(bannedVehicleId, bannedLink, bannedTime), "Should be banned");
    }


    private VehicleBanChecker buildVehicleBanChecker() {
        VehicleBanChecker checker = new VehicleBanChecker() {
            private Id<Vehicle> bannedVehicle = Id.create("v1", Vehicle.class);
            private Id<Link> bannedLink = Id.createLinkId("l1");

            @Override
            public boolean isBanned(Id<Vehicle> vehicleId, Id<Link> linkId, double time) {
                return isBannedVehicle(vehicleId) && isBannedLink(linkId) && isBannedTime(time);
            }

            @Override
            public boolean isBannedVehicle(Id<Vehicle> vehicleId) {
                return vehicleId.equals(bannedVehicle);
            }

            @Override
            public boolean isBannedLink(Id<Link> linkId) {
                return linkId.equals(bannedLink);
            }

            @Override
            public boolean isBannedTime(double time) {
                return time >= Time.parseTime("06:00:00") && time <= Time.parseTime("08:00:00");
            }

            @Override
            public Plan getSelectedPlan(Id<Vehicle> vehicleId) {
                return null;
            }
        };
        return checker;
    }
}