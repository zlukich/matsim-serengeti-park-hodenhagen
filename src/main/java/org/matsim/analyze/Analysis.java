package org.matsim.analyze;

import com.google.common.primitives.Doubles;
import org.matsim.analysis.VolumesAnalyzer;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;
import org.matsim.vehicles.Vehicle;


import java.io.*;
import java.util.*;

//erst mal nur fuer den base case, spaeter ueber String[] allCaseIdentifier iterieren und index als identifier mitgeben
public class Analysis {

    final static String networkFilePath0 = "./scenarios/output/output-serengeti-park-v1.0-run17000visitors/serengeti-park-v1.0-run1.output_network.xml.gz";
    final static String networkFilePath1 = "./scenarios/output/output-serengeti-park-eickelohOpen-run17000visitors/serengeti-park-v1.0-run1.output_network.xml.gz";
    final static String networkFilePath2 = "./scenarios/output/output-serengeti-park-TimeSlots-run17000visitors/serengeti-park-v1.0-run1.output_network.xml.gz";
    final static String networkFilePath3 = "./scenarios/output/output-serengeti-park-eickelohOpenAndTimeSlots-run17000visitors/serengeti-park-v1.0-run1.output_network.xml.gz";
    final static String eventsFile0 = "./scenarios/output/output-serengeti-park-v1.0-run17000visitors/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile1 = "./scenarios/output/output-serengeti-park-eickelohOpen-run17000visitors/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile2 = "./scenarios/output/output-serengeti-park-TimeSlots-run17000visitors/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile3 = "./scenarios/output/output-serengeti-park-eickelohOpenAndTimeSlots-run17000visitors/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String[] criticalLinkIdsLOS = {"1256928940003f","261715680000r", "246929390041f"}; //{ende l191, ende access road, ende safari}
    final static String caseIdentifier = "BaseCase";
    final static int minTime = 8*3600;
    final static int maxTime = 19*3600;
    final static int timeBinSize = 1800; // halbe stunde


    public static void main(String[] args) {

        Network network0 = NetworkUtils.readNetwork(networkFilePath0);
        Network network1 = NetworkUtils.readNetwork(networkFilePath1);
        Network network2 = NetworkUtils.readNetwork(networkFilePath2);
        Network network3 = NetworkUtils.readNetwork(networkFilePath3);

        //for (Double e : calculateTTIs(network, eventsFile))

       System.out.println(calculateOverallTTLoss(network0, eventsFile0));
        System.out.println(calculateOverallTTLoss(network1, eventsFile1));
        System.out.println(calculateOverallTTLoss(network2,eventsFile2));
        System.out.println(calculateOverallTTLoss(network3, eventsFile3));

        //writeLOS2CSV(manager, network, eventsFile, criticalLinkIdsLOS, caseIdentifier);
        //System.out.println("Overall Travel Time Index was: "+calculateOverallTTI(network, eventsFile));

    }

    //effectivity check

