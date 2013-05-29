package fr.inria.shi3ld.utils;

import javax.ws.rs.WebApplicationException;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.JerseyClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Graph Store Protocol Wrapper
 * Implements the jersey-client library to perform HTTP requests to fuseki
 * 
 * @author oscar
 */
public class GSPWrapper {
	
	public static String doGET(String graphName, String accept) throws Exception {
		String queryURL = Config.fusekiDataURL + "?graph=" + graphName;
		String output = "";
		try {
			WebResource webResource = JerseyClient.client.resource(queryURL);
			ClientResponse response = webResource.accept(accept).get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new WebApplicationException(response.getStatus());
			}
			output = response.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
		  } catch (Exception e) {
			  throw e;
		  }
		return output;
	}
	
	public static void doPOST(String graphName, String contentType, String requestBody) throws Exception {
		String queryURL;
		//String output = "";
		if(graphName.equals(""))
			queryURL = Config.fusekiDataURL + "default";
		else
			queryURL = Config.fusekiDataURL + "?graph=" + graphName;
	
		try {
			WebResource webResource = JerseyClient.client.resource(queryURL);
			ClientResponse response = webResource.type(contentType).post(ClientResponse.class, requestBody);
			
			System.out.println(response.getStatus());
			//if (response.getStatus() != 201 && response.getStatus() != 204 ) {
				throw new WebApplicationException(response.getStatus());
			//}
				/*
			if(response.getStatus() == 204){
				output = "204 No Content";
			} else {
				output = response.getEntity(String.class);
			}
			System.out.println("Output from Server: \n");
			System.out.println(output);*/
		  } catch (Exception e) {
			  throw e;
		  }
		//return output;
	}
	
	public static void doPUT(String graphName, String contentType, String RDFPayload) throws Exception {
		String queryURL;
		//String output = "";
		if(graphName.equals(""))
			queryURL = Config.fusekiDataURL + "default";
		else
			queryURL = Config.fusekiDataURL + "?graph=" + graphName;
	
		try {
			WebResource webResource = JerseyClient.client.resource(queryURL);
			ClientResponse response = webResource.type(contentType).put(ClientResponse.class, RDFPayload);
			
			//if (response.getStatus() != 201) {
				throw new WebApplicationException(response.getStatus());
			//}
			
			//output = response.getEntity(String.class);
			
			//System.out.println("Output from Server .... \n");
			//System.out.println(output);
		  } catch (Exception e) {
			  throw e;
		  }
		//return output;
	}
	
	public static String doDELETE(String graphName) throws Exception{
		//String output = "";
		String queryURL = Config.fusekiDataURL + "?graph=" + graphName;
	
		try {
			WebResource webResource = JerseyClient.client.resource(queryURL);
			ClientResponse response = webResource.delete(ClientResponse.class);
			
			//if (response.getStatus() != 200 && response.getStatus() != 204 ) {
			   throw new WebApplicationException(response.getStatus());
			//}

		  } catch (Exception e) {
			  throw e;
		  }
		//return output;
	}
}
