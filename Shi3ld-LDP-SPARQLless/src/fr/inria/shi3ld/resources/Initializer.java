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
