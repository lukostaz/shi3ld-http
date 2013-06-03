package fr.inria.shi3ld.services;

import fr.inria.shi3ld.Config;

public class ContextURI {
	private static String base = Config.contextBaseURI;
	public static String get(String sessid) {
		return base + sessid;
	}
	
	public static String get() {
		return base;
	}
}
