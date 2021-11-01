package org.matsim.analyze;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;

import java.io.*;
import java.util.*;

// dazu da um anzugucken wie sich reisezeiten verändern bzw in parkzeit umgewandelt werden
// BETRACHTET REALE REISEZEITEN / FREEFLOWZEITEN / LOSS AUF ALLEN LINKS AUSSER AUF DEN DURCH EXCLUDEDLINKS FILE AUSGESCHLOSSENEN ABSCHNITTEN
// HIER WIRD DIE SAFARI TOUR AUSGESCHLOSSEN UM NUR DIE ANREISEZEIT AUCH IM OWNCAR-FALL ZU BEWERTEN

public class TestClass9modifiesTestClass8_outliersAnalysisOnly {



    // 5050
    // basisfall
    final static String eventsFile0 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-v1.0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";

    // time slots
    final static String eventsFile1 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile2 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile3 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile4 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile5 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile6 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile7 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile8 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile9 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile10 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile11 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile12 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile13 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile14 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile15 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";

    // eickeloh open
    final static String eventsFile16 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";

    // both measures at a time
    final static String eventsFile17 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile18 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile19 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile20 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile21 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile22 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile23 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile24 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile25 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile26 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile27 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile28 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile29 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile30 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile31 = "D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_events.xml.gz";

    // basisfall
    final static Network network0 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-v1.0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");

    // time slots
    final static Network network1 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network2 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network3 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network4 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network5 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-2TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network6 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network7 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network8 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network9 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network10 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-3TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network11 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network12 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network13 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network14 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network15 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-4TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");

    // eickeloh open
    final static Network network16 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");

    // both measures at a time
    final static Network network17 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network18 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network19 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network20 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network21 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_2TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network22 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network23 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network24 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network25 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network26 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_3TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network27 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots0-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network28 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots25-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network29 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots50-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network30 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots75-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network31 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/50-50/output-serengeti-park-eickelohOpen_4TimeSlots100-run17000visitors-50/serengeti-park-v1.0-run1.output_network.xml.gz");

    
    
/*
    
    
    // 8020
    // basisfall
    final static String eventsFile0 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-v1.0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";

    // time slots
    final static String eventsFile1 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile2 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile3 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile4 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile5 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile6 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile7 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile8 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile9 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile10 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile11 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile12 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile13 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile14 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile15 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";

    // eickeloh open
    final static String eventsFile16 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";

    // both measures at a time
    final static String eventsFile17 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile18 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile19 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile20 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile21 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile22 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile23 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile24 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile25 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile26 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile27 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile28 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile29 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile30 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";
    final static String eventsFile31 = "D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_events.xml.gz";

    // basisfall
    final static Network network0 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-v1.0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");

    // time slots
    final static Network network1 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network2 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network3 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network4 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network5 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-2TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network6 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network7 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network8 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network9 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network10 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-3TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network11 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network12 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network13 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network14 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network15 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-4TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");

    // eickeloh open
    final static Network network16 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");

    // both measures at a time
    final static Network network17 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network18 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network19 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network20 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network21 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_2TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network22 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network23 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network24 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network25 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network26 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_3TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network27 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots0-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network28 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots25-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network29 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots50-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network30 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots75-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");
    final static Network network31 = NetworkUtils.readNetwork("D:/serengetipark ergebnisse/final final/80-80/output-serengeti-park-eickelohOpen_4TimeSlots100-run17000visitors-80/serengeti-park-v1.0-run1.output_network.xml.gz");

*/

    // final static String eventsFile = "";
    // final static Network network = NetworkUtils.readNetwork("");


    // parking destinations
    private static final String e1 = "serengetiParkplatz";
    private static final String e2 = "wasserlandParkplatz";
    private static final String e3 = "eickelohParkplatz";
    private static final String separator ="-";

    private static final String excludedLinksFile = "./src/main/java/org/matsim/analyze/input_analysis/LinksSafariTour.txt";

    //final static Network network4 = NetworkUtils.readNetwork("serengeti-park-v1.0-run1.output_network.xml.gz");

