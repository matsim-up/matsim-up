package org.matsim.up.utils;

import org.junit.Assert;
import org.matsim.testcases.MatsimTestUtils;

public class UpUtilsTest {

    @org.junit.Test
    public void round() {
        Assert.assertEquals("Incorrect rounding", 1.234, UpUtils.round(1.2342d, 3), MatsimTestUtils.EPSILON);
        Assert.assertEquals("Incorrect rounding", 1.234, UpUtils.round(1.2335d, 3), MatsimTestUtils.EPSILON);
    }
}