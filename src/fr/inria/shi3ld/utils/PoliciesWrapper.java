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

package fr.inria.shi3ld.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import fr.inria.edelweiss.kgram.api.core.Edge;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;
import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.PoliciesSet;
import fr.inria.shi3ld.services.Store;

public class PoliciesWrapper {
	private Graph graph;
	
	public PoliciesWrapper(String policyString){
		this.graph = Graph.create();
		InputStream is = new ByteArrayInputStream(policyString.getBytes());
		//Load.create(this.graph);

		try {
			Load.create(this.graph).load(is, ".ttl");
		} catch (LoadException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPolicies(){
		Node n = this.graph.getResource(Config.contextsURI);
		Node p = this.graph.getResource(Config.s4acPrefixURI + "appliesTo");

		for (Entity ent : this.graph.getEdges(p, n, 0)){
			Edge e = ent.getEdge();
			String resourceURI = e.getNode(1).getLabel();
			String policyURI = e.getNode(0).getLabel();
			System.out.println("Found policy: " + policyURI + " Applies to: " + resourceURI);
			System.out.println("Loading permissions...");
			String permissions = this.getPermissions(policyURI);
			System.out.println("Permissions found: " + permissions);

			if(!Store.policiesStorage.containsKey(resourceURI)){
				System.out.println("Storage doesn't contain policies for: " + resourceURI);
				HashMap<String, ArrayList<PoliciesSet>> rHM = new HashMap<String, ArrayList<PoliciesSet>>();
				rHM.put(permissions, new ArrayList<PoliciesSet>());
				Store.policiesStorage.put(resourceURI, rHM);
				this.addNewPolicySet(resourceURI, permissions, policyURI);				
			} else {
				System.out.println("Storage already contains policies for: " + resourceURI);
				boolean graphFound = false;
				HashMap<String, ArrayList<PoliciesSet>> rHM = Store.policiesStorage.get(resourceURI);
				if(!rHM.containsKey(permissions)){
					rHM.put(permissions, new ArrayList<PoliciesSet>());
				}
				ArrayList<PoliciesSet> ps = rHM.get(permissions);
				for (PoliciesSet tmpPS : ps) {
					if (tmpPS.getPoliciesGraph() == this.graph) {
						System.out.println("Graph already present, just adding policy: " + policyURI);
						tmpPS.addPolicyURI(policyURI);
						graphFound = true;
						break;
					}
				}
				if(!graphFound)
					this.addNewPolicySet(resourceURI, permissions, policyURI);
			}	
		}
	}
	
	public void addNewPolicySet(String resourceURI, String permissions, String policyURI){
		System.out.println("Adding policy: " + policyURI + " to resource URI: " + resourceURI);
		PoliciesSet ps = new PoliciesSet(this.graph);
		ps.addPolicyURI(policyURI);
		Store.policiesStorage.get(resourceURI).get(permissions).add(ps);
	}
	
	public String getPermissions(String policyURI){
		Node n = this.graph.getResource(policyURI);
		Node p = this.graph.getResource(Config.s4acPrefixURI + "hasAccessPrivilege");
		Entity ent = (Entity) this.graph.getEdge(p, n, 0);
		Edge e = ent.getEdge();
		String resourceURI = e.getNode(1).getLabel();
		System.out.println("Found permissions: " + resourceURI + " for policy: " + policyURI);
		return resourceURI;
	}
	
}
