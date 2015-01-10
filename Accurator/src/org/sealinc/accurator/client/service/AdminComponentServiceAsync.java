package org.sealinc.accurator.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminComponentServiceAsync {

	void register(String user, String password, String realName, AsyncCallback<Boolean> callback);

	void getJSON(String url, AsyncCallback<String> callback);
	
	void getCountries(AsyncCallback<List<String>> callback);
	
	void getLanguages(AsyncCallback<List<String>> callback);

}
