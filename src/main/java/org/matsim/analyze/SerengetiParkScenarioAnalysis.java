/*
package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.vehicles.Vehicle;

import java.io.*;
import java.util.*;

public class SerengetiParkScenarioAnalysis {

    final static String networkFile = "./scenarios/serengeti-park-v1.0/input/serengeti-park-network-v1.0.xml.gz";
    final static String eventsFile = "./scenarios/serengeti-park-v1.0/output/output-serengeti-park-v1.0-run1/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String accessRoadLinks = "./src/main/java/org/matsim/analyze/input_analysis/LinksAccessRoad.txt";
    final static String safariTourLinks = "./src/main/java/org/matsim/analyze/input_analysis/LinksSafariTour.txt";
    final static String l191Links = "./src/main/java/org/matsim/analyze/input_analysis/LinksL191.txt";

    public static void main(String[] args) {

        //calculateFreespeedTTsWholeJourney(eventsFile);
        double [] accessRoadResults = analyzeAccessRoad(eventsFile);
        double [] safariTourResults = analyzeSafariRoad(eventsFile);
        double [] l191Results = analyzeL191(eventsFile);

         try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/resultsTTanalysisBaseCase.csv"));
            writer.write("AREA,TOTAL_TT,AVG_TT,MAX_TT,TOT_TT_LOSS");
            for(double d : accessRoadResults) {
                writer.write(accessRoadResults[d] + "," + accessRoadResults[d] + "," + accessRoadResults [2] + "," + accessRoadResults[3] + "," + accessRoadResults[4] + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public static void calculateFreespeedTTsWholeJourney(String eventsFile) {
        EventsManager manager = EventsUtils.createEventsManager();
        TravelledLinksEventHandler handler = new TravelledLinksEventHandler();
        manager.addHandler(handler);
        EventsUtils.readEvents(manager, eventsFile);
    }


    //calculate {totalTT, avgTT, maxTT, totalTTLoss} on Access Road
    private static double[] analyzeSection(String eventsFile) {
        String accessLink = "2344589960000f";
        String exitLink = "3624560720003f";
        EventsManager manager = EventsUtils.createEventsManager();
        TravelTimeEventHandler accessRoadHandler = new TravelTimeEventHandler(accessLink, exitLink);
        manager.addHandler(accessRoadHandler);
        EventsUtils.readEvents(manager, eventsFile);

        double totalTT = calcTotalTravelTime(accessRoadHandler.vehicleToTravelTimes);
        double avgTT = calculateAvgTravelTime(accessRoadHandler.vehicleToTravelTimes);
        double maxTT = calcMaxTravelTime(accessRoadHandler.vehicleToTravelTimes);

        double freespeedTTAccessRoad = calculateFreespeedTravelTime(loadLinksFile(accessRoadLinks));
        double totalTTLoss = totalTT - accessRoadHandler.vehicleToTravelTimes.size() * freespeedTTAccessRoad;

        double [] results = {1., totalTT, avgTT, maxTT, totalTTLoss};

        return results;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/access_road_tts.csv"));
            writer.write("VEH_ID,ENTER_TIME,LEAVE_TIME,TT,TT_LOSS");
            for (Id<Vehicle> lId : accessRoadHandler.vehicleToTravelTimes.keySet()) {
                double TT_loss = accessRoadHandler.vehicleToTravelTimes.get(lId).get(2) - freespeedTTAcessRoad;
                writer.write(lId.toString() + "," + accessRoadHandler.vehicleToTravelTimes.get(lId).get(0) + "," + ","+accessRoadHandler.vehicleToTravelTimes.get(lId).get(1) + "," + accessRoadHandler.vehicleToTravelTimes.get(lId).get(2) + "," + TT_loss + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //calculate {totalTT, avgTT, maxTT, totalTTLoss} on Safari
    private static double[] analyzeSafariRoad(String eventsFile) {
        String accessLink = "7235731770000f";
        String exitLink = "246929390045f";
        EventsManager manager = EventsUtils.createEventsManager();
        TravelTimeEventHandler safariHandler = new TravelTimeEventHandler(accessLink, exitLink);
        manager.addHandler(safariHandler);
        EventsUtils.readEvents(manager, eventsFile);

        double totalTT = calcTotalTravelTime(safariHandler.vehicleToTravelTimes);
        double avgTT = calculateAvgTravelTime(safariHandler.vehicleToTravelTimes);
        double maxTT = calcMaxTravelTime(safariHandler.vehicleToTravelTimes);

        double freespeedTTSafari = calculateFreespeedTravelTime(loadLinksFile(safariTourLinks));
        double totalTTLoss = totalTT - safariHandler.vehicleToTravelTimes.size() * freespeedTTSafari;

        double [] results = {2., totalTT, avgTT, maxTT, totalTTLoss};

        return results;
    }

    //calculate {totalTT, avgTT, maxTT, totalTTLoss} on L191
    private static double[] analyzeL191(String eventsFile) {
        String accessLink = "2344590910000r";
        String exitLink = "1256928940003f";
        EventsManager manager = EventsUtils.createEventsManager();
        TravelTimeEventHandler l191Handler = new TravelTimeEventHandler(accessLink, exitLink);
        manager.addHandler(l191Handler);
        EventsUtils.readEvents(manager, eventsFile);

        double totalTT = calcTotalTravelTime(l191Handler.vehicleToTravelTimes);
        double avgTT = calculateAvgTravelTime(l191Handler.vehicleToTravelTimes);
        double maxTT = calcMaxTravelTime(l191Handler.vehicleToTravelTimes);

        double freespeedTTL191 = calculateFreespeedTravelTime(loadLinksFile(l191Links));
        double totalTTLoss = totalTT - l191Handler.vehicleToTravelTimes.size() * freespeedTTL191;

        double [] results = {3., totalTT, avgTT, maxTT, totalTTLoss};

        return results;


String accessLink = "7232382780000f";
        String exitLink = "261715680000r";
        EventsManager manager = EventsUtils.createEventsManager();
        TravelTimeEventHandler accessRoadHandler = new TravelTimeEventHandler(accessLink, exitLink);
        manager.addHandler(accessRoadHandler);
        EventsUtils.readEvents(manager, eventsFile);

        double freespeedTTAcessRoad = calculateFreespeedTravelTime(loadLinksFile(accessRoadLinks));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/access_road_tts.csv"));
            writer.write("VEH_ID,ENTER_TIME,LEAVE_TIME,TT,TT_LOSS");
            for (Id<Vehicle> lId : accessRoadHandler.vehicleToTravelTimes.keySet()) {
                double TT_loss = accessRoadHandler.vehicleToTravelTimes.get(lId).get(2) - freespeedTTAcessRoad;
                writer.write(lId.toString() + "," + accessRoadHandler.vehicleToTravelTimes.get(lId).get(0) + "," + ","+accessRoadHandler.vehicleToTravelTimes.get(lId).get(1) + "," + accessRoadHandler.vehicleToTravelTimes.get(lId).get(2) + "," + TT_loss + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //calculate freespeed travel time in a particular section (serves the determination of tt loss):
    private static double calculateFreespeedTravelTime(Set<Id<Link>> questionableRoadLinks) {
        Network network = NetworkUtils.readNetwork(networkFile);
        double freespeed;
        double length;
        double freespeedTT_onLink;
        double freespeedTT = 0;
        for (Id<Link> lId : questionableRoadLinks) {
            length = network.getLinks().get(Id.createLinkId(lId)).getLength();
            freespeed = network.getLinks().get(Id.createLinkId(lId)).getFreespeed();
            freespeedTT_onLink = length / freespeed;
            freespeedTT += freespeedTT_onLink;
        }
        return freespeedTT;
    }

    private static double calcTotalTravelTime(Map<Id<Vehicle>, Double> vehicleToTravelTimes) {
        double tt_sum = 0;
        for(Id<Vehicle> vId : vehicleToTravelTimes.keySet() ) {
            tt_sum += vehicleToTravelTimes.get(vId);
        }
        return tt_sum;
    }

    private static double calculateAvgTravelTime(Map<Id<Vehicle>, Double> vehicleToTravelTimes) {
        return calcTotalTravelTime(vehicleToTravelTimes) / vehicleToTravelTimes.size();
    }

    private static double calcMaxTravelTime(Map<Id<Vehicle>, Double> vehicleToTravelTimes) {
        return Collections.max(vehicleToTravelTimes.values());
    }

    //read links txt file (serves freespeed tt -> tt loss calculation):
    static Set<Id<Link>> loadLinksFile(String fileName){
        Set<Id<Link>> links = new HashSet<>();
        try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line = reader.readLine();
                while(line != null){
                    links.add(Id.createLinkId(line));
                    line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("could not load file " + fileName + ".\n you should do something else");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return links;
    }
}
*/
