package nl.wisdelft.accurator.client.service;

import java.util.List;

import nl.wisdelft.accurator.shared.ResourceDetail;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResourceDetailServiceAsync {

	void GetResourceDetail(String resourceURI, AsyncCallback<ResourceDetail> callback);

	void GetResourcesDetail(List<String> resourceURIs, AsyncCallback<List<ResourceDetail>> callback);

}
