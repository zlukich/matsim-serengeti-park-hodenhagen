package org.matsim.prepare;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NetworkModifyForC {

    private static final String INPUT_NETWORK = "./scenarios/input/serengeti-park-network-v1.0.xml.gz";
    private static final String OUTPUT_NETWORK = "./scenarios/input/serengeti-park-network-C.xml.gz";


    public static void main(String[] args) {

        Network network = NetworkUtils.readNetwork(INPUT_NETWORK);
        NetworkFactory factory = network.getFactory();

        //leave parking lot to safari
        Set<String> modes = new HashSet<>();
        modes.add(TransportMode.car);

        Node start = network.getNodes().get(Id.createNodeId("1566973144"));

        //erster ueber parkplatz
        Node n1 = NetworkUtils.createAndAddNode(network, Id.createNodeId("914822850"), new Coord(542021.211,5845141.804));
        Link l1 = NetworkUtils.createAndAddLink(network, Id.createLinkId("924490180134f"), start, n1,  47.479, 2.7777777777777777, 1440., 2.);
        l1.setAllowedModes(modes);


        //zweiter ueber parkplatz
        Node n2 = NetworkUtils.createAndAddNode(network, Id.createNodeId("914822851"), new Coord(541937.095,5845158.739));
        Link l2 = NetworkUtils.createAndAddLink(network, Id.createLinkId("924490180135f"), n1, n2,  85.868, 2.7777777777777777, 1440., 2.);
        l2.setAllowedModes(modes);

        //dritter ueber parkeplatz
        Node notEndOfAccessRoad = NetworkUtils.createAndAddNode(network, Id.createNodeId("914822852"), new Coord(541883.6524166291, 5845151.907719018));
        Link l3 = NetworkUtils.createAndAddLink(network, Id.createLinkId("924490180136f"), n2, notEndOfAccessRoad,  53.789, 2.7777777777777777, 1440., 2.);
        l3.setAllowedModes(modes);

        //ersatz fuer den link der direkt geradeaus fuehrt
        Node notEndOfAccessRoadNode = network.getNodes().get(Id.createNodeId("914822852"));
        Node originalLinkToNode = network.getNodes().get(Id.createNodeId("3666177100"));
        Link originalLink =  network.getLinks().get(Id.createLinkId("1325764790000f"));

        Link replacement = factory.createLink(Id.createLinkId("924490180137f"), notEndOfAccessRoadNode, originalLinkToNode);
        replacement.setLength(originalLink.getLength());
        replacement.setCapacity(originalLink.getCapacity());
        replacement.setAllowedModes(originalLink.getAllowedModes());
        replacement.setNumberOfLanes(originalLink.getNumberOfLanes());
        replacement.setFreespeed(originalLink.getFreespeed());
        network.addLink(replacement);


        //access the parking lots
        //zufahrt1
        createInverseLink(network, factory,"246929390062f", "246929390062r");
        createInverseLink(network, factory, "246929390061f", "246929390061r");
        createInverseLink(network, factory, "246929390060f", "246929390060r");
        createInverseLink(network, factory,"246929390059f","246929390059r");
        createInverseLink(network, factory,"246929390058f", "246929390058r");
        createInverseLink(network, factory,"246929390057f", "246929390057r");


        //restrictions
        Set<Id<Link>> forCarsRestrictedLinks = new HashSet<>(Arrays.asList(
                //zufahrt
                Id.createLinkId("246929390062f"),
                Id.createLinkId("246929390061f"),
                Id.createLinkId("246929390060f"),
                Id.createLinkId("246929390059f"),
                Id.createLinkId("246929390058f"),

                //serengetiparkplatz westseite
                //zu
                Id.createLinkId("3551887010000f"),
                //aus
                Id.createLinkId("2226309730000r"),
                Id.createLinkId("394368880003r"),
                //kasse oben
                Id.createLinkId("3624560720000f"),
                Id.createLinkId("3624560720001f"),
                Id.createLinkId("3624560720002f"),
                Id.createLinkId("3624560720003f"),

                //zufahrtslink hinter access road die direkt geradeaus fuehrt
                Id.createLinkId("1325764790000f")

                //Id.createLinkId(""),2226309730000r

        ));

        for (Link link : network.getLinks().values()) {

            if (forCarsRestrictedLinks.contains(link.getId())) {
                link.setFreespeed(0.001);
                link.setCapacity(0.);
            }
        }



        //dump out network

        NetworkUtils.writeNetwork(network, OUTPUT_NETWORK);

    }

    private static void createInverseLink(Network network, NetworkFactory factory, String idOriginal, String idInverse){

        Link original = network.getLinks().get(Id.createLinkId(idOriginal));

        Link inverseLink = factory.createLink(Id.createLinkId(idInverse), original.getToNode(), original.getFromNode());
        inverseLink.setLength(original.getLength());
        inverseLink.setCapacity(original.getCapacity());
        inverseLink.setAllowedModes(original.getAllowedModes());
        inverseLink.setNumberOfLanes(original.getNumberOfLanes());
        inverseLink.setFreespeed(original.getFreespeed());

        network.addLink(inverseLink);
    }

}
