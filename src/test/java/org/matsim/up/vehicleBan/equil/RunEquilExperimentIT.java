package org.matsim.up.vehicleBan.equil;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;

public class RunEquilExperimentIT {

	@Rule
	final public MatsimTestUtils utils = new MatsimTestUtils();

	private static String populationFile;
	final private static double SCORE = 121.5458171899399; // 20190203 (JWJ)

	@Test
	public void testMain() {
		populationFile = utils.getOutputDirectory() + "output_stuck_0.25_0500/output_plans.xml.gz";

		String[] args = new String[5];
		args[0] = String.valueOf(0.25);
		args[1] = String.valueOf(500.0);
		args[2] = String.valueOf(true);
		args[3] = "./input/equil/network.xml";
		args[4] = utils.getOutputDirectory();

		RunEquilExperiment.main(args);
	}

	@Ignore /* FIXME cannot get reproducible results after going from Java 8 to Java 11. */
	@Test
	public void testPersonScore() {
		Plan plan = null;
		try {
			Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
			new PopulationReader(sc).readFile(populationFile);
			plan = sc.getPopulation().getPersons().get(Id.createPersonId("1")).getSelectedPlan();
		} catch (Exception e) {
			Assert.fail("Should find output population.");
		}

		Assert.assertEquals("Wrong score for person '1'.", SCORE, plan.getScore(), MatsimTestUtils.EPSILON);
	}

}