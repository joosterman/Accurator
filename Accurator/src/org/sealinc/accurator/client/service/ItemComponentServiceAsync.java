package org.sealinc.accurator.client.service;

import java.util.List;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ItemComponentServiceAsync {
	void getItems(List<String> resourceURIs,
			AsyncCallback<List<CollectionItem>> callback);

	void getTopic(String resourceURI, AsyncCallback<String> callback);

}
