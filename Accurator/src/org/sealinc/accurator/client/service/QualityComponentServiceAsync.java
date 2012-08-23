package org.sealinc.accurator.client.service;

import java.util.List;
import org.sealinc.accurator.shared.Annotation;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QualityComponentServiceAsync {

	void getRecentAnnotations(AsyncCallback<List<Annotation>> callback);

	void getTrustworthiness(String annotationURI, AsyncCallback<Double> callback);

}
