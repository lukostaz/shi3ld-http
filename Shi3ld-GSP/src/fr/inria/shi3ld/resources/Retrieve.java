package fr.inria.shi3ld.resources;

import fr.inria.shi3ld.exceptions.BadRequestException;
import fr.inria.shi3ld.utils.GSPWrapper;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author oscar
 */

@Path("/retrieve")
public class Retrieve {


    @GET
    public String getResource(@QueryParam("graph") String graphName, @HeaderParam("Accept") String acceptHeader) throws Exception {
        try {
       		return GSPWrapper.doGET(graphName, acceptHeader);
        } catch (Exception e){
            throw e;
        } 
    }

    @PUT
    public void putResource(String postRequestBody, @QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
    	try {
			if(contentTypeHeader.equals("") || contentTypeHeader == null)
        		throw new BadRequestException("Content-Type header is missing");
			
        	GSPWrapper.doPUT(graphName, contentTypeHeader, postRequestBody);
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @POST
    public void postResource(String postRequestBody, @QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
    	try {
			if(contentTypeHeader.equals("") || contentTypeHeader == null)
        		throw new BadRequestException("Content-Type header is missing");

        	GSPWrapper.doPOST(graphName, contentTypeHeader, postRequestBody);
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @HEAD
    public String headResource(@QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Accept") String acceptHeader) throws Exception {
        try {
       		GSPWrapper.doGET(graphName, acceptHeader);
       		return "";
        } catch (Exception e){
            throw e;
        } 
    }
    
    @DELETE
    public String deleteResource(@QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader) throws Exception{
    	try {
        	return GSPWrapper.doDELETE(graphName);
        } catch (Exception e){
            throw e;
        } 
    }    
}
