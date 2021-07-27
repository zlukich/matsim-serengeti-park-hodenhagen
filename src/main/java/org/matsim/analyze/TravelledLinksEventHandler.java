package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;

import java.util.*;

//dient dazu die free flow travel time fuer ausgewaehlte zu uebergebende personen zu berechnen dazu gibts die calculate ... methode

public class TravelledLinksEventHandler implements LinkEnterEventHandler {

    // temporary information
    private final Network network;
    private Set<Id<Person>> questionableAgents;
    private final Map<Id<Person>, Map<Id<Link>, Double>> usedLinksFFTTs2QuestionableAgents = new LinkedHashMap<>();


    public TravelledLinksEventHandler(Set<Id<Person>> questionableAgents, Network network) {
        this.network = network;
        this.questionableAgents = questionableAgents;
    }


    @Override
    public void handleEvent(LinkEnterEvent linkEnterEvent) {

        Id<Person> p = Id.createPersonId( linkEnterEvent.getVehicleId().toString() );

        if ( questionableAgents.contains(p) ) {

            double freespeed = network.getLinks().get(linkEnterEvent.getLinkId()).getFreespeed();
            double length = network.getLinks().get(linkEnterEvent.getLinkId()).getLength();
            double freespeedTravelTimeOnLink = length / freespeed ;



            if (usedLinksFFTTs2QuestionableAgents.containsKey(p)) {
                usedLinksFFTTs2QuestionableAgents.get(p).put(linkEnterEvent.getLinkId(), freespeedTravelTimeOnLink);
            } else {
                Map<Id<Link>, Double> linksMap = new LinkedHashMap<>();
                linksMap.put(linkEnterEvent.getLinkId(), freespeedTravelTimeOnLink);
                usedLinksFFTTs2QuestionableAgents.put(p, linksMap);
            }


        }

    }


    public double calculateFreespeedTravelTimeForCertainPerson (Id<Person> person) {
        double sum = 0;
        for ( Double v : usedLinksFFTTs2QuestionableAgents.get(person).values() ) {
            sum += v;
        }
        return sum;
    }

    public Map<Id<Person>, Map<Id<Link>, Double>> getUsedLinksFFTTs2QuestionableAgents() {
        return usedLinksFFTTs2QuestionableAgents;
    }
}