    private static Map<String, Double> agentNumberToTravelTime_BaseCase = new LinkedHashMap<>();
    private static Map<String, Double> agentNumberToFreeflowTravelTime_BaseCase = new LinkedHashMap<>();
    private static Map<String, Double> agentNumberToTravelTimeLoss_BaseCase = new LinkedHashMap<>();


    private static final double vot_freeflow = -10.35/3600;
    private static final double vot_congested = -14.71/3600;
    private static final double vot_leisure = 11.97/3600;


    public static void main(String[] args) {

        ArrayList<String> eventsFiles = new ArrayList<>(Arrays.asList(eventsFile0, eventsFile1, eventsFile2, eventsFile3, eventsFile4, eventsFile5,
                eventsFile6, eventsFile7, eventsFile8, eventsFile9, eventsFile10, eventsFile11, eventsFile12, eventsFile13, eventsFile14, eventsFile15,
                eventsFile16, eventsFile17, eventsFile18, eventsFile19, eventsFile20, eventsFile21, eventsFile22, eventsFile23, eventsFile24,
                eventsFile25, eventsFile26, eventsFile27, eventsFile28, eventsFile29, eventsFile30, eventsFile31));

        ArrayList<Network> networkFiles = new ArrayList<>(Arrays.asList(network0, network1, network2, network3, network4, network5, network6, network7,
                network8, network9 , network10 , network11 , network12, network13, network14, network15, network16, network17, network18, network19, network20,
                network21, network22, network23, network24, network25, network26, network27, network28, network29, network30, network31));


        ArrayList<Double> asbussum_dFFTT = new ArrayList<>();
        ArrayList<Double> asbussum_dLoss = new ArrayList<>();
        ArrayList<Double> asbussum_dTT = new ArrayList<>();
        ArrayList<Double> asbussum_dParking = new ArrayList<>();
        ArrayList<Double> asbussum_dTGes = new ArrayList<>();
        ArrayList<Integer> asbuscounterT = new ArrayList<>();
        ArrayList<Integer> asbuscounterTGes = new ArrayList<>();
        ArrayList<Integer> asbuscounterU = new ArrayList<>();
        ArrayList<Double> asbussumsum_dU = new ArrayList<>();

        ArrayList<Double> aowncarsum_dFFTT = new ArrayList<>();
        ArrayList<Double> aowncarsum_dLoss = new ArrayList<>();
        ArrayList<Double> aowncarsum_dTT = new ArrayList<>();
        ArrayList<Double> aowncarsum_dParking = new ArrayList<>();
        ArrayList<Double> aowncarsum_dTGes = new ArrayList<>();
        ArrayList<Integer> aowncarcounterT = new ArrayList<>();
        ArrayList<Integer> aowncarcounterTGes = new ArrayList<>();
        ArrayList<Integer> aowncarcounterU = new ArrayList<>();
        ArrayList<Double> aowncarsumsum_dU = new ArrayList<>();

        ArrayList<Double> asum_dFFTT = new ArrayList<>();
        ArrayList<Double> asum_dLoss = new ArrayList<>();
        ArrayList<Double> asum_dTT = new ArrayList<>();
        ArrayList<Double> asum_dParking = new ArrayList<>();
        ArrayList<Double> asum_dTGes = new ArrayList<>();
        ArrayList<Integer> acounterT = new ArrayList<>();
        ArrayList<Integer> acounterTGes = new ArrayList<>();
        ArrayList<Integer> acounterU = new ArrayList<>();
        ArrayList<Double> asumsum_dU = new ArrayList<>();



        ArrayList<Double> aTTI = new ArrayList<>();
        ArrayList<Double> asafari_max = new ArrayList<>();
        ArrayList<Double> aowncar_max = new ArrayList<>();
        ArrayList<Double> asafariTTR = new ArrayList<>();
        ArrayList<Double> aowncarTTR = new ArrayList<>();





        int numberAgents = 0;
        int numberSBUSAgents = 0;
        int numberOWNCARAgents =0;

        int counter = 0;

        for (String events : eventsFiles) {

            String eventsIdentifier = "eventsFile"+(counter);

            EventsManager manager = EventsUtils.createEventsManager();

            JourneyVSParkingTimeHandler2 actualTimesHandler = new JourneyVSParkingTimeHandler2();
            HomeEndTimesEventHandler startTimesHandler = new HomeEndTimesEventHandler();

            manager.addHandler(actualTimesHandler);
            manager.addHandler(startTimesHandler);

            EventsUtils.readEvents(manager, events);

            // travel / parking time / safari time - linked map
            Map<Id<Person>, Double> personToTravelTime = actualTimesHandler.getPersonToTravelTime();
            Map<Id<Person>, Double> personToParkingTime = actualTimesHandler.getPersonToParkingTime();
            Map<Id<Person>, Double> personToSafariTime = actualTimesHandler.getPersonToSafariTime();
            Map<Id<Person>, Double> personToStartTime = startTimesHandler.getPersonToStartFromHomeTime();

            // reisezeiten nachkorrigieren


            for ( Map.Entry<Id<Person>, Double> e : personToSafariTime.entrySet() ) {

                personToTravelTime.merge(e.getKey(), -e.getValue(), Double::sum);

            }


            //double parkingTime = chronListedOwnCarPersonToParkingTime.get(key) != null? chronListedOwnCarPersonToParkingTime.get(key) : 0.;

            Map<Id<Person>, Double> safaribusPersonToTravelTime = new LinkedHashMap<>();
            Map<Id<Person>, Double> owncarPersonToTravelTime = new LinkedHashMap<>();


            for (Map.Entry<Id<Person>, Double> e : personToTravelTime.entrySet()) {

                Id<Person> person = e.getKey();
                final String id_string = person.toString();
                int pos = id_string.lastIndexOf(separator) + 1;
                String ending = id_string.substring(pos);

                // safari bus persons
                if (ending.equals(e1) || ending.equals(e2) || ending.equals(e3)) {
                    safaribusPersonToTravelTime.put(person, e.getValue());
                    // safari own car persons
                } else {
                    owncarPersonToTravelTime.put(person, e.getValue());
                }
            }


            // bestimmen welche agenten es gibt:
            Set<Id<Person>> agents = personToTravelTime.keySet();

            numberAgents = agents.size();
            numberSBUSAgents = safaribusPersonToTravelTime.size();
            numberOWNCARAgents = owncarPersonToTravelTime.size();


            // travel time loss per agent...

            EventsManager manager2 = EventsUtils.createEventsManager();

            // stelle set von excludedLinks bereit..

            Set <Id<Link>> excludedLinks = loadLinksFile(excludedLinksFile);

            TravelledLinksEventHandler travelledLinksHandler = new TravelledLinksEventHandler( excludedLinks, agents, networkFiles.get(counter) );

            manager2.addHandler(travelledLinksHandler);

            EventsUtils.readEvents(manager2, events);

            // FFTT:      unsortiert

            Map<Id<Person>, Double> personToFreeflowTravelTime = travelledLinksHandler.getPersonToFreeflowTravelTime();

            // LOSS:     unsortiert

            Map<Id<Person>, Double> personToTravelTimeLoss = new HashMap<>();

            for (Map.Entry<Id<Person>, Double> e : personToTravelTime.entrySet()) {

                double loss = e.getValue() - personToFreeflowTravelTime.get(e.getKey());

                personToTravelTimeLoss.put(e.getKey(), loss);
            }



            // merke ergebnisse für Basisszenario
            if (counter==0) {

                for (Map.Entry<Id<Person>, Double> e : personToTravelTime.entrySet()) {

                    Id<Person> person = e.getKey();

                    String name = person.toString();
                    int first_Position = name.indexOf('_')+1;
                    int second_Position = name.indexOf('_',first_Position+1);
                    String agentNumber = name.substring(first_Position, second_Position);

                    agentNumberToTravelTime_BaseCase.put(agentNumber, e.getValue());
                    agentNumberToFreeflowTravelTime_BaseCase.put(agentNumber, personToFreeflowTravelTime.get(person) );
                    agentNumberToTravelTimeLoss_BaseCase.put(agentNumber, personToTravelTimeLoss.get(person) );
                }



  /*              //write metrics_3 to csv
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/output-final/METRICS_3_BasisSzenario.csv"));
                    writer.write("SBUS_AGENTID,START,FFTT,LOSS\n");

                    for (Id<Person> person : safaribusPersonToTravelTime.keySet()) {
                        writer.write(person.toString()+","+personToStartTime.get(person)+","+personToFreeflowTravelTime.get(person)+","+personToTravelTimeLoss.get(person)+"\n");
                    }

                    writer.write("\n");
                    writer.write("OWNCAR_AGENTID,START,FFTT,LOSS\n");

                    for (Id<Person> person : owncarPersonToTravelTime.keySet()) {
                        writer.write(person.toString()+","+personToStartTime.get(person)+","+personToFreeflowTravelTime.get(person)+","+personToTravelTimeLoss.get(person)+"\n");

                    }

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

*/

                // eingehende betrachtung basisfall durchfuehren....


                // bestimme TTI

                double t_max = Collections.max(personToTravelTime.values());

                List< Id<Person> > keys = new ArrayList<>();

                for (Map.Entry<Id<Person>, Double> entry : personToTravelTime.entrySet()) {

                    if ( entry.getValue() == t_max ) {

                        keys.add( entry.getKey() );

                    }

                }

                List< Double > fftt_values = new ArrayList<>();

                for ( Map.Entry <Id<Person>, Double> entry : personToFreeflowTravelTime.entrySet() ) {

                    if ( keys.contains( entry.getKey() ) ) {

                        fftt_values.add( entry.getValue() );

                    }

                }

                double fftt_smallest = Collections.min(fftt_values);

                double tti = t_max / fftt_smallest;

                // bestimme t_max und TTR pro Gruppe

                // 1. Safari

                double safariT_max = Collections.max(safaribusPersonToTravelTime.values());


                // zurr kontrollle

                System.out.println("safari person times "+safaribusPersonToTravelTime);
                for (Map.Entry <Id<Person>, Double> entry : safaribusPersonToTravelTime.entrySet()) {

                    if (entry.getValue()==safariT_max) {
                        System.out.println("safari max t person was " + entry.getKey());
                        System.out.println("safari max t was " + entry.getValue());
                    }

                }


                double sum_safariT = 0;

                for (Double value : safaribusPersonToTravelTime.values()) {
                    sum_safariT += value;
                }

                double avg_safariT = sum_safariT / numberSBUSAgents;

                double safariTTR = safariT_max - avg_safariT;

                // 2. TTR OwnCar

                // sum up travel and parking time
                Map<Id<Person>, Double> owncarPersonToTotalTravelTime = new LinkedHashMap<>();

                for ( Map.Entry <Id<Person>, Double> entry : owncarPersonToTravelTime.entrySet() ) {

                    owncarPersonToTotalTravelTime.put( entry.getKey(), entry.getValue() + (personToParkingTime.get( entry.getKey() ) != null? personToParkingTime.get( entry.getKey() ) : 0.) );

                }


                double ownCarTGes_max = Collections.max(owncarPersonToTotalTravelTime.values());


                // zurr kontrollle
                System.out.println("own car person times "+owncarPersonToTotalTravelTime);
                for (Map.Entry <Id<Person>, Double> entry : owncarPersonToTotalTravelTime.entrySet()) {

                    if (entry.getValue()==ownCarTGes_max) {
                        System.out.println("owncar max t person was " + entry.getKey());
                        System.out.println("owncar max t was " + entry.getValue());
                    }

                }


                double sum_owncarTGes = 0;

                for (Double value : owncarPersonToTotalTravelTime.values()) {
                    sum_owncarTGes += value;
                }

                double avg_owncarTGes =  sum_owncarTGes / numberOWNCARAgents;

                double owncarTTR =  ownCarTGes_max - avg_owncarTGes;


                // VOR DEN ZAEHLER SCHLEIFE ZU ARRAYS HINZUFUEGEN ...
                aTTI.add(tti);
                asafari_max.add(safariT_max);
                aowncar_max.add(ownCarTGes_max);
                asafariTTR.add(safariTTR);
                aowncarTTR.add(owncarTTR);








            }

            if (counter!=0) {

                // berechne diff freeflow time and loss:

                Map<Id<Person>, Double> diff_FreeFlowTravelTime = new LinkedHashMap<>();
                Map<Id<Person>, Double> diff_TravelTimeLoss = new LinkedHashMap<>();
                Map<Id<Person>, Double> diff_TravelTime = new LinkedHashMap<>();


                for (Map.Entry<Id<Person>, Double> e : personToTravelTime.entrySet()) {

                    Id<Person> person = e.getKey();

                    String name = e.toString();
                    int first_Position = name.indexOf('_')+1;
                    int second_Position = name.indexOf('_',first_Position+1);
                    String agentNumber = name.substring(first_Position, second_Position);

                    diff_TravelTime.put( person, e.getValue() - agentNumberToTravelTime_BaseCase.get(agentNumber) );
                    diff_FreeFlowTravelTime.put( person, personToFreeflowTravelTime.get(person) - agentNumberToFreeflowTravelTime_BaseCase.get(agentNumber) );
                    diff_TravelTimeLoss.put( person, personToTravelTimeLoss.get(person) - agentNumberToTravelTimeLoss_BaseCase.get(agentNumber) );

                }

                // -> ergebnisse sollten negativ sein wenn verbesserung stattgefunden hat!

                // BERECHNUNG NUTZENAENDERUNG:

                Map<Id<Person>, Double> dU_FreeFlowTravelTime = new LinkedHashMap<>();
                Map<Id<Person>, Double> dU_TravelTimeLoss = new LinkedHashMap<>();

                Map<Id<Person>, Double> dU_Parking = new LinkedHashMap<>();

                for (Map.Entry<Id<Person>, Double> e : diff_FreeFlowTravelTime.entrySet()) {
                    dU_FreeFlowTravelTime.put(e.getKey(), vot_freeflow * e.getValue());
                }


                for (Map.Entry<Id<Person>, Double> e : diff_TravelTimeLoss.entrySet()) {
                    dU_TravelTimeLoss.put(e.getKey(), vot_congested * e.getValue());
                }

                for (Map.Entry<Id<Person>, Double> e : personToParkingTime.entrySet()) {
                    dU_Parking.put(e.getKey(), vot_leisure * e.getValue());
                }

                // SUMME ΔU

                Map<Id<Person>, Double> sum_dU = new LinkedHashMap<>();

                for (Map.Entry<Id<Person>, Double> e : dU_FreeFlowTravelTime.entrySet()) {
                    Id<Person> person = e.getKey();
                    double du_freeflow = e.getValue();
                    double du_loss = dU_TravelTimeLoss.get(person);
                    double du_parking = dU_Parking.get(person) != null? dU_Parking.get(person) : 0.;

                    double sum =  du_freeflow + du_loss + du_parking;
                    sum_dU.put(person, sum);
                }



                // variablen fuer sbus analyse



                double sbussum_dFFTT = 0;
                double sbussum_dLoss = 0;
                double sbussum_dTT = 0;
                double sbussum_dParking = 0;
                double sbussum_dTGes = 0;
                int sbuscounterT = 0;
                int sbuscounterTGes = 0;
                int sbuscounterU = 0;
                double sbussumsum_dU=0;


                // variablen fuer owncar analyse

                int owncarcounterT = 0;
                int owncarcounterTGes = 0;
                int owncarcounterU = 0;

                double owncarsum_dFFTT = 0;
                double owncarsum_dLoss = 0;
                double owncarsum_dTT = 0;
                double owncarsum_dParking = 0;
                double owncarsum_dTGes = 0;
                double owncarsumsum_dU=0;

                // variablen gesamt

                int counterT = 0;
                int counterTGes = 0;
                int counterU = 0;

                double sum_dFFTT = 0;
                double sum_dLoss = 0;
                double sum_dTT = 0;
                double sum_dParking = 0;
                double sum_dTGes = 0;
                double sumsum_dU=0;


/*

                //write metrics_3 to csv
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/output-final/METRICS_3_"+eventsIdentifier+".csv"));


                    writer.write("SBUS_AGENTID,ΔTT,ΔFFTT,ΔLOSS,dU_FFTT,dU_LOSS,SUM_dU\n");

                    for (Map.Entry<Id<Person>, Double> e : safaribusPersonToTravelTime.entrySet()) {
                        Id<Person> person = e.getKey();
                        writer.write(person.toString()+","+diff_TravelTime.get(person)+","+diff_FreeFlowTravelTime.get(person)+","+diff_TravelTimeLoss.get(person)+","
                                +dU_FreeFlowTravelTime.get(person)+","+dU_TravelTimeLoss.get(person)+","+sum_dU.get(person)+"\n");


                        sbussum_dFFTT += diff_FreeFlowTravelTime.get(person);
                        sbussum_dLoss += diff_TravelTimeLoss.get(person);
                        sbussum_dTT += diff_TravelTime.get(person);
                        sbussum_dParking += 0;
                        sbussum_dTGes += diff_TravelTime.get(person);

                        if ( diff_TravelTime.get(person)<0 || diff_TravelTime.get(person)==0 ) {
                            sbuscounterT++;
                            sbuscounterTGes++;
                        }

                        if ( sum_dU.get(person)>0 || sum_dU.get(person)==0 ) {
                            sbuscounterU++;
                        }


                        sbussumsum_dU += sum_dU.get(person);

                    }

                    writer.write("\n");


                    writer.write("OWNCAR_AGENTID,ΔTT,ΔFFTT,ΔLOSS,PARKING,dU_FFTT,dU_LOSS,dU_PARKING,SUM_dU\n");

                    for (Map.Entry<Id<Person>, Double> e : owncarPersonToTravelTime.entrySet()) {
                        Id<Person> person = e.getKey();
                        writer.write(person.toString()+","+diff_TravelTime.get(person)+","+diff_FreeFlowTravelTime.get(person)+","+diff_TravelTimeLoss.get(person)+","
                                +personToParkingTime.get(person)+","+dU_FreeFlowTravelTime.get(person)+","+dU_TravelTimeLoss.get(person)+","+dU_Parking.get(person)+","+sum_dU.get(person)+"\n");


                        owncarsum_dFFTT += diff_FreeFlowTravelTime.get(person);
                        owncarsum_dLoss += diff_TravelTimeLoss.get(person);
                        owncarsum_dTT += diff_TravelTime.get(person);
                        owncarsum_dParking += (personToParkingTime.get(person) != null? personToParkingTime.get(person) : 0.);
                        owncarsum_dTGes += diff_TravelTime.get(person) + (personToParkingTime.get(person) != null? personToParkingTime.get(person) : 0.);

                        if ( diff_TravelTime.get(person)<0 || diff_TravelTime.get(person)==0 ) {
                            owncarcounterT++;
                        }

                        double diff_t_ges = diff_TravelTime.get(person) + (personToParkingTime.get(person) != null? personToParkingTime.get(person) : 0.);

                        if (diff_t_ges <0 || diff_t_ges ==0) {
                            owncarcounterTGes++;
                        }

                        if ( sum_dU.get(person)>0 || sum_dU.get(person)==0 ) {
                            owncarcounterU++;
                        }

                        owncarsumsum_dU += sum_dU.get(person);

                    }


                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

*/

                // arrays befuellen (nur gruppen)

                asbussum_dFFTT.add(sbussum_dFFTT);
                asbussum_dLoss.add(sbussum_dLoss);
                asbussum_dTT.add(sbussum_dTT);
                asbussum_dParking.add(sbussum_dParking);
                asbussum_dTGes.add(sbussum_dTGes);
                asbuscounterT.add(sbuscounterT);
                asbuscounterTGes.add(sbuscounterTGes);
                asbuscounterU.add(sbuscounterU);
                asbussumsum_dU.add(sbussumsum_dU);

                aowncarsum_dFFTT.add(owncarsum_dFFTT);
                aowncarsum_dLoss.add(owncarsum_dLoss);
                aowncarsum_dTT.add(owncarsum_dTT);
                aowncarsum_dParking.add(owncarsum_dParking);
                aowncarsum_dTGes.add(owncarsum_dTGes);
                aowncarcounterT.add(owncarcounterT);
                aowncarcounterTGes.add(owncarcounterTGes);
                aowncarcounterU.add(owncarcounterU);
                aowncarsumsum_dU.add(owncarsumsum_dU);

                // array befuellen summe aus beiden gruppen

                asum_dFFTT.add(sbussum_dFFTT + owncarsum_dFFTT);
                asum_dLoss.add(sbussum_dLoss + owncarsum_dLoss);
                asum_dTT.add( sbussum_dTT + owncarsum_dTT);
                asum_dParking.add(sbussum_dParking + owncarsum_dParking);
                asum_dTGes.add(sbussum_dTGes + owncarsum_dTGes);
                acounterT.add(sbuscounterT + owncarcounterT);
                acounterTGes.add(sbuscounterTGes + owncarcounterTGes);
                acounterU.add(sbuscounterU + owncarcounterU);
                asumsum_dU.add(sbussumsum_dU + owncarsumsum_dU);



                // eingehende betrachtung durchfuehren....


                // bestimme TTI

                double t_max = Collections.max(personToTravelTime.values());

                List< Id<Person> > keys = new ArrayList<>();

                for (Map.Entry<Id<Person>, Double> entry : personToTravelTime.entrySet()) {

                    if ( entry.getValue() == t_max ) {

                        keys.add( entry.getKey() );

                    }

                }

                List< Double > fftt_values = new ArrayList<>();

                for ( Map.Entry <Id<Person>, Double> entry : personToFreeflowTravelTime.entrySet() ) {

                    if ( keys.contains( entry.getKey() ) ) {

                        fftt_values.add( entry.getValue() );

                    }

                }

                double fftt_smallest = Collections.min(fftt_values);

                double tti = t_max / fftt_smallest;

                // bestimme t_max und TTR pro Gruppe

                // 1. Safari

                double safariT_max = Collections.max(safaribusPersonToTravelTime.values());


                // zurr kontrollle

                System.out.println("safari person times "+safaribusPersonToTravelTime);
                for (Map.Entry <Id<Person>, Double> entry : safaribusPersonToTravelTime.entrySet()) {

                    if (entry.getValue()==safariT_max) {
                        System.out.println("safari max t person was " + entry.getKey());
                        System.out.println("safari max t was " + entry.getValue());
                    }

                }


                double sum_safariT = 0;

                for (Double value : safaribusPersonToTravelTime.values()) {
                    sum_safariT += value;
                }

                double avg_safariT = sum_safariT / numberSBUSAgents;

                double safariTTR = safariT_max - avg_safariT;

                // 2. TTR OwnCar

                // sum up travel and parking time
                Map<Id<Person>, Double> owncarPersonToTotalTravelTime = new LinkedHashMap<>();

                for ( Map.Entry <Id<Person>, Double> entry : owncarPersonToTravelTime.entrySet() ) {

                    owncarPersonToTotalTravelTime.put( entry.getKey(), entry.getValue() + (personToParkingTime.get( entry.getKey() ) != null? personToParkingTime.get( entry.getKey() ) : 0.) );

                }


                double ownCarTGes_max = Collections.max(owncarPersonToTotalTravelTime.values());


                // zurr kontrollle
                System.out.println("own car person times "+owncarPersonToTotalTravelTime);
                for (Map.Entry <Id<Person>, Double> entry : owncarPersonToTotalTravelTime.entrySet()) {

                    if (entry.getValue()==ownCarTGes_max) {
                        System.out.println("owncar max t person was " + entry.getKey());
                        System.out.println("owncar max t was " + entry.getValue());
                    }

                }


                double sum_owncarTGes = 0;

                for (Double value : owncarPersonToTotalTravelTime.values()) {
                    sum_owncarTGes += value;
                }

                double avg_owncarTGes =  sum_owncarTGes / numberOWNCARAgents;

                double owncarTTR =  ownCarTGes_max - avg_owncarTGes;


               // VOR DEN ZAEHLER SCHLEIFE ZU ARRAYS HINZUFUEGEN ...
                aTTI.add(tti);
                asafari_max.add(safariT_max);
                aowncar_max.add(ownCarTGes_max);
                asafariTTR.add(safariTTR);
                aowncarTTR.add(owncarTTR);


                }


            counter ++;
        }


/*


        //write metrics_3_summen to csv: // umrechnung in avg min / kopf oder avg / kopf ! nur gruppen IN MIN PRO AGENT
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/output-final/METRICS_3_summen_alle.csv"));
            writer.write("SBUS,AVG_D_FFTT,AVG_D_LOSS,AVG_D_TT,AVG_D_PARKING,AVG_D_TGES,ANTEIL_D_TT<0,ANTEIL_TGES<0,ANTEIL_DU>=0,AVG_SUM_DU\n");


            for ( int i=0; i<asum_dFFTT.size(); i++) {
                writer.write(""+","+ asbussum_dFFTT.get(i) / ( 60. * numberSBUSAgents ) +","+ asbussum_dLoss.get(i) / ( 60. * numberSBUSAgents ) +","
                        + asbussum_dTT.get(i) / ( 60. * numberSBUSAgents ) +","+asbussum_dParking.get(i) / ( 60. * numberSBUSAgents ) +","
                        +asbussum_dTGes.get(i) / ( 60. * numberSBUSAgents ) + "," + (double)asbuscounterT.get(i) / numberSBUSAgents + ","
                        + (double)asbuscounterTGes.get(i) / numberSBUSAgents + "," + (double)asbuscounterU.get(i) / numberSBUSAgents + ","
                        + asbussumsum_dU.get(i) / numberSBUSAgents + "\n");
            }

            writer.write("OWNCAR,AVG_D_FFTT,AVG_D_LOSS,AVG_D_TT,AVG_D_PARKING,AVG_D_TGES,ANTEIL_D_TT<0,ANTEIL_TGES<0,ANTEIL_DU>=0,AVG_SUM_DU\n");

            for ( int i=0; i<asum_dFFTT.size(); i++) {
                writer.write("" + "," + aowncarsum_dFFTT.get(i) / ( 60. * numberOWNCARAgents ) +","+ aowncarsum_dLoss.get(i) / ( 60. * numberOWNCARAgents ) +","
                        + aowncarsum_dTT.get(i) / ( 60. * numberOWNCARAgents ) +","+aowncarsum_dParking.get(i) / ( 60. * numberOWNCARAgents ) +","
                        +aowncarsum_dTGes.get(i) / ( 60. * numberOWNCARAgents ) + "," + (double)aowncarcounterT.get(i) / numberOWNCARAgents + ","
                        + (double)aowncarcounterTGes.get(i) / numberOWNCARAgents + "," + (double)aowncarcounterU.get(i) / numberOWNCARAgents + ","
                        + aowncarsumsum_dU.get(i) / numberOWNCARAgents + "\n");
            }

            writer.write("OVERALL,AVG_D_FFTT,AVG_D_LOSS,AVG_D_TT,AVG_D_PARKING,AVG_D_TGES,ANTEIL_D_TT<0,ANTEIL_TGES<0,ANTEIL_DU>=0,AVG_SUM_DU\n");

            for ( int i=0; i<asum_dFFTT.size(); i++) {
                writer.write("" + "," + asum_dFFTT.get(i) / ( 60 * numberAgents ) +","+ asum_dLoss.get(i) / ( 60 * numberAgents ) +","
                        + asum_dTT.get(i) / ( 60 * numberAgents ) +","+asum_dParking.get(i) / ( 60 * numberAgents ) +"," +asum_dTGes.get(i) / ( 60 * numberAgents )
                        + "," + (double)acounterT.get(i) / numberAgents + "," + (double)acounterTGes.get(i) / numberAgents + "," + (double)acounterU.get(i) / numberAgents + ","
                        + asumsum_dU.get(i) / numberAgents + "\n");
            }



            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

*/

        //write metrics_3_naehereBetrachtung to csv
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/java/org/matsim/analyze/output_analysis/output-final/METRICS_3_NaehereBetrachtung.csv"));


            writer.write("TTI,SBUS_MAX,OWNCAR_MAX,SBUS_TTR,OWNCAR_TTR\n");

            for ( int i=0; i<aTTI.size(); i++) {
                writer.write(aTTI.get(i) +","+ asafari_max.get(i) / 60. +","+ aowncar_max.get(i) / 60. +","
                        + asafariTTR.get(i) / 60. +","+ aowncarTTR.get(i) / 60.  + "\n");
            }


            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    //read links txt file
    static Set<Id<Link>> loadLinksFile(String fileName){

        Set<Id<Link>> links = new HashSet<>();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while(line != null){

                links.add(Id.createLinkId(line));
                line = reader.readLine();

            }

        } catch (FileNotFoundException e) {

            throw new RuntimeException("could not load file " + fileName + ".\n you should do something else");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return links;
    }


}
