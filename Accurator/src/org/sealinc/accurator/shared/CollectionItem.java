package org.sealinc.accurator.shared;

import java.util.ArrayList;
import java.util.List;

public class CollectionItem extends RDFObject {
	public String title;
	public String description;
	public String thumbnailURL;
	public String imageURL;
	public String notes;
	public String objectNumber;
	public List<String> maker;
	public List<String> contentClassification;
	
	public CollectionItem(){
		contentClassification = new ArrayList<String>();
	}

}
