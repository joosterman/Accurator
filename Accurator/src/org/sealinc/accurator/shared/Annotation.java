package org.sealinc.accurator.shared;

import java.util.Date;

public class Annotation extends RDFObject{
	public Date annotated;
	public String annotator;
	public String hasBody;
	public String hasTarget;
	public String annotationField;
	public int typingTime;
	public double trustworthiness;
	
	public static final String rdfType = "http://www.w3.org/ns/openannotation/core/Annotation";
}
