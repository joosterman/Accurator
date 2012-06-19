package nl.wisdelft.accurator.client.service;

import java.util.List;

import nl.wisdelft.accurator.shared.ResourceWithValue;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RecommenderServiceAsync {

	void getRankedRecommendedItems(String userURI, AsyncCallback<List<ResourceWithValue<Double>>> callback);

	void getRecommendedItems(String userURI, AsyncCallback<List<String>> callback);

}
