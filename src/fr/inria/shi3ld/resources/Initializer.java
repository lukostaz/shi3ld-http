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

import fr.inria.acacia.corese.api.EngineFactory;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.services.Store;
import fr.inria.shi3ld.utils.FileWrapper;

public class Initializer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		System.out.println("************");
		System.out.println("*** Loading policies from filesystem ***");
		System.out.println("***********");
		System.out.println("Reading folder: " + Config.policiesStoragePath);
		File[] listOfFiles = FileWrapper.listOfFiles(Config.policiesStoragePath);

		String files;
		//Store.graph = Graph.create();
		Store.ef = new EngineFactory();
		Store.engine = Store.ef.newInstance();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".rdf") || files.endsWith(".ttl")) {
					System.out.println("Loading policy from file: " + files);
					try {
						Store.engine.load(Config.policiesStoragePath + "/" + files);
					} catch (EngineException e) {
						e.printStackTrace();
					}
				}
			}
		}/*
		String SPARQLquery = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object }";
		IResults ires;
		try {
			ires = Store.engine.SPARQLQuery(SPARQLquery);
			for (Enumeration<IResult> en = ires.getResults(); en.hasMoreElements();) {
				IResult r = en.nextElement();
				System.out.println(r);
			}
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
