/**
 * Shi3ld - Shi3ld for HTTP is an access control module for enforcing 
 * authorization on triple stores. Shi3ld for HTTP protects HTTP operations 
 * on Linked Data and relies on attribute-based access policies.
 *
 * Copyright (C) 2013 Luca Costabello, Serena Villata,
 *  Oscar Rodriguez-Rocha, Fabien Gandon - v1.0
 * *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package fr.inria.shi3ld.services;

import java.util.ArrayList;
import java.util.List;

import fr.inria.edelweiss.kgram.api.core.Edge;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.utils.GraphHelper;

public class PoliciesSet {
	private List<String> apURIs;
	private Graph policiesGraph;
	
	public PoliciesSet(Graph policiesGraph) {
		this.setPoliciesGraph(policiesGraph);
		apURIs = new ArrayList<String>();
	}

	public List<String> getAPURIs() {
		return apURIs;
	}
	
	public void addPolicyURI(String policy){
		this.apURIs.add(policy);
	}

	public void setAPURIs(List<String> aPURIs) {
		apURIs = aPURIs;
	}

	public Graph getPoliciesGraph() {
		return policiesGraph;
	}

	public void setPoliciesGraph(Graph policiesGraph) {
		this.policiesGraph = policiesGraph;
	}

	public ACSType getAcstype(String policyURI){
		return ACSType.CONJUNCTIVE;
	}
	
	public ArrayList<Graph> getAccessConditions(String policyURI) throws Exception{
		ArrayList<Graph> acSet = new ArrayList<Graph>();
		Node n0 = this.policiesGraph.getResource(policyURI);
		String predicate = Config.s4acPrefixURI + "hasAccessConditionSet";
		GraphHelper gh = new GraphHelper(this.policiesGraph);
		
		if(gh.findN1byPredicate(n0, predicate)){
			Node acSetNode = gh.getN1();
			System.out.println(acSetNode.getLabel());
			//lGraph = gh.generateGraph(n1, lGraph);
			//System.out.println(lGraph);
			//return lGraph;
			Node p1 = this.policiesGraph.getResource(Config.s4acPrefixURI + "hasAccessCondition");
			for (Entity ent1 : this.policiesGraph.getEdges(p1, acSetNode, 0)){
				Edge e1 = ent1.getEdge();
				//System.out.println(e1.getNode(1).getLabel());
				Node acccessConditionNode = e1.getNode(1);
				Node p2 = this.policiesGraph.getResource(Config.s4acPrefixURI + "hasContext");
				Edge contextEdge = this.policiesGraph.getEdge(p2, acccessConditionNode, 0);
				//Edge acccessConditionEdge = contextEntity.getEdge();
				Node context = contextEdge.getNode(1);
				Graph lGraph = Graph.create();
				lGraph = this.generateGraph(context, lGraph);
				acSet.add(lGraph);
			}
		} else {
			throw new Exception("No AccessConditionSet found for policy " + policyURI);
		}
		return acSet;
	}
	
	public Graph generateGraph(Node n0, Graph lGraph){
	
		for (Entity r : this.policiesGraph.getEdges(n0, 0)){
			Edge tmpEdge = r.getEdge();
			lGraph.addEdgeWithNode(tmpEdge);
			
			if (policiesGraph.getEdges(tmpEdge.getNode(1), 0).iterator().hasNext()){
				lGraph = generateGraph(tmpEdge.getNode(1), lGraph);
			}
		}
		return lGraph;
	}

}
