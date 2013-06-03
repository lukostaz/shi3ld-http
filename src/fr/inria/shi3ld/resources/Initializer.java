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

import java.io.File;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.JerseyClient;
import fr.inria.shi3ld.utils.FileWrapper;

public class Initializer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		System.out.println("\n*** Loading policies from filesystem ***\n");
		System.out.println("Reading folder: " + Config.policiesStoragePath);
		File[] listOfFiles = FileWrapper.listOfFiles(Config.policiesStoragePath);

		String files;
		Client client = Client.create();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".ttl")) {
					System.out.println("Loading policy from file: " + files);
					try {
						String fileContent = FileWrapper.readFile(Config.policiesStoragePath + "/" + files);
						String queryURL = Config.fusekiDataURL + "?graph=default";
						WebResource webResource = client.resource(queryURL);
						ClientResponse response = webResource.type("text/turtle").post(ClientResponse.class, fileContent);
						if (response.getStatus() != 201 || response.getStatus() != 204) {
							System.out.println("Loaded successfully with status: " + response.getStatus());
						} else {
							System.out.println("There was a problem loading the policy, fuseki status:" + response.getStatus());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		client.destroy();
		JerseyClient.client = new Client();
	}
}
