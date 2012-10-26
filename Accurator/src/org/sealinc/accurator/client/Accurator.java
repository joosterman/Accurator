package org.sealinc.accurator.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.sealinc.accurator.client.component.AnnotateScreen;
import org.sealinc.accurator.client.component.ProfileScreen;
import org.sealinc.accurator.client.component.RecommendedItems;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Accurator implements EntryPoint {
	interface MyUiBinder extends UiBinder<Widget, Accurator> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Panel content;
	@UiField
	Anchor lnkRegister;
	@UiField
	Label lblLoginMessage, lblRegisterMessage;
	@UiField
	Button btnDone, btnAnnotate, btnProfile;
	@UiField
	Anchor lnkLogout, lnkAboutAccurator;

	private Map<String, Double> expertise;
	private Queue<String> castlesURIs;
	private Queue<String> floraURIs;

	private AnnotateScreen annotateScreen;
	private ProfileScreen profileScreen;
	private RecommendedItems recommendationScreen;
	private UserManagement management = new UserManagement(this);

	private enum State {
		Annotate, Profile, Quality, Admin, Recommendation
	};

	@UiHandler("lnkAboutAccurator")
	void lnkAboutClick(ClickEvent e) {
		openAboutDialog();
	}

	private native void openAboutDialog() /*-{
		$wnd.jQuery("#dialog-about").dialog({
			autoOpen : false,
			modal : true,
			draggable : false,
			resizable : false,
		});
		$wnd.jQuery("#dialog-about").dialog("open");
	}-*/;

	@UiHandler("lnkLogout")
	void lnkLogoutClick(ClickEvent e) {
		// delete stored credentials
		Utility.deleteStoredUserCredentials();
		// logout the annotation component
		management.logout();
		// refresh the page to show the loginpage again and reset GWT state.
		Window.Location.reload();
	}

	@UiHandler("btnAnnotate")
	void btnAnnotateClick(ClickEvent e) {
		History.newItem(State.Annotate.toString());
	}

	@UiHandler("btnProfile")
	void btnProfileClick(ClickEvent e) {
		History.newItem(State.Profile.toString());
	}

	@UiHandler("btnDone")
	void btnDoneClick(ClickEvent e) {
		// load as page
		History.newItem(State.Recommendation.toString());
	}

	@UiHandler("lnkRegister")
	void registerClickHandler(ClickEvent e) {
		management.closeLogin();
		management.openRegister();
	}

	private native void loadUIThemeElements()/*-{
		$wnd.jQuery("button").button();
		$wnd.jQuery(".button").button();
	}-*/;

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		Widget w = uiBinder.createAndBindUi(this);
		lnkLogout.setVisible(false);
		btnDone.setVisible(false);

		rootPanel.add(w);

		loadUIThemeElements();
		initHistorySupport();
		// load prints
		Utility.assignService.getNextItemsToAnnotate(100, "kasteel", new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				castlesURIs = new java.util.PriorityQueue<String>();
				castlesURIs.addAll(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
		Utility.assignService.getNextItemsToAnnotate(100, "bloem", new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				floraURIs = new java.util.PriorityQueue<String>();
				floraURIs.addAll(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

		management.login();
	}

	private void annotate(String resourceURI, String topic) {
		// extra stylesheet for annotation component
		String stylesheet = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/css/jacconator.css";
		String url = Config.getAnnotationComponentURL() + "?target=" + resourceURI + "&stylesheet=" + stylesheet;
		String ui = "";
		if ("flora".equals(topic)) {
			ui = "&ui=http://semanticweb.cs.vu.nl/annotate/nicheAccuratorFlowerDemoUi";
		}
		else if ("castle".equals(topic)) {
			ui = "&ui=http://semanticweb.cs.vu.nl/annotate/nicheAccuratorCastleDemoUi";
		}
		// complete url for the iframe containing the annotation component
		url += ui;
		// create or load the frame with the
		if (annotateScreen == null) {
			annotateScreen = new AnnotateScreen(this, resourceURI, url);
		}
		else {
			annotateScreen.loadResource(resourceURI, url);
		}
		// show the annotation page
		if (!History.getToken().equals(State.Annotate.toString())) {
			History.newItem(State.Annotate.toString());
		}
	}

	public void annotate(final String resourceURI) {
		// determine the topic of the resource
		String topic = Utility.getResourceTopic(resourceURI);
		// if not known, get it and store it
		if (topic == null) {
			Utility.itemService.getTopic(resourceURI, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String topic) {
					Utility.setResourceTopic(resourceURI, topic);
					annotate(resourceURI, topic);
				}

				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		else {
			annotate(resourceURI, topic);
		}
	}

	private final native JsArray<JsUserProfileEntry> parseExpertise(String json) /*-{
		return eval(json);
	}-*/;

	protected void loadExpertise() {
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				JsArray<JsUserProfileEntry> entries = parseExpertise(json);
				expertise = new HashMap<String, Double>();
				for (int i = 0; i < entries.length(); i++) {
					JsUserProfileEntry entry = entries.get(i);
					double value = entry.getValueAsDouble();
					expertise.put(entry.getScope(), value);
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {}
		};
		// get all expertises
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "expertise",null, null, callback);
	}

	public List<String> getNextPrintsToAnnotate(int nrPrints) {
		List<String> uris = new ArrayList<String>();
		if (expertise == null || castlesURIs == null || floraURIs == null) {
			return uris;
		}

		double castleExp = 0;
		double floraExp = 0;
		if (expertise.containsKey("castle")) castleExp = expertise.get("castle");
		if (expertise.containsKey("flora")) floraExp = expertise.get("flora");
		// scale the expertises to sum to 1
		double upperbound = castleExp + floraExp;
		if (upperbound > 0.1) {
			castleExp /= upperbound;
			floraExp /= upperbound;
		}
		if (castlesURIs != null && floraURIs != null) {
			for (int i = 0; i < nrPrints; i++) {
				boolean addFlora = false;
				// random number if needed
				double r = Random.nextDouble();
				if (r < castleExp) addFlora = false;
				else addFlora = true;

				if (addFlora) {
					uris.add(floraURIs.poll());

				}
				else {
					uris.add(castlesURIs.poll());
				}
			}
		}
		return uris;
	}

	private void LoadState(String token) {
		State state = null;
		try {
			try {
				state = State.valueOf(token);
			}
			catch (IllegalArgumentException ex) {
				state = State.Annotate;
			}
			btnDone.setVisible(false);
			content.clear();

			switch (state) {

				case Annotate:
					btnDone.setVisible(true);
					loadExpertise();
					if (annotateScreen == null) annotateScreen = new AnnotateScreen(this);
					content.add(annotateScreen);
					break;
				case Profile:
					if (profileScreen == null) profileScreen = new ProfileScreen(this);
					content.add(profileScreen);
					profileScreen.loadUIThemeElements();
					profileScreen.loadData();
					break;
				case Quality:
					break;
				case Admin:
					break;
				case Recommendation:
					if (recommendationScreen == null) recommendationScreen = new RecommendedItems(this);
					content.add(recommendationScreen);

					break;
			}
			loadUIThemeElements();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			// state not parsable
		}
	}

	protected void loadCurrentHistory() {
		String token = History.getToken();
		if (token != null && token.length() > 0) {
			LoadState(token);
		}
		else {
			LoadState(State.Annotate.toString());
		}
	}

	private void initHistorySupport() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				LoadState(token);
			}
		});

	}
}
