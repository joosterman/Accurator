package org.sealinc.accurator.shared;

import java.util.Date;

public class View extends RDFObject {

	@Namespace(NS.accurator)
	public String viewer;
	@Namespace(NS.dcterms)
	public Date date;
	@Namespace(NS.accurator)
	public Boolean fromRecommendation;
}
