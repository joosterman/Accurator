package org.sealinc.accurator.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AssignComponentServiceAsync {

	void getNextItemsToAnnotate(int nritems, AsyncCallback<List<String>> callback);

}
