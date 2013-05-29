package fr.inria.shi3ld.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.inria.shi3ld.Config;

//import javax.ws.rs.WebApplicationException;

import com.hp.hpl.jena.query.*;

import fr.inria.shi3ld.utils.CRUDType;
import fr.inria.shi3ld.utils.GSPWrapper;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * This class handles the security checks to be performed before doing operations with semantic resources
 * 
 * @author oscar
 */
public class Security {
	
	private Map<String, AccessPolicy> apsMap = new HashMap<String, AccessPolicy>();
	private Map<CRUDType, Set<String>> apIndexMap = new HashMap<CRUDType, Set<String>>();

    public void loadPolicies(String resourceName) throws Exception {
        String fusekiQueryURL = Config.fusekiQueryURL;
        String query = this.getPoliciesQuery(resourceName);
        QueryExecution qe = QueryExecutionFactory.sparqlService(fusekiQueryURL, query);
        try{
        	System.out.println("Executing query: " + query);
	        ResultSet results = qe.execSelect();
	        while (results.hasNext()) {
	            QuerySolution qs = results.nextSolution();

	            String apURI = qs.get("ap").toString();
	            String acsType = qs.get("acstype").toString();
	            //String graph = qs.get("graph").toString();
	            String ask = qs.get("ask").toString();
	            String privilege = qs.get("privilege").toString();
	            //System.out.println("URI-->" + apURI + " TYPE--> " + acsType + " -- " + ask);
	            
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
    	} finally {
        	qe.close();
        }
    }
    
    public Map<String, AccessPolicy> getAps() {
		return apsMap;
	}

	public Map<CRUDType, Set<String>> getApindex() {
		return apIndexMap;
	}

	public boolean check(String sessid, CRUDType privilege) throws Exception {
		boolean checkResult = true;
		for (AccessPolicy ap : apsMap.values()) {
			boolean isok = true;
			boolean isokFalse = false;
			if (ap.getPrivileges().contains(privilege)){
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
		}

		System.out.println("Global check result is: " + checkResult);
		return checkResult;
	}
	
	public boolean ask(String queryContent, String sessid) throws Exception {
		String SPARQLEndpoint = Config.fusekiQueryURL;
		if(sessid != null && !sessid.equals("")){
			queryContent += "\nVALUES ?context {<"+ ContextURI.get(sessid) +"#ctx>}";
		}
		boolean result = false;
		System.out.println("Performing ASK: \n" + queryContent);
		QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQLEndpoint, queryContent);
		try {
			result = qe.execAsk();
		} catch (Exception e) {
			throw e;
		} finally {
			qe.close();
		}
        //log.debug("RESULT ASK : "+String.valueOf(res));
		System.out.println("RESULT ASK : \n" + String.valueOf(result));
        return result;
	}
	
	public void updateContext(String sessid, String context) throws Exception {
		System.out.println("Updating context for SESS: \n" + sessid);
		
		//String fusekiQueryURL = Config.fusekiQueryURL;
		//context = context.replaceAll("SESSION_ID", sessid);
		context = fixContextURI(context, sessid);
		System.out.println("Updating turtle context: \n" + context);
		try {
			GSPWrapper.doPOST("default", "text/turtle", context);
			/*
			UpdateRequest request = UpdateFactory.create(context);		
			UpdateExecutionFactory.createRemote(request, fusekiQueryURL);*/
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("Context update performed!");
	}
	
	private String fixContextURI(String context, String sessid){
		String re1="(@)";	// Any Single Character 1
	    String re2="(prefix)";	// Word 1
	    String re3="( )";	// White Space 1
	    String re4="(:)";	// Any Single Character 2
	    String re5="( )";	// White Space 2
	    String re6="(.)";	// Any Single Character 3
	    String re7="((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))";	// HTTP URL 1

	    return context.replaceAll(re1+re2+re3+re4+re5+re6+re7, "@prefix : <" + Config.contextBaseURI + sessid + "#>.");
	}

	public String getPoliciesQuery(String resourceName){
		String policiesQuery = "PREFIX s4ac:<" + Config.s4acPrefixURI + "> "
				+ "PREFIX rdf:<" + Config.rdfPrefixURI + "> "
				+ "SELECT * "
				+ "FROM <http://example.com/policies> "
				+ "WHERE{ "
				+ "?ap a s4ac:AccessPolicy . "
				+ "?ap s4ac:appliesTo <" + resourceName + "> . "
				+ "?ap s4ac:hasAccessConditionSet ?acs . "
				+ "?ap s4ac:hasAccessPrivilege ?privilege . "
				+ "?acs rdf:type ?acstype . "
				+ "FILTER(?acstype = <" + Config.s4acPrefixURI + "ConjunctiveAccessConditionSet> || ?acstype=<" + Config.s4acPrefixURI + "DisjunctiveAccessConditionSet>) . "
				+ "?acs s4ac:hasAccessCondition ?ac . "
				+ "?ac s4ac:hasQueryAsk ?ask .} ";
		
		return policiesQuery;
	}
}