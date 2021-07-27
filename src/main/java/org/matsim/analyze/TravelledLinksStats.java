package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// bestimmt wie oft jeder link jeweils befahren wurde
public class TravelledLinksStats implements LinkEnterEventHandler {

    private final Map<Id<Link>, Integer> linkToNumberOfUses = new LinkedHashMap<>();


    @Override
    public void handleEvent(LinkEnterEvent event) {

        if ( linkToNumberOfUses.containsKey(event.getLinkId()) ) {
            int current = linkToNumberOfUses.get(event.getLinkId());
            current ++;
            linkToNumberOfUses.replace(event.getLinkId(), current);
        } else {
            linkToNumberOfUses.put(event.getLinkId(), 1);
        }
    }


    public Map<Id<Link>, Integer> getLinkToNumberOfUses() {
        return linkToNumberOfUses;
    }
}
