package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.population.Person;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HomeEndTimesEventHandler implements ActivityEndEventHandler {

    public Map<Id<Person>, Double> getPersonToStartFromHomeTime() {
        return personToStartFromHomeTime;
    }

    private Map<Id<Person>, Double> personToStartFromHomeTime = new HashMap<>();


    @Override
    public void handleEvent(ActivityEndEvent event) {

        if (event.getActType().equals("home")) {

            personToStartFromHomeTime.put(event.getPersonId(), event.getTime());

        }

    }
}
