package org.matsim.up.vehicleBan;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleImpl;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleTypeImpl;

public class VehicleBanCheckerTest {

    @Test
    public void testBuilder() {
        try {
            VehicleBanChecker checker = buildVehicleBanChecker();
        } catch (Exception e) {
            Assert.fail("Should build the VehicleBanChecker without exception.");
        }
    }

    @Test
    public void testVehicleBanChecker() {
        VehicleBanChecker checker = buildVehicleBanChecker();

        Vehicle bannedVehicle = new VehicleImpl(Id.create("v1", Vehicle.class), new VehicleTypeImpl(Id.create("v1Type", VehicleType.class)));
        Vehicle NonBannedVehicle = new VehicleImpl(Id.create("v2", Vehicle.class), new VehicleTypeImpl(Id.create("v1Type", VehicleType.class)));
        Id<Link> bannedLink = Id.createLinkId("l1");
        Id<Link> NonBannedLink = Id.createLinkId("l2");
        double bannedTime = Time.parseTime("07:00:00");
        double NonBannedTime = Time.parseTime("09:00:00");

        Assert.assertTrue("Vehicle should be banned.", checker.isBannedVehicle(bannedVehicle));
        Assert.assertFalse("Vehicle should not be banned.", checker.isBannedVehicle(NonBannedVehicle));

        Assert.assertTrue("Link should be banned.", checker.isBannedLink(bannedLink));
        Assert.assertFalse("Link should not be banned.", checker.isBannedLink(NonBannedLink));

        Assert.assertTrue("Time should be banned", checker.isBannedTime(bannedTime));
        Assert.assertFalse("Time should not be banned", checker.isBannedTime(NonBannedTime));

        Assert.assertTrue("Should be banned", checker.isBanned(bannedVehicle, bannedLink, bannedTime));
    }

    private VehicleBanChecker buildVehicleBanChecker() {
        VehicleBanChecker checker = new VehicleBanChecker() {
            private Id<Vehicle> bannedVehicle = Id.create("v1", Vehicle.class);
            private Id<Link> bannedLink = Id.createLinkId("l1");

            @Override
            public boolean isBanned(Vehicle vehicle, Id<Link> linkId, double time) {
                return isBannedVehicle(vehicle) && isBannedLink(linkId) && isBannedTime(time);
            }

            @Override
            public boolean isBannedVehicle(Vehicle vehicle) {
                return vehicle.getId().equals(bannedVehicle);
            }

            @Override
            public boolean isBannedLink(Id<Link> linkId) {
                return linkId.equals(bannedLink);
            }

            @Override
            public boolean isBannedTime(double time) {
                return time >= Time.parseTime("06:00:00") && time <= Time.parseTime("08:00:00");
            }
        };
        return checker;
    }
}