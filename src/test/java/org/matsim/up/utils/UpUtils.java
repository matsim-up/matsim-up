package org.matsim.up.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Some general utilities used in the University of Pretoria (UP) Centre for
 * Transport Development.
 *
 * @author jwjoubert
 */
public class UpUtils {

    /**
     * Rounding a given value to a certain number of digits. This utility method
     * aims to overcome the typical problem caused by <code>String.format("%.3f", someValue)</code>,
     * which causes the string to use a decimal comma if the {@link Locale} is
     * not explicitly (and correctly) specified.
     *
     * @param value to be rounded;
     * @param places the number of decimal places;
     * @return a double, rounded as we were <i>normally</i> told in school:
     *         values >= 0.5 are rounded up and values < 0.5 are rounded down.
     */
    public static double round(double value, int places){
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
