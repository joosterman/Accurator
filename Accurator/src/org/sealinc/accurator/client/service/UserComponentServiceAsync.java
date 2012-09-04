package org.sealinc.accurator.client.service;

import java.util.List;

import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.Review;
import org.sealinc.accurator.shared.View;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserComponentServiceAsync {

	void getAnnotations(String user, AsyncCallback<List<Annotation>> callback);

	void setReview(String annotationURI, Review review, AsyncCallback<Boolean> callback);

	void setViewed(String resourceURI, View view, AsyncCallback<Boolean> callback);


}
