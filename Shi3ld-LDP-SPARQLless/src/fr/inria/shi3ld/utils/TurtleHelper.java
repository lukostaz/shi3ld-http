package fr.inria.shi3ld.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TurtleHelper {

	public static String getPrefixValue(String turtleGraph, String prefix){
		String re1 = "(@)"; // Any Single Character 1
		String re2 = "(prefix)"; // Word 1
		String re3 = "( )"; // White Space 1
		String re4 = "(" + prefix + ":)"; // Any Single Character 2
		String re5 = "( )"; // White Space 2
		String re6 = "(<)"; // Any Single Character 3
		String re7 = "((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))";
		String re8 = "(>.)";
		
		Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(turtleGraph);
		if (m.find()) {
			/*
			String c1 = m.group(1);
			String word1 = m.group(2);
			String ws1 = m.group(3);
			String c2 = m.group(4);
			String ws2 = m.group(5);
			String c3 = m.group(6);*/
			String httpurl1 = m.group(7);
			//String c4 = m.group(8);
			/*System.out.print("(" + c1.toString() + ")" + "(" + word1.toString()
					+ ")" + "(" + ws1.toString() + ")" + "(" + c2.toString()
					+ ")" + "(" + ws2.toString() + ")" + "(" + c3.toString()
					+ ")" + "(" + httpurl1.toString() + ")" + "(" + c4.toString()
					+ ")" + "\n");*/
			//System.out.println("===M=== " + m.replaceAll("test") + " ==end M ==");
			return httpurl1.toString();
		}
		return "";
	}
	
	public static String replacePrefixURI(String turtleGraph, String prefix, String URI){
		String re1 = "(@)"; // Any Single Character 1
		String re2 = "(prefix)"; // Word 1
		String re3 = "( )"; // White Space 1
		String re4 = "(" + prefix + ":)"; // Any Single Character 2
		String re5 = "( )"; // White Space 2
		String re6 = "(<)"; // Any Single Character 3
		String re7 = "((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))";
		String re8 = "(>.)";
		
		Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(turtleGraph);
		if (m.find()) {
			return m.replaceAll("@prefix " + prefix + ": <" +  URI + ">.");
		}
		return "turtleGraph";
	}
	
	public static String toBlankNodes(String turtleGraph){
		String str;
		String finalGraph = "";
		BufferedReader reader = new BufferedReader(new StringReader(turtleGraph));
		
		try {
			while ((str = reader.readLine()) != null) {
				if (!str.contains("rdf:type s4ac:AccessPolicy")){
					str = str.replaceAll("(^| )(:)((?:[a-z][a-z]+))", "$1_$2$3");
				}
				finalGraph += str + "\n";
		  	}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return finalGraph;
	}
}
