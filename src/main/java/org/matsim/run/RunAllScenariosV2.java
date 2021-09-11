/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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


package org.matsim.run;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamicsCorrectionApproach;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.lanes.Lane;
import org.matsim.lanes.LanesFactory;
import org.matsim.lanes.LanesToLinkAssignment;

import org.matsim.prepare.CreatePopulationModBasicPlans;
import org.matsim.prepare.CreatePopulationTimeBased;
import org.matsim.prepare.CreatePopulationTimeBasedDemand;
import org.matsim.prepare.CreatePopulationV2;


import java.io.IOException;
import java.util.*;

/**
 *
 * @author ikaddoura
 *
 */



public final class RunAllScenariosV2 {

	private static final Logger log = Logger.getLogger(RunAllScenariosV2.class );

	private final static int totalVisitors = 17000;
	private final static double percentageSafariOwnCar = 0.8;
	private final static double percentageVisitorsOwnCar = 0.9;
	private final static double checkInOpeningTime = 9.5*3600.;
	private final static double checkInClosingTime = 16.5*3600.; // check-in closing at 16:00 at the earliest  & 2 h before park closing time at the latest
	private final static double parkClosingTime = 18.5*3600;
	private final static double walkingTime = 252.;
	//

	private final ArrayList<String> parkingLots; //= {"Wasserlandparkplatz", "Serengeti-Parkplatz"}; // {"Eickeloh-Parkplatz"}
	private final int numberOfTimeSlots;	// 1 for no slot system, e.g. 4 slots: 7h / 4 slots => 1.75 h - slot => sharePerTS in CreatePopulationV2 anpassen!

	private final String caseIdentifier;
	final String networkFileName; // = e.g. "serengeti-park-network-v1.0.xml.gz";
	final String outputDirectory; // = e.g. "./scenarios/output/output-serengeti-park-v1.0-run17000visitors";

	// Supply
	// 45 sec per veh --> 3600/45 = 80 veh/h per check-in lane
	private final static int capacityPerCheckInBooth = 38; //jora vision: 	60
	private final static int numberOfUltimateCheckInBooths = 5;
	private final static int numberOfNorthCheckInBooths = 6;
	private final static int numberOfSouthCheckInBooths = 9;


	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();


		for (String arg : args) {
			log.info( arg );
		}

		if ( args.length==0 ) {
			args = new String[] {"./scenarios/input/serengeti-park-config-v1.0.xml"}  ;
		}

		//create cases
		ArrayList<String> twoLots = new ArrayList<>(Arrays.asList("serengetiParkplatz", "wasserlandParkplatz"));
		ArrayList<String> eickeloh = new ArrayList<>(Arrays.asList("eickelohParkplatz"));

		RunAllScenariosV2 baseScenario = new RunAllScenariosV2(twoLots, 1,"v1.0", "v1.0");
		RunAllScenariosV2 eickelohOpen = new RunAllScenariosV2(eickeloh, 1,"EickelohOpen", "eickelohOpen");
		RunAllScenariosV2 fourTimeSlots = new RunAllScenariosV2(twoLots, 4,"v1.0", "4TimeSlots");
		RunAllScenariosV2 eickelohOpenAndFourTimeSlots = new RunAllScenariosV2(eickeloh, 4,"EickelohOpen", "eickelohOpen_4TimeSlots");

		RunAllScenariosV2 threeTimeSlots = new RunAllScenariosV2(twoLots, 3,"v1.0", "3TimeSlots");
		RunAllScenariosV2 eickelohOpenAndThreeTimeSlots = new RunAllScenariosV2(eickeloh, 3,"EickelohOpen", "eickelohOpen_3TimeSlots");
		RunAllScenariosV2 twoTimeSlots = new RunAllScenariosV2(twoLots, 2,"v1.0", "2TimeSlots");
		RunAllScenariosV2 eickelohOpenAndTwoTimeSlots = new RunAllScenariosV2(eickeloh, 2,"EickelohOpen", "eickelohOpen_2TimeSlots");


