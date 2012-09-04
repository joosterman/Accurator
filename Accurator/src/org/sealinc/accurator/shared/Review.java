package org.sealinc.accurator.shared;

import java.util.Date;

public class Review extends RDFObject {
	  @Namespace(NS.review)
		public String reviewer;
	  @Namespace(NS.review)
		public Integer minRating;
	  @Namespace(NS.review)
		public Integer maxRating;
	  @Namespace(NS.review)
		public Integer rating;
	  @Namespace(NS.review)
		public String title;
	  @Namespace(NS.review)
		public String text;
	  @Namespace(NS.review)
		public Integer positiveVotes;
	  @Namespace(NS.review)
		public Integer totalVotes;		
	  @Namespace(NS.accurator)
	  public Boolean approved;
	  @Namespace(NS.dcterms)
	  public Date date;
}
