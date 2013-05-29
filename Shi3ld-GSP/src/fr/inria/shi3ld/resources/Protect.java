package fr.inria.shi3ld.resources;

import com.sun.jersey.core.util.Base64;

import fr.inria.shi3ld.exceptions.BadRequestException;
import fr.inria.shi3ld.exceptions.UnauthorizedException;
import fr.inria.shi3ld.services.Security;
import fr.inria.shi3ld.utils.CRUDType;
import fr.inria.shi3ld.utils.GSPWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Shi3ld Web Service endpoint
 *
 * @author oscar
 */

// TODO Improve exceptions-handling (more accurate)
// TODO Implement a multipart @POST request (receive files)
// TODO Migrate system.out.println to a logging system (log4j?) 
// TODO Improve comments and documentation for classes and methods

@Path("/gsp")
public class Protect {

	@Context
	protected HttpServletRequest req;
	protected Security securityCheck;
	
    /**
     * Creates a new instance of ServiceEndpoint
     */
    public Protect() {
    	this.securityCheck = new Security();
    }

    @GET
    public String getResource(@QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Accept") String acceptHeader) throws Exception {
        try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(graphName, sessid, CRUDType.READ))
        		return GSPWrapper.doGET(graphName, acceptHeader);
        	else
        		throw new UnauthorizedException();
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
            throw e;
        } 
    }

    @PUT
    public void putResource(String postRequestBody, @QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
			if(contentTypeHeader.equals("") || contentTypeHeader == null)
        		throw new BadRequestException("Content-Type header is missing");
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(graphName, sessid, CRUDType.UPDATE))
        		GSPWrapper.doPUT(graphName, contentTypeHeader, postRequestBody);
        	else
        		throw new UnauthorizedException();
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @POST
    public void postResource(String postRequestBody, @QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        		
			if(contentTypeHeader.equals("") || contentTypeHeader == null)
        		throw new BadRequestException("Content-Type header is missing");
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(graphName, sessid, CRUDType.CREATE))
        		GSPWrapper.doPOST(graphName, contentTypeHeader, postRequestBody);
        	else
        		throw new UnauthorizedException();
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @HEAD
    public String headResource(@QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Accept") String acceptHeader) throws Exception {
        try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(graphName, sessid, CRUDType.READ)){
        		GSPWrapper.doGET(graphName, acceptHeader);
        		return "";
        	} else {
        		throw new UnauthorizedException();
        	}
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
            throw e;
        } 
    }
    
    @DELETE
    public String deleteResource(@QueryParam("graph") String graphName, @HeaderParam("Authorization") String authHeader) throws Exception{
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(graphName, sessid, CRUDType.DELETE))
        		return GSPWrapper.doDELETE(graphName);
        	else
        		throw new UnauthorizedException();
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
            throw e;
        } 
    }

    private void validateRequest(String sessid, String authHeader) throws Exception  {
		if (authHeader == null)
			throw new UnauthorizedException("Authorization header not found");

		System.out.println("Auth Header: " + authHeader);
		String[] split = authHeader.split(":");
		if(split.length < 2)
			throw new UnauthorizedException("Invalid authorization header format");
		
		String authType = split[0];
		String authContext = split[1];

		if (!Base64.isBase64(authContext))
			throw new UnauthorizedException("Invalid authorization header");

		String authContextDecoded = Base64.base64Decode(authContext);
		System.out.println("Context decoded: " + authContextDecoded);

		if (authType.equals("Shi3ld")) {
			if (authContextDecoded != null && !authContextDecoded.isEmpty()) {
				System.out.println("Updating context...");
				this.securityCheck.updateContext(sessid, authContextDecoded);
				System.out.println("Context updated!");
			} else {
				throw new UnauthorizedException(
						"Authorization context information not found");
			}
		} else {
			throw new UnauthorizedException("Authorization header not found");
		}
    }
    
    private boolean checkPermissions(String resourceName, String sessid, CRUDType privilege) throws Exception {
    	System.out.println("Loading policies...");
        this.securityCheck.loadPolicies(resourceName);
        System.out.println("Policies loaded!");
        return this.securityCheck.check(sessid, privilege);
    }
    
}
