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

import fr.inria.edelweiss.kgram.api.core.Edge;
import fr.inria.edelweiss.kgram.api.core.Entity;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgraph.core.Graph;

public class GraphHelper {
	private Node n1;
	private Graph g;
	
	/**
	 * @param g
	 */
	public GraphHelper(Graph g) {
		this.g = g;
	}

	public boolean findN1byPredicate(Node n0, String predicate){
		for (Entity r : this.g.getEdges(n0, 0)){
			Edge tmpEdge = r.getEdge();
			if (tmpEdge.getLabel().equals(predicate)){
				this.setN1(tmpEdge.getNode(1));
				return true;
			}
			if (g.getEdges(tmpEdge.getNode(1), 0).iterator().hasNext())
				if(findN1byPredicate(tmpEdge.getNode(1), predicate))
					return true;
		}
		return false;
	}


	public Node getN1() {
		return n1;
	}

	public void setN1(Node n1) {
		this.n1 = n1;
	}
}
