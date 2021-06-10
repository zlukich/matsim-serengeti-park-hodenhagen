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
 *
 * @author ikaddoura
 *
 */

// Schliessung der kassen frühestens um 16 uhr & spätestens 2h vor parkschließzeit, im fall 10-18: um 4 -> für die timeslots beachten!

public class CreatePopulationComplete {

	private static final Logger log = Logger.getLogger(CreatePopulationComplete.class);

	private int personCounter = 0;
	private final double freespeedTravelTime = 360.;
	private final double timeSlotDuration;
	private final int numberOfTimeSlots;
	private final double openingTime;

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
		final String outputFilePopulation = "./scenarios/input/serengeti-park-population-BaseCase.xml.gz";

		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkFile);
		Scenario scenario = ScenarioUtils.loadScenario(config);

		CreatePopulationComplete popGenerator = new CreatePopulationComplete(1000, 675, 1569, 543, 7200, 10., 18.);
		popGenerator.runBaseCase(scenario);

		new PopulationWriter(scenario.getPopulation(), scenario.getNetwork()).write(outputFilePopulation);
		log.info("Population written to: " + outputFilePopulation);

	}


	public CreatePopulationComplete(int numberOfSafariVisitors, int safariParkplatzVisitors, int wasserlandParkplatzVisitors, int eickelohParkplatzVisitors, int timeSlotDuration, double openingTime, double closingTime) throws IOException {

		this.timeSlotDuration = timeSlotDuration;
		this.openingTime = openingTime;
		if (timeSlotDuration!=0) {
			this.numberOfTimeSlots = (int) ((closingTime - openingTime) * 3600) / timeSlotDuration;
		} else {
			this.numberOfTimeSlots = 1;
		}


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


	public Scenario runBaseCase(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			createSafariVisitorsBaseCase(scenario, linkId, linkId2numberOfVisitorsSerengetiPark.get(linkId), this.serengetiParkDestination);
		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	public Scenario runA(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsEickelohParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsEickelohParkplatz.get(linkId), this.eickelohParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			createSafariVisitorsBaseCase(scenario, linkId, linkId2numberOfVisitorsSerengetiPark.get(linkId), this.serengetiParkDestination);
		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;
	}



	public Scenario runB(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();


		// ms: divide SerengetiParkplatz visitors equally between numberOfTimeSlots slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiParkplatz.put(linkId, (int) linkId2numberOfVisitorsSerengetiParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination, i+1);
			}
		}

		// divide WasserlandParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsWasserland = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			partOfLinkId2numberOfVisitorsWasserland.put(linkId, (int) linkId2numberOfVisitorsWasserland.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsWasserland.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination, i+1);
			}
		}

		// divide SerengetiPark visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiPark.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark.keySet()) {
				createSafariVisitorsB(scenario, linkId, partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId), this.serengetiParkDestination, i+1);
			}
		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	public Scenario runC(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination);
		}

		//first divide the SerengetiPark visitors into number of parking areas groups
		Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers = new HashMap<>();
		Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark_WasserlandUsers = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) / 2); //2=currently available parking lots
			linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) /2);
		}

		//create SafariVisitors for each group
		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.keySet()) {
			createSafariVisitorsC(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.keySet()) {
			createSafariVisitorsC(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.wasserlandParkplatzDestination);
		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	public Scenario runAB(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		// ms: divide SerengetiParkplatz visitors equally between numberOfTimeSlots slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiParkplatz.put(linkId, (int) linkId2numberOfVisitorsSerengetiParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination, i+1);
			}
		}

		// divide WasserlandParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsWasserland = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			partOfLinkId2numberOfVisitorsWasserland.put(linkId, (int) linkId2numberOfVisitorsWasserland.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsWasserland.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination, i+1);
			}
		}

		// divide EickelohParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsEickelohParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsEickelohParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsEickelohParkplatz.put(linkId, (int) linkId2numberOfVisitorsEickelohParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsEickelohParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsEickelohParkplatz.get(linkId), this.eickelohParkplatzDestination, i+1);
			}
		}

		// divide SerengetiPark visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiPark.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark.keySet()) {
				createSafariVisitorsB(scenario, linkId, partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId), this.serengetiParkDestination, i+1);
			}
		}


		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	public Scenario runAC(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsEickelohParkplatz.keySet()) {
			createVisitorsBaseCase(scenario, rnd, linkId, linkId2numberOfVisitorsEickelohParkplatz.get(linkId), this.eickelohParkplatzDestination);
		}

		//first divide the SerengetiPark visitors into numberOfParkingAreas groups
		Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers = new HashMap<>();
		Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark_WasserlandUsers = new HashMap<>();
		Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers = new HashMap<>();

		// ASSUMPTION: PARKING LOTS EQUALLY SHARED
		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) /3); //3=currently available parking lots
			linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) /3);
			linkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) /3);
		}

		//create SafariVisitors for each group
		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.keySet()) {
			createSafariVisitorsC(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.serengetiParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.keySet()) {
			createSafariVisitorsC(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark_WasserlandUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.wasserlandParkplatzDestination);
		}

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.keySet()) {
			createSafariVisitorsC(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.eickelohParkplatzDestination);
		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	public Scenario runBC(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		// ms: divide SerengetiParkplatz visitors equally between numberOfTimeSlots slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiParkplatz.put(linkId, (int) linkId2numberOfVisitorsSerengetiParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination, i+1);
			}
		}

		// divide WasserlandParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsWasserland = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			partOfLinkId2numberOfVisitorsWasserland.put(linkId, (int) linkId2numberOfVisitorsWasserland.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsWasserland.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination, i+1);
			}
		}

		// divide SerengetiPark visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiPark.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) / this.numberOfTimeSlots);
		}

		// per time slot: divide SerengetiPark visitors into numberOfParkingAreas groups and create SafariVisitors for each group
		for (int i = 0; i < numberOfTimeSlots; i++) {

			Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers = new HashMap<>();
			Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers = new HashMap<>();

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark.keySet()) {
				partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.put(linkId, (int) partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId) / 2); //2=currently available parking lots
				partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.put(linkId, (int) partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId) /2);
			}

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.keySet()) {
				createSafariVisitorsBC(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.serengetiParkplatzDestination, i+1);
			}

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.keySet()) {
				createSafariVisitorsBC(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.wasserlandParkplatzDestination, i+1);
			}

		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;

	}

	// originale Methode run ABC
	public Scenario runABC(Scenario scenario) {

		Random rnd = MatsimRandom.getRandom();

		// ms: divide SerengetiParkplatz visitors equally between numberOfTimeSlots slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiParkplatz.put(linkId, (int) linkId2numberOfVisitorsSerengetiParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination, i + 1);
			}
		}

		// divide WasserlandParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsWasserland = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			partOfLinkId2numberOfVisitorsWasserland.put(linkId, (int) linkId2numberOfVisitorsWasserland.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsWasserland.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination, i + 1);
			}
		}

		// divide EickelohParkplatz visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsEickelohParkplatz = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsEickelohParkplatz.keySet()) {
			partOfLinkId2numberOfVisitorsEickelohParkplatz.put(linkId, (int) linkId2numberOfVisitorsEickelohParkplatz.get(linkId) / this.numberOfTimeSlots);
		}

		for (int i = 0; i < numberOfTimeSlots; i++) {
			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsEickelohParkplatz.keySet()) {
				createVisitorsB(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsEickelohParkplatz.get(linkId), this.eickelohParkplatzDestination, i + 1);
			}
		}


		// divide SerengetiPark visitors eq. between slots and create visitors for each slot
		Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark = new HashMap<>();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			partOfLinkId2numberOfVisitorsSerengetiPark.put(linkId, (int) linkId2numberOfVisitorsSerengetiPark.get(linkId) / this.numberOfTimeSlots);
		}

		// per time slot: divide SerengetiPark visitors into numberOfParkingAreas groups and create SafariVisitors for each group
		for (int i = 0; i < numberOfTimeSlots; i++) {

			Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers = new HashMap<>();
			Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers = new HashMap<>();
			Map<Id<Link>, Integer> partOfLinkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers = new HashMap<>();

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark.keySet()) {
				partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.put(linkId, (int) partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId) / 3); //3=currently available parking lots
				partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.put(linkId, (int) partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId) /3);
				partOfLinkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.put(linkId, (int) partOfLinkId2numberOfVisitorsSerengetiPark.get(linkId) /3);

			}

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.keySet()) {
				createSafariVisitorsBC(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiPark_SerengetiParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.serengetiParkplatzDestination, i+1);
			}

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.keySet()) {
				createSafariVisitorsBC(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiPark_WasserlandUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.wasserlandParkplatzDestination, i+1);
			}

			for (Id<Link> linkId : partOfLinkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.keySet()) {
				createSafariVisitorsBC(scenario, rnd, linkId, partOfLinkId2numberOfVisitorsSerengetiPark_EickelohParkplatzUsers.get(linkId), this.freespeedTravelTime, this.serengetiParkDestination, this.eickelohParkplatzDestination, i+1);
			}

		}

		log.info("Population contains " + personCounter + " agents.");

		return scenario;
	}



	// createVisitors methods

	private void createVisitorsBaseCase(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, String type) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

