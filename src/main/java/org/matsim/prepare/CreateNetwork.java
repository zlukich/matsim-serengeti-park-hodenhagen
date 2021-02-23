package org.matsim.prepare;


import java.util.Arrays;
import java.util.HashSet;

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
				
				.addOverridingLinkProperties(OsmTags.UNCLASSIFIED, new LinkProperties(LinkProperties.LEVEL_UNCLASSIFIED, 1, 15 / 3.6, 720, false))
				.addOverridingLinkProperties(OsmTags.SERVICE, new LinkProperties(9, 1, 10 / 3.6, 720, false))
				.setPreserveNodeWithId((id) -> true) // make sure we keep the geometry of the roads...
				.setCoordinateTransformation(ct)
				.setAdjustCapacityLength(1.)
				
//				.setIncludeLinkAtCoordWithHierarchy((coord, hierachyLevel) ->
//						hierachyLevel <= 9 &&
//								coord.getX() >= 537566 && coord.getX() <= 548009 &&
//								coord.getY() >= 5841715 && coord.getY() <= 5848804
//				)

				.setAfterLinkCreated((link, osmTags, isReverse) -> link.setAllowedModes(new HashSet<>(Arrays.asList(TransportMode.car))))
				.build()
				.read(osmFile);

		// make sure there is space for at least one vehicle on each link
		for (Link link : network.getLinks().values()) {
			if (link.getLength() <= 7.5) {
				link.setLength(8.);
			}
		}

		new NetworkWriter(network).write(outputFile);
	}

}
