package org.sealinc.accurator.client.service;

import java.util.List;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AssignComponentServiceAsync {

	void search(String text, AsyncCallback<List<CollectionItem>> callback);

	void getNextItemsToAnnotate(int nritems, String matches, AsyncCallback<List<String>> callback);

}
