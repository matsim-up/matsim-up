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
import org.matsim.core.utils.io.IOUtils;
import org.matsim.up.utils.Header;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;


/**
 * Single class to run all the experiments in one go. Since the different
 * runs are completely independent, it makes sense to run them all in
 * parallel. The idea is to build a release, and from the same repository
 * directory call the <code>jar-with-dependencies.jar</code> file.
 *
 * @author jwjoubert
 */
public class RunAllExperiments {
    final private static Logger LOG = Logger.getLogger(RunAllExperiments.class);
    final private static String JAR_FILE = "./target/matsim-up-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
    final private static String OUTPUT_CONSOLIDATION = "./output/";
    final private static String OUTPUT_PARENT = "./output/allRuns/";
    final private static String INPUT_FOLDER = "./input/equil/";
    final private static File statsFile = new File(OUTPUT_CONSOLIDATION + EquilBanScenario.OUTPUT_FILENAME_STATS);
    final private static File peakFile = new File(OUTPUT_CONSOLIDATION + EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_PEAK);
    final private static File offpeakFile = new File(OUTPUT_CONSOLIDATION + EquilBanScenario.OUTPUT_FILENAME_ROUTE_CHOICE_OFFPEAK);

    /**
     * @param args the following arguments are required, and in the following order:
     *             <ol>
     *             <li> number of threads to use; and</li>
     *             <li> the path to the executable JAR-file.</li>
     *             </ol>
     */
    public static void main(String[] args) {
        Header.printHeader(RunAllExperiments.class, args);
        int numberOfThreads = Integer.parseInt(args[0]);

        File jarFile = new File(args[1]);
//		File jarFile = new File(JAR_FILE);
        File inputFolder = new File(INPUT_FOLDER);

        File runsFolder = new File(OUTPUT_PARENT);
        boolean createRunsFolder = runsFolder.mkdirs();
        if (!createRunsFolder) {
            LOG.warn("Could not create runs folder. Possibly it already exists.");
        }

        boolean deleteStats = statsFile.delete();
        boolean deletePeak = peakFile.delete();
        boolean deleteOffPeak = offpeakFile.delete();
        if (!deleteStats || !deletePeak || !deleteOffPeak) {
            LOG.warn("One or more of the output files were not deleted. Possible they didn't exist.");
            LOG.error("     Stats: " + deleteStats);
            LOG.error("      Peak: " + deleteStats);
            LOG.error("   OffPeak: " + deleteStats);
        }

        List<Double> probs = new ArrayList<>();
        probs.add(0.5);
//		for(double d = 0.0 ; d <= 1.0 ; d += 0.10) {
//			probs.add(d);
//		}

        List<Double> costs = new ArrayList<>();
        costs.add(100.0);
//		for(double d = 0.0; d <= 1000; d+=100) {
//            costs.add(d);
//        }

        ExecutorService threadService = Executors.newFixedThreadPool(numberOfThreads);
        Map<String, Future<List<String>>> jobs = new TreeMap<>();

        for (double prob : probs) {
            for (double fine : costs) {
                {
                    initCallable(jarFile, inputFolder, runsFolder, threadService, jobs, prob, fine, true);
                }

                {
                    initCallable(jarFile, inputFolder, runsFolder, threadService, jobs, prob, fine, false);
                }
            }
        }

        threadService.shutdown();
        while (!threadService.isTerminated()) {
            /* Just wait */
        }

        /* Concatenate the output */
        writeHeaders();
        for (String job : jobs.keySet()) {
            consolidateFile(job, jobs.get(job));
        }

        Header.printFooter();
    }

    private static void initCallable(File jarFile, File inputFolder, File runsFolder, ExecutorService threadService, Map<String, Future<List<String>>> jobs, double prob, double fine, boolean stuck) {
        final String name = String.format("%s_%.2f_%04.0f", stuck ? "stuck" : "fine", prob, fine);
        Callable<List<String>> callable = new EquilBanCallable(jarFile, inputFolder.getAbsoluteFile(), runsFolder, prob, fine, stuck);
        Future<List<String>> job = threadService.submit(callable);
        jobs.put(name, job);
    }


    private static void consolidateFile(String job, Future<List<String>> result) {
        List<String> thisResult;
        try {
            thisResult = result.get();
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Could not get the result for job " + job);
        }

        consolidateStatsFile(job, thisResult.get(0));
        consolidateSingleFile(job, thisResult.get(1), peakFile);
        consolidateSingleFile(job, thisResult.get(2), offpeakFile);
    }

    private static void writeHeaders() {
        BufferedWriter bwStats = IOUtils.getBufferedWriter(statsFile.getAbsolutePath());
        BufferedWriter bwPeak = IOUtils.getBufferedWriter(peakFile.getAbsolutePath());
        BufferedWriter bwOffpeak = IOUtils.getBufferedWriter(offpeakFile.getAbsolutePath());
        try {
            bwStats.write("type,prob,fine,control,experiment,violators\n");

            String header = "type,prob,fine,iter,c1,e1,c2,e2,c3,e3,c4,e4,c5,e5,c6,e6,c7,e7,c8,e8,c9,e9\n";
            bwPeak.write(header);
            bwOffpeak.write(header);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot write to header files.");
        } finally {
            try {
                bwStats.close();
                bwPeak.close();
                bwOffpeak.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot close header files.");
            }
        }
    }

    private static void consolidateStatsFile(String job, String resultToRead) {
        String[] sa = job.split("_");

        BufferedReader br = IOUtils.getBufferedReader(resultToRead);
        BufferedWriter bw = IOUtils.getAppendingBufferedWriter(RunAllExperiments.statsFile.getAbsolutePath());
        String line;
        try {
            line = br.readLine(); /* Header */
            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(",");
                String extendedLine = String.format("%s,%s,%s,%s,%s,%s\n",
                        sa[0], sa[1], sa[2], lineArray[0], lineArray[1], lineArray[2]);
                bw.write(extendedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read/write results for job " + job);
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot close consolidate results file(s)");
            }
        }
    }

    private static void consolidateSingleFile(String job, String resultToRead, File outputFile) {
        String[] sa = job.split("_");

        BufferedReader br = IOUtils.getBufferedReader(resultToRead);
        BufferedWriter bw = IOUtils.getAppendingBufferedWriter(outputFile.getAbsolutePath());
        String line;
        try {
            line = br.readLine(); /* Header */
            while ((line = br.readLine()) != null) {
                String extendedLine = String.format("%s,%s,%s,%s\n",
                        sa[0], sa[1], sa[2], line);
                bw.write(extendedLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read/write results for job " + job);
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot close consolidate results file(s)");
            }
        }
    }


}
