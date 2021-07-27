package org.matsim.analyze;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;

import java.util.HashMap;
import java.util.Map;

public class LinkToLinkPeakLoadCalculator implements LinkEnterEventHandler, LinkLeaveEventHandler {

    private final Map<Integer, Integer> counterToTimeBin = new HashMap<>();

    private final String startLink;
    private final String endLink;

    private final int minTime;
    private final int maxTime;
    private final int timeBinSize;
    private final int maxSlotIndex;

    public LinkToLinkPeakLoadCalculator(String startLink, String endLink, int minTime, int maxTime, int timeBinSize) {
        this.startLink = startLink;
        this.endLink = endLink;

        this.minTime = minTime;
        this.maxTime = maxTime;

        this.timeBinSize = timeBinSize;
        this.maxSlotIndex = (maxTime - minTime) / this.timeBinSize + 1;

    }



    @Override
    public void handleEvent(LinkEnterEvent event) {
        if (event.getLinkId().toString().equals(startLink)) {

            int timeslot = this.getTimeSlotIndex(event.getTime());

            if ( !counterToTimeBin.containsKey(timeslot) ) {
                counterToTimeBin.put(timeslot, 1);
            } else {
                int counter = counterToTimeBin.get(timeslot);
                counter++;
                counterToTimeBin.replace(timeslot, counter);
            }

        }
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        if (event.getLinkId().toString().equals(endLink)) {

            int timeslot = this.getTimeSlotIndex(event.getTime());

            if ( !counterToTimeBin.containsKey(timeslot) ) {
                counterToTimeBin.put(timeslot, -1);
            } else {
                int counter = counterToTimeBin.get(timeslot);
                counter--;
                counterToTimeBin.replace(timeslot, counter);
            }

        }
    }

    // MACHT DAS UEBERHAUPT SINN
    private int getTimeSlotIndex(double time) {
        return time > (double)this.maxTime ? this.maxSlotIndex : (int)((time - this.minTime) / this.timeBinSize);
    }

    public Map<Integer, Integer> getCounterToTimeBin() {
        return counterToTimeBin;
    }
}
