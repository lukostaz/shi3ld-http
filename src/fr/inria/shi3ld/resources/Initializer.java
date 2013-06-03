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
