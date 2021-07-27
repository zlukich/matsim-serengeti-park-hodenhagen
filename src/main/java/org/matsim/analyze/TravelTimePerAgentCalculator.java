package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.vehicles.Vehicle;

import java.util.*;

//tut genau was der name schon sagt
public class TravelTimePerAgentCalculator implements PersonDepartureEventHandler, PersonArrivalEventHandler {

    // parking destinations
    private final String e1 = "serengetiParkplatz";
    private final String e2 = "wasserlandParkplatz";
    private final String e3 = "eickelohParkplatz";

    //safari destinations
    private final String e4 = "serengetiPark";
    private final String e5 = "serengetiPark_eickelohParkplatz";

    private final String separator ="-";

    private final Map<Id<Person>, Double> personToDepartureTime = new HashMap<>();
    private final Map<Id<Person>, Double> parkingPersonToTravelTimes = new HashMap<>();
    private final Map<Id<Person>, Double> safariPersonToTravelTimes = new HashMap<>();

    //nicht interessant
    private final Map<Id<Person>, Double> personToDepartureTimeWithinSlot = new HashMap<>();
    private final Map<Id<Person>, Double> personToTravelTimesWithinSlot = new HashMap<>();

    public static void main(String[] args) {

        String eventsFile = "./scenarios/output/output-serengeti-park-eickelohOpen-run200visitors/serengeti-park-v1.0-run1.output_events.xml.gz";

        EventsManager manager1 = EventsUtils.createEventsManager();
        TravelTimePerAgentCalculator travelTimePerAgentCalculator = new TravelTimePerAgentCalculator();
        manager1.addHandler(travelTimePerAgentCalculator);
        EventsUtils.readEvents(manager1, eventsFile);

        System.out.println("size was: "+travelTimePerAgentCalculator.getParkingPersonToTravelTimes().size());
        System.out.println("size was: "+travelTimePerAgentCalculator.getSafariPersonToTravelTimes().size());



        /*for (Map.Entry<Id<Person>, Double> e : travelTimePerAgentCalculator.getParkingPersonToTravelTimes().entrySet()) {
            System.out.println("tt was :" +e.getValue() );
            System.out.println("id: " +e.getKey().toString());
        }*/



    }

    //ohne zeitliche eingrenzung: was auch so sein soll INTERESSANTER TEIL 02.07.21
    @Override
    public void handleEvent(PersonDepartureEvent event) {
        if (event.getLegMode()=="car") {

            if (!personToDepartureTime.containsKey(event.getPersonId())) {
                personToDepartureTime.put(event.getPersonId(), event.getTime());
                //System.out.println("Abfahrt von Link-Nr: "+event.getLinkId().toString());
            } else {
                personToDepartureTime.replace(event.getPersonId(), event.getTime());
            }
        }

    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {

        if (event.getLegMode()=="car") {

            double startTime = personToDepartureTime.get(event.getPersonId());
            double travelTime = event.getTime() - startTime;

            // differenziere zwischen bus- und privatfahrzeugnutzern
            int pos = event.getPersonId().toString().lastIndexOf(separator)+1;
            int length = event.getPersonId().toString().length();
            String ending = event.getPersonId().toString().substring(pos, length);

            /*if (ending.equals(e1) || ending.equals(e2) || ending.equals(e3)) {*/

            if (ending.equals("eickelohParkplatz")) {

                if (!parkingPersonToTravelTimes.containsKey(event.getPersonId())) {
                    parkingPersonToTravelTimes.put(event.getPersonId(), travelTime);
                } else {
                    double total = parkingPersonToTravelTimes.get(event.getPersonId());
                    total += travelTime;
                    parkingPersonToTravelTimes.replace(event.getPersonId(), total);
                }

            } else {

                if (!safariPersonToTravelTimes.containsKey(event.getPersonId())) {
                    safariPersonToTravelTimes.put(event.getPersonId(), travelTime);
                } else {
                    double total = safariPersonToTravelTimes.get(event.getPersonId());
                    total += travelTime;
                    safariPersonToTravelTimes.replace(event.getPersonId(), total);
                }

            }

        }
    }

    public double getMaximumTravelTimeParkingPersons () {
        return Collections.max(this.parkingPersonToTravelTimes.values());
    }

    public double getMaximumTravelTimeSafariPersons () {
        return Collections.max(this.safariPersonToTravelTimes.values());
    }

    public double getAverageTravelTimeParkingPersons () {
        double sum = 0;
        for (Double t : parkingPersonToTravelTimes.values()) {
            sum += t;
        }
        return sum / parkingPersonToTravelTimes.size();
    }

    public double getAverageTravelTimeSafariPersons () {
        double sum = 0;
        for (Double t : safariPersonToTravelTimes.values()) {
            sum += t;
        }
        return sum / safariPersonToTravelTimes.size();
    }

    public ArrayList<Double> getTravelTimeReliability_perRoute () {
        ArrayList<Double> travelTimeReliabilitiesPerRoute = new ArrayList<>();
        travelTimeReliabilitiesPerRoute.add(getMaximumTravelTimeParkingPersons()-getAverageTravelTimeParkingPersons());
        travelTimeReliabilitiesPerRoute.add(getMaximumTravelTimeSafariPersons()-getAverageTravelTimeSafariPersons());
        return travelTimeReliabilitiesPerRoute;
    }


    public double getTotalTravelTime() {
        double totalTravelTime = 0;
        for (Double value : parkingPersonToTravelTimes.values()) {
            totalTravelTime += value;
        }
        for (Double value : safariPersonToTravelTimes.values()) {
            totalTravelTime += value;
        }
        return totalTravelTime;
    }

    public Map<Id<Person>, Double> getParkingPersonToTravelTimes() {
        return parkingPersonToTravelTimes;
    }

    public Map<Id<Person>, Double> getSafariPersonToTravelTimes() {
        return safariPersonToTravelTimes;
    }




    /*//mit zeitlicher eingrenzung (das ist NICHT MEHR INTERESSANT. WIR HABEN DEN TTI JETZT ALS MAX VALUE /FREEFLOW TT DEFINIERT!
    public void handleWithinSlotEvent(PersonDepartureEvent event) {
        if (event.getTime() >= 10*3600) {
            this.personToDepartureTimeWithinSlot.put(event.getPersonId(), event.getTime());
            System.out.println("Abfahrt von Link-Nr: "+event.getLinkId().toString());
        }
    }

    public void handleWithinSlotEvent(PersonArrivalEvent event) {
        System.out.println("Person " + event.getPersonId().toString() + "arrived at " + event.getEventType());
        double startTime = personToDepartureTimeWithinSlot.get(event.getPersonId());
        double travelTime = event.getTime() - startTime;
        this.personToTravelTimesWithinSlot.put(event.getPersonId(), travelTime);
        System.out.println("Travel time was: " + travelTime);
    }

    public double getTotalTravelTimeWithinSlot() {
        double totalTravelTime = 0;
        for (Double value : this.personToTravelTimesWithinSlot.values()) {
            totalTravelTime += value;
        }
        return totalTravelTime;
    }

    public double getAVGTravelTimePerAgentWithinSlot () {
        return ( getTotalTravelTimeWithinSlot() / this.personToTravelTimesWithinSlot.size() );
    }*/

}
