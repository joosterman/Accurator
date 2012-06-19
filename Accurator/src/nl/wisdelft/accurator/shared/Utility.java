package nl.wisdelft.accurator.shared;


public class Utility {

	private static final String endpoint = "http://e-culture.multimedian.nl/sealinc/sparql/";
	
	private static final String annotationComponentURL = "http://e-culture.multimedian.nl/sealinc/annotate" ;
	
	private Utility(){}
	
	public static String getAnnotationComponentURL(){
		return annotationComponentURL;
	}
	public static String getEndpoint(){
		return endpoint;
		
	}
}
