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

package org.matsim.up.acceleration.grid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jzy3d.maths.Coord3d;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Counter;

/**
 * Class that implements the container for the three-dimensional grid 
 * containing the centroids of the polyhedra that is used for the Digicore 
 * accelerometer research. Associated with the grid is the number of 
 * observations in each dodecahedron (cell) and also the rating of each cell. 
 * All three dimensions are related to acceleration.
 *
 * @see DigiGrid3D
 * @author jwjoubert
 */
public class DigiGrid_XYZ extends DigiGrid3D{
	final private Logger LOG = Logger.getLogger(DigiGrid_XYZ.class);
	private int COLUMN_X_ACCELERATION = 5;
	private int COLUMN_Y_ACCELERATION = 6;
	private int COLUMN_Z_ACCELERATION = 7;
	private boolean INPUT_HAS_HEADER = false;

	public DigiGrid_XYZ(double scale) {
		super(scale);
	}
	
	/**
	 * Override the default columns where the x, y, and z acceleration values
	 * are found in the input data.
	 * 
	 * @param xColumn
	 * @param yColumn
	 * @param zColumn
	 */
	public void setInputColumns(int xColumn, int yColumn, int zColumn) {
		COLUMN_X_ACCELERATION = xColumn;
		COLUMN_Y_ACCELERATION = yColumn;
		COLUMN_Z_ACCELERATION = zColumn;
	}
	
	/**
	 * Override the default indicating if the input data has a header or not.
	 * The default is that there is <i><b>no header</b></i>.
	 * 
	 * @param hasHeader
	 */
	public void setInputHasHeader(boolean hasHeader) {
		this.INPUT_HAS_HEADER = hasHeader;
	}


	public void setupGrid(String filename){
		/* Check that risk zone thresholds have been set. */
		if(riskThresholds == null){
			LOG.error("Cannot build the grid without risk thresholds.");
			throw new RuntimeException("First set thresholds with setRiskThresholds() method.");
		}
	
		LOG.info("Calculating the data extent...");
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		Counter counter = new Counter("   lines # ");
		BufferedReader br = IOUtils.getBufferedReader(filename);
		try{
			/* Ignore the header if present. */
			String line = null;
			if(INPUT_HAS_HEADER) {
				line = br.readLine();
			}
			while( (line = br.readLine()) != null){
				String[] sa = line.split(",");
				double x = Double.parseDouble(sa[COLUMN_X_ACCELERATION]);
				double y = Double.parseDouble(sa[COLUMN_Y_ACCELERATION]);
				double z = Double.parseDouble(sa[COLUMN_Z_ACCELERATION]);
				minX = Math.min(minX, x);
				minY = Math.min(minY, y);
				minZ = Math.min(minZ, z);
				maxX = Math.max(maxX, x);
				maxY = Math.max(maxY, y);
				maxZ = Math.max(maxZ, z);
				counter.incCounter();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot read from " + filename);
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + filename);
			}
		}
		counter.printCounter();

		/* Establish the centroid grid given the point extent. */
		FCCGrid fccg = new FCCGrid(minX, maxX, minY, maxY, minZ, maxZ, scale);
		GridPoint[] ga = fccg.getFcGrid();
		for(GridPoint gp : ga){
			minX = Math.min(minX, gp.getX());
			minY = Math.min(minY, gp.getY());
			minZ = Math.min(minZ, gp.getZ());
			maxX = Math.max(maxX, gp.getX());
			maxY = Math.max(maxY, gp.getY());
			maxZ = Math.max(maxZ, gp.getZ());
		}
		LOG.info("Done calculating data extent.");

		/* Establish and populate the OcTree given the centroid extent. */
		LOG.info("Building OcTree with dodecahedron centroids... (" + ga.length + " centroids)");
		Counter centroidCounter = new Counter("   centroid # "); 
		map = new HashMap<Coord3d, Double>(ga.length);
		ot = new OcTree<Coord3d>(minX, minY, minZ, maxX, maxY, maxZ);
		for(GridPoint gp : ga){
			Coord3d c = convertToCoord3d(gp.getX(), gp.getY(), gp.getZ());
			ot.put(gp.getX(), gp.getY(), gp.getZ(), c);
			map.put(c, new Double(0.0));
			centroidCounter.incCounter();
		}
		centroidCounter.printCounter();
		LOG.info("Done populating centroid grid: " + ga.length + " points.");
	}
	

	@Override
	public Coord3d convertToCoord3d(double x, double y, double z) {
		return new Coord3d(x, y, z);
	}


	@Override
	public Coord3d getClosest(double x, double y, double z){
		return this.ot.get(x, y, z);
	}

	/**
	 * Writes the accelerometer 'blob' results: the number of observations in
	 * each cell (only those with a value greater than zero), and the risk class
	 * of the cell. The output file with name <code>cellValuesAndRiskClasses.csv</code>
	 * will be created in the output folder.
	 * 
	 * @param outputFolder
	 */
	public void writeCellCountsAndRiskClasses(String outputFolder){
		if(map.size() == 0 || mapRating == null){
			throw new RuntimeException("Insufficient data to write. Either no grids, or no ranking.");
		}
		String filename = outputFolder + (outputFolder.endsWith("/") ? "" : "/") + "cellValuesAndRiskClasses.csv";
		LOG.info("Writing the cell values and risk classes to " + filename); 
		
		/* Report the risk thresholds for which this output holds. */
		LOG.info("  \\_ Accelerometer risk thresholds:");
		for(int i = 0; i < this.riskThresholds.size(); i++){
			LOG.info(String.format("      \\_ Risk %d: %.4f", i, this.riskThresholds.get(i)));
		}
		
		/* Write the cell values and their risk classes. */
		BufferedWriter bw = IOUtils.getBufferedWriter(filename);
		try{
			/* Header. */
			bw.write("x,y,z,count,class");
			bw.newLine();
			
			for(Coord3d c : this.map.keySet()){
				bw.write(String.format("%.4f, %.4f,%.4f,%.1f,%d\n", c.x, c.y, c.z, this.map.get(c), this.mapRating.get(c)));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot write to " + filename);
		} finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot close " + filename);
			}
		}
		LOG.info("Done writing cell values and risk classes.");
	}


}
