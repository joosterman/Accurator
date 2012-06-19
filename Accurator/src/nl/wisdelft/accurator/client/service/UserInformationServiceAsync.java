package nl.wisdelft.accurator.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInformationServiceAsync {

	void GetAnnotations(String userURI, AsyncCallback<List<String>> callback);

}
