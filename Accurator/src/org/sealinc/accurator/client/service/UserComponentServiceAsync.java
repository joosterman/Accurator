package org.sealinc.accurator.client.service;

import java.util.Date;
import java.util.List;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Review;
import org.sealinc.accurator.shared.View;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserComponentServiceAsync {

	void setReview(String annotationURI, Review review, AsyncCallback<Boolean> callback);

	void setViewed(String resourceURI, View view, AsyncCallback<Boolean> callback);

	void getAnnotations(String user, int nrAnnotations, AsyncCallback<List<Annotation>> callback);

	void getTotalAnnotatedPrints(String user, Date annotatedSince, AsyncCallback<Integer> callback);

	void getLastAnnotatedItems(String user, int nrItems, AsyncCallback<List<CollectionItem>> callback);

}
