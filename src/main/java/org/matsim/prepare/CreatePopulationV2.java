
/* *********************************************************************** *
 * project: org.matsim.*																															*
 *                                                                         *
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


package org.matsim.prepare;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author ikaddoura
 *
 */


//avg_freespeedTravelTime erklaeren

public class CreatePopulationV2 {

	private static final Logger log = Logger.getLogger(CreatePopulationV2.class);

	private int personCounter = 0;
	private final double avg_freespeedTravelTime = 231.;
	private final double freespeedTravelTimeToEickeloh = 535.;	// avg aus allen drei free flow anreisedauern
	private final int numberOfAvailableParkingLots;
	private final int numberOfTimeSlots;
	private final double timeSlotDuration;
	private final double checkInOpeningTime;
    private final double checkInClosingTime;

	private final double mean_demandThroughoutWholeDay = 2.447790887;
    private final double stdDev_demandThroughoutWholeDay = 0.102385966;
    private final double mean_slotArrivalDistribution = 4.31;
	private final double stdDev_slotArrivalDistribution = 0.42;
	private final double walkingTime = 198.;


	private final Map<String, SimpleFeature> features = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsWasserland = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsEickelohParkplatz = new HashMap<>();

	private final String activityType = "park";
	private final String parkingActivityType = "parking";
	private final String serengetiParkplatzDestination = "serengetiParkplatz";
	private final String wasserlandParkplatzDestination = "wasserlandParkplatz";
	private final String serengetiParkDestination = "serengetiPark";
	private final String eickelohParkplatzDestination = "eickelohParkplatz";

	private final String serengetiParkplatzShp = "./original-input-data/shp-files/serengeti-parkplatz/serengeti-parkplatz.shp";
	private final String wasserlandParkplatzShp = "./original-input-data/shp-files/wasserland-parkplatz/wasserland-parkplatz.shp";
	private final String serengetiParkShp = "./original-input-data/shp-files/serengeti-park/serengeti-park.shp";
	private final String eickelohParkplatzShp = "./additional-input-data/shp-files/eickeloh-parkplatz/eickeloh-parkplatz.shp";


	public static void main(String[] args) throws IOException {

		final String networkFile = "./scenarios/input/serengeti-park-network-v1.0.xml.gz";
		final String outputFilePopulation = "./scenarios/input/population/serengeti-park-population-EickelohOpenAndTimeSlots.xml.gz";

		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkFile);
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// BaseC: Safari: 2000, each parking lot: 1250
		// EickelohOpen: Safari: 2000, eickeloh: 2500
		// TimeSlots: Safari: 2000, each parking lot: 1250
		// EickelohOpenAndTimeSlots: EickelohOpen: Safari: 2000, eickeloh: 2500

		CreatePopulationV2 popGenerator = new CreatePopulationV2(2000, 0, 0, 2500, 1, 4, 9.5*3600., 16.5*3600.);
		popGenerator.run(scenario);

