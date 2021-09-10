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
import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.analysis.VolumesAnalyzer;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.api.internal.MatsimReader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.misc.OptionalTime;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author ikaddoura
 *
 */









//avg_freespeedTravelTime erklaeren

public class CreatePopulationModBasicPlans {

	private static final Logger log = Logger.getLogger(CreatePopulationModBasicPlans.class);

//	private final ArrayList<Double> timeBasedDemand = new ArrayList<>( Arrays. asList(0.153808594, 0.304199219, 0.314453125, 0.141113281, 0.055175781, 0.024902344, 0.006347656, 0.) );
	private final ArrayList<Double> timeBasedDemand = new ArrayList<>( Arrays. asList(0.153808594, 0.152099609, 0.152099609, 0.157226563, 0.157226563, 0.070556641, 0.070556641, 0.027587891, 0.027587891, 0.012451172, 0.012451172, 0.003173828, 0.003173828) ); // 0.5-hourly demand
	private final ArrayList<Double> demandUnderCapacityRestBy_25 = new ArrayList<>( Arrays. asList(0.1357777, 0.1357777, 0.1357777, 0.1357777, 0.1357777,
		0.1357777, 0.098908019, 0.027587891, 0.027587891, 0.012451172, 0.012451172, 0.003173828, 0.003173828, 0.) );
	private final ArrayList<Double> demandUnderCapacityRestBy_50 = new ArrayList<>( Arrays. asList(0.11444444, 0.11444444,
			0.11444444, 0.11444444, 0.11444444, 0.11444444, 0.11444444, 0.11444444, 0.05319448, 0.012451172, 0.012451172,
			0.003173828, 0.003173828, 0.));
	private final ArrayList<Double> demandUnderCapacityRestBy_75 = new ArrayList<>( Arrays. asList(0.0931111, 0.0931111, 0.0931111,
			0.0931111, 0.0931111, 0.0931111, 0.0931111, 0.0931111, 0.0931111, 0.0931111, 0.062541344, 0.003173828, 0.003173828, 0.));

	private int personCounter = 0;
	private final double avg_freespeedTravelTime = 231.;
	private final ArrayList<String> availableParkingLots;
	private final double numberOfTimeSlots;
	private final double timeSlotDuration;
	private final double checkInOpeningTime;
    private final double checkInClosingTime;

	private final double mean_demandThroughoutWholeDay = 2.447790887;
    private final double stdDev_demandThroughoutWholeDay = 0.102385966;
    private final double mean_slotArrivalDistribution = 4.31;
	private final double stdDev_slotArrivalDistribution = 0.42;
	private final double walkingTime = 252.;


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

	String plansFilePath = "./scenarios/output/output-serengeti-park-v1.0-run17000visitors-50-50-highLinkCap/serengeti-park-v1.0-run1.output_plans.xml.gz";


	public static void main(String[] args) throws IOException {

		final String networkFile = "./scenarios/input/serengeti-park-network-v1.0.xml.gz";
		final String outputFilePopulation = "./scenarios/input/population/serengeti-park-population-modv1.0.xml.gz";

		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkFile);
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// BaseC: Safari: 2000, each parking lot: 1250
		// EickelohOpen: Safari: 2000, eickeloh: 2500
		// TimeSlots: Safari: 2000, each parking lot: 1250
		// EickelohOpenAndTimeSlots: EickelohOpen: Safari: 2000, eickeloh: 2500

		ArrayList<String> twoLots = new ArrayList<>(Arrays.asList("serengetiParkplatz", "wasserlandParkplatz"));
		ArrayList<String> eickeloh = new ArrayList<>(Arrays.asList("eickelohParkplatz"));
		//CreatePopulationV2 popGenerator = new CreatePopulationV2(2000, 1250, 1250, 0, twoLots, 1, 9.5*3600., 16.5*3600.);
		//CreatePopulationV2 popGenerator = new CreatePopulationV2(2000, 0, 0, 2500, eickeloh, 1, 9.5*3600., 16.5*3600.);


		//CreatePopulationModBasicPlans popGenerator = new CreatePopulationModBasicPlans(twoLots, 4, 9.5*3600., 16.5*3600.);
		//CreatePopulationModBasicPlans popGenerator = new CreatePopulationModBasicPlans(eickeloh, 1, 9.5*3600., 16.5*3600.);
		CreatePopulationModBasicPlans popGenerator = new CreatePopulationModBasicPlans(eickeloh, 4, 9.5*3600., 16.5*3600.);



		//CreatePopulationV2 popGenerator = new CreatePopulationV2(2000, 1250, 1250, 0, twoLots, 7, 9.5*3600., 16.5*3600.);
		//CreatePopulationModBasicPlans popGenerator = new CreatePopulationModBasicPlans(2000, 1250, 1250, 0, twoLots, 1, 9.5*3600., 16.5*3600.);
		popGenerator.run(scenario);

