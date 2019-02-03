package org.matsim.up.vehicleBan;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.vehicles.Vehicle;

public class VehicleBanControllerTest {

    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();

    @Test
    public void testControlerConfigGroup() {
        Controler controler = null;
        try {
            controler = initControler();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should instantiate Controler without exceptions.");
        }
        new ConfigWriter(controler.getConfig()).write(utils.getOutputDirectory() + "config.xml");

        Config config2 = ConfigUtils.createConfig();
        new ConfigReader(config2).readFile(utils.getOutputDirectory() + "config.xml");
        ConfigGroup group = config2.getModules().get(VehicleBanConfigGroup.NAME);

        Assert.assertNotNull("Should find config group.", group);
        Assert.assertEquals("Wrong fine.", 0.0, Double.parseDouble( group.getParams().get("fine") ), MatsimTestUtils.EPSILON);
        Assert.assertEquals("Wrong probability.", 0.0, Double.parseDouble( group.getParams().get("probability") ), MatsimTestUtils.EPSILON);
        Assert.assertFalse("Should not be stuck.", Boolean.parseBoolean( group.getParams().get("stuck") ));
        Assert.assertFalse("Should not be spot-fined.", Boolean.parseBoolean( group.getParams().get("spotFined") ));
    }

    private Controler initControler() {
        Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        VehicleBanType type = VehicleBanUtils.createVehicleBanType(VehicleBanType.Type.FINE_ONLY, 0.0, 0.0);
        VehicleBanChecker checker = VehicleBanUtils.createVehicleBanCheckerThatAllowsAllLinks();
        return VehicleBanUtils.createVehicleBanControler(sc, type, checker);
    }

}