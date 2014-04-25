package org.sealinc.accurator.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminComponentServiceAsync {

	void register(String user, String password, String realName, AsyncCallback<Boolean> callback);

	void getJSON(String url, AsyncCallback<String> callback);

}
