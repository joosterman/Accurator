package org.sealinc.accurator.shared;

import java.util.ArrayList;
import java.util.List;

public class CollectionItem extends RDFObject {
	public String id;
	public String title;
	public String description;
	public String thumbnailURL;
	public String imageURL;
	public String notes;
	public String objectNumber;
	public String maker;
	public List<String> contentClassification;

	public static final String rdfType = "http://www.europeana.eu/schemas/edm/ProvidedCHO";

	public CollectionItem() {
		contentClassification = new ArrayList<String>();
	}

	public CollectionItem(String id) {
		this();
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof CollectionItem))
			return false;
		CollectionItem otherMyClass = (CollectionItem) other;
		return otherMyClass.id == this.id;
	}
}
