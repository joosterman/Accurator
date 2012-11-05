package org.sealinc.accurator.client.component;

import java.util.Date;
import org.sealinc.accurator.client.Accurator;
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
	Accurator accurator;
	public String resourceURI; 

	public void loadResource(final String resourceURI, String url) {
		// store that the user has viewed the resource
		View view = new View();
		view.date = new Date();
		view.fromRecommendation = false;
		view.viewer = Config.getUserComponentUserURI() + Utility.getStoredUsername();
		Utility.userService.setViewed(resourceURI, view, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				System.out.println("Store View result: " + result);
			}

			@Override
			public void onFailure(Throwable caught) {}
		});
		// set the url of the frame to the correct url
		annotationFrame.setUrl(url);
		this.resourceURI = resourceURI;
	}

	@UiFactory
	NamedFrame makeNamedFrame() {
		return new NamedFrame(annotationFrameName);
	}

	public void setLanguage(String language) {
		String urlToHit = Config.getAnnotationComponentChangePreferenceURL() + "?lang=" + language;
		Utility.hitURLWithJsonp(urlToHit);
	}

	public AnnotateScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
	}
}