//			double startTime = calculateNormallyDistributedTime(11 * 3600., 1 * 3600.); // normally distributed
			double startTime = calculateRandomlyDistributedValue(11 * 3600., 1 * 3600.); // randomly distributed

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
	}

	private void createSafariVisitorsBaseCase(Scenario scenario, Id<Link> linkId, double odSum, String type) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

//			double startTime = calculateNormallyDistributedTime(11 * 3600., 1 * 3600.); // normally distributed
			double startTime = calculateRandomlyDistributedValue(11 * 3600., 1 * 3600.); // randomly distributed

			startActivity.setEndTime(startTime);
			plan.addActivity(startActivity);

			Leg leg1 = popFactory.createLeg("car");
			plan.addLeg(leg1);

			Id<Link> endLinkId = Id.createLinkId("246929390045f");
			Activity endActivity = popFactory.createActivityFromLinkId(this.activityType, endLinkId);
			plan.addActivity(endActivity);

			pers.addPlan(plan);
			population.addPerson(pers);

			pers.getAttributes().putAttribute("subpopulation", type);

			personCounter++;
		}
	}

	private void createVisitorsB(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, String type, int timeSlot) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_S"+timeSlot+"_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

			double startTime = calculateRandomlyDistributedValue( this.openingTime*3600. + this.timeSlotDuration * (timeSlot-1) , 1 * this.freespeedTravelTime); // randomly distributed

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

	private void createSafariVisitorsB(Scenario scenario, Id<Link> linkId, double odSum, String type, int timeSlot) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_S"+timeSlot+"_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

			double startTime = calculateRandomlyDistributedValue(this.openingTime*3600. + this.timeSlotDuration * (timeSlot-1) , 1 * this.freespeedTravelTime); // randomly distributed

			startActivity.setEndTime(startTime);
			plan.addActivity(startActivity);

			Leg leg1 = popFactory.createLeg("car");
			plan.addLeg(leg1);

			Id<Link> endLinkId = Id.createLinkId("246929390045f");
			Activity endActivity = popFactory.createActivityFromLinkId(this.activityType + "_S"+timeSlot, endLinkId);
			plan.addActivity(endActivity);

			pers.addPlan(plan);
			population.addPerson(pers);

			pers.getAttributes().putAttribute("subpopulation", type);

			personCounter++;
		}
	}

	private void createSafariVisitorsC(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, double freespeedTT, String finalType, String parkingType) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + finalType + "_" + parkingType, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());
