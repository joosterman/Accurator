package org.sealinc.accurator.client;

import org.sealinc.accurator.client.component.AdminScreen;
import org.sealinc.accurator.client.component.AnnotateScreen;
import org.sealinc.accurator.client.component.Header;
import org.sealinc.accurator.client.component.ProfileScreen;
import org.sealinc.accurator.client.component.QualityScreen;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Accurator implements EntryPoint {
	private Header header;
	private VerticalPanel verticalPanel_3;
	private SimplePanel mainContent;
	private AnnotateScreen annotateScreen;
	private ProfileScreen profileScreen;
	private QualityScreen qualityScreen;
	private AdminScreen adminScreen;

	private enum State {
		Annotate, Profile, Quality, Admin
	};

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
	
		verticalPanel_3 = new VerticalPanel();
		verticalPanel_3.setStyleName("page");
		rootPanel.add(verticalPanel_3);

		header = new Header();
		header.setStyleName("header");
		verticalPanel_3.add(header);

		mainContent = new SimplePanel();
		mainContent.setStyleName("content");
		verticalPanel_3.add(mainContent);

		annotateScreen = new AnnotateScreen();
		mainContent.setWidget(annotateScreen);

		profileScreen = new ProfileScreen();

		qualityScreen = new QualityScreen();

		adminScreen = new AdminScreen();

		initHistorySupport();
	}

	private void LoadState(String token) {
		State state = null;
		try {
			state = State.valueOf(token);
			switch (state) {
			case Annotate:
				mainContent.setWidget(annotateScreen);
				break;
			case Profile:
				mainContent.setWidget(profileScreen);
				break;
			case Quality:
				mainContent.setWidget(qualityScreen);
				break;

			case Admin:
				mainContent.setWidget(adminScreen);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			// state not parsable
		}
	}

	private void initHistorySupport() {
		String token = History.getToken();
		if (token != null && token.length() > 0)
			LoadState(token);

		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				LoadState(token);
			}
		});
	}
}
