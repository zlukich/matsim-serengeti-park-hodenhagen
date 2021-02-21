package org.matsim.prepare;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.LinkProperties;
import org.matsim.contrib.osm.networkReader.OsmTags;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
 * 
 * Previous step:
 * 
 * 1) export map from https://www.openstreetmap.org/export#map=13/52.7559/9.6485
 * 2) osmosis --read-xml file="/Users/ihab/Desktop/map-export.osm" --write-pbf file="/Users/ihab/Desktop/map-export.pbf"
 * 
 * Creates the road network layer.
 *
 * @author ikaddoura
 */

public final class CreateNetwork {

	private final String osmFile = "./original-input-data/osm-export.pbf";
	private final String outputFile = "./scenarios/serengeti-park-v1.0/input/serengeti-park-network-v1.0.xml.gz";

	public static void main(String[] args) throws Exception {
		CreateNetwork createNetwork = new CreateNetwork();
		createNetwork.run();
	}

	public void run() throws Exception {
		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:25832");
		
		Network network = new SupersonicOsmNetworkReader.Builder()
				
				.addOverridingLinkProperties(OsmTags.SERVICE, new LinkProperties(9, 1, 10 / 3.6, 300, false))
				.setPreserveNodeWithId((id) -> true) // make sure we keep the geometry of the roads...
				.setCoordinateTransformation(ct)
				
//				.setIncludeLinkAtCoordWithHierarchy((coord, hierachyLevel) ->
//						hierachyLevel <= 9 &&
//								coord.getX() >= 537566 && coord.getX() <= 548009 &&
//								coord.getY() >= 5841715 && coord.getY() <= 5848804
//				)

				.setAfterLinkCreated((link, osmTags, isReverse) -> link.setAllowedModes(new HashSet<>(Arrays.asList(TransportMode.car))))
				.build()
				.read(osmFile);
		
		
		Set<Id<Link>> forCarsRestrictedLinks = new HashSet<>(Arrays.asList(
				Id.createLinkId("3622817410000f"), Id.createLinkId("3622817410000r"),
				Id.createLinkId("3622817520000f"), Id.createLinkId("3622817520000r")));
		
		Set<Id<Link>> kassenLinks = new HashSet<>(Arrays.asList(
				// north
				Id.createLinkId("3624560720003f"),
				Id.createLinkId("3624560680002f"),
				Id.createLinkId("3624560690002f"),
				Id.createLinkId("3624560660002f"),
				
				// south
				Id.createLinkId("5297562640002f"),
				Id.createLinkId("2184588460002f"),
				Id.createLinkId("2184588440002f")));

		// make sure there is space for at least one vehicle on each link
		for (Link link : network.getLinks().values()) {
			if (link.getLength() <= 7.5) {
				link.setLength(8.);
			}
			
			if (forCarsRestrictedLinks.contains(link.getId())) {
				link.setFreespeed(0.001);
			}
			
			if (kassenLinks.contains(link.getId())) {
				link.setCapacity(120); // 30 sec per veh --> 3600/30 = 120
			}
		}

		new NetworkWriter(network).write(outputFile);
	}

}
