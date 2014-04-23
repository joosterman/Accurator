package org.sealinc.accurator.shared;

public class Config {
	public static final String baseURL = "http://ops.few.vu.nl/chris/";
	public static final String adminUsername = "accurator";
	public static final String adminPassword = "Angfsd3E11";
	
	/*public static final String baseURL = "http://eculture.cs.vu.nl/sealinctest/";
	public static final String adminUsername = "admin";
	public static final String adminPassword = "sealink";*/
	
	public static final String sparqlEndpoint = baseURL + "sparql/";
	public static final String annotationComponentURL = baseURL + "annotate";
	public static final String annotationComponentChangePreferenceURL = baseURL +"admin/changePreferences";
	public static final String assignComponentURL = baseURL + "assign";
	public static final String assignComponentStrategy = "user";
	public static final int assignComponentNrItems = 30;
	public static final int qualityComponentNrRecentAnnotations = 10;
	public static final String qualityComponentTrustworthinessURL = baseURL + "quality";
	public static String qualityComponentTrustworthinessStrategy = "valid";
	public static final String loginURL = baseURL + "servlets/login";
	public static final String logoutURL = baseURL + "servlets/logout";
	public static final String adminRegisterUserURL = baseURL + "admin/selfRegister";
	public static final String userComponentUserURI = baseURL + "user/";
	public static final String adminComponentUploadDataURL = baseURL + "servlets/uploadData";
	public static final String adminComponentBaseURI = "http://purl.org/accurator/";
	public static final String recommendationComponentSearchURL = baseURL + "assign?strategy=literal_matching&predicate=http://purl.org/dc/terms/description&nritems=3";

	public static final String sparqlPrefixes = "PREFIX rmaschema: <http://purl.org/collections/nl/rma/schema#> "
			+ "PREFIX rmaterms: <http://purl.org/collections/nl/rma/terms/> " + "PREFIX oa: <http://www.w3.org/ns/openannotation/core/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "PREFIX acc: <http://purl.org/accurator/NS/> "
			+ "PREFIX rev: <http://purl.org/stuff/rev#>";

	public static String[] profileExpertises = new String[] {"castle","flower"};
	public static boolean profileShowExpertises = true;
	private Config() {}

}