package org.sealinc.accurator.shared;

public class Config {
	private static final String baseURL = "http://eculture.cs.vu.nl/sealinc/";
	private static final String sparqlEndpoint = baseURL + "sparql/";
	private static final String annotationComponentURL = baseURL + "annotate";
	private static final String annotationComponentChangePreferenceURL = baseURL +"admin/changePreferences";
	private static final String assignComponentURL = baseURL + "assign";
	private static final String assignComponentStrategy = "user";
	private static final int assignComponentNrItems = 30;
	private static final int qualityComponentNrRecentAnnotations = 10;
	private static final String qualityComponentTrustworthinessURL = baseURL + "quality";
	private static String qualityComponentTrustworthinessStrategy = "valid";
	private static final String loginURL = baseURL + "servlets/login";
	private static final String logoutURL = baseURL + "servlets/logout";
	private static final String adminRegisterUserURL = baseURL + "admin/selfRegister";
	private static final String adminUsername = "admin";
	private static final String adminPassword = "Anuz3EeY";
	private static final String userComponentUserURI = baseURL + "user/";
	private static final String adminComponentUploadDataURL = baseURL + "servlets/uploadData";
	private static final String adminComponentBaseURI = "http://purl.org/accurator/";
	private static final String recommendationComponentSearchURL = baseURL + "assign?strategy=literal_matching&predicate=http://purl.org/dc/terms/description&nritems=3";

	private static final String sparqlPrefixes = "PREFIX rmaschema: <http://purl.org/collections/nl/rma/schema#> "
			+ "PREFIX rmaterms: <http://purl.org/collections/nl/rma/terms/> " + "PREFIX oa: <http://www.w3.org/ns/openannotation/core/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "PREFIX acc: <http://purl.org/accurator/NS/> "
			+ "PREFIX rev: <http://purl.org/stuff/rev#>";

	private Config() {}

	public static String getAssignStrategy() {
		return assignComponentStrategy;
	}

	public static String getAssignComponentURL() {
		return assignComponentURL;
	}

	public static String getAnnotationComponentURL() {
		return annotationComponentURL;
	}

	public static String getSparqlEndpoint() {
		return sparqlEndpoint;
	}

	public static int getAssignComponentNrItems() {
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

	public static String getLoginURL() {
		return loginURL;
	}

	public static String getLogoutURL() {
		return logoutURL;
	}

	public static String getAdminUsername() {
		return adminUsername;
	}

	public static String getAdminPassword() {
		return adminPassword;
	}

	public static String getUserComponentUserURI() {
		return userComponentUserURI;
	}

	public static String getAdminComponentUploadDataURL() {
		return adminComponentUploadDataURL;
	}

	public static String getAdminComponentBaseURI() {
		return adminComponentBaseURI;
	}

	public static String getAdminRegisterUserURL() {
		return adminRegisterUserURL;
	}

	public static String getRecommendationComponentSearchURL() {
		return recommendationComponentSearchURL;
	}

	public static String getAnnotationComponentChangePreferenceURL() {
		return annotationComponentChangePreferenceURL;
	}
}