		new PopulationWriter(scenario.getPopulation(), scenario.getNetwork()).write(outputFilePopulation);
		log.info("Population written to: " + outputFilePopulation);

	}


	public CreatePopulationV2(int numberOfSafariVisitors, int safariParkplatzVisitors, int wasserlandParkplatzVisitors, int eickelohParkplatzVisitors, int numberOfAvailableParkingLots, int numberOfTimeSlots, double checkInOpeningTime, double checkInClosingTime) throws IOException {

		this.numberOfAvailableParkingLots = numberOfAvailableParkingLots;
		this.numberOfTimeSlots = numberOfTimeSlots;
		this.timeSlotDuration = (int) ( (checkInClosingTime-checkInOpeningTime) / numberOfTimeSlots );
		this.checkInOpeningTime = checkInOpeningTime;
		this.checkInClosingTime = checkInClosingTime;

		// capacity 675
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("2344590910000r"), (int) (safariParkplatzVisitors * 0.8)); // Motorway
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("44371520007f"), (int) (safariParkplatzVisitors * 0.1)); // North
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("377320760000r"), (int) (safariParkplatzVisitors * 0.1)); // Hodenhagen

		// capacity 1569
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("2344590910000r"), (int) (wasserlandParkplatzVisitors * 0.8)); // Motorway
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("44371520007f"), (int) (wasserlandParkplatzVisitors * 0.1)); // North
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("377320760000r"), (int) (wasserlandParkplatzVisitors * 0.1)); // Hodenhagen

		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("2344590910000r"), (int) (numberOfSafariVisitors * 0.8)); // Motorway
		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("44371520007f"), (int) (numberOfSafariVisitors * 0.1)); // North
		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("377320760000r"), (int) (numberOfSafariVisitors * 0.1)); // Hodenhagen

		linkId2numberOfVisitorsEickelohParkplatz.put(Id.createLinkId("2344590910000r"), (int) (eickelohParkplatzVisitors * 0.8)); // Motorway
		linkId2numberOfVisitorsEickelohParkplatz.put(Id.createLinkId("44371520007f"), (int) (eickelohParkplatzVisitors * 0.1)); // North
		linkId2numberOfVisitorsEickelohParkplatz.put(Id.createLinkId("377320760000r"), (int) (eickelohParkplatzVisitors * 0.1)); // Hodenhagen

		log.info("Reading shp files...");

		{
			SimpleFeatureIterator it = ShapeFileReader.readDataFile(serengetiParkplatzShp).getFeatures().features();
			while (it.hasNext()) {
				SimpleFeature ft = it.next();
				features.put(this.serengetiParkplatzDestination, ft);
			}
			it.close();
		}

		{
			SimpleFeatureIterator it = ShapeFileReader.readDataFile(wasserlandParkplatzShp).getFeatures().features();
			while (it.hasNext()) {
				SimpleFeature ft = it.next();
				features.put(this.wasserlandParkplatzDestination, ft);
			}
			it.close();
		}

		{
			SimpleFeatureIterator it = ShapeFileReader.readDataFile(serengetiParkShp).getFeatures().features();
			while (it.hasNext()) {
				SimpleFeature ft = it.next();
				features.put(this.serengetiParkDestination, ft);
			}
			it.close();
		}

		{
			SimpleFeatureIterator it = ShapeFileReader.readDataFile(eickelohParkplatzShp).getFeatures().features();
			while (it.hasNext()) {
				SimpleFeature ft = it.next();
				features.put(this.eickelohParkplatzDestination, ft);
			}
			it.close();
		}

		log.info("Reading shp files... Done.");

	}


	public Scenario run(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		for (int i=0; i<numberOfTimeSlots; i++) {

			if (this.numberOfAvailableParkingLots > 1) {

				//SerengetiParkplatz
				for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
					createVisitors(scenario, rnd, linkId, (int) (linkId2numberOfVisitorsSerengetiParkplatz.get(linkId) * sharePerTS(i)), this.serengetiParkplatzDestination, i + 1);
				}

				//WasserlandParkplatz
				for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
					createVisitors(scenario, rnd, linkId, (int) (linkId2numberOfVisitorsWasserland.get(linkId) * sharePerTS(i)), this.wasserlandParkplatzDestination, i + 1);
				}

			} else {

				// Eickeloh Parkplatz
				for (Id<Link> linkId : linkId2numberOfVisitorsEickelohParkplatz.keySet()) {
					createVisitors(scenario, rnd, linkId, (int) (linkId2numberOfVisitorsEickelohParkplatz.get(linkId) * sharePerTS(i) ), this.eickelohParkplatzDestination, i+1);
				}

			}

			//Safari guests
			if (this.numberOfAvailableParkingLots > 1) {

				for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
					createSafariVisitors(scenario, rnd, linkId, (int) (linkId2numberOfVisitorsSerengetiPark.get(linkId) * sharePerTS(i) ), this.serengetiParkDestination, "", i+1);
				}

			} else {

				for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
					createSafariVisitors(scenario, rnd, linkId, (int) (linkId2numberOfVisitorsSerengetiPark.get(linkId) * sharePerTS(i) / this.numberOfAvailableParkingLots), this.serengetiParkDestination, this.eickelohParkplatzDestination,i+1);
				}

			}

			System.out.println("Timeslot share: "+sharePerTS(i));

		}


		log.info("Population contains " + personCounter + " agents.");

		return scenario;
	}


	private void createVisitors(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, String type, int timeSlot) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		if (this.numberOfTimeSlots==1) {

			for (int i = 0; i < odSum; i++) {
				Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));

				Plan plan = popFactory.createPlan();

				Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

				double startTime = calculateLogNormallyDistributedTime(mean_demandThroughoutWholeDay, stdDev_demandThroughoutWholeDay, checkInOpeningTime-avg_freespeedTravelTime, checkInClosingTime-avg_freespeedTravelTime);
