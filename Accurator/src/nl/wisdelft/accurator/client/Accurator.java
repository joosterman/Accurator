package nl.wisdelft.accurator.client;

import nl.wisdelft.accurator.client.component.AdminScreen;
import nl.wisdelft.accurator.client.component.AnnotateScreen;
import nl.wisdelft.accurator.client.component.Header;
import nl.wisdelft.accurator.client.component.ProfileScreen;
import nl.wisdelft.accurator.client.component.QualityScreen;

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
		rootPanel.setSize("", "");

		verticalPanel_3 = new VerticalPanel();
		rootPanel.add(verticalPanel_3);
		verticalPanel_3.setSize("100%", "");

		header = new Header();
		verticalPanel_3.add(header);
		header.setSize("100%", "100%");

		mainContent = new SimplePanel();
		verticalPanel_3.add(mainContent);
		mainContent.setSize("", "");

		annotateScreen = new AnnotateScreen();
		mainContent.setWidget(annotateScreen);
		annotateScreen.setSize("100%", "100%");

		profileScreen = new ProfileScreen();
		profileScreen.setSize("100%", "100%");

		qualityScreen = new QualityScreen();
		qualityScreen.setSize("100%", "100%");
		
		adminScreen = new AdminScreen();
		adminScreen.setSize("100%","100%");

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
		if(token!=null && token.length()>0)
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
