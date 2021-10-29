package org.matsim.analyze;

import org.apache.commons.lang3.StringUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.population.Person;

import java.util.LinkedHashMap;
import java.util.Map;

public class JourneyVSParkingTimeHandler2 implements VehicleEntersTrafficEventHandler, VehicleLeavesTrafficEventHandler, PersonArrivalEventHandler,/* PersonDepartureEventHandler,*/ ActivityEndEventHandler, ActivityStartEventHandler, LinkLeaveEventHandler {

    // parking destinations
    private static final String e1 = "serengetiParkplatz";
    private static final String e2 = "wasserlandParkplatz";
    private static final String e3 = "eickelohParkplatz";
    private static final String destinationSeparator ="-";

    private final String activitySeparator ="_";

    private Map<Id<Person>, Double> personToTravelTime = new LinkedHashMap<>();
    private Map<Id<Person>, Double> personToParkingTime = new LinkedHashMap<>();



    private Map<Id<Person>, Double> personToSafariTime = new LinkedHashMap<>();


    // determination of parking time
    @Override
    public void handleEvent(ActivityEndEvent event) {

        String actType = event.getActType();
        String activity = StringUtils.substringBefore(actType, activitySeparator);
        //System.out.println("activity was: "+event.getActType());
        //System.out.println("actType was:"+actType);
        //System.out.println("activity was: "+activity);

        if ( activity.equals("parking") ) {
            personToParkingTime.merge(event.getPersonId(), event.getTime(), Double::sum);
            //System.out.println("parking end time was:"+event.getTime());
            Id<Person> personId = Id.createPersonId("visitor_4267_2344590910000r-eickelohParkplatz");
            if (event.getPersonId().equals(personId)) {
                System.out.println("visitor ends parking activity at " + event.getTime());
            }
        }
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        String actType = event.getActType();
        String activity = StringUtils.substringBefore(actType, activitySeparator);

        if ( activity.equals("parking") ) {
            personToParkingTime.merge(event.getPersonId(), -event.getTime(), Double::sum);
            Id<Person> personId = Id.createPersonId("visitor_4267_2344590910000r-eickelohParkplatz");
            if (event.getPersonId().equals(personId)) {
                System.out.println("visitor ends parking activity at " + event.getTime());
            }
        }
    }

    // determination of travel time

    // alternative mit vehicle enters / leaves traffic

    @Override
    public void handleEvent(VehicleEntersTrafficEvent event) {

        Id<Person> personId = Id.createPersonId(event.getVehicleId().toString());

        personToTravelTime.merge(personId, -event.getTime(), Double::sum);

        Id<Person> questionablePersonId = Id.createPersonId("visitor_4267_2344590910000r-eickelohParkplatz");
        if (event.getPersonId().equals(questionablePersonId)) {
            System.out.println("visitor enters traffic at " + event.getTime());
        }

    }

    @Override
    public void handleEvent(VehicleLeavesTrafficEvent event) {

        Id<Person> personId = Id.createPersonId(event.getVehicleId().toString());

        personToTravelTime.merge(personId, event.getTime(), Double::sum);


        Id<Person> questionablePersonId = Id.createPersonId("visitor_4267_2344590910000r-eickelohParkplatz");
        if (event.getPersonId().equals(questionablePersonId)) {
            System.out.println("visitor leaves traffic at " + event.getTime());
        }

    }


    @Override
    public void handleEvent(PersonArrivalEvent event) {

        // end of safari time:

        if ( event.getLinkId().toString().equals("246929390045f") ) {

            personToSafariTime.merge(event.getPersonId(), event.getTime(), Double::sum);

        }

    }



/*
    @Override
    public void handleEvent(PersonDepartureEvent event) {

        personToTravelTime.merge(event.getPersonId(), -event.getTime(), Double::sum);

    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {

        personToTravelTime.merge(event.getPersonId(), event.getTime(), Double::sum);

        // end of safari time:

        if ( event.getLinkId().toString().equals("246929390045f") ) {

            personToSafariTime.merge(event.getPersonId(), event.getTime(), Double::sum);

        }

    }
*/


    // owncar agents only : start of safari time
    @Override
    public void handleEvent(LinkLeaveEvent event) {

        if ( event.getLinkId().toString().equals("1325764750005f") ) {

            Id<Person> personId = Id.createPersonId(event.getVehicleId().toString());

            personToSafariTime.merge(personId, -event.getTime(), Double::sum);

        }

    }



    // total time including parking
    public Map<Id<Person>, Double> calculateTotalTimeIncludingParking () {
        Map<Id<Person>, Double> personToTotalTime = new LinkedHashMap<>();

        for (Map.Entry<Id<Person>, Double> e : personToTravelTime.entrySet()) {
            Id<Person> personId = e.getKey();
            double travelTime = e.getValue();
            double parkingTime = personToParkingTime.get(personId) != null? personToParkingTime.get(personId) : 0.;
            //double parkingTime = personToParkingTime.get(personId);
            personToTotalTime.put(personId, travelTime+parkingTime);
        }

        return personToTotalTime;
    }

    public Map<Id<Person>, Double> getPersonToTravelTime() {
        return personToTravelTime;
    }

    public Map<Id<Person>, Double> getPersonToParkingTime() {
        return personToParkingTime;
    }

    public Map<Id<Person>, Double> getPersonToSafariTime() {
        return personToSafariTime;
    }



}
