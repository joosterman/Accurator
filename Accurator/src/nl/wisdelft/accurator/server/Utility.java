package nl.wisdelft.accurator.server;


public class Utility {

	private static final String endpoint = "http://e-culture.multimedian.nl/sealinc/sparql/";
	
	private Utility(){}
	
	public static String getEndpoint(){
		return endpoint;
		
	}
}
