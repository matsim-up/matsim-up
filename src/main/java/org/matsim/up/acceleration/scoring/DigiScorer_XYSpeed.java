/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,     *
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

/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,     *
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

package org.matsim.up.acceleration.scoring;

import org.apache.log4j.Logger;
import org.matsim.up.acceleration.grid.DigiGrid_XYSpeed;


/**
 * Basic interface to calculate the risk profile/score of a person, all based 
 * on the raw accelerometer records provided. The x and y-dimensions relate to
 * acceleration, while the z-axis relates to speed. To ensure the z-dimension 
 * is scaled in a useful manner, a multiplier is introduced.
 *  
 * @author jwjoubert
 */
public interface DigiScorer_XYSpeed extends DigiScorer{
	Logger LOG = Logger.getLogger(DigiScorer_XYZ.class);
	
	void buildScoringModel(String filename);

	RISK_GROUP getRiskGroup(String record);

	void rateIndividuals(String filename, String outputFolder);

	DigiGrid_XYSpeed getGrid();

	void setGrid(DigiGrid_XYSpeed grid);
}
