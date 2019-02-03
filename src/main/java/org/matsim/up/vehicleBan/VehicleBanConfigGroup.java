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

import org.matsim.core.config.ReflectiveConfigGroup;

import java.util.Map;

/**
 * @author jwjoubert
 */
final class VehicleBanConfigGroup extends ReflectiveConfigGroup {
    public final static String NAME = "vehicleBan";

    final static String PROBABILITY = "probability";
    final static String FINE = "fine";
    final static String STUCK = "stuck";
    final static String FINE_ON_THE_SPOT = "spotFined";

    private double fine = 0.0;
    private double probability = 0.0;
    private boolean stuck = false;
    private boolean spotFined = false;

    VehicleBanConfigGroup() {
        super(NAME);
    }


    @Override
    public Map<String, String> getComments() {
        Map<String, String> comments = super.getComments();
        comments.put(FINE, "The size of the (monetary) fine when caught. Default value is 0.0");
        comments.put(STUCK, "Whether or not an 'AgentStuckEvent' should be thrown over-and-above the fine (when caught). " +
                "Possible values: 'true' and 'false' (default)");
        comments.put(PROBABILITY, "The probability, in the range [0,1] of being caught. Default value is 0.0");
        comments.put(FINE_ON_THE_SPOT, "Boolean variable indicating if a vehicle is fined every time it enters a banned link. " +
                "A value 'false' (default) will see a vehicle only fined once per trip, irrespective of the number of links traversed. " +
                "A value 'true' will incur a fine for every banned link entry (as in automated ITS enforcement).");
        return comments;
    }

    @StringGetter(FINE)
    public double getFine() {
        return this.fine;
    }

    @StringSetter(FINE)
    public void setFine(double fine) {
        this.fine = fine;
    }

    @StringGetter(STUCK)
    public boolean isStuck() {
        return stuck;
    }

    @StringSetter(STUCK)
    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    @StringGetter(PROBABILITY)
    public double getProbability() {
        return probability;
    }

    @StringSetter(PROBABILITY)
    public void setProbability(double probability) {
        this.probability = probability;
    }

    @StringGetter(FINE_ON_THE_SPOT)
    public boolean isSpotFined() {
        return spotFined;
    }

    @StringSetter(FINE_ON_THE_SPOT)
    public void setSpotFined(boolean spotFined) {
        this.spotFined = spotFined;
    }
}
