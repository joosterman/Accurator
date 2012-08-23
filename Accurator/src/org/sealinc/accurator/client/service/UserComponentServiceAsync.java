package org.sealinc.accurator.client.service;

import java.util.List;

import org.sealinc.accurator.shared.Annotation;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserComponentServiceAsync {

	void getAnnotations(String user, AsyncCallback<List<Annotation>> callback);

}
