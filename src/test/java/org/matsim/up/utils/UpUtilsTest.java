package org.matsim.up.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.matsim.testcases.MatsimTestUtils;

public class UpUtilsTest {

    @Test
    public void round() {
        Assertions.assertEquals(1.234, UpUtils.round(1.2342d, 3), MatsimTestUtils.EPSILON, "Incorrect rounding");
        Assertions.assertEquals(1.234, UpUtils.round(1.2335d, 3), MatsimTestUtils.EPSILON, "Incorrect rounding");
    }
}