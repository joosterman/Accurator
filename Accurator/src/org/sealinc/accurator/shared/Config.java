package org.sealinc.accurator.shared;


public class Config {
	private static final String sparqlEndpoint = "http://eculture.cs.vu.nl/sealinc/sparql/";	
	private static final String annotationComponentURL = "http://eculture.cs.vu.nl/sealinc/annotate";	
	private static final String assignComponentURL  ="http://eculture.cs.vu.nl/sealinc/assign";
	private static final String assignComponentStrategy = "random";
	private static final int assignComponentNrItems = 3;	
	private static final int qualityComponentNrRecentAnnotations = 10;
	private static final String qualityComponentTrustworthinessURL = "http://eculture.cs.vu.nl/sealinc/quality";
	private static String qualityComponentTrustworthinessStrategy = "valid";
	
	private static final String sparqlPrefixes = "PREFIX rmaschema: <http://purl.org/collections/nl/rma/schema#> "
			+ "PREFIX rmaterms: <http://purl.org/collections/nl/rma/terms/> "
			+ "PREFIX oa: <http://www.w3.org/ns/openannotation/core/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

			
	private Config(){}
	
	public static String getAssignStrategy(){
		return assignComponentStrategy;
	}
	
	public static String getAssignComponentURL(){
		return assignComponentURL;		
	}
	
	public static String getAnnotationComponentURL(){
		return annotationComponentURL;
	}
	public static String getSparqlEndpoint(){
		return sparqlEndpoint;
	}

	public static int getAssigncomponentNrItems() {
		return assignComponentNrItems;
	}

	public static String getRDFPrefixes() {
		return sparqlPrefixes;
	}

	public static int getQualityComponentNrRecentAnnotations() {
		return qualityComponentNrRecentAnnotations;
	}

	public static String getQualityComponentTrustworthinessURL() {
		return qualityComponentTrustworthinessURL;
	}

	public static String getQualityComponentTrustworthinessStrategy() {
		return qualityComponentTrustworthinessStrategy;
	}
}
