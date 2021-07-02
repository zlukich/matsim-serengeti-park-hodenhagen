package org.matsim.analyze;

import org.matsim.api.core.v01.Id;

import org.matsim.api.core.v01.events.LinkLeaveEvent;

import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//dient dazu den freespeed travel time auszurechnen: schreibt length und freespeed der used links heraus nach csv
public class TravelledLinksEventHandler implements LinkLeaveEventHandler {

    private final Network network = NetworkUtils.readNetwork("./scenarios/output/output-serengeti-park-eickelohOpen-run200visitors/serengeti-park-v1.0-run1.output_network.xml.gz");
    private double hodenhagenCounter =0.;
    private double northCounter =0.;
    private double motorwayCounter =0.;

    private final Map<Id<Link>, ArrayList<Double>> hodenhagenRepresentativeUsedLinks = new LinkedHashMap<>(); //377320760000r
    private final Map<Id<Link>, ArrayList<Double>> northRepresentativeUsedLinks = new LinkedHashMap<>();  //44371520007f
    private final Map<Id<Link>, ArrayList<Double>> motorwayRepresentativeUsedLinks = new LinkedHashMap<>();   //2344590910000r

    // run
    public static void main(String[] args) {
        EventsManager manager = EventsUtils.createEventsManager();
        TravelledLinksEventHandler handler = new TravelledLinksEventHandler();
        manager.addHandler(handler);
        EventsUtils.readEvents(manager, "./scenarios/output/output-serengeti-park-eickelohOpen-run200visitors/serengeti-park-v1.0-run1.output_events.xml.gz");
    }

    @Override
    public void handleEvent(LinkLeaveEvent linkLeaveEvent) {

        if(linkLeaveEvent.getVehicleId().toString().equals("visitor_0_377320760000r-eickelohParkplatz")) {
            double freespeed = network.getLinks().get(linkLeaveEvent.getLinkId()).getFreespeed();
            double length = network.getLinks().get(linkLeaveEvent.getLinkId()).getLength();

            ArrayList<Double> a = new ArrayList<>();
            a.add(hodenhagenCounter);
            a.add(length);
            a.add(freespeed);

            this.hodenhagenRepresentativeUsedLinks.put(linkLeaveEvent.getLinkId(), a);
            this.hodenhagenCounter ++;
        }

        if(linkLeaveEvent.getVehicleId().toString().equals("visitor_20_44371520007f-eickelohParkplatz")) {
            double freespeed = network.getLinks().get(linkLeaveEvent.getLinkId()).getFreespeed();
            double length = network.getLinks().get(linkLeaveEvent.getLinkId()).getLength();

            ArrayList<Double> a = new ArrayList<>();
            a.add(northCounter);
            a.add(length);
            a.add(freespeed);

            this.northRepresentativeUsedLinks.put(linkLeaveEvent.getLinkId(), a);
            this.northCounter ++;
        }

        if(linkLeaveEvent.getVehicleId().toString().equals("visitor_24_2344590910000r-serengetiParkeickelohParkplatz")) {
            double freespeed = network.getLinks().get(linkLeaveEvent.getLinkId()).getFreespeed();
            double length = network.getLinks().get(linkLeaveEvent.getLinkId()).getLength();

            ArrayList<Double> a = new ArrayList<>();
            a.add(motorwayCounter);
            a.add(length);
            a.add(freespeed);

            this.motorwayRepresentativeUsedLinks.put(linkLeaveEvent.getLinkId(), a);
            this.motorwayCounter ++;
        }

        //this.motorwayRepresentativeUsedLinks.remove(Id.createLinkId(Id.createLinkId("2344590910000f")));

        //write to csv:
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/usedLinksFromHodenhagenInEickelohOpenScenario.csv"));
            writer.write("COUNTER,LID,LENGTH,FREESPEED\n");
            for (Id<Link> lId : this.hodenhagenRepresentativeUsedLinks.keySet()) {
                writer.write(this.hodenhagenRepresentativeUsedLinks.get(lId).get(0)+","+lId.toString()+","+this.hodenhagenRepresentativeUsedLinks.get(lId).get(1)+ "," + this.hodenhagenRepresentativeUsedLinks.get(lId).get(2)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/usedLinksFromNorthInEickelohOpenScenario.csv"));
            writer.write("COUNTER,LID,LENGTH,FREESPEED\n");
            for (Id<Link> lId : this.northRepresentativeUsedLinks.keySet()) {
                writer.write(this.northRepresentativeUsedLinks.get(lId).get(0)+","+lId.toString()+","+this.northRepresentativeUsedLinks.get(lId).get(1)+ "," + this.northRepresentativeUsedLinks.get(lId).get(2)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/usedLinksFromMotorwayInEickelohOpenScenario.csv"));
            writer.write("COUNTER,LID,LENGTH,FREESPEED\n");
            for (Id<Link> lId : this.motorwayRepresentativeUsedLinks.keySet()) {
                writer.write(this.motorwayRepresentativeUsedLinks.get(lId).get(0)+","+lId.toString()+","+this.motorwayRepresentativeUsedLinks.get(lId).get(1)+ "," + this.motorwayRepresentativeUsedLinks.get(lId).get(2)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}