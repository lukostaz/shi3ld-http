package fr.inria.shi3ld.services;

import java.util.ArrayList;
import java.util.HashMap;

import fr.inria.acacia.corese.api.EngineFactory;
import fr.inria.acacia.corese.api.IEngine;
import fr.inria.edelweiss.kgraph.core.Graph;

public class Store {
	public static EngineFactory ef;
	public static IEngine engine;
	
	// ResourceID, Permissions, ArrayList of PoliciesSet
	public static HashMap<String, HashMap<String, ArrayList<PoliciesSet>>> policiesStorage;
	public static HashMap<String, Graph> contextStorage;
}
