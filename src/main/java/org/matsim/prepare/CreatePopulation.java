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

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author ikaddoura
 *
 */
public class CreatePopulation {

	private static final Logger log = Logger.getLogger(CreatePopulation.class);

	private int personCounter = 0;
	private final Map<String, SimpleFeature> features = new HashMap<>();
	private final Scenario scenario;
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiParkplatz = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsWasserland = new HashMap<>();
	private final Map<Id<Link>, Integer> linkId2numberOfVisitorsSerengetiPark = new HashMap<>();

	private final String activityType = "park";
	private final String serengetiParkplatzDestination = "serengetiParkplatz";
	private final String wasserlandParkplatzDestination = "wasserlandParkplatz";
	private final String serengetiParkDestination = "serengetiPark";
	
	final String networkFile = "./scenarios/serengeti-park-v1.0/input/serengeti-park-network-v1.0.xml.gz";
	final String serengetiParkplatzShp = "./original-input-data/shp-files/serengeti-parkplatz/serengeti-parkplatz.shp";
	final String wasserlandParkplatzShp = "./original-input-data/shp-files/wasserland-parkplatz/wasserland-parkplatz.shp";
	final String serengetiParkShp = "./original-input-data/shp-files/serengeti-park/serengeti-park.shp";

	final String outputFilePopulation = "./scenarios/serengeti-park-v1.0/input/serengeti-park-population-v1.0.xml.gz";

	public static void main(String [] args) throws IOException, ParseException {

		CreatePopulation popGenerator = new CreatePopulation();
		popGenerator.run();	
	}


	public CreatePopulation() throws IOException {
		
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("2344590910000r"), 5000);
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("44371520007f"), 2500);
		linkId2numberOfVisitorsSerengetiParkplatz.put(Id.createLinkId("377320760000r"), 2500);
		
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("2344590910000r"), 5000);
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("44371520007f"), 2500);
		linkId2numberOfVisitorsWasserland.put(Id.createLinkId("377320760000r"), 2500);
		
		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("2344590910000r"), 10000);
		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("44371520007f"), 2500);
		linkId2numberOfVisitorsSerengetiPark.put(Id.createLinkId("377320760000r"), 2500);
		
		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkFile);
		scenario = ScenarioUtils.loadScenario(config);
		
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
		
		log.info("Reading shp files... Done.");
		
	}


	private void run() throws ParseException, IOException {

		Random rnd = MatsimRandom.getRandom();

		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiParkplatz.keySet()) {
			createVisitors(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiParkplatz.get(linkId), this.serengetiParkplatzDestination);
		}	
		
		for (Id<Link> linkId : linkId2numberOfVisitorsWasserland.keySet()) {
			createVisitors(scenario, rnd, linkId, linkId2numberOfVisitorsWasserland.get(linkId), this.wasserlandParkplatzDestination);
		}
		
		for (Id<Link> linkId : linkId2numberOfVisitorsSerengetiPark.keySet()) {
			createVisitors(scenario, rnd, linkId, linkId2numberOfVisitorsSerengetiPark.get(linkId), this.serengetiParkDestination);
		}

		new PopulationWriter(scenario.getPopulation(), scenario.getNetwork()).write(outputFilePopulation);
		log.info("Population written to: " + outputFilePopulation);
		log.info("Population contains " + personCounter + " agents.");
	}


	private void createVisitors(Scenario scenario, Random rnd, Id<Link> linkId, double odSum, String type) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		for (int i = 0; i < odSum; i++) {
			Person pers = popFactory.createPerson(Id.create("visitor_" + personCounter + "_" + linkId.toString() + "-" + type, Person.class));
			
			Plan plan = popFactory.createPlan();
						
			Activity startActivity = popFactory.createActivityFromCoord(this.activityType, scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

			double startTime = calculateNormallyDistributedTime(9 * 3600., 1 * 3600.); // normally distributed around 9
//			double startTime = calculateRandomlyDistributedValue(9 * 3600., 1 * 3600.); // randomly distributed between 8 and 10 

			startActivity.setEndTime(startTime);
			plan.addActivity(startActivity);

			Leg leg1 = popFactory.createLeg("car");
			plan.addLeg(leg1);

			Point endPoint = getRandomPointInFeature(rnd, features.get(type));
			if ( endPoint==null ) log.warn("Point is null.");
			
			Activity endActivity = popFactory.createActivityFromCoord(this.activityType, MGC.point2Coord(endPoint) ) ;
			plan.addActivity(endActivity);

			pers.addPlan(plan) ;
			population.addPerson(pers) ;
			
			pers.getAttributes().putAttribute("subpopulation", type);
			
			personCounter++;
		}
	}

	private double calculateRandomlyDistributedValue(double i, double abweichung) {
		Random rnd = MatsimRandom.getRandom();
		double rnd1 = rnd.nextDouble();
		double rnd2 = rnd.nextDouble();
		
		double vorzeichen = 0;
		if (rnd1<=0.5){
			vorzeichen = -1.0;
		}
		else {
			vorzeichen = 1.0;
		}
		double endTimeInSec = (i + (rnd2 * abweichung * vorzeichen));
		return endTimeInSec;
	}
	
	private double calculateNormallyDistributedTime(double mean, double stdDev) {
		Random random = MatsimRandom.getRandom();
		boolean leaveLoop = false;
		double endTimeInSec = Double.MIN_VALUE;
		
		while(leaveLoop == false) {
			double normal = random.nextGaussian();
			endTimeInSec = mean + stdDev * normal;
			
			if (endTimeInSec >= 0. && endTimeInSec <= 24. * 3600.) {
				leaveLoop = true;
			}
		}
		
		if (endTimeInSec < 0. || endTimeInSec > 24. * 3600) {
			throw new RuntimeException("Shouldn't happen. Aborting...");
		}
		return endTimeInSec;
	}
	
	private static Point getRandomPointInFeature(Random rnd, SimpleFeature ft) {

		if ( ft!=null ) {

			Point p = null;
			double x, y;
			do {
				x = ft.getBounds().getMinX() + rnd.nextDouble() * (ft.getBounds().getMaxX() - ft.getBounds().getMinX());
				y = ft.getBounds().getMinY() + rnd.nextDouble() * (ft.getBounds().getMaxY() - ft.getBounds().getMinY());
				p = MGC.xy2Point(x, y);
			} while ( !((Geometry) ft.getDefaultGeometry()).contains(p));
			return p;

		} else {
			return null ;
		}


	}
	
}
