/* *********************************************************************** *
 * project: org.matsim.*
 * Controler.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.prepare;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;

import java.util.HashSet;
import java.util.Set;

public class NetworkAddEickelohParkingLot {


	private static final String INPUT_NETWORK = "./scenarios/serengeti-park-v1.0/input/serengeti-park-network-v1.0.xml.gz";
	private static final String OUTPUT_NETWORK = "./scenarios/serengeti-park-v1.0/input/serengeti-park-network-with-eickeloh-v1.0.xml.gz";


	public static void main(String[] args) {

		Network network = NetworkUtils.readNetwork(INPUT_NETWORK);

		NetworkFactory factory = network.getFactory();

		Node bestehenderKnoten = network.getNodes().get(Id.createNodeId(99999));
		Node node1 = NetworkUtils.createAndAddNode(network, Id.createNodeId(1000000 + 1), new Coord(546546546, 45465546));


		Set<String> modes = new HashSet<>();
		modes.add(TransportMode.car);

		//entweder Link selber erstellen und dann nach und nach die Attribute setzen
		Link link1 = factory.createLink(Id.createLinkId("zufahrtZumParkplatz"), bestehenderKnoten, node1);
		link1.setLength(1);
		link1.setCapacity(1);
		link1.setAllowedModes(modes);
		link1.getNumberOfLanes(1);
		link1.setNumberOfLanes(1);
		//dann ganz wichtig! Link hinzufuegen
		network.addLink(link1);

		//oder NetworkUtils.createAndAddLink verwenden
		Link link1Copy = NetworkUtils.createAndAddLink(network, Id.createLinkId(1), bestehenderKnoten, node1, 1., 1., 1., 1.);
		link1Copy.setAllowedModes(modes);

		Link link1_r = factory.createLink(Id.createLinkId("zufahrtZumParkplatz"), bestehenderKnoten, node1);



		//dump out network

		NetworkUtils.writeNetwork(network, OUTPUT_NETWORK);

	}
}
