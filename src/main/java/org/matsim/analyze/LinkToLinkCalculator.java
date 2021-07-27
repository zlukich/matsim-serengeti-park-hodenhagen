package org.matsim.analyze;

import org.geotools.util.DerivedMap;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.*;

public class LinkToLinkCalculator implements LinkEnterEventHandler, LinkLeaveEventHandler {

    private final String startLink;
    private final String endLink;

    private final int minTime;
    private final int maxTime;
    private final int timeBinSize;
    private final int maxSlotIndex;

    //double[] avg_tts;

    //id: vehicleID, then timeslot , then dep time
    private final Map<Id<Vehicle>, ArrayList<Integer>> vehicleToEntryTime_TimeBin = new HashMap<>();
    //id: timeslot, then vehicleID, then trav time
    private final Map<Integer, Map<Id<Vehicle>, Double>> vehicleToTravelTime_TimeBin = new HashMap<>();


    public LinkToLinkCalculator(String startLink, String endLink, int minTime, int maxTime, int timeBinSize) {

        this.startLink = startLink;
        this.endLink = endLink;
        this.minTime = minTime;
        this.maxTime = maxTime;

        this.timeBinSize = timeBinSize;
        this.maxSlotIndex = (maxTime - minTime) / this.timeBinSize + 1;

    }

    @Override
    public void handleEvent(LinkEnterEvent event) {

        int timeslot = this.getTimeSlotIndex(event.getTime());

        if (event.getLinkId().toString().equals(this.startLink)) {

            ArrayList<Integer> timeSlotAndEntryTime = new ArrayList<>();
            timeSlotAndEntryTime.add(timeslot);
            timeSlotAndEntryTime.add( (int)event.getTime() );
            vehicleToEntryTime_TimeBin.put(event.getVehicleId(), timeSlotAndEntryTime);

        }

    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {

        if (event.getLinkId().toString().equals(this.endLink)) {

            Integer startTime = vehicleToEntryTime_TimeBin.get(event.getVehicleId()).get(1);
            Double travelTime = event.getTime() - startTime;
            Integer timeSlot = vehicleToEntryTime_TimeBin.get(event.getVehicleId()).get(0);

            if ( !vehicleToTravelTime_TimeBin.containsKey(timeSlot) ) {
                Map<Id<Vehicle>, Double> vehicleToTravelTime = new HashMap<>();
                vehicleToTravelTime.put(event.getVehicleId(), travelTime);
                vehicleToTravelTime_TimeBin.put(timeSlot, vehicleToTravelTime);
            } else {
                vehicleToTravelTime_TimeBin.get(timeSlot).put(event.getVehicleId(), travelTime);
            }

        }

    }

    // MACHT DAS UEBERHAUPT SINN
    private int getTimeSlotIndex(double time) {
        return time > (double)this.maxTime ? this.maxSlotIndex : (int)((time - this.minTime) / this.timeBinSize);
    }

    // AVG TT INNERHALB SLOT
    public double getAverageTravelTimeInSlot (int slotIndex) {

        double sum = 0;
        for (Double travelTime : this.vehicleToTravelTime_TimeBin.get(slotIndex).values()) {
            sum += travelTime;
        }
        return sum/this.vehicleToTravelTime_TimeBin.get(slotIndex).size();


    }

    public Map<Integer, Double> getAvgTT_TimeBin () {

        Map<Integer, Double> avgTT_TimeBin = new HashMap<>();

        for (Integer slot : this.vehicleToTravelTime_TimeBin.keySet()) {
            avgTT_TimeBin.put(slot, getAverageTravelTimeInSlot(slot));
        }

        return avgTT_TimeBin;
    }

    public Map<Integer, Double> getMaxTT_TimeBin () {

        Map<Integer, Double> maxTT_TimeBin = new HashMap<>();

        for (Map.Entry<Integer, Map<Id<Vehicle>, Double>> e : this.vehicleToTravelTime_TimeBin.entrySet()) {
            maxTT_TimeBin.put(e.getKey(), Collections.max(e.getValue().values()));
        }

        return maxTT_TimeBin;
    }

    public Map<Integer, Double> getTotalTT_TimeBin () {

        Map<Integer, Double> totalTT_TimeBin = new HashMap<>();

        for (Map.Entry<Integer, Map<Id<Vehicle>, Double>> slot : this.vehicleToTravelTime_TimeBin.entrySet()) {

            double totalPerSlot = 0;
            for (Double tt : this.vehicleToTravelTime_TimeBin.get(slot).values()) {
                totalPerSlot += tt;
            }
            totalTT_TimeBin.put(slot.getKey(), totalPerSlot);
        }

        return totalTT_TimeBin;
    }


    public Map<Integer, Map<Id<Vehicle>, Double>> getVehicleToTravelTime_TimeBin() {
        return vehicleToTravelTime_TimeBin;
    }
}