    private static void writeLOS2CSV(Network network, String eventsFile, String[] criticalLinkIds, String caseIdentifier){

        EventsManager manager = EventsUtils.createEventsManager();

        VolumesAnalyzer hourlyLinkVolumesAnalyzer = new VolumesAnalyzer(3600, 86399, network);
        manager.addHandler(hourlyLinkVolumesAnalyzer);
        EventsUtils.readEvents(manager, eventsFile);

        for (String lId : criticalLinkIds) {

            double criticalLinkCapacity = network.getLinks().get( Id.createLinkId(lId) ).getCapacity();

            double[] hlv = hourlyLinkVolumesAnalyzer.getVolumesPerHourForLink(Id.createLinkId(lId));
            List<Double> hourlyLinkVolumes = Doubles.asList(hlv);
            List<Double> hourlyLinkLOS = new ArrayList<>();

            for (double volume : hourlyLinkVolumes) {
                hourlyLinkLOS.add(volume/criticalLinkCapacity);
            }

            //write 2 csv
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/output-LOS/LOSHourly-"+caseIdentifier+"-Link" + lId + ".csv"));
                writer.write("VOLUMES_PER_HOUR,LOS_PER_HOUR");

                for (int i=0; i< hourlyLinkVolumes.size(); i++) {
                    writer.write(hourlyLinkVolumes.get(i) + "," + hourlyLinkLOS.get(i) + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // analyze costs

    //1.: gesamtbetrachtung

    // TT LOSS (OVERALL)
    private static double calculateOverallTTLoss(Network network, String eventsFile) {

        EventsManager manager = EventsUtils.createEventsManager();

        //actual travel time handler
        TravelTimePerAgentCalculator travelTimePerAgentCalculator = new TravelTimePerAgentCalculator();
        manager.addHandler(travelTimePerAgentCalculator);
        //freespeed travel time handler
        TravelledLinksStats travelledLinksStats = new TravelledLinksStats();
        manager.addHandler(travelledLinksStats);

        EventsUtils.readEvents(manager, eventsFile);
        //actual tt calc
        double totalTT= travelTimePerAgentCalculator.getTotalTravelTime();

        //freespeed tt calc
        double totalFreespeedTT=0;
        Map<Id<Link>, Integer> linkUses = travelledLinksStats.getLinkToNumberOfUses();
        for (Id<Link> link : linkUses.keySet() ) {
            totalFreespeedTT += linkUses.get(link) * calculateFreeSpeedTTOnLink(network, link);
        }

        return totalTT-totalFreespeedTT;
    }

    // TTI (PER BUS-/ OWN CAR USERS)
    public static List<Double> calculateTTIs(Network network, String eventsFile) {

        EventsManager manager1 = EventsUtils.createEventsManager();

        //actual travel time handler
        TravelTimePerAgentCalculator travelTimePerAgentCalculator = new TravelTimePerAgentCalculator();
        manager1.addHandler(travelTimePerAgentCalculator);
        EventsUtils.readEvents(manager1, eventsFile);

        Map<Id<Person>, Double> parkingPersonToTravelTimes = travelTimePerAgentCalculator.getParkingPersonToTravelTimes();
        double maxTravelTimeParking = Collections.max(parkingPersonToTravelTimes.values());

        Map<Id<Person>, Double> safariPersonToTravelTimes = travelTimePerAgentCalculator.getSafariPersonToTravelTimes();
        double maxTravelTimeSafari = Collections.max(safariPersonToTravelTimes.values());

        Set<Id<Person>> personsWithHighestValuesParking = new HashSet<>();
        for (Map.Entry<Id<Person>, Double> e : parkingPersonToTravelTimes.entrySet()) {
            if (e.getValue()==maxTravelTimeParking) {
                personsWithHighestValuesParking.add(e.getKey());
            }
        }

        Set<Id<Person>> personsWithHighestValuesSafari = new HashSet<>();
        for (Map.Entry<Id<Person>, Double> e : safariPersonToTravelTimes.entrySet()) {
            if (e.getValue()==maxTravelTimeSafari) {
                personsWithHighestValuesSafari.add(e.getKey());
            }
        }

        List<Double> ttis = new ArrayList<>();

        // PARKING

        EventsManager manager2 = EventsUtils.createEventsManager();

        TravelledLinksEventHandler travelledLinksHandler1 = new TravelledLinksEventHandler(personsWithHighestValuesParking, network);
        manager2.addHandler(travelledLinksHandler1);
        EventsUtils.readEvents(manager2, eventsFile);

        //such zu den gesammelten links die freeflow travel times raus, rechne tti aus und fuege sie ttis hinzu
        List<Double> ttis_Parking = new ArrayList<>();

        for (Id<Person> person : personsWithHighestValuesParking) {
            //test
            System.out.println("fragliche person: "+person.toString());
            //test
            System.out.println("persons real tt: "+parkingPersonToTravelTimes.get(person));
            //test
            System.out.println("persons ff tt: "+travelledLinksHandler1.calculateFreespeedTravelTimeForCertainPerson(person));

            double tti_person = parkingPersonToTravelTimes.get(person) / travelledLinksHandler1.calculateFreespeedTravelTimeForCertainPerson(person);
            ttis_Parking.add(tti_person);
        }

        ttis.add(Collections.max(ttis_Parking));

        // SAFARI

        EventsManager manager3 = EventsUtils.createEventsManager();

        TravelledLinksEventHandler travelledLinksHandler2 = new TravelledLinksEventHandler(personsWithHighestValuesSafari, network);
        manager3.addHandler(travelledLinksHandler2);
        EventsUtils.readEvents(manager3, eventsFile);

        List<Double> ttis_Safari = new ArrayList<>();

        for (Id<Person> person : personsWithHighestValuesSafari) {

            double tti_person = safariPersonToTravelTimes.get(person) / travelledLinksHandler2.calculateFreespeedTravelTimeForCertainPerson(person);
            ttis_Safari.add(tti_person);
        }

        ttis.add(Collections.max(ttis_Safari));

        return ttis;

    }

    /*public static List<Double> analyzeSection (Network network, String eventsFile) {


        int timeslice = 900;
        int maxTime = 86400;
        boolean calculateLinkTravelTimes = true;
        boolean calculateLinkToLinkTravelTimes = true;
        boolean filterModes = false;
        Set<String> analyzedModes = new HashSet<>(Arrays.asList("car"));

        TravelTimeCalculator monitorer = new TravelTimeCalculator

    }*/


    public static double calculateFreeSpeedTTOnLink (Network network, Id<Link> link) {
        double s = network.getLinks().get(link).getLength();
        double v = network.getLinks().get(link).getFreespeed();
        return s/v;
    }

    //calculate freespeed travel time on a particular section (for determination of tt loss):
    private static double calculateFreespeedTravelTime(Network network, Set<Id<Link>> questionableRoadLinks) {
        double freespeedTT = 0;
        for (Id<Link> lId : questionableRoadLinks) {
            double length = network.getLinks().get(Id.createLinkId(lId)).getLength();
            double freespeed = network.getLinks().get(Id.createLinkId(lId)).getFreespeed();
            double freespeedTT_onLink = length / freespeed;
            freespeedTT += freespeedTT_onLink;
        }
        return freespeedTT;
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

    public Map<Integer, Double> calculateTTLossOnSection_perBin (String eventsFile, Network network, String sectionLinksFileName, String startLink, String endLink) {

        Set<Id<Link>> links = loadLinksFile(sectionLinksFileName);

        EventsManager manager = EventsUtils.createEventsManager();
        LinkToLinkCalculator handler = new LinkToLinkCalculator(startLink, endLink, minTime, maxTime, timeBinSize);
        manager.addHandler(handler);
        EventsUtils.readEvents(manager, eventsFile);

        Map<Integer, Double> TTLossOnSection_perBin = new HashMap<>();

        for ( Map.Entry<Integer, Map<Id<Vehicle>, Double>> e :handler.getVehicleToTravelTime_TimeBin().entrySet() ) {
            //totalLossInBin = total TT per bin - number of usages * free flow TT on section
            double totalLossInBin = handler.getTotalTT_TimeBin().get(e.getKey()) - e.getValue().size()*calculateFreespeedTravelTime(network, links);
            TTLossOnSection_perBin.put(e.getKey(), totalLossInBin);
        }

        return TTLossOnSection_perBin;
    }




}
