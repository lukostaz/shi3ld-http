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
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.PoliciesSet;
import fr.inria.shi3ld.services.Store;
import fr.inria.shi3ld.utils.FileWrapper;
import fr.inria.shi3ld.utils.PoliciesWrapper;
import fr.inria.shi3ld.utils.TurtleHelper;
import fr.inria.edelweiss.kgraph.core.Graph;

public class Initializer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		System.out.println("*** Loading policies from filesystem ***");
		System.out.println("Reading folder: " + Config.policiesStoragePath);
		File[] listOfFiles = FileWrapper.listOfFiles(Config.policiesStoragePath);

		String files;
		Store.contextStorage = new HashMap<String, Graph>();
		Store.policiesStorage = new HashMap<String, HashMap<String,ArrayList<PoliciesSet>>>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".ttl")) {
					System.out.println("Loading policy from file: " + files);
					try {

						String tmpFile = FileWrapper.readFile(Config.policiesStoragePath + "/" + files);
						// adds rdf prefix
						tmpFile = "@prefix rdf: <" + Config.rdfPrefixURI + ">. \n" + tmpFile;
						// replaces turtle "a" with "rdf:type"
						tmpFile = tmpFile.replaceAll(" a ", " rdf:type ");
						// fixes the base URI of the policy
						tmpFile = TurtleHelper.replacePrefixURI(tmpFile, "", Config.shi3ldURI);
						
						tmpFile = TurtleHelper.toBlankNodes(tmpFile);
						System.out.println(tmpFile);
						PoliciesWrapper p = new PoliciesWrapper(tmpFile);
												
						p.loadPolicies();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
