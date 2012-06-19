package nl.wisdelft.accurator.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResourceDetail implements IsSerializable {
	
	private String description;
	private String imageURL;
	private String title;
	private String URI;
	
	private ResourceDetail(){
		
	}
	public ResourceDetail(String URI){
		this.URI= URI;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

}
