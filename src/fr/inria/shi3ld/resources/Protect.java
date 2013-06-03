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

package fr.inria.shi3ld.resources;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.Base64;
//import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataParam;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.exceptions.UnauthorizedException;
import fr.inria.shi3ld.services.Security;
import fr.inria.shi3ld.utils.CRUDType;
import fr.inria.shi3ld.utils.FileWrapper;
//import fr.inria.shield.exceptions.BadRequestException;
//import fr.inria.shield.utils.GSPWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Shi3ld Web Service endpoint
 *
 * @author oscar
 */

// TODO Improve exceptions-handling (more accurate)
// TODO Implement a multipart @POST request (receive files)
// TODO Migrate system.out.println to a logging system (log4j?)
// TODO Migrate WebApplicationException responses to javax.ws.rs.core.Response
// TODO Improve comments and documentation for classes and methods

@Path("/{resource}")
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
    public String getResource(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader) throws Exception {
        try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(resourceName, CRUDType.READ, sessid))
        		return FileWrapper.readFile(Config.resourceStoragePath + "/" + resourceName);
        	else
        		throw new UnauthorizedException();
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
            throw e;
        } 
    }
    
    @PUT
    public void putResource(String postRequestBody, @PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	String targetFileLocation;
        	
        	if(resourceName.equals(""))
        		throw new WebApplicationException(400);
        	else
        		targetFileLocation = Config.resourceStoragePath + "/" + resourceName;
        	
        	this.validateRequest(sessid, authHeader);
        	
			if (FileWrapper.fileExists(targetFileLocation)) {
				if (checkPermissions(resourceName, CRUDType.UPDATE, sessid)) {
					FileWrapper.deleteFile(targetFileLocation);
					FileWrapper.saveTextFile(postRequestBody, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			} else {
				if(checkPermissions(resourceName, CRUDType.CREATE, sessid)){
					FileWrapper.saveTextFile(postRequestBody, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			}

        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void putResource(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	String targetFileLocation;
        	
        	if(resourceName.equals(""))
        		throw new WebApplicationException(400);
        	else
        		targetFileLocation = Config.resourceStoragePath + "/" + resourceName;
        	
        	this.validateRequest(sessid, authHeader);
        	
			if (FileWrapper.fileExists(targetFileLocation)) {
				if (checkPermissions(resourceName, CRUDType.UPDATE, sessid)) {
					FileWrapper.deleteFile(targetFileLocation);
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			} else {
				if(checkPermissions(resourceName, CRUDType.CREATE, sessid)){
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			}

        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @POST
    public void postResource(String postRequestBody, @PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Content-Type") String contentTypeHeader) throws Exception {
       	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	String targetFileLocation;
        	
        	if(resourceName.equals(""))
        		throw new WebApplicationException(400);
        	else
        		targetFileLocation = Config.resourceStoragePath + "/" + resourceName;
        	
        	this.validateRequest(sessid, authHeader);
        	if (FileWrapper.fileExists(targetFileLocation)) {
				if (checkPermissions(resourceName, CRUDType.UPDATE, sessid)) {
					FileWrapper.deleteFile(targetFileLocation);
					//FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					FileWrapper.saveTextFile(postRequestBody, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			} else {
				if(checkPermissions(resourceName, CRUDType.CREATE, sessid)){
					//FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					FileWrapper.saveTextFile(postRequestBody, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			}
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }
  
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void postResourceMP(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	String targetFileLocation;
        	
        	if(resourceName.equals(""))
        		targetFileLocation = Config.resourceStoragePath + "/" + fileDetail.getFileName();
        	else
        		targetFileLocation = Config.resourceStoragePath + "/" + resourceName;
        	
        	this.validateRequest(sessid, authHeader);
        	if (FileWrapper.fileExists(targetFileLocation)) {
				if (checkPermissions(resourceName, CRUDType.UPDATE, sessid)) {
					FileWrapper.deleteFile(targetFileLocation);
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			} else {
				if(checkPermissions(resourceName, CRUDType.CREATE, sessid)){
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
				} else {
					throw new UnauthorizedException();
				}
			}
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (Exception e){
        	throw e;
        } 
    }
    
    @HEAD
    public void headResource(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader, @HeaderParam("Accept") String acceptHeader) throws Exception {
        try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(resourceName, CRUDType.READ, sessid)){
        		FileWrapper.readFile(Config.resourceStoragePath + "/" + resourceName);
        		throw new WebApplicationException(204);
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
    public String deleteResource(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader) throws Exception{
    	try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);        	
        	
        	this.validateRequest(sessid, authHeader);
        	if(checkPermissions(resourceName, CRUDType.DELETE, sessid)){
        		FileWrapper.deleteFile(Config.resourceStoragePath + "/" + resourceName);
        		throw new WebApplicationException(410);
        	} else {
        		throw new UnauthorizedException();
        	}
        } catch (UnauthorizedException ue){
        	throw ue;
        } catch (FileNotFoundException fne) {
        	throw new WebApplicationException(404);
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
			if (authContextDecoded!= null && !authContextDecoded.isEmpty()) {
				System.out.println("Updating context...");
				this.securityCheck.updateContext(sessid, authContextDecoded);
				System.out.println("Context updated!");
			} else {
				throw new UnauthorizedException("Authorization context information not found");
			}
		} else {
			throw new UnauthorizedException("Authorization header not found");
		}
    }
 
    private boolean checkPermissions(String resourceName, CRUDType cType, String sessid) throws Exception {
    	System.out.println("Loading policies from store...");
        this.securityCheck.loadPoliciesFromStore(resourceName, cType);
        System.out.println("Policies were loaded!");
    	System.out.println("Authorizing...");
        return this.securityCheck.check(sessid);
    }
}
