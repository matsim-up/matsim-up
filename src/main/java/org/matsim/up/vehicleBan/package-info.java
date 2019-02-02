/**
 * Code to try and get modal bans modelled in MATSim. For this working paper I
 * followed the following approach:
 * <ul> 
 * 		<li> check whenever a banned-sensitive vehicle uses a link on which a 
 *   		 banned is imposed, during a banned period, and flag it accordingly;
 * 		<li> at the end of the mobility simulation, use {@link org.matsim.core.scoring.SumScoringFunction.BasicScoring}
 * 			 function to apply the given penalty, but <b><i>only if the the 
 * 			 vehicle is caught</i></b>, which is dependent on a given probability.
 * </ul>
 * Currently the probability of being caught is set generally, and is not a 
 * person-specific. This could be extended in future. The fact that the fine is
 * fixed is plausible, and we should not assume a driver/vehicle can get away
 * with a lower penalty because of a bribe...
 * 
 * @author jwjoubert
 */
package org.matsim.up.vehicleBan;