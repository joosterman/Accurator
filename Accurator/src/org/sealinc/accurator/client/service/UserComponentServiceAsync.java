package org.sealinc.accurator.client.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

	void getAnnotationFieldsParam(String user, String resourceURI, AsyncCallback<String> callback);

	void getExpertise(String user, AsyncCallback<Map<String,Integer>> callback);

}
