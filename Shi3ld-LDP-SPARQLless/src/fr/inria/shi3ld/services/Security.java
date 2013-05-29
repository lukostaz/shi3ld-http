package fr.inria.shi3ld.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//import javax.ws.rs.WebApplicationException;

//import com.hp.hpl.jena.query.*;
//import com.hp.hpl.jena.update.*;

import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.load.LoadException;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.utils.CRUDType;
import fr.inria.shi3ld.utils.TurtleHelper;
//import fr.inria.shi3ld.utils.FileWrapper;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import fr.inria.shield.utils.GSPWrapper;

/**
 * This class handles the security checks to be performed before doing operations with semantic resources
 * 
 * @author oscar
 */
public class Security {
	private ArrayList<PoliciesSet> localStore;
	
	public void loadPoliciesFromStore(String resourceName, CRUDType cType) throws Exception {
		resourceName = Config.resourceExternalPath + resourceName;
		System.out.println("Retrieving policies for resource: " + resourceName + " and with type: " + cType.getPrivilegeURI());
		this.localStore = new ArrayList<PoliciesSet>();
		if(Store.policiesStorage.get(resourceName) != null){
			if(Store.policiesStorage.get(resourceName).get(cType.getPrivilegeURI()) != null){
				this.localStore = Store.policiesStorage.get(resourceName).get(cType.getPrivilegeURI());
				System.out.println("Policies were loaded!");
			}
		} else {
			System.out.println("No policies found for resource: " + resourceName + " and with type: " + cType.getPrivilegeURI());
		}
	}
	
	public boolean check(String sessid) throws Exception {
		boolean checkResult = true;System.out.println(this.localStore.size());
		if(this.localStore.size() > 0){
			for (PoliciesSet policySet : this.localStore) {
				for(String policyURI : policySet.getAPURIs()){
					boolean isok = true;
					boolean isokFalse = false;
	
					if (policySet.getAcstype(policyURI) == ACSType.CONJUNCTIVE) {
						for (Graph acGraph : policySet.getAccessConditions(policyURI)) {
							isok = isok && ask(acGraph, sessid);
						}
						checkResult = checkResult && isok;
					} else if (policySet.getAcstype(policyURI) == ACSType.DISJUNCTIVE) {
						for (Graph acGraph : policySet.getAccessConditions(policyURI)) {
							isokFalse = ask(acGraph, sessid);
							if (isokFalse == true) {
								break;
							}
						}
						checkResult = checkResult && isokFalse;
					}
				}
			}
		}
		
		System.out.println("Global check result is: " + checkResult);
		return checkResult;
	}
	
	public boolean ask(Graph acGraph, String sessid) {
		System.out.println(acGraph);
		
		Graph actualCtx = Store.contextStorage.get(sessid);
		System.out.println(actualCtx);
		QueryProcess exec = QueryProcess.create(actualCtx);
		Mappings map;
		map = exec.query(acGraph);
		System.out.println("Map Size is " + map.size());
		if (map.size()>=1)
			return true;
		else if (map.size() == 0)
			return false;
		return false; 
	}
	
	public void updateContext(String sessid, String context) throws IOException {
		System.out.println("Updating context for SESS: \n" + sessid);
		System.out.println("Updating turtle context: \n" + context);
		
		context = TurtleHelper.replacePrefixURI(context, "", Config.shi3ldURI);
		context = "@prefix rdf: <" + Config.rdfPrefixURI + ">. \n" + context;
		context = context.replaceAll(" a ", " rdf:type ");
		System.out.println("Fixed turtle context: \n" + context);
		
		Graph tmpCtx = Graph.create();
		
		InputStream is = new ByteArrayInputStream(context.getBytes());

		try {
			Load.create(tmpCtx).load(is, "c.ttl");
		} catch (LoadException e) {
			e.printStackTrace();
		}
		
		Store.contextStorage.put(sessid, tmpCtx);
		System.out.println("Context update performed!");
	}

}