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
