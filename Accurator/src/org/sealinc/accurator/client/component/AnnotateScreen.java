package org.sealinc.accurator.client.component;

import java.util.Date;
import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.View;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.Widget;

public class AnnotateScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, AnnotateScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final String annotationFrameName = "annotationFrame";

	@UiField
	NamedFrame annotationFrame;

	public void loadResource(String resourceURI){
		String url = Config.getAnnotationComponentURL() +"?target="+resourceURI;
		annotationFrame.setUrl(url);
	  // store that the user has viewed the resource
		View view = new View();
		view.date = new Date();
		view.fromRecommendation = false;
		view.viewer = Config.getUserComponentUserURI() + Utility.getUser();
		Utility.userService.setViewed(resourceURI, view, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				System.out.println("Store View result: " + result);
			}

			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void setNextItemToAnnotate() {
		Utility.assignService.getNextItemsToAnnotate(1, new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				if (result != null && result.size() > 0) {
					//set the frame to the new annotation
					loadResource(result.get(0));					
				}
			}

			@Override
			public void onFailure(Throwable caught) {}
		});

	}
	
	@UiFactory NamedFrame makeNamedFrame(){
		return new NamedFrame(annotationFrameName);
	}

	public AnnotateScreen() {
		initWidget(uiBinder.createAndBindUi(this));
		setNextItemToAnnotate();
	}
	
	public AnnotateScreen(String resourceURI){
		initWidget(uiBinder.createAndBindUi(this));
		loadResource(resourceURI);
		
	}

}