		new PopulationWriter(scenario.getPopulation(), scenario.getNetwork()).write(outputFilePopulation);
		log.info("Population written to: " + outputFilePopulation);

	}


	public CreatePopulationModBasicPlans(ArrayList<String> availableParkingLots, int numberOfTimeSlots, double checkInOpeningTime, double checkInClosingTime) throws IOException {

		this.availableParkingLots = availableParkingLots;
		this.numberOfTimeSlots = numberOfTimeSlots;
		this.timeSlotDuration = (int) ( (checkInClosingTime-checkInOpeningTime) / numberOfTimeSlots );
		this.checkInOpeningTime = checkInOpeningTime;
		this.checkInClosingTime = checkInClosingTime;

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


		Config pseudoconfig = ConfigUtils.createConfig();
		Scenario pseudoscenario = ScenarioUtils.createScenario( pseudoconfig ) ;

		MatsimReader popReader = new PopulationReader(pseudoscenario);
		popReader.readFile(plansFilePath);



		// zeitslots:

		if ( this.numberOfTimeSlots != 1 ) {

			// erst mal alle agenten mit id und abfahrtszeit in neue map reinpacken

			Map<Id<Person>, OptionalTime> personToDepartureTime = new HashMap<>();

			for (Map.Entry<Id<Person>, ? extends Person> e : pseudoscenario.getPopulation().getPersons().entrySet()) {

				for (PlanElement pE : e.getValue().getSelectedPlan().getPlanElements()) {
					if (pE instanceof Activity) {
						Activity act = ((Activity) pE);

						if (act.getType().equals("home")) {
							OptionalTime homeEndTime = act.getEndTime();
							personToDepartureTime.put(e.getKey(), homeEndTime);
						}
					}
				}
			}

			// zeitslots: pack in map mit key=slot alle leute jeweils rein die gern drin waeren...

			Map<Integer, Set<Id<Person>>> slotToPerson = new HashMap<>();

			for (Map.Entry<Id<Person>, OptionalTime> e : personToDepartureTime.entrySet()) {
				OptionalTime time = e.getValue();
				int slot = getTimeSlotIndex(time.seconds());

				if ( slotToPerson.containsKey(slot) ) {
					slotToPerson.get(slot).add(e.getKey());
				} else {
					Set<Id<Person>> persons = new HashSet<>();
					persons.add(e.getKey());
					slotToPerson.put(slot, persons);
				}
			}

			// zeitslots: geh durch und sortiere ueberstand nach hinten weg

			int totalNumberAgents = 0;
			for (Map.Entry<Integer, Set<Id<Person>>> e: slotToPerson.entrySet()) {
				totalNumberAgents += e.getValue().size();
			}


			// erstelle neue Map wo alles reinkommt wie es am ende sein soll:

			Map<Integer, Set<Id<Person>>> slotToPerson_soll = slotToPerson;

			for (Map.Entry<Integer, Set<Id<Person>>> e : slotToPerson.entrySet()) {

				int originalSlot = e.getKey();

				//Set<Id<Person>> supernumPersons = new HashSet<>();
				int intendedSlotCapacity = (int) Math.round( sharePerTS(e.getKey()) * totalNumberAgents );

				Set<Id<Person>> setOfpersons = e.getValue();

				ArrayList<Id<Person>> persons = new ArrayList<>();
				persons.addAll(setOfpersons);

				int currentNumberOfPersonsInThisSlot = persons.size();



				while ( currentNumberOfPersonsInThisSlot>intendedSlotCapacity ) {

					System.out.println("test ");

					String reallocatedPersonIdString = "";



					// gleich verteilen auf den nächsten freien slot:

					boolean quit = false;

					// 3. Using Iterator with generic
					Iterator<Map.Entry<Integer, Set<Id<Person>>>> soll_entries = slotToPerson_soll.entrySet().iterator();

					while ( soll_entries.hasNext() && !quit ) {

						Map.Entry<Integer, Set<Id<Person>>> soll_entry = soll_entries.next();

						int assignedSlotCapacity = (int) Math.round( (sharePerTS(soll_entry.getKey()) * totalNumberAgents) );

						if ( soll_entry.getValue().size()<assignedSlotCapacity ) {

							System.out.println("slotToPerson_soll.size before assignment was: "+soll_entry.getValue().size());

							Id<Person> reallocatedPerson = persons.get( currentNumberOfPersonsInThisSlot - 1 );

							reallocatedPersonIdString = reallocatedPerson.toString();
							System.out.println("reallocated person id was: "+reallocatedPerson);

							soll_entry.getValue().add( reallocatedPerson );

							System.out.println("slotToPerson_soll.size after assignment was: "+soll_entry.getValue());

							quit = true;

						}

					}

					slotToPerson_soll.get( originalSlot ).remove( Id.createPersonId(reallocatedPersonIdString) );

					currentNumberOfPersonsInThisSlot -=1 ;

				}







				System.out.println("time slot share was: "+sharePerTS(e.getKey()));


				// HIER MUSS DAS


			}

			// erstelle die menschen in ihren slots wie gewohnt

			for (Map.Entry<Integer, Set<Id<Person>>> personsInSlot : slotToPerson_soll.entrySet()) {


				for (Id<Person> person : personsInSlot.getValue()) {

					String personName = person.toString();
					String type = personName.substring(personName.lastIndexOf("-") + 1);
					int f_Position = personName.lastIndexOf('_')+1;
					int s_Position = personName.indexOf('-');
					String origin = personName.substring(f_Position, s_Position);
					Id<Link> linkId = Id.createLinkId(origin);

						int first_Position =personName.indexOf('_')+1;
						int second_Position =personName.indexOf('_',first_Position+1);
						String agentNumber = personName.substring(first_Position, second_Position);


					if ( !availableParkingLots.contains(eickelohParkplatzDestination) ) {

						if ( !type.equals("serengetiPark") ){

							createVisitor_TimeSlots(scenario, rnd, agentNumber, linkId, personsInSlot.getKey(), type);

						} else {

							createSafariVisitor_TimeSlots(scenario, rnd, agentNumber, linkId, personsInSlot.getKey(), this.serengetiParkDestination, "");

						}
					} else {

						if ( !type.equals("serengetiPark") ){

							createVisitor_TimeSlots(scenario, rnd, agentNumber, linkId, personsInSlot.getKey(), this.eickelohParkplatzDestination);

						} else {

							createSafariVisitor_TimeSlots(scenario, rnd, agentNumber, linkId, personsInSlot.getKey(), this.serengetiParkDestination, this.eickelohParkplatzDestination);

						}

					}

				}

			}

		} else {

			// nur eickeloh-open scenario: ueber personen iterieren...

			for (Map.Entry<Id<Person>, ? extends Person> e : pseudoscenario.getPopulation().getPersons().entrySet()) {

				Id<Person> pId = e.getKey();
				String personName = pId.toString();
				String type = personName.substring(personName.lastIndexOf("-") + 1);
				int f_Position = personName.lastIndexOf('_')+1;
				int s_Position = personName.indexOf('-');
				String origin = personName.substring(f_Position, s_Position);
				Id<Link> linkId = Id.createLinkId(origin);

				int first_Position =personName.indexOf('_')+1;
				int second_Position =personName.indexOf('_',first_Position+1);
				String agentNumber = personName.substring(first_Position, second_Position);


				for (PlanElement pE : e.getValue().getSelectedPlan().getPlanElements()) {
					if (pE instanceof Activity) {
						Activity act = ((Activity) pE);

						if (act.getType().equals("home")) {

							OptionalTime homeEndTime = act.getEndTime();

							if ( !type.equals("serengetiPark") ){

								createVisitor_NoTimeSlots(scenario, rnd, agentNumber, linkId, this.eickelohParkplatzDestination, homeEndTime.seconds());

							} else {

								createSafariVisitor_NoTimeSlots(scenario, rnd, agentNumber, linkId, this.serengetiParkDestination, this.eickelohParkplatzDestination, homeEndTime.seconds());

							}

						}
					}
				}
			}


		}


		log.info("Population contains " + personCounter + " agents.");

		return scenario;
	}




	private void createVisitor_TimeSlots(Scenario scenario, Random rnd, String agentNumber, Id<Link> linkId, int time_index, String type) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();



		Person pers = popFactory.createPerson(Id.create("visitor_" + agentNumber + "_" + linkId.toString() + "_S" + (time_index+1) + "-" + type , Person.class));

		Plan plan = popFactory.createPlan();

		Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());


		double slotEndTime = checkInOpeningTime + ( (time_index + 1) * timeSlotDuration );
		double startTime =  slotEndTime - calculateLogNormallyDistributedSlotArrivalLeftTime(mean_slotArrivalDistribution, stdDev_slotArrivalDistribution) - avg_freespeedTravelTime;

		startActivity.setEndTime(startTime);
		plan.addActivity(startActivity);

		Leg leg1 = popFactory.createLeg("car");
		plan.addLeg(leg1);

		Point endPoint = getRandomPointInFeature(rnd, features.get(type));
		if ( endPoint==null ) log.warn("Point is null.");

		Activity endActivity = popFactory.createActivityFromCoord(this.activityType +"_S" + (time_index+1), MGC.point2Coord(endPoint));
		plan.addActivity(endActivity);

		pers.addPlan(plan);
		population.addPerson(pers);

		pers.getAttributes().putAttribute("subpopulation", type);

		personCounter++;

	}



	private void createSafariVisitor_TimeSlots(Scenario scenario, Random rnd, String agentNumber, Id<Link> linkId, int time_index, String finalType, String parkingType) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		Person pers = popFactory.createPerson(Id.create("visitor_" + agentNumber + "_" + linkId.toString() + "_S" + (time_index+1) + "-" + finalType, Person.class));

		Plan plan = popFactory.createPlan();

		Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

		double slotEndTime = checkInOpeningTime + ( (time_index + 1) * timeSlotDuration );
		double startTime =  slotEndTime - calculateLogNormallyDistributedSlotArrivalLeftTime(mean_slotArrivalDistribution, stdDev_slotArrivalDistribution) - avg_freespeedTravelTime;

		startActivity.setEndTime(startTime);
		plan.addActivity(startActivity);

		Leg leg1 = popFactory.createLeg("car");
		plan.addLeg(leg1);

		if (availableParkingLots.contains(eickelohParkplatzDestination)) {

			Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
			if ( parkingPoint==null ) log.warn("Point is null.");

			Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
			parkingActivity.setMaximumDuration(walkingTime + calculateRandomlyDistributedValue(4.5*60., 2.5*60.));
			plan.addActivity(parkingActivity);

			Leg leg2 = popFactory.createLeg("car");
			plan.addLeg(leg2);
		}

		Id<Link> endLinkId = Id.createLinkId("246929390045f");
		Activity endActivity = popFactory.createActivityFromLinkId(this.activityType + "_S" + (time_index+1), endLinkId);
		plan.addActivity(endActivity);

		pers.addPlan(plan);
		population.addPerson(pers);

		pers.getAttributes().putAttribute("subpopulation", finalType);

		personCounter++;

	}



	private void createVisitor_NoTimeSlots(Scenario scenario, Random rnd, String agentNumber, Id<Link> linkId, String type, double startFromHome) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		Person pers = popFactory.createPerson(Id.create("visitor_" + agentNumber + "_" + linkId.toString() + "-" + type, Person.class));

		Plan plan = popFactory.createPlan();
		Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

		double startTime =  startFromHome;

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

	private void createSafariVisitor_NoTimeSlots(Scenario scenario, Random rnd, String agentNumber, Id<Link> linkId, String finalType, String parkingType,  double startFromHome) {
		Population population = scenario.getPopulation();
		PopulationFactory popFactory = population.getFactory();

		Person pers = popFactory.createPerson(Id.create("visitor_" + agentNumber + "_" + linkId.toString() + "-" + finalType, Person.class));

		Plan plan = popFactory.createPlan();

		Activity startActivity = popFactory.createActivityFromCoord("home", scenario.getNetwork().getLinks().get(linkId).getFromNode().getCoord());

		double startTime =  startFromHome;
		startActivity.setEndTime(startTime);
		plan.addActivity(startActivity);

		Leg leg1 = popFactory.createLeg("car");
		plan.addLeg(leg1);

		Point parkingPoint = getRandomPointInFeature(rnd, features.get(parkingType));
		if ( parkingPoint==null ) log.warn("Point is null.");

		Activity parkingActivity = popFactory.createActivityFromCoord(this.parkingActivityType, MGC.point2Coord(parkingPoint));
		parkingActivity.setMaximumDuration(walkingTime + calculateRandomlyDistributedValue(4.5*60., 2.5*60.));
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

	private double calculateLogNormallyDistributedSlotArrivalLeftTime(double mean, double stdDev) {

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

			//return calculateLogNormallyDistributedTimeSlotShare(i);
			return 1/numberOfTimeSlots;	//gleichverteilt


			//return this.demandUnderCapacityRestBy_75.get(2*i) + this.demandUnderCapacityRestBy_75.get((2*i)+1);

		}
	}

	private double calculateLogNormallyDistributedTimeSlotShare (int i) {

		LogNormalDistribution demandDist = new LogNormalDistribution(mean_demandThroughoutWholeDay, stdDev_demandThroughoutWholeDay);
		double slotStart =  ( checkInOpeningTime + (i*timeSlotDuration) ) / 3600.;
		double slotEnd = ( checkInOpeningTime + ( (i+1) * timeSlotDuration ) ) / 3600.;
		System.out.println("slotStart: "+slotStart);
		System.out.println("slotEnd: "+slotEnd);

		return (1/0.9723) * ( demandDist.cumulativeProbability(slotEnd) - demandDist.cumulativeProbability(slotStart) );
	}



	private int getTimeSlotIndex(double time) {
		return (int) ( (time > this.checkInClosingTime || time == this.checkInClosingTime) ? this.numberOfTimeSlots-1 :  ( time - this.checkInOpeningTime ) / this.timeSlotDuration);
	}



}


