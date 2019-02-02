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

package org.matsim.up.vehicleBan.equil;

import org.apache.log4j.Logger;
import org.matsim.up.utils.FileUtils;
import org.matsim.up.vehicleBan.VehicleBanType;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class to execute a single {@link RunEquilExperiment} instance, and return
 * the results from the <code>output_vehicleBanStatistics.csv</code> file.
 *  
 * @author jwjoubert
 */
class EquilBanCallable implements Callable<List<String>> {
	final private Logger log = Logger.getLogger(EquilBanCallable.class);
	final private File originalJarFile;
	final private File inputFolder;
	final private File parentOutputFolder;
	final private VehicleBanType type;

	EquilBanCallable(
			final File originalJarFile,
			final File inputFolder,
			final File parentOutputFolder,
			final VehicleBanType type) {
		
		if(!originalJarFile.exists()) {
			throw new IllegalArgumentException("Input jar file does not exist");
		}
		this.originalJarFile = originalJarFile;

		if(!inputFolder.exists() || !inputFolder.isDirectory()) {
			throw new IllegalArgumentException("Input folder does not exist");
		}
		this.inputFolder = inputFolder;

		if(!parentOutputFolder.exists() || !parentOutputFolder.isDirectory()) {
			throw new IllegalArgumentException("Parent output folder does not exist");
		}
		this.parentOutputFolder = parentOutputFolder;

		this.type = type;
	}
	
	
	@Override
	public List<String> call() throws Exception {
		File thisOutputFolder = new File(String.format("%s/%s_%.2f_%04.0f/",
				parentOutputFolder.getAbsolutePath(),
				type.getShortName(), 
				type.getProbabilityGettingCaught(), 
				type.getFineWhenCaught()));
		boolean created = thisOutputFolder.mkdir();
		if(!created){
			throw new RuntimeException("Could not create the output folder " + thisOutputFolder);
		}

		File thisJarFile = new File(thisOutputFolder.getAbsolutePath() + "/thisJar.jar");
		
		FileUtils.copyFile(originalJarFile, thisJarFile);

		ProcessBuilder runBuilder;
		runBuilder = new ProcessBuilder(
				"java",
				"-Xmx2g",
				"-cp",
				"thisJar.jar",
				"org.matsim.up.vehicleBan.equil.RunEquilExperiment",
				type.getType().name(),
				String.valueOf(type.getProbabilityGettingCaught()),
				String.valueOf(type.getFineWhenCaught()),
				inputFolder.getAbsolutePath() + "/network.xml",
				thisOutputFolder.getAbsolutePath()
				);
		runBuilder.directory(thisOutputFolder);
		runBuilder.redirectErrorStream(true);
		final Process equilProcess = runBuilder.start();
		log.info(String.format("Process started for run '%s_%.2f_%04.0f'...", 
				type.getShortName(), 
				type.getProbabilityGettingCaught(), 
				type.getFineWhenCaught()));
		log.info(" in folder " + thisOutputFolder.getAbsolutePath());
		BufferedReader br = new BufferedReader(new InputStreamReader(equilProcess.getInputStream()));
		String line;
		while((line = br.readLine()) != null) {
			/* Do nothing. */
			log.info(line);
		}
		int equilExitCode = equilProcess.waitFor();
		log.info(String.format("Process ended for run '%s_%.2f_%04.0f'; exit status '%d'.", 
				type.getShortName(), 
				type.getProbabilityGettingCaught(), 
				type.getFineWhenCaught(), equilExitCode));
		if(equilExitCode != 0) {
			log.error(String.format("Could not complete run '%s_%.2f_%04.0f'.",
					type.getShortName(),
					type.getProbabilityGettingCaught(), 
					type.getFineWhenCaught()));
		}
		
		/* Clean up. */
		FileUtils.delete(thisJarFile);
		FileUtils.delete(new File(thisOutputFolder.getAbsolutePath() + "/config.xml"));
		FileUtils.delete(new File(thisOutputFolder.getAbsolutePath() + "/network.xml.gz"));
		FileUtils.delete(new File(thisOutputFolder.getAbsolutePath() + "/population.xml.gz"));
		FileUtils.delete(new File(thisOutputFolder.getAbsolutePath() + "/vehicles.xml.gz"));

		String statsFile = String.format("%s/output_%s_%.2f_%04.0f/%s", 
				thisOutputFolder,
				type.getShortName(),
				type.getProbabilityGettingCaught(),
				type.getFineWhenCaught(),
				EquilBanScenario.OUTPUT_FILENAME_STATS);
		String peakFile = String.format("%s/output_%s_%.2f_%04.0f/%s", 
				thisOutputFolder,
				type.getShortName(),
				type.getProbabilityGettingCaught(),
				type.getFineWhenCaught(),
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_PEAK);
		String offpeakFile = String.format("%s/output_%s_%.2f_%04.0f/%s",
				thisOutputFolder,
				type.getShortName(),
				type.getProbabilityGettingCaught(),
				type.getFineWhenCaught(),
				EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_OFFPEAK);
		List<String> paths = new ArrayList<>(3);
		paths.add(statsFile);
		paths.add(peakFile);
		paths.add(offpeakFile);
		
		return paths;
	}

}