//			double startTime = calculateNormallyDistributedTime(11 * 3600., 1 * 3600.); // normally distributed
			double startTime = calculateRandomlyDistributedValue(11 * 3600., 1 * 3600.); // randomly distributed
			startActivity.setEndTime(startTime);
			plan.addActivity(startActivity);

			Leg leg1 = popFactory.createLeg("car");
			leg1.setTravelTime(freespeedTT);
			plan.addLeg(leg1);

			Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
			if ( parkingPoint==null ) log.warn("Point is null.");

			Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
			parkingActivity.setEndTime( startTime + leg1.getTravelTime().seconds() + calculateRandomlyDistributedValue(20*60., 5*60.));
			plan.addActivity(parkingActivity);

			Leg leg2 = popFactory.createLeg("car");
			plan.addLeg(leg2);

			Id<Link> endLinkId = Id.createLinkId("246929390045f");
			Activity endActivity = popFactory.createActivityFromLinkId(this.activityType, endLinkId);
			plan.addActivity(endActivity);

			pers.addPlan(plan);
			population.addPerson(pers);

			pers.getAttributes().putAttribute("subpopulation", finalType);

			personCounter++;
		}

	}

	private void createSafariVisitorsBC(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, double freespeedTT, String finalType, String parkingType, int timeSlot) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_S"+timeSlot+"_" + personCounter + "_" + linkId.toString() + "-" + finalType + "_" + parkingType, Person.class));

			Plan plan = popFactory.createPlan();

			Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());
			double startTime = calculateRandomlyDistributedValue(this.openingTime*3600. + this.timeSlotDuration * (timeSlot-1) , 1 * this.freespeedTravelTime); // randomly distributed
			startActivity.setEndTime(startTime);
			plan.addActivity(startActivity);

			Leg leg1 = popFactory.createLeg("car");
			leg1.setTravelTime(freespeedTT);
			plan.addLeg(leg1);

			Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
			if ( parkingPoint==null ) log.warn("Point is null.");

			Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
			parkingActivity.setEndTime( startTime + leg1.getTravelTime().seconds() + calculateRandomlyDistributedValue(20*60., 5*60.));
			plan.addActivity(parkingActivity);

			Leg leg2 = popFactory.createLeg("car");
			plan.addLeg(leg2);

			Id<Link> endLinkId = Id.createLinkId("246929390045f");
			Activity endActivity = popFactory.createActivityFromLinkId(this.activityType + "_S"+timeSlot, endLinkId);
			plan.addActivity(endActivity);

			pers.addPlan(plan);
			population.addPerson(pers);

			pers.getAttributes().putAttribute("subpopulation", finalType);

			personCounter++;
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

}
