package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TravelTimeEventHandler implements LinkEnterEventHandler, LinkLeaveEventHandler {

    private String accessLink;
    private String exitLink;
    final Map<Id<Vehicle>, Double> vehicleToEntryTime = new HashMap<>();
    //final Map<Id<Vehicle>, ArrayList<Double>> vehicleToTravelTimes = new LinkedHashMap<>();
    final Map<Id<Vehicle>, Double> vehicleToTravelTimes = new LinkedHashMap<>();

    public TravelTimeEventHandler(String accessLink, String exitLink) {
        this.accessLink = accessLink;
        this.exitLink = exitLink;
    }

    @Override
    public void handleEvent(LinkEnterEvent linkEnterEvent) {
        if (linkEnterEvent.getLinkId().equals(this.accessLink)) {
            this.vehicleToEntryTime.put(linkEnterEvent.getVehicleId(), linkEnterEvent.getTime());
            System.out.println("Befahren der Zufahrtsstrasse von veh: "+linkEnterEvent.getLinkId().toString()+" zur Uhrzeit: "+linkEnterEvent.getTime());
        }
    }

    @Override
    public void handleEvent(LinkLeaveEvent linkLeaveEvent) {
        if (linkLeaveEvent.getLinkId().equals(this.exitLink)) {
            System.out.println("Verlassen der Zufahrtsstrasse von veh: " + linkLeaveEvent.getVehicleId().toString()+" zur Uhrzeit: "+linkLeaveEvent.getTime());
            double startTime = vehicleToEntryTime.get(linkLeaveEvent.getVehicleId());
            double travelTime = linkLeaveEvent.getTime() - startTime;
            //ArrayList<Double> a = new ArrayList<>();
            //a.add(startTime);
            //a.add(linkLeaveEvent.getTime());
            //a.add(travelTime);
            this.vehicleToTravelTimes.put(linkLeaveEvent.getVehicleId(), travelTime);
            System.out.println("TT was: "+linkLeaveEvent.getTime());
        }
    }
}