//				double startTime = calculateNormallyDistributedTime(12 * 3600., 1* 3600.); // NORMALLY distributed

				startActivity.setEndTime(startTime);
				plan.addActivity(startActivity);

				Leg leg1 = popFactory.createLeg("car");
				plan.addLeg(leg1);

				Point endPoint = getRandomPointInFeature(rnd, features.get(type));
				if ( endPoint==null ) log.warn("Point is null.");

				Activity endActivity = popFactory.createActivityFromCoord(this.activityType, MGC.point2Coord(endPoint) ) ;
				plan.addActivity(endActivity);

				pers.addPlan(plan);
				population.addPerson(pers);

				pers.getAttributes().putAttribute("subpopulation", type);

				personCounter++;
			}

		} else {

			for (int i = 0; i < odSum; i++) {
				Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + type + "_S" + timeSlot, Person.class));

				Plan plan = popFactory.createPlan();

				Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

				double startTime = (checkInOpeningTime + (timeSlotDuration * (timeSlot-1)) + calculateLogNormallyDistributedSlotArrivalSecond(mean_slotArrivalDistribution, stdDev_slotArrivalDistribution) );

				startActivity.setEndTime(startTime);
				plan.addActivity(startActivity);

				Leg leg1 = popFactory.createLeg("car");
				plan.addLeg(leg1);

				Point endPoint = getRandomPointInFeature(rnd, features.get(type));
				if ( endPoint==null ) log.warn("Point is null.");

				Activity endActivity = popFactory.createActivityFromCoord(this.activityType +"_S"+timeSlot, MGC.point2Coord(endPoint));
				plan.addActivity(endActivity);

				pers.addPlan(plan);
				population.addPerson(pers);

				pers.getAttributes().putAttribute("subpopulation", type);

				personCounter++;
			}

		}

	}


	private void createSafariVisitors(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, String finalType, String parkingType, int timeSlot) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		if (this.numberOfTimeSlots==1) { //Base Case , EickelohOpen

			for (int i = 0; i < odSum; i++) {
				Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + finalType + parkingType, Person.class));

				Plan plan = popFactory.createPlan();

				Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());
				double startTime = calculateLogNormallyDistributedTime(mean_demandThroughoutWholeDay, stdDev_demandThroughoutWholeDay, checkInOpeningTime-avg_freespeedTravelTime, checkInClosingTime-avg_freespeedTravelTime);
