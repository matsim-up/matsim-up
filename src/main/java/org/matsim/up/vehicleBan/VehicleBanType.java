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
  
/**
 * @author jwjoubert
 */
final public class VehicleBanType {
	final private double probabilityGettingCaught;
	final private double fineWhenCaught;
	final private Type type;
	
	VehicleBanType(Type type, double probabilityGettingCaught, double fineWhenCaught) {
		this.probabilityGettingCaught = probabilityGettingCaught;
		this.fineWhenCaught = fineWhenCaught;
		this.type = type;
	}

	public double getProbabilityGettingCaught() {
		return this.probabilityGettingCaught;
	}
	
	public double getFineWhenCaught() {
		return this.fineWhenCaught;
	}
	
	public String getShortName() {
		return this.type.getShortName();
	}
	
	public Type getType() {
		return this.type;
	}
	
	public enum Type{
		FINE_ONLY("fine"), FINE_AND_STUCK("stuck");
		
		private final String shortName;
		
		Type(String name) {
			this.shortName = name;
		}
		
		public String getShortName() {
			return this.shortName;
		}
	}
}
