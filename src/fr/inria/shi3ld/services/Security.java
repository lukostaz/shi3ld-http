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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.inria.acacia.corese.api.IResult;
import fr.inria.acacia.corese.api.IResults;
import fr.inria.acacia.corese.exceptions.EngineException;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.utils.CRUDType;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import fr.inria.shi3ld.utils.TurtleHelper;

/**
 * This class handles the security checks to be performed before doing operations with semantic resources
 * 
 * @author oscar
 */
public class Security {
	private Map<String, AccessPolicy> apsMap = new HashMap<String, AccessPolicy>();
	private Map<CRUDType, Set<String>> apIndexMap = new HashMap<CRUDType, Set<String>>();
	
	public void loadPoliciesFromStore(String resourceName, CRUDType cType) throws Exception {
		System.out.println("Generating SPARQL query to get only the access policies for the resource");
        String query = this.getPoliciesQuery(resourceName, cType.getPrivilegeURI());
        System.out.println("Generated SPARQL query to retrieve policies for the resource: \n" + query);
        try{			
			IResults ires = Store.engine.SPARQLQuery(query);
			
			for (Enumeration<IResult> en = ires.getResults(); en.hasMoreElements();) {
				IResult r = en.nextElement();
				String apURI = r.getStringValue("?ap");
				String acsType = r.getStringValue("?acstype");
				String ask = r.getStringValue("?ask");
				String privilege = cType.getPrivilegeURI();
				System.out.println("URI-->" + apURI + " TYPE--> " + acsType + " ASK--> " + ask + " PRIVILEGE--> " + privilege);
				
	            AccessPolicy ap;
        		if(apsMap.containsKey(apURI)) {
    				ap = apsMap.get(apURI);
        		} else {
    				ap = new AccessPolicy(apURI, acsType, new HashSet<String>());
    				apsMap.put(apURI, ap);
    			}
        		ap.addGraph(resourceName);
    			ap.addAsk(ask);
    			        		
        		CRUDType type = CRUDType.UNKNOWN;
        		
    			if(privilege.equals(Config.s4acPrefixURI + "Create")) type = CRUDType.CREATE;
    			else if(privilege.equals(Config.s4acPrefixURI + "Read")) type = CRUDType.READ;
    			else if(privilege.equals(Config.s4acPrefixURI + "Update")) type = CRUDType.UPDATE;
    			else if(privilege.equals(Config.s4acPrefixURI + "Delete")) type = CRUDType.DELETE;
    			
    			ap.addPrivilege(type);
    			
    			Set<String> index;
    			if(apIndexMap.containsKey(type)) {
    				index = apIndexMap.get(type);
    			} else {
    				index = new HashSet<String>();
    				apIndexMap.put(type, index);
    			}
    			index.add(apURI);
			}
        } catch (Exception e) {
        	throw e;
    	}
    }
	
	public boolean check(String sessid) throws Exception {
		boolean checkResult = true;
		
		for (AccessPolicy ap : apsMap.values()) {
			boolean isok = true;
			boolean isokFalse = false;
			if (ap.getAcstype() == ACSType.CONJUNCTIVE) {
				for (String askQuery : ap.getAsks()) {
					isok = isok && ask(askQuery, sessid);
				}
				checkResult = checkResult && isok;
			} else if (ap.getAcstype() == ACSType.DISJUNCTIVE) {
				for (String askQuery : ap.getAsks()) {
					isokFalse = ask(askQuery, sessid);
					if (isokFalse == true) {
						break;
					}
				}
				checkResult = checkResult && isokFalse;
			} 
		}
		// delete context file
		//FileWrapper.deleteFile(Config.resourceStoragePath + "/" + sessid + ".ttl");
		
		System.out.println("Global check result is: " + checkResult);
		return checkResult;
	}
	
	private boolean ask(String queryContent, String sessid) throws Exception {		
		if(sessid != null && !sessid.equals("")){
			queryContent += "\nBINDINGS ?context {(<"+ ContextURI.get(sessid) +"#ctx>)}";
		}
		
		System.out.println("Performing ASK: \n" + queryContent);		
		IResults ires = Store.engine.SPARQLQuery(queryContent);
		System.out.println(ires.size());
		
        return (ires.size() != 0);
	}
	
	public void updateContext(String sessid, String context) throws EngineException {
		System.out.println("Updating context for SESS: \n" + sessid);
		context = TurtleHelper.replacePrefixURI(context, "", Config.contextBaseURI + sessid);
		System.out.println("Updating turtle context: \n" + context);

		context = TurtleHelper.toQuery(context, sessid);
		Store.engine.SPARQLQuery(context);
		System.out.println("Context update performed!");
	}
	
	public String getPoliciesQuery(String resourceName, String privilege){
		resourceName = Config.baseURI + resourceName;
		String policiesQuery = "PREFIX s4ac: <" + Config.s4acPrefixURI + "> \n"
				+ "PREFIX rdf: <" + Config.rdfPrefixURI + "> \n"
				+ "SELECT * \n"
				//+ "FROM <http://example.com/policies> "
				+ "WHERE{ \n"
				+ "?ap a s4ac:AccessPolicy . \n"
				+ "?ap s4ac:appliesTo <" + resourceName + "> . \n"
				+ "?ap s4ac:hasAccessConditionSet ?acs . \n"
				+ "?ap s4ac:hasAccessPrivilege <" + privilege + "> . \n"
				+ "?acs rdf:type ?acstype . \n"
				+ "FILTER(?acstype = <" + Config.s4acPrefixURI + "ConjunctiveAccessConditionSet> || ?acstype=<" + Config.s4acPrefixURI + "DisjunctiveAccessConditionSet>) . \n"
				+ "?acs s4ac:hasAccessCondition ?ac . \n"
				+ "?ac s4ac:hasQueryAsk ?ask .} ";
		
		return policiesQuery;
	}
}