//				double startTime = calculateNormallyDistributedTime(12 * 3600., 1 * 3600.); // normally distributed
//				double startTime = calculateRandomlyDistributedValue(12 * 3600., 2 * 3600.); // randomly distributed

				startActivity.setEndTime(startTime);
				plan.addActivity(startActivity);

				Leg leg1 = popFactory.createLeg("car");
				plan.addLeg(leg1);

				if (numberOfAvailableParkingLots==1) {	// EickelohOpen

					//TODO: GEHT DAS AUCH IRGENDWIE ANDERS ODER SO
					leg1.setTravelTime(freespeedTravelTimeToEickeloh);

					Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
					if ( parkingPoint==null ) log.warn("Point is null.");

					Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
					parkingActivity.setEndTime( startTime + leg1.getTravelTime().seconds() + walkingTime + calculateRandomlyDistributedValue(4.5*60., 2.5*60.));
					plan.addActivity(parkingActivity);

					Leg leg2 = popFactory.createLeg("car");
					plan.addLeg(leg2);

				}

				Id<Link> endLinkId = Id.createLinkId("246929390045f");
				Activity endActivity = popFactory.createActivityFromLinkId(this.activityType, endLinkId);
				plan.addActivity(endActivity);

				pers.addPlan(plan);
				population.addPerson(pers);

				pers.getAttributes().putAttribute("subpopulation", finalType);

				personCounter++;
			}
		} else { // TimeSlots, EickelohOpen&TimeSlots

			for (int i = 0; i < odSum; i++) {
				Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + finalType + "_S" + timeSlot, Person.class));

				Plan plan = popFactory.createPlan();

				Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

				double startTime = (checkInOpeningTime + (timeSlotDuration * (timeSlot-1)) + calculateLogNormallyDistributedSlotArrivalSecond(mean_slotArrivalDistribution, stdDev_slotArrivalDistribution) );

				startActivity.setEndTime(startTime);
				plan.addActivity(startActivity);

				Leg leg1 = popFactory.createLeg("car");
				plan.addLeg(leg1);

				if (numberOfAvailableParkingLots==1) {

					//TODO: GEHT DAS AUCH IRGENDWIE ANDERS ODER SO
					leg1.setTravelTime(this.freespeedTravelTimeToEickeloh);

					Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
					if ( parkingPoint==null ) log.warn("Point is null.");

					Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
					parkingActivity.setEndTime( startTime + leg1.getTravelTime().seconds() + walkingTime + calculateRandomlyDistributedValue(4.5*60., 2.5*60.));
					plan.addActivity(parkingActivity);

					Leg leg2 = popFactory.createLeg("car");
					plan.addLeg(leg2);

				}

				Id<Link> endLinkId = Id.createLinkId("246929390045f");
				Activity endActivity = popFactory.createActivityFromLinkId(this.activityType + "_S"+timeSlot, endLinkId);
				plan.addActivity(endActivity);

				pers.addPlan(plan);
				population.addPerson(pers);

				pers.getAttributes().putAttribute("subpopulation", finalType);

				personCounter++;
			}

		}

	}



	private static Point getRandomPointInFeature(Random rnd, SimpleFeature ft) {

		if (ft != null) {

			Point p;
			double x, y;
			do {
				x = ft.getBounds().getMinX() + rnd.nextDouble() * (ft.getBounds().getMaxX() - ft.getBounds().getMinX());
				y = ft.getBounds().getMinY() + rnd.nextDouble() * (ft.getBounds().getMaxY() - ft.getBounds().getMinY());
				p = MGC.xy2Point(x, y);
			} while (!((Geometry) ft.getDefaultGeometry()).contains(p));
			return p;

		} else {
			return null;
		}


	}

	private double calculateRandomlyDistributedValue(double i, double abweichung) {
		Random rnd = MatsimRandom.getRandom();
		double rnd1 = rnd.nextDouble();
		double rnd2 = rnd.nextDouble();

		double vorzeichen;
		if (rnd1 <= 0.5) {
			vorzeichen = -1.0;
		} else {
			vorzeichen = 1.0;
		}
		return (i + (rnd2 * abweichung * vorzeichen));
	}
	
	private double calculateNormallyDistributedTime(double mean, double stdDev) {
		Random random = MatsimRandom.getRandom();
		boolean leaveLoop = false;
		double endTimeInSec = Double.MIN_VALUE;

		while (!leaveLoop) {
			double normal = random.nextGaussian();
			endTimeInSec = mean + stdDev * normal;

			if (endTimeInSec >= 9. * 3600 && endTimeInSec <= 13. * 3600.) {
				leaveLoop = true;
			}
		}

		if (endTimeInSec < 0. || endTimeInSec > 24. * 3600) {
			throw new RuntimeException("Shouldn't happen. Aborting...");
		}
		return endTimeInSec;
	}

	// gibt mit mean_demandThroughoutWholeDay = 2.447790887 und stdDev_demandThroughoutWholeDay = 0.102385966 sowas hier zurueck: 11.120570031423256 * 3600
	private double calculateLogNormallyDistributedTime(double mean, double stdDev, double earliestTime, double latestTime) {

        LogNormalDistribution logNormal = new LogNormalDistribution(mean, stdDev);
        boolean leaveLoop = false;
        double endTimeInSec = Double.MIN_VALUE;

        while (!leaveLoop) {
            endTimeInSec = logNormal.sample() * 3600.;

            if (endTimeInSec >= earliestTime && endTimeInSec <= latestTime) {
                leaveLoop = true;
            }
        }

        if (endTimeInSec < 0. || endTimeInSec > 24. * 3600) {
            throw new RuntimeException("Shouldn't happen. Aborting...");
        }
        return endTimeInSec;

	}

	private double calculateLogNormallyDistributedSlotArrivalSecond(double mean, double stdDev) {

		LogNormalDistribution logNormal = new LogNormalDistribution(mean, stdDev);
		boolean leaveLoop = false;
		double endTimeInSec = Double.MIN_VALUE;

		while (!leaveLoop) {
			endTimeInSec = (timeSlotDuration / 300. ) * logNormal.sample();

			if (endTimeInSec >= 0. && endTimeInSec <= timeSlotDuration) {
				leaveLoop = true;
			}
		}

		if (endTimeInSec < 0. || endTimeInSec > 24. * 3600) {
			throw new RuntimeException("Shouldn't happen. Aborting...");
		}
		return endTimeInSec;

	}


	private double sharePerTS(int i){
		if (this.numberOfTimeSlots==1) {
			return 1;
		} else {
			return calculateLogNormallyDistributedTimeSlotShare(i);
//			return 1/numberOfTimeSlots;	//gleichverteilt
		}
	}

	private double calculateLogNormallyDistributedTimeSlotShare (int i) {

		LogNormalDistribution demandDist = new LogNormalDistribution(mean_demandThroughoutWholeDay, stdDev_demandThroughoutWholeDay);
		double slotStart =  ( checkInOpeningTime + (i*timeSlotDuration) ) / 3600.;
		double slotEnd = ( checkInOpeningTime + ( (i+1) * timeSlotDuration ) ) / 3600.;
		/*System.out.println("slotStart: "+slotStart);
		System.out.println("slotEnd: "+slotEnd);*/

		return (1/0.9723) * ( demandDist.cumulativeProbability(slotEnd) - demandDist.cumulativeProbability(slotStart) );
	}


	/*//opening time + i = slotStartTime
	private double calculateNormallyDistributedTimeSlotShare (int i) {
		NormalDistribution demandInOneDayDist = new NormalDistribution(14*3600., 0.5*(14*3600.-this.openingTime));
		double upperBound = this.openingTime + ( (i+1)*this.timeSlotDuration );
		double lowerBound = this.openingTime + (i*this.timeSlotDuration);
		System.out.println("upperBound: "+upperBound);
		System.out.println("lowerBound: "+lowerBound);

		return (1/0.95) * ( demandInOneDayDist.cumulativeProbability(upperBound) - demandInOneDayDist.cumulativeProbability(lowerBound) );
	}*/


}