		/*RunAllScenariosV2 sevenTimeSlots = new RunAllScenariosV2(twoLots, 7,"v1.0", "7TimeSlots");
		RunAllScenariosV2 eickelohOpenAndSevenTimeSlots = new RunAllScenariosV2(eickeloh, 7,"EickelohOpen", "eickelohOpenAnd7TimeSlots");
		RunAllScenariosV2 eightTimeSlots = new RunAllScenariosV2(twoLots, 8,"v1.0", "8TimeSlots");*/


		List<RunAllScenariosV2> scenarios = new ArrayList<>();
		scenarios.add(baseScenario);
		scenarios.add(eickelohOpen);
		scenarios.add(fourTimeSlots);
		scenarios.add(eickelohOpenAndFourTimeSlots);
		scenarios.add(threeTimeSlots);
		scenarios.add(eickelohOpenAndThreeTimeSlots);
		scenarios.add(twoTimeSlots);
		scenarios.add(eickelohOpenAndTwoTimeSlots);

		/*scenarios.add(sevenTimeSlots);
		scenarios.add(eickelohOpenAndSevenTimeSlots);
		scenarios.add(eightTimeSlots);*/


		for (RunAllScenariosV2 s: scenarios) {

			Config config = prepareConfig( args, s.parkingLots, s.numberOfTimeSlots, s.networkFileName, s.outputDirectory ) ;

			Scenario scenario = prepareScenario( config, s.caseIdentifier, s.parkingLots, s.numberOfTimeSlots);
			Controler controler = prepareControler( scenario ) ;
			controler.run();
			long endTime = System.currentTimeMillis();
			System.out.printf("Time taken: %d seconds%n", (endTime - startTime)/1000);

		}

	}

	//networkIdentifier: either v1.0 or EickelohOpen, caseIdentifier: either v1.0, EickelohOpen, TimeSlots or EickelohOpenAndTimeSlots
	public RunAllScenariosV2(ArrayList<String> parkingLots, int numberOfTimeSlots, String networkIdentifier, String caseIdentifier) {
		this.parkingLots = parkingLots;
		this.numberOfTimeSlots = numberOfTimeSlots;
		this.caseIdentifier = caseIdentifier;
		this.networkFileName = ("serengeti-park-network-" + networkIdentifier+ ".xml.gz");
		this.outputDirectory = ("./scenarios/output/output-serengeti-park-" + caseIdentifier + "-run" + totalVisitors + "visitors" + "-80-20");
	}


	public static Controler prepareControler( Scenario scenario ) {

		Gbl.assertNotNull(scenario);

		final Controler controler = new Controler( scenario );

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		return controler;
	}


	public static Scenario prepareScenario( Config config, String caseIdentifier, ArrayList<String> parkingLots, int numberOfTimeSlots ) throws IOException {
		Gbl.assertNotNull( config );

		final Scenario scenario = ScenarioUtils.createScenario( config );
		ScenarioUtils.loadScenario(scenario);

		Set<Id<Link>> forCarsRestrictedLinks = new HashSet<>(Arrays.asList(

				// ultimate check-in path (actually bus lane) should be unidirectional
				Id.createLinkId("3622817520000r"), Id.createLinkId("3622817410000f"),

				/*// bus lane reverse direction
				Id.createLinkId("3622817520000f"), Id.createLinkId("3622817410000r"),*/


				// longterm parking I guess
				Id.createLinkId("7232641180000f"),

				// ms: shortcut links on safari
				Id.createLinkId("394368960004r")

		));


		Set<Id<Link>> kassenLinksToBeRestricted = new HashSet<>(Arrays.asList(
				// north
				Id.createLinkId("3624560720003f"),
				Id.createLinkId("3624560680002f"),
				Id.createLinkId("3624560690002f"),
				Id.createLinkId("3624560660002f"),

				// south
				Id.createLinkId("5297562640002f"),
				Id.createLinkId("2184588460002f"),
				Id.createLinkId("2184588440002f")));

		Set<Id<Link>> usedKassenLinksNorth = new HashSet<>(Arrays.asList(
				// north
				Id.createLinkId("3624560720000f"),
				Id.createLinkId("3624560720001f"),
				Id.createLinkId("3624560720002f"),
				Id.createLinkId("3624560720003f")));

		Set<Id<Link>> usedKassenLinksSouth = new HashSet<>(Arrays.asList(
				// south
				Id.createLinkId("2184588440000f"),
				Id.createLinkId("2184588440001f"),
				Id.createLinkId("2184588440002f")));



		for (Link link : scenario.getNetwork().getLinks().values()) {

			if (forCarsRestrictedLinks.contains(link.getId())) {
				link.setFreespeed(0.001);
				link.setCapacity(0.);
			}

			// double capacity of first link on access road
			if (link.getId().equals(Id.createLinkId("2344589960000f"))) {
				link.setCapacity(1440.);
			}

			// restrict capacity of first link on safari tour
			if (link.getId().equals(Id.createLinkId("7232641200000f"))) {
				link.setCapacity(296.); //197 //296 - war gut //476
			}



			// ms: hier beginnen dinge die nur relevant sind wenn eickeloh nicht geoeffnet ist

			if ( !parkingLots.contains("eickelohParkplatz") ) {

				// use single check-in link instead of several parallel check-in links...
				if (kassenLinksToBeRestricted.contains(link.getId())) {
					link.setFreespeed(0.001);
					link.setCapacity(0.);
				}

				if (usedKassenLinksNorth.contains(link.getId())) {
					link.setNumberOfLanes(numberOfNorthCheckInBooths);
					link.setCapacity(numberOfNorthCheckInBooths * 720);
				}

				if (usedKassenLinksSouth.contains(link.getId())) {
					link.setNumberOfLanes(6.);
					link.setCapacity(6. * 720);
				}

				// link directly behind southern check-in
				if (link.getId().toString().equals("2184588440003f")) {
					link.setNumberOfLanes(4);
					link.setCapacity(4 * 720.);
				}


				// keep just one link for the north check-in area
				if (link.getId().toString().equals("3624560720003f")) {
					link.setCapacity(capacityPerCheckInBooth * numberOfNorthCheckInBooths);
					link.setFreespeed(2.7777);

					// account for the other check-in links
					link.setNumberOfLanes(numberOfNorthCheckInBooths);
				}


				// keep just one link for the south check-in area
				if (link.getId().toString().equals("2184588440002f")) {
					link.setCapacity(capacityPerCheckInBooth * numberOfSouthCheckInBooths);
					link.setFreespeed(2.7777);

					// account for the other check-in links
					//link.setLength(40. * (numberOfSouthCheckInBooths - 1));
					link.setNumberOfLanes(7.);
				}

				// install ultimate check-in booth next to main access link
				if (link.getId().toString().equals("3622817520005f")) {
					link.setCapacity(capacityPerCheckInBooth * numberOfUltimateCheckInBooths);
					link.setFreespeed(2.7777);

					//link.setLength(30. * (numberOfUltimateCheckInBooths - 1));
					link.setNumberOfLanes(2.);
				}
			}
		}

		// ms: ebenfalls dinge die im massnahmenfall eickeloh geoeffnet nicht relevant sind
		if ( !parkingLots.contains("eickelohParkplatz") ) {

			Id<Link> linkIdBeforeIntersection = Id.createLinkId("1325764790002f");
			Id<Link> nextLinkIdLeftTurn = Id.createLinkId("3624560720000f");
			Id<Link> nextLinkIdStraight = Id.createLinkId("1325764790003f");
			Id<Lane> leftTurnLaneId = Id.create("1325764790002f_left", Lane.class);
			Id<Lane> straightLaneId = Id.create("1325764790002f_straight", Lane.class);

			LanesFactory factory = scenario.getLanes().getFactory();
			// add lanes for link "1325764790002f"
			{
				LanesToLinkAssignment laneLinkAssignment = factory.createLanesToLinkAssignment(linkIdBeforeIntersection);

				Lane laneIn = factory.createLane(Id.create("1325764790002f_in", Lane.class));
				laneIn.addToLaneId(leftTurnLaneId);
				laneIn.addToLaneId(straightLaneId);
				laneIn.setStartsAtMeterFromLinkEnd(165.67285516126265);
				laneIn.setCapacityVehiclesPerHour(720. * 4);
				laneIn.setNumberOfRepresentedLanes(4.0);
				laneLinkAssignment.addLane(laneIn);

				// TODO: outgoing lanes must start after the in lane
				Lane lane0 = factory.createLane(leftTurnLaneId);
				lane0.addToLinkId(nextLinkIdLeftTurn); // turn left towards check-in link
				lane0.setStartsAtMeterFromLinkEnd(165.67285516126265 - 1);
				lane0.setCapacityVehiclesPerHour(720.);
				laneLinkAssignment.addLane(lane0);

				Lane lane1 = factory.createLane(straightLaneId);
				lane1.addToLinkId(nextLinkIdStraight); // straight!
				lane1.setStartsAtMeterFromLinkEnd(165.67285516126265 - 1);
				lane1.setCapacityVehiclesPerHour(720. * 3.0);
				lane1.setNumberOfRepresentedLanes(3.0);
				laneLinkAssignment.addLane(lane1);

				scenario.getLanes().addLanesToLinkAssignment(laneLinkAssignment);
			}

			/*// install ultimate check-in booth next to main access link
			NetworkFactory networkFactory = scenario.getNetwork().getFactory();*/

		}

		if (caseIdentifier.equals("v1.0")) {
			// demand: car occupation 3.4, 90% of all visitors arrive by own car while 50% of all visitors go on safari by own car
			// assumption: parking lot usage equally shared
			int ownCarVisitorsVehicles = (int) ( (percentageVisitorsOwnCar * totalVisitors) / 3.4);
			int serengetiParkVehicles = (int) ( (percentageSafariOwnCar * totalVisitors) / 3.4);
			int carparkVehicles = ownCarVisitorsVehicles - serengetiParkVehicles;
			int serengetiCarparkVehicles = (int) (carparkVehicles / parkingLots.size());
			int wasserlandCarparkVehicles = (int) (carparkVehicles / parkingLots.size());

			CreatePopulationTimeBased createPopulation = new CreatePopulationTimeBased (serengetiParkVehicles, serengetiCarparkVehicles, wasserlandCarparkVehicles, parkingLots, numberOfTimeSlots, checkInOpeningTime, checkInClosingTime);
			createPopulation.run(scenario);

		} else {

			CreatePopulationModBasicPlans createPopulation = new CreatePopulationModBasicPlans (parkingLots, numberOfTimeSlots, checkInOpeningTime, checkInClosingTime);
			createPopulation.run(scenario);

		}

		return scenario;
	}


	public static Config prepareConfig( String [] args, ArrayList<String> parkingLots, int numberOfTimeSlots, String networkFileName, String outputDirectory, ConfigGroup... customModules){

		OutputDirectoryLogging.catchLogEntries();

		String[] typedArgs = Arrays.copyOfRange( args, 1, args.length );

		final Config config = ConfigUtils.loadConfig( args[ 0 ], customModules );

//		config.controler().setRoutingAlgorithmType( FastAStarLandmarks );


		config.plansCalcRoute().setRoutingRandomness( 0. );

		config.plansCalcRoute().setAccessEgressType(PlansCalcRouteConfigGroup.AccessEgressType.accessEgressModeToLink);


		config.qsim().setUsingTravelTimeCheckInTeleportation( true );
		config.qsim().setTrafficDynamicsCorrectionApproach(TrafficDynamicsCorrectionApproach.INCREASE_NUMBER_OF_LANES);

		ConfigUtils.applyCommandline( config, typedArgs ) ;

		config.planCalcScore().getActivityParams("park").setOpeningTime( checkInOpeningTime );
		config.planCalcScore().getActivityParams("park").setClosingTime( parkClosingTime );
		config.planCalcScore().getActivityParams("park").setTypicalDuration( 4 * 3600. );


		if (parkingLots.contains("eickelohParkplatz")) {

			// set strategy settings for eickeloh subpopulation
			StrategyConfigGroup.StrategySettings stratEickelohBeta = new StrategyConfigGroup.StrategySettings();
			stratEickelohBeta.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
			stratEickelohBeta.setSubpopulation("eickelohParkplatz");
			stratEickelohBeta.setWeight(0.9);
			config.strategy().addStrategySettings(stratEickelohBeta);

			StrategyConfigGroup.StrategySettings stratEickelohReRoute = new StrategyConfigGroup.StrategySettings();
			stratEickelohReRoute.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
			stratEickelohReRoute.setSubpopulation("eickelohParkplatz");
			stratEickelohReRoute.setWeight(0.1);
			config.strategy().addStrategySettings(stratEickelohReRoute);

			/*StrategyConfigGroup.StrategySettings stratEickelohDeparture = new StrategyConfigGroup.StrategySettings();
			stratEickelohDeparture.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.TimeAllocationMutator);*/


			// create parking activity
			PlanCalcScoreConfigGroup.ActivityParams parkingActivity = new PlanCalcScoreConfigGroup.ActivityParams("parking");
			parkingActivity.setOpeningTime(checkInOpeningTime);
			parkingActivity.setClosingTime( checkInClosingTime + (2*3600.) );
			parkingActivity.setTypicalDuration( walkingTime + (4.5*60) ); // erwartete strecke zu laufen (1 richtung): 130 m, usual pace: 1.31 m/s, erw. dauer alles erledigen: 2-7 min => 468s.
			parkingActivity.setMinimalDuration(318.); // 198 s + 2*60 s
			config.planCalcScore().addActivityParams(parkingActivity);

			PlansConfigGroup plans = config.plans();
			PlansConfigGroup.ActivityDurationInterpretation actDurInterpret = PlansConfigGroup.ActivityDurationInterpretation.tryEndTimeThenDuration;
			plans.setActivityDurationInterpretation(actDurInterpret);

		}

		// install numberOfTimeSlots time slots activity types "park_Sx"
		if (numberOfTimeSlots>1) {

			int timeSlotDuration = (int) ( (checkInClosingTime-checkInOpeningTime) / numberOfTimeSlots );

			for(int i=0; i<numberOfTimeSlots; i++){
				PlanCalcScoreConfigGroup.ActivityParams slotDependentParkActivity = new PlanCalcScoreConfigGroup.ActivityParams("park_S"+(i+1));
				slotDependentParkActivity.setOpeningTime( checkInOpeningTime + (i * timeSlotDuration) );
				double closing = checkInOpeningTime + ( (i+1)*timeSlotDuration );
				slotDependentParkActivity.setClosingTime( closing );
				slotDependentParkActivity.setLatestStartTime(closing);
				slotDependentParkActivity.setTypicalDuration(4*3600.);
				config.planCalcScore().addActivityParams(slotDependentParkActivity);
			}
		}

		// set networkInput
		config.network().setInputFile(networkFileName);

		// set outputDirectory
		config.controler().setOutputDirectory(outputDirectory);

		config.controler().setWriteEventsInterval(50);
		config.controler().setWritePlansInterval(50);
		config.controler().setLastIteration(100);

		// test

		return config ;
	}

}