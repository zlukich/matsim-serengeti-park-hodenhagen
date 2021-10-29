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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NetworkAddEickelohOpen {

	// last nodeId in original serengeti-park-network-v1.0.xml: 914822754
	// last linkId in original serengeti-park-network-v1.0.xml: 924490180004f/r

	private static int nodesIndex = 914822755;
	private static long linksIndex = 924490180005L;

	private static final int lastNodeOfAccessRoad = 286589707;
	private static final int lastNodeOfNorthernAccessRoadToEickeloh = 1566972959;

	private static final String INPUT_NETWORK = "./scenarios/input/serengeti-park-network-v1.0.xml.gz";
	private static final String OUTPUT_NETWORK = "./scenarios/input/serengeti-park-network-EickelohOpen.xml.gz";
	

	public static void main(String[] args) {

		Network network = NetworkUtils.readNetwork(INPUT_NETWORK);
		NetworkFactory factory = network.getFactory();

		Set<String> modes = new HashSet<>();
		modes.add(TransportMode.car);
		
		//ADD EICKELOH PARKING LOT

		//NODES CREATION:
		
		//access and exit
		createNode(network,542492.6276,5844926.2603);
		createNode(network,542502.242,5844918.065);
		createNode(network,542533.766,5844867.479);
		createNode(network,542558.3866,5844851.1593);
		//west side
		createNode(network, 542549.859,5844837.991);
		createNode(network, 542541.4577,5844825.2919);
		createNode(network, 542533.161,5844812.190);
		createNode(network, 542524.5540,5844799.4178);
		createNode(network, 542516.2197,5844786.6875);
		createNode(network, 542507.8476,5844773.8565);
		createNode(network, 542499.517,5844761.067);
		createNode(network, 542491.137,5844748.203);
		createNode(network, 542482.6894,5844735.3718);
		createNode(network, 542474.2249,5844722.4232);
		createNode(network, 542465.7269,5844709.5166);
		createNode(network, 542457.5731,5844696.9543);
		createNode(network, 542449.1507,5844684.0813);
		createNode(network, 542440.7786,5844671.2754);
		createNode(network, 542432.222,5844658.293);

		createNode(network, 542415.326,5844632.362);
		createNode(network, 542407.013,5844619.447);
		createNode(network, 542398.515,5844606.566);
		createNode(network, 542390.235,5844593.886);
		createNode(network, 542381.586,5844580.778);
		createNode(network, 542373.189,5844567.897);
		createNode(network, 542364.926,5844555.166);
		createNode(network, 542356.5371,5844542.4193);
		createNode(network, 542348.0727,5844529.4791);
		createNode(network, 542339.7090,5844516.7824);
		createNode(network, 542331.0094,5844503.3888);
		createNode(network, 542322.7297,5844490.7173);
		createNode(network, 542314.467,5844478.105);
		createNode(network, 542305.952,5844465.055);
		createNode(network, 542297.7227,5844452.4845);
		createNode(network, 542289.418,5844439.704);
		createNode(network, 542280.853,5844426.604);
		createNode(network, 542272.321,5844413.672);
		createNode(network, 542263.8481,5844400.7154);
		createNode(network, 542255.535,5844387.951);
		createNode(network, 542250.1731,5844379.7810);

		//south side
		createNode(network,542251.5839,5844371.8413);
		createNode(network,542302.1690,5844337.8261);
		createNode(network,542374.1987,5844289.4074);

		//east side
		createNode(network,542382.8143,5844302.5366);
		createNode(network,542391.321,5844315.368);
		createNode(network,542399.819,5844328.333);
		createNode(network,542408.031,5844340.895);
		createNode(network,542416.613,5844353.961);
		createNode(network,542425.078,5844366.893);
		createNode(network,542433.3491,5844379.5563);
		createNode(network,542441.8471,5844392.5301);
		createNode(network,542450.3872,5844405.5291);
		createNode(network,542458.6081,5844418.0662);
		createNode(network,542467.1397,5844431.0736);
		createNode(network,542475.5874,5844443.9802);
		createNode(network,542483.859,5844456.568);
		createNode(network,542492.474,5844469.667);
		createNode(network,542500.922,5844482.482);
		createNode(network,542509.084,5844494.943);
		createNode(network,542517.6493,5844508.0430);
		createNode(network,542526.005,5844520.832);

		createNode(network,542543.219,5844546.897);
		createNode(network,542551.331,5844559.342);
		createNode(network,542559.846,5844572.391);
		createNode(network,542568.3604,5844585.4154);
		createNode(network,542576.7325,5844598.1373);
		createNode(network,542585.1382,5844610.9515);
		createNode(network,542593.5690,5844623.8162);
		createNode(network,542602.0167,5844636.6640);
		createNode(network,542610.3888,5844649.4614);
		createNode(network,542619.0212,5844662.6032);
		createNode(network,542627.125,5844675.014);
		createNode(network,542635.606,5844687.913);
		createNode(network,542644.205,5844700.928);
		createNode(network,542652.5515,5844713.7173);
		createNode(network,542661.1251,5844726.7835);
		createNode(network,542669.2043,5844739.1422);
		createNode(network,542677.8136,5844752.2650);
		createNode(network,542686.336179,5844765.265308);

		// extension south side
		createNode(network,542293.4642,5844325.3775);
		createNode(network,542284.8990,5844312.1182);
		createNode(network,542276.443,5844299.170);
		createNode(network,542268.2094,5844286.4183);

		//remaining end points
		//above the river
		createNode(network,542481.252,5844606.325);
		createNode(network,542520.249,5844562.491);
		//below the river
		createNode(network,542450.384,5844608.374);
		createNode(network,542495.057,5844559.871);
		//extension south side west
		createNode(network,542259.682,5844347.504);
		createNode(network,542251.234,5844334.707);
		createNode(network,542242.921,5844321.641);
		createNode(network,542234.448,5844308.818);
		//extension south side east
		createNode(network,542367.6709,5844274.8679);
		createNode(network,542359.2736,5844262.1544);
		createNode(network,542350.8008,5844249.2478);
		createNode(network,542342.303,5844236.047);

		//LINKS CREATION: last linkId in original serengeti-park-network-v1.0.xml: 924490180004f/r

		NetworkAddEickelohOpen l = new NetworkAddEickelohOpen();

		//access and exit
		/*createBidirectionalSimpleLink(network, factory,modes,lastNodeOfNorthernAccessRoadToEickeloh,914822755, 59.347);
		createBidirectionalSimpleLink(network, factory,modes, 914822755, 914822756, 12.866);
		createBidirectionalSimpleLink(network, factory,modes,914822756, 914822757, 59.599);
		createBidirectionalSimpleLink(network, factory,modes,914822757, 914822758, 29.808);*/

		/*createBidirectionalLink(network, factory,modes,lastNodeOfNorthernAccessRoadToEickeloh,914822755, 59.347, 3.);
		createBidirectionalLink(network, factory,modes, 914822755, 914822756, 12.866, 3.);
		createBidirectionalLink(network, factory,modes,914822756, 914822757, 59.599, 3.);
		createBidirectionalLink(network, factory,modes,914822757, 914822758, 29.808, 3.);*/

		

		createBidirectionalLink2(network, factory, modes, lastNodeOfNorthernAccessRoadToEickeloh,914822755, 59.347, 2., 2.);
		createBidirectionalLink2(network, factory,modes, 914822755, 914822756, 12.866, 2., 2.);
		createBidirectionalLink2(network, factory,modes,914822756, 914822757, 59.599, 2., 2.);
		createBidirectionalLink2(network, factory,modes,914822757, 914822758, 29.808, 2., 2.);
		


		//west side
	/*	createBidirectionalSimpleLink(network, factory,modes,914822758, 914822759, 15.564);
		createBidirectionalSimpleLink(network, factory,modes,914822759, 914822760, 15.174);
		createBidirectionalSimpleLink(network, factory,modes,914822760, 914822761, 15.798);
		createBidirectionalSimpleLink(network, factory,modes, 914822761, 914822762, 15.395);
		createBidirectionalSimpleLink(network, factory,modes,914822762, 914822763, 15.508);
		createBidirectionalSimpleLink(network, factory,modes, 914822763, 914822764, 15.026);
		createBidirectionalSimpleLink(network, factory,modes, 914822764, 914822765, 15.641);
		createBidirectionalSimpleLink(network, factory,modes, 914822765, 914822766, 15.564);
		createBidirectionalSimpleLink(network, factory,modes,914822766, 914822767, 15.118);
		createBidirectionalSimpleLink(network, factory,modes, 914822767, 914822768, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822768, 914822769, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822769, 914822770, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822770, 914822771, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822771, 914822772, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822772, 914822773, 15.452);

		createBidirectionalSimpleLink(network, factory,modes, 914822773, 914822774, 31.033);
		createBidirectionalSimpleLink(network, factory,modes, 914822774, 914822775, 15.380);
		createBidirectionalSimpleLink(network, factory,modes, 914822775, 914822776, 15.692);
		createBidirectionalSimpleLink(network, factory,modes, 914822776, 914822777, 15.009);
		createBidirectionalSimpleLink(network, factory,modes, 914822777, 914822778, 15.598);
		createBidirectionalSimpleLink(network, factory,modes, 914822778, 914822779, 15.508);
		createBidirectionalSimpleLink(network, factory,modes, 914822779, 914822780, 15.493);
		createBidirectionalSimpleLink(network, factory,modes, 914822780, 914822781, 15.082);
		createBidirectionalSimpleLink(network, factory,modes, 914822781, 914822782, 15.636);
		createBidirectionalSimpleLink(network, factory,modes, 914822782, 914822783, 15.082);
		createBidirectionalSimpleLink(network, factory,modes, 914822783, 914822784, 16.136);
		createBidirectionalSimpleLink(network, factory,modes, 914822784, 914822785, 15.514);
		createBidirectionalSimpleLink(network, factory,modes, 914822785, 914822786, 15.008);
		createBidirectionalSimpleLink(network, factory,modes, 914822786, 914822787, 15.525);
		createBidirectionalSimpleLink(network, factory,modes, 914822787, 914822788, 15.084);
		createBidirectionalSimpleLink(network, factory,modes, 914822788, 914822789, 15.564);
		createBidirectionalSimpleLink(network, factory,modes, 914822789, 914822790, 15.654);
		createBidirectionalSimpleLink(network, factory,modes, 914822790, 914822791, 15.654);
		createBidirectionalSimpleLink(network, factory,modes, 914822791, 914822792, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822792, 914822793, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822793, 914822794, 9.636094);*/



		createBidirectionalLink(network, factory,modes,914822758, 914822759, 15.564, 2);
		createBidirectionalLink(network, factory,modes,914822759, 914822760, 15.174, 2.);
		createBidirectionalLink(network, factory,modes,914822760, 914822761, 15.798, 2.);
		createBidirectionalLink(network, factory,modes, 914822761, 914822762, 15.395, 2.);
		createBidirectionalLink(network, factory,modes,914822762, 914822763, 15.508, 2.);
		createBidirectionalLink(network, factory,modes, 914822763, 914822764, 15.026, 2.);
		createBidirectionalLink(network, factory,modes, 914822764, 914822765, 15.641, 2.);
		createBidirectionalLink(network, factory,modes, 914822765, 914822766, 15.564, 2.);
		createBidirectionalLink(network, factory,modes,914822766, 914822767, 15.118, 2.);
		createBidirectionalLink(network, factory,modes, 914822767, 914822768, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822768, 914822769, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822769, 914822770, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822770, 914822771, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822771, 914822772, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822772, 914822773, 15.452, 2.);

		createBidirectionalLink(network, factory,modes, 914822773, 914822774, 31.033, 2.);
		createBidirectionalLink(network, factory,modes, 914822774, 914822775, 15.380, 2.);
		createBidirectionalLink(network, factory,modes, 914822775, 914822776, 15.692, 2.);
		createBidirectionalLink(network, factory,modes, 914822776, 914822777, 15.009, 2.);
		createBidirectionalLink(network, factory,modes, 914822777, 914822778, 15.598, 2.);
		createBidirectionalLink(network, factory,modes, 914822778, 914822779, 15.508, 2.);
		createBidirectionalLink(network, factory,modes, 914822779, 914822780, 15.493, 2.);
		createBidirectionalLink(network, factory,modes, 914822780, 914822781, 15.082, 2.);
		createBidirectionalLink(network, factory,modes, 914822781, 914822782, 15.636, 2.);
		createBidirectionalLink(network, factory,modes, 914822782, 914822783, 15.082, 2.);
		createBidirectionalLink(network, factory,modes, 914822783, 914822784, 16.136, 2.);
		createBidirectionalLink(network, factory,modes, 914822784, 914822785, 15.514, 2.);
		createBidirectionalLink(network, factory,modes, 914822785, 914822786, 15.008, 2.);
		createBidirectionalLink(network, factory,modes, 914822786, 914822787, 15.525, 2.);
		createBidirectionalLink(network, factory,modes, 914822787, 914822788, 15.084, 2.);
		createBidirectionalLink(network, factory,modes, 914822788, 914822789, 15.564, 2.);
		createBidirectionalLink(network, factory,modes, 914822789, 914822790, 15.654, 2.);
		createBidirectionalLink(network, factory,modes, 914822790, 914822791, 15.654, 2.);
		createBidirectionalLink(network, factory,modes, 914822791, 914822792, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822792, 914822793, 15.452, 2.);
		createBidirectionalLink(network, factory,modes, 914822793, 914822794, 9.636094, 2.);
		
		

		//south side
		/*createBidirectionalSimpleLink(network, factory,modes, 914822794, 914822795, 8.097033);
		createBidirectionalSimpleLink(network, factory,modes, 914822795, 914822796, 60.954);
		createBidirectionalSimpleLink(network, factory,modes, 914822796, 914822797, 86.863);*/

		createBidirectionalLink(network, factory,modes, 914822794, 914822795, 8.097033, 1.);
		createBidirectionalLink(network, factory,modes, 914822795, 914822796, 60.954, 1.);
		createBidirectionalLink(network, factory,modes, 914822796, 914822797, 86.863, 1.);

		//east side
		/*createBidirectionalSimpleLink(network, factory,modes, 914822797, 914822798, 15.654);
		createBidirectionalSimpleLink(network, factory,modes, 914822798, 914822799, 15.617);
		createBidirectionalSimpleLink(network, factory,modes, 914822799, 914822800, 15.489);
		createBidirectionalSimpleLink(network, factory,modes, 914822800, 914822801, 14.953);
		createBidirectionalSimpleLink(network, factory,modes, 914822801, 914822802, 15.635);
		createBidirectionalSimpleLink(network, factory,modes, 914822802, 914822803, 15.508);
		createBidirectionalSimpleLink(network, factory,modes, 914822803, 914822804, 15.137);
		createBidirectionalSimpleLink(network, factory,modes, 914822804, 914822805, 15.581);
		createBidirectionalSimpleLink(network, factory,modes, 914822805, 914822806, 15.600);
		createBidirectionalSimpleLink(network, factory,modes, 914822806, 914822807, 14.861);
		createBidirectionalSimpleLink(network, factory,modes, 914822807, 914822808, 15.617);
		createBidirectionalSimpleLink(network, factory,modes, 914822808, 914822809, 15.581);
		createBidirectionalSimpleLink(network, factory,modes, 914822809, 914822810, 14.989);
		createBidirectionalSimpleLink(network, factory,modes, 914822810, 914822811, 15.581);
		createBidirectionalSimpleLink(network, factory,modes, 914822811, 914822812, 15.544);
		createBidirectionalSimpleLink(network, factory,modes, 914822812, 914822813, 14.861);
		createBidirectionalSimpleLink(network, factory,modes, 914822813, 914822814, 14.861);
		createBidirectionalSimpleLink(network, factory,modes, 914822814, 914822815, 15.472);

		createBidirectionalSimpleLink(network, factory,modes, 914822815, 914822816, 31.179);
		createBidirectionalSimpleLink(network, factory,modes, 914822816, 914822817, 14.901);
		createBidirectionalSimpleLink(network, factory,modes, 914822817, 914822818, 15.728);
		createBidirectionalSimpleLink(network, factory,modes, 914822818, 914822819, 15.581);
		createBidirectionalSimpleLink(network, factory,modes, 914822819, 914822820, 15.063);
		createBidirectionalSimpleLink(network, factory,modes, 914822820, 914822821, 15.472);
		createBidirectionalSimpleLink(network, factory,modes, 914822821, 914822822, 15.544);
		createBidirectionalSimpleLink(network, factory,modes, 914822822, 914822823, 15.045);
		createBidirectionalSimpleLink(network, factory,modes, 914822823, 914822824, 15.452);
		createBidirectionalSimpleLink(network, factory,modes, 914822824, 914822825, 15.562);
		createBidirectionalSimpleLink(network, factory,modes, 914822825, 914822826, 14.917);
		createBidirectionalSimpleLink(network, factory,modes, 914822826, 914822827, 15.489);
		createBidirectionalSimpleLink(network, factory,modes, 914822827, 914822828, 15.654);
		createBidirectionalSimpleLink(network, factory,modes, 914822828, 914822829, 14.971);
		createBidirectionalSimpleLink(network, factory,modes, 914822829, 914822830, 16.029);
		createBidirectionalSimpleLink(network, factory,modes, 914822830, 914822831, 14.602);
		createBidirectionalSimpleLink(network, factory,modes, 914822831, 914822832, 16.065);
		createBidirectionalSimpleLink(network, factory,modes, 914822832, 914822833, 15.617);*/


		createBidirectionalLink(network, factory,modes, 914822797, 914822798, 15.654, 1.);
		createBidirectionalLink(network, factory,modes, 914822798, 914822799, 15.617, 1.);
		createBidirectionalLink(network, factory,modes, 914822799, 914822800, 15.489, 1.);
		createBidirectionalLink(network, factory,modes, 914822800, 914822801, 14.953, 1.);
		createBidirectionalLink(network, factory,modes, 914822801, 914822802, 15.635, 1.);
		createBidirectionalLink(network, factory,modes, 914822802, 914822803, 15.508, 1.);
		createBidirectionalLink(network, factory,modes, 914822803, 914822804, 15.137, 1.);
		createBidirectionalLink(network, factory,modes, 914822804, 914822805, 15.581, 1.);
		createBidirectionalLink(network, factory,modes, 914822805, 914822806, 15.600, 1.);
		createBidirectionalLink(network, factory,modes, 914822806, 914822807, 14.861, 1.);
		createBidirectionalLink(network, factory,modes, 914822807, 914822808, 15.617, 1.);
		createBidirectionalLink(network, factory,modes, 914822808, 914822809, 15.581, 1.);
		createBidirectionalLink(network, factory,modes, 914822809, 914822810, 14.989, 1.);
		createBidirectionalLink(network, factory,modes, 914822810, 914822811, 15.581, 1.);
		createBidirectionalLink(network, factory,modes, 914822811, 914822812, 15.544, 1.);
		createBidirectionalLink(network, factory,modes, 914822812, 914822813, 14.861, 1.);
		createBidirectionalLink(network, factory,modes, 914822813, 914822814, 14.861, 1.);
		createBidirectionalLink(network, factory,modes, 914822814, 914822815, 15.472, 1.);

		createBidirectionalLink(network, factory,modes, 914822815, 914822816, 31.179, 1.);
		createBidirectionalLink(network, factory,modes, 914822816, 914822817, 14.901, 1.);
		createBidirectionalLink(network, factory,modes, 914822817, 914822818, 15.728, 1.);
		createBidirectionalLink(network, factory,modes, 914822818, 914822819, 15.581, 1.);
		createBidirectionalLink(network, factory,modes, 914822819, 914822820, 15.063, 1.);
		createBidirectionalLink(network, factory,modes, 914822820, 914822821, 15.472, 1.);
		createBidirectionalLink(network, factory,modes, 914822821, 914822822, 15.544, 1.);
		createBidirectionalLink(network, factory,modes, 914822822, 914822823, 15.045, 1.);
		createBidirectionalLink(network, factory,modes, 914822823, 914822824, 15.452, 1.);
		createBidirectionalLink(network, factory,modes, 914822824, 914822825, 15.562, 1.);
		createBidirectionalLink(network, factory,modes, 914822825, 914822826, 14.917, 1.);
		createBidirectionalLink(network, factory,modes, 914822826, 914822827, 15.489, 1.);
		createBidirectionalLink(network, factory,modes, 914822827, 914822828, 15.654, 1.);
		createBidirectionalLink(network, factory,modes, 914822828, 914822829, 14.971, 1.);
		createBidirectionalLink(network, factory,modes, 914822829, 914822830, 16.029, 1.);
		createBidirectionalLink(network, factory,modes, 914822830, 914822831, 14.602, 1.);
		createBidirectionalLink(network, factory,modes, 914822831, 914822832, 16.065, 1.);
		createBidirectionalLink(network, factory,modes, 914822832, 914822833, 15.617, 1.);


		//north side link
		/*l.createUniDirectionalSimpleLink(network, factory,modes, 914822833, 914822758, 153.843);*/
		createUniDirectionalLink(network, factory,modes, 914822833, 914822758, 153.843, 1.);


		// extension south side
		createBidirectionalSimpleLink(network, factory,modes, 914822796, 914822834, 15.289);
		createBidirectionalSimpleLink(network, factory,modes, 914822834, 914822835, 15.973);
		createBidirectionalSimpleLink(network, factory,modes, 914822835, 914822836, 15.544);
		createBidirectionalSimpleLink(network, factory,modes, 914822836, 914822837, 15.009);

		//remaining
		//above the river
		createBidirectionalSimpleLink(network, factory,modes, 914822759, 914822832,  154.088);
		createBidirectionalSimpleLink(network, factory,modes, 914822760, 914822831,  154.134);
		createBidirectionalSimpleLink(network, factory,modes, 914822761, 914822830,  153.865);
		createBidirectionalSimpleLink(network, factory,modes, 914822762, 914822829,  154.266);
		createBidirectionalSimpleLink(network, factory,modes, 914822763, 914822828,  154.067);
		createBidirectionalSimpleLink(network, factory,modes, 914822764, 914822827,  154.067);
		createBidirectionalSimpleLink(network, factory,modes, 914822765, 914822826, 153.932);
		createBidirectionalSimpleLink(network, factory,modes, 914822766, 914822825,  153.865);
		createBidirectionalSimpleLink(network, factory,modes, 914822767, 914822824,  154.201);
		createBidirectionalSimpleLink(network, factory,modes, 914822768, 914822823,  153.667);
		createBidirectionalSimpleLink(network, factory,modes, 914822769, 914822822,  153.798);
		createBidirectionalSimpleLink(network, factory,modes, 914822770, 914822821,  154.001);
		createBidirectionalSimpleLink(network, factory,modes, 914822771, 914822820,  154.067);
		createBidirectionalSimpleLink(network, factory,modes, 914822772, 914822819,  154.072);
		createBidirectionalSimpleLink(network, factory,modes, 914822773, 914822818,  154.201);

		createBidirectionalSimpleLink(network, factory,modes, 914822817, 914822838, 84.323);
		createBidirectionalSimpleLink(network, factory,modes, 914822816, 914822839, 27.841);

		//below the river
		createBidirectionalSimpleLink(network, factory,modes, 914822774, 914822840, 42.627);
		createBidirectionalSimpleLink(network, factory,modes, 914822775, 914822841, 106.230);

		createBidirectionalSimpleLink(network, factory,modes, 914822776, 914822815, 153.364);
		createBidirectionalSimpleLink(network, factory,modes, 914822777, 914822814, 153.802);
		createBidirectionalSimpleLink(network, factory,modes, 914822778, 914822813, 153.603);
		createBidirectionalSimpleLink(network, factory,modes, 914822779, 914822812, 154.009);
		createBidirectionalSimpleLink(network, factory,modes, 914822780, 914822811, 153.802);
		createBidirectionalSimpleLink(network, factory,modes, 914822781, 914822810, 153.845);
		createBidirectionalSimpleLink(network, factory,modes, 914822782, 914822809, 153.718);
		createBidirectionalSimpleLink(network, factory,modes, 914822783, 914822808, 153.826);
		createBidirectionalSimpleLink(network, factory,modes, 914822784, 914822807, 153.718);
		createBidirectionalSimpleLink(network, factory,modes, 914822785, 914822806, 153.552);
		createBidirectionalSimpleLink(network, factory,modes, 914822786, 914822805, 153.700);
		createBidirectionalSimpleLink(network, factory,modes, 914822787, 914822804, 153.761);
		createBidirectionalSimpleLink(network, factory,modes, 914822788, 914822803, 153.553);
		createBidirectionalSimpleLink(network, factory,modes, 914822789, 914822802, 153.761);
		createBidirectionalSimpleLink(network, factory,modes, 914822790, 914822801, 153.388);
		createBidirectionalSimpleLink(network, factory,modes, 914822791, 914822800, 153.573);
		createBidirectionalSimpleLink(network, factory,modes, 914822792, 914822799, 153.344);
		createBidirectionalSimpleLink(network, factory,modes, 914822793, 914822798, 153.553);

		//extension south side west
		createBidirectionalSimpleLink(network, factory,modes, 914822834, 914822842, 40.401);
		createBidirectionalSimpleLink(network, factory,modes, 914822835, 914822843, 40.510);
		createBidirectionalSimpleLink(network, factory,modes, 914822836, 914822844, 40.447);
		createBidirectionalSimpleLink(network, factory,modes, 914822837, 914822845, 40.526);

		//extension south side east
		createBidirectionalSimpleLink(network, factory,modes, 914822834, 914822846, 89.650);
		createBidirectionalSimpleLink(network, factory,modes, 914822835, 914822847, 89.613);
		createBidirectionalSimpleLink(network, factory,modes, 914822836, 914822848, 89.826);
		createBidirectionalSimpleLink(network, factory,modes, 914822837, 914822849, 89.795);
		
		
		
		//ADAPT ENTRANCE PROCESS
		
		// nothern entrance road to eickeloh

		// create new nodes:

		createNode(network, 541931.300,5845155.075 );
		createNode(network, 542096.706,5845131.317);
		createNode(network, 542213.65,5845062.53);
		createNode(network, 542234.725,5845058.709);
		createNode(network, 542255.839,5845046.740);
		createNode(network, 542363.360,5845027.872);
		createNode(network, 542428.3132,5845000.9822);
		createNode(network, 542451.9028,5844976.2991);

		// create new links:
		createUniDirectionalLink(network, factory, modes, lastNodeOfAccessRoad, 914822850, 47.910, 2.);
		createUniDirectionalLink(network, factory, modes, 914822850, 914822851, 167.212, 2.);
		createUniDirectionalLink(network, factory, modes, 914822851, 914822852, 135.747, 2.);
		createUniDirectionalLink(network, factory, modes, 914822852, 914822853, 21.557, 2.);
		createUniDirectionalLink(network, factory, modes, 914822853, 914822854, 24.171, 2.);
		createUniDirectionalLink(network, factory, modes, 914822854, 914822855, 109.036, 2.);
		createUniDirectionalLink(network, factory, modes, 914822855, 914822856, 70.361, 2.);
		createUniDirectionalLink(network, factory, modes, 914822856, 914822857, 33.952, 2.);
		createUniDirectionalLink(network, factory, modes, 914822857, lastNodeOfNorthernAccessRoadToEickeloh, 51.992, 2.);


		// DO NOT INCREASE IT ANYMORE!!!!!!!!


		// allow access to the safari by turning left
		createSimpleInverseLink(network,factory,"7232641190002f", "7232641190002r");
		createSimpleInverseLink(network,factory,"7232641190001f", "7232641190001r");



		// increase capacity on southern (exit route) links from 1 to 2 lanes:
		Set<Id<Link>> toIncreaseCapacityLinks = new HashSet<>(Arrays.asList(


				Id.createLinkId("7232641190002r"),

				Id.createLinkId("7232641190001r"),
				Id.createLinkId("7232641190002f"),

				Id.createLinkId("246929390061f"),
				Id.createLinkId("246929390060f"),
				Id.createLinkId("246929390059f"),
				Id.createLinkId("246929390058f"),
				Id.createLinkId("246929390057f"),
				Id.createLinkId("246929390056f"),
				Id.createLinkId("246929390055f"),
				Id.createLinkId("246929390054f"),
				Id.createLinkId("246929390053f"),
				Id.createLinkId("246929390052f"),
				Id.createLinkId("246929390051f"),
				Id.createLinkId("246929390050f"),
				Id.createLinkId("246929390049f"),
				Id.createLinkId("246929390048f"),
				Id.createLinkId("246929390047f"),
				Id.createLinkId("246929390046f")

		));

		for (Link link : network.getLinks().values()) {

			if (toIncreaseCapacityLinks.contains(link.getId())) {
				link.setCapacity(1440.0);
				link.setNumberOfLanes(2.);
			}
		}



		// restrict links which do not belong to the intended "to-the-safari"-route + RESTRICT SHORT CUT LINK FROM SAFARI TO EICKELOH PARKING LOT
		Set<Id<Link>> restrictedLinks = new HashSet<>(Arrays.asList(

				//serengetiparkplatz westseite
				Id.createLinkId("2226309730000r"),
				Id.createLinkId("394368880003r"),


				// ex bus lane (ultimate check-in)
				Id.createLinkId("3622817520001f"),

				// straight link behind access road
				Id.createLinkId("1325764790000f"),


				// check-in north
				Id.createLinkId("3624560720000f"),
				Id.createLinkId("3624560720001f"),
				Id.createLinkId("3624560720002f"),
				Id.createLinkId("3624560720003f"),

				//wasserlandparkplatz suedseite
				Id.createLinkId("1432053600000f"),
				Id.createLinkId("261715690000f"),
				Id.createLinkId("3551886970000r"),
				Id.createLinkId("394590950000r"),
				Id.createLinkId("1432053530019r"),

				//serengetiparkplatz nordseite
				Id.createLinkId("7259809370008r"),
				Id.createLinkId("7259809390010r"),
				Id.createLinkId("774722210000r"),

				//left turn behind main check-in booth
				Id.createLinkId("5297562640008f"),

				//shortcut safari eickeloh
				Id.createLinkId("4429169870000r")



		));

		for (Link link : network.getLinks().values()) {

			if (restrictedLinks.contains(link.getId())) {
				link.setFreespeed(0.001);
				link.setCapacity(0.);
			}
		}
		
		

		//dump out network
		NetworkUtils.writeNetwork(network, OUTPUT_NETWORK);

	}

	//useful funcions

	private static void createNode(Network network, double x, double y) {
		Node nodeN = NetworkUtils.createAndAddNode(network, Id.createNodeId(nodesIndex), new Coord(x, y));
		nodesIndex++;
	}

	private void createUniDirectionalSimpleLink(Network network, NetworkFactory factory, Set<String> modes, long idPt1, long idPt2, double distance){
		Node pt1 = network.getNodes().get(Id.createNodeId(idPt1));
		Node pt2 = network.getNodes().get(Id.createNodeId(idPt2));

		Link forwardLink = factory.createLink(Id.createLinkId(linksIndex+"f"), pt1, pt2);
		forwardLink.setLength(distance);
		forwardLink.setFreespeed(10.0/3.6);
		forwardLink.setCapacity(720);
		forwardLink.setNumberOfLanes(1);
		forwardLink.setAllowedModes(modes);
		network.addLink(forwardLink);

		linksIndex++;
	}

	private static void createBidirectionalSimpleLink(Network network, NetworkFactory factory, Set<String> modes, long idPt1, long idPt2, double distance){
		Node pt1 = network.getNodes().get(Id.createNodeId(idPt1));
		Node pt2 = network.getNodes().get(Id.createNodeId(idPt2));

		Link forwardLink = factory.createLink(Id.createLinkId(linksIndex+"f"), pt1, pt2);
		forwardLink.setLength(distance);
		forwardLink.setFreespeed(10.0/3.6);
		forwardLink.setCapacity(720);
		forwardLink.setNumberOfLanes(1);
		forwardLink.setAllowedModes(modes);
		network.addLink(forwardLink);

		Link reverseLink = factory.createLink(Id.createLinkId(linksIndex+"r"), pt2, pt1);
		reverseLink.setLength(distance);
		reverseLink.setFreespeed(10.0/3.6);
		reverseLink.setCapacity(720);
		reverseLink.setNumberOfLanes(1);
		reverseLink.setAllowedModes(modes);
		network.addLink(reverseLink);

		linksIndex++;
	}


	private static void createUniDirectionalLink(Network network, NetworkFactory factory, Set<String> modes, long idPt1, long idPt2, double distance, double lanes){
		Node pt1 = network.getNodes().get(Id.createNodeId(idPt1));
		Node pt2 = network.getNodes().get(Id.createNodeId(idPt2));

		Link forwardLink = factory.createLink(Id.createLinkId(linksIndex+"f"), pt1, pt2);
		forwardLink.setLength(distance);
		forwardLink.setFreespeed(10.0/3.6);
		forwardLink.setCapacity(720 * lanes);
		forwardLink.setNumberOfLanes(lanes);
		forwardLink.setAllowedModes(modes);
		network.addLink(forwardLink);

		linksIndex++;

	}

	private static void createBidirectionalLink(Network network, NetworkFactory factory, Set<String> modes, long idPt1, long idPt2, double distance, double lanes){
		Node pt1 = network.getNodes().get(Id.createNodeId(idPt1));
		Node pt2 = network.getNodes().get(Id.createNodeId(idPt2));

		Link forwardLink = factory.createLink(Id.createLinkId(linksIndex+"f"), pt1, pt2);
		forwardLink.setLength(distance);
		forwardLink.setFreespeed(10.0/3.6);
		forwardLink.setCapacity(720 * lanes);
		forwardLink.setNumberOfLanes(lanes);
		forwardLink.setAllowedModes(modes);
		network.addLink(forwardLink);


		Link reverseLink = factory.createLink(Id.createLinkId(linksIndex+"r"), pt2, pt1);
		reverseLink.setLength(distance);
		reverseLink.setFreespeed(10.0/3.6);
		reverseLink.setCapacity(720 * lanes);
		reverseLink.setNumberOfLanes(lanes);
		reverseLink.setAllowedModes(modes);
		network.addLink(reverseLink);

		linksIndex++;
	}


	private static void createBidirectionalLink2(Network network, NetworkFactory factory, Set<String> modes, long idPt1, long idPt2, double distance, double lanesf, double lanesr){
		Node pt1 = network.getNodes().get(Id.createNodeId(idPt1));
		Node pt2 = network.getNodes().get(Id.createNodeId(idPt2));

		Link forwardLink = factory.createLink(Id.createLinkId(linksIndex+"f"), pt1, pt2);
		forwardLink.setLength(distance);
		forwardLink.setFreespeed(10.0/3.6);
		forwardLink.setCapacity(720 * lanesf);
		forwardLink.setNumberOfLanes(lanesf);
		forwardLink.setAllowedModes(modes);
		network.addLink(forwardLink);


		Link reverseLink = factory.createLink(Id.createLinkId(linksIndex+"r"), pt2, pt1);
		reverseLink.setLength(distance);
		reverseLink.setFreespeed(10.0/3.6);
		reverseLink.setCapacity(720 * lanesr);
		reverseLink.setNumberOfLanes(lanesr);
		reverseLink.setAllowedModes(modes);
		network.addLink(reverseLink);

		linksIndex++;
	}





	private static void createSimpleInverseLink(Network network, NetworkFactory factory, String idOriginal, String idInverse){

		Link original = network.getLinks().get(Id.createLinkId(idOriginal));

		Link inverseLink = factory.createLink(Id.createLinkId(idInverse), original.getToNode(), original.getFromNode());
		inverseLink.setLength(original.getLength());
		inverseLink.setCapacity(original.getCapacity());
		inverseLink.setAllowedModes(original.getAllowedModes());
		inverseLink.setNumberOfLanes(original.getNumberOfLanes());
		inverseLink.setFreespeed(original.getFreespeed());

		network.addLink(inverseLink);
	}

	private static void createInverseLink(Network network, NetworkFactory factory, String idOriginal, String idInverse, double lanes){

		Link original = network.getLinks().get(Id.createLinkId(idOriginal));

		Link inverseLink = factory.createLink(Id.createLinkId(idInverse), original.getToNode(), original.getFromNode());
		inverseLink.setLength(original.getLength());
		inverseLink.setCapacity(720 * lanes);
		inverseLink.setAllowedModes(original.getAllowedModes());
		inverseLink.setNumberOfLanes(lanes);
		inverseLink.setFreespeed(original.getFreespeed());

		network.addLink(inverseLink);
	}
}
