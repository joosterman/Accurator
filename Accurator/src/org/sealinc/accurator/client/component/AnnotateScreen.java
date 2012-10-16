package org.sealinc.accurator.client.component;

import java.util.Date;
import java.util.List;
import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.View;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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

	public void loadResource(final String resourceURI) {
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
		// set the url of the frame to the correct url
		String stylesheet = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/css/jacconator.css";
		String ui = accurator.getUIFieldsParam(resourceURI);
		String url = Config.getAnnotationComponentURL() + "?target=" + resourceURI + "&stylesheet=" + stylesheet + ui;
		annotationFrame.setUrl(url);
	}

	private void setNextItemToAnnotate() {
		List<String> uri = accurator.getNextPrintsToAnnotate(1);
		if (uri.size() > 0) {
			loadResource(uri.get(0));
		}
		else {
			Timer t = new Timer() {
				@Override
				public void run() {
						setNextItemToAnnotate();
				}
			};
			t.schedule(200);
		}
	}

	@UiFactory
	NamedFrame makeNamedFrame() {
		return new NamedFrame(annotationFrameName);
	}

	public AnnotateScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
		setNextItemToAnnotate();
	}

	public AnnotateScreen(Accurator acc, String resourceURI) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
		loadResource(resourceURI);

	}

}
