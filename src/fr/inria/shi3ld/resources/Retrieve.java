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
import com.sun.jersey.multipart.FormDataParam;
import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.Security;
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
 * @author oscar
 */

@Path("/retrieve/{resource}")
public class Retrieve {

	@Context
	protected HttpServletRequest req;

	protected Security securityCheck;
	
    /**
     * Creates a new instance of ServiceEndpoint
     */
    public Retrieve() {
    	this.securityCheck = new Security();
    }

    @GET
    public String getResource(@PathParam("resource") String resourceName, @HeaderParam("Authorization") String authHeader) throws Exception {
        try {
        	HttpSession session = this.req.getSession(true);
        	String sessid = session.getId();
        	System.out.println("SESS ID: " + sessid);

        	return FileWrapper.readFile(Config.resourceStoragePath + "/" + resourceName);
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
        		throw new WebApplicationException(404);
        	else
        		targetFileLocation = Config.resourceStoragePath + resourceName;
        	
			if (FileWrapper.fileExists(targetFileLocation)) {
					FileWrapper.deleteFile(targetFileLocation);
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
			} else {
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
			}

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
        		targetFileLocation = Config.resourceStoragePath + fileDetail.getFileName();
        	else
        		targetFileLocation = Config.resourceStoragePath + resourceName;
        	
        	if (FileWrapper.fileExists(targetFileLocation)) {
					FileWrapper.deleteFile(targetFileLocation);
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
			} else {
					FileWrapper.writeToFile(uploadedInputStream, targetFileLocation);
					throw new WebApplicationException(201);
			}
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
        	
        	FileWrapper.readFile(Config.resourceStoragePath + "/" + resourceName);
        	throw new WebApplicationException(204);
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
        	
        	FileWrapper.deleteFile(Config.resourceStoragePath + "/" + resourceName);
        	throw new WebApplicationException(410);
        } catch (FileNotFoundException fne) {
        	throw new WebApplicationException(404);
        } catch (Exception e){
            throw e;
        } 
    }
}
