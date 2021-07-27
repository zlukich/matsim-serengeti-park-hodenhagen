package org.matsim.analyze;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LinkToLinkLoadCalculator implements LinkEnterEventHandler, LinkLeaveEventHandler {

    private static int counter = 0;

    private final String startLink;
    private final String endLink;

    private final Map<Double, Integer> counterToTime = new HashMap<>();


    public LinkToLinkLoadCalculator(String startLink, String endLink) {
        this.startLink = startLink;
        this.endLink = endLink;
    }


    @Override
    public void handleEvent(LinkEnterEvent event) {
        if (event.getLinkId().toString().equals(startLink)) {
            counter ++;
            counterToTime.put(event.getTime(), counter);
        }
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        if (event.getLinkId().toString().equals(endLink)) {
            counter --;
            counterToTime.put(event.getTime(), counter);
        }
    }

    public double getPeakLoad () {
        return Collections.max(this.counterToTime.values());
    }

}
