package org.sealinc.accurator.client.service;

import java.util.List;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AssignComponentServiceAsync {

	void getNextItemsToAnnotate(int nritems, AsyncCallback<List<String>> callback);

	void search(String text, AsyncCallback<List<CollectionItem>> callback);

}
