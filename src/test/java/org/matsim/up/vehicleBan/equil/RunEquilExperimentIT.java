package org.matsim.up.vehicleBan.equil;

import org.junit.Test;

import static org.junit.Assert.*;

public class RunEquilExperimentIT {

    @Test
    public void testMain() {
        String[] args = new String[5];
        args[0] = String.valueOf(0.25);
        args[1] = String.valueOf(500.0);
        args[2] = String.valueOf(true);
        args[3] = "./input/equil/network.xml";
        args[4] = "./output/";

        RunEquilExperiment.main(args);
    }
}