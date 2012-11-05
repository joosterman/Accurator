package org.sealinc.accurator.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
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

	private List<HandlerRegistration> regs = new ArrayList<HandlerRegistration>();
	private Map<String, Double> expertise;
	private LinkedList<JsRecommendedItem> recommendedItems;

	private LinkedList<String> predefinedCastleURIs = new LinkedList<String>();
	private LinkedList<String> predefinedFloraURIs = new LinkedList<String>();

	private AnnotateScreen annotateScreen;
	private ProfileScreen profileScreen;
	private RecommendedItems recommendationScreen;
	private UserManagement management;
	private int nrInitialPrints = 50;
	private boolean hasPredefinedAnnotationOrder = false;

	private AnnotateScreen getAnnotateScreen() {
		if (annotateScreen == null) annotateScreen = new AnnotateScreen(this);
		return annotateScreen;
	}

	private ProfileScreen getProfileScreen() {
		if (profileScreen == null) profileScreen = new ProfileScreen(this);
		return profileScreen;
	}

	private RecommendedItems getRecommendationScreen() {
		if (recommendationScreen == null) recommendationScreen = new RecommendedItems(this);
		return recommendationScreen;
	}

	private UserManagement getManagement() {
		if (management == null) management = new UserManagement(this);
		return management;
	}

	public enum State {
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
		getManagement().logout();
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
		// remove the item from the to predefined order
		String uri = annotateScreen.resourceURI;
		predefinedCastleURIs.remove(uri);
		predefinedFloraURIs.remove(uri);
		// load recommendation
		History.newItem(State.Recommendation.toString());
	}

	@UiHandler("lnkRegister")
	void registerClickHandler(ClickEvent e) {
		getManagement().closeLogin();
		getManagement().openRegister();
	}

	private native void loadUIThemeElements()/*-{
		$wnd.jQuery("button").button();
		$wnd.jQuery(".button").button();
	}-*/;

	public void updateLanguageForAnnotationComponent() {
		RequestCallback callback = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				if (entries.length() > 0) {
					String language = entries.get(0).getValueAsString();
					getAnnotateScreen().setLanguage(language);
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {}
		};
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(), callback);
	}

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		Widget w = uiBinder.createAndBindUi(this);
		lnkLogout.setVisible(false);
		btnDone.setVisible(false);

		rootPanel.add(w);
		loadUIThemeElements();
		initHistorySupport();
		
		getManagement().login();
	}

	protected void loadRecommendations() {
		String url = Config.getAssignComponentURL()+"?"+"strategy="+Config.getAssignStrategy()+"&nritems="+Config.getAssignComponentNrItems()+"&user="+Utility.getQualifiedUsername();
		Utility.adminService.getJSON(url, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String json) {
				recommendedItems = new LinkedList<JsRecommendedItem>();
				JsArray<JsRecommendedItem> recs = parseRecommendations(json);
				for(int i=0;i<recs.length();i++){
					recommendedItems.add(recs.get(i));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.err.println(caught.toString());				
			}
		});
	}

	/**
	 * Reloads the complete page with the new language. All GWT state is lost.
	 * 
	 * @param language
	 */
	public void changeLanguage(String language) {
		// store the new preference
		Utility.storeUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(), language,
				"string");
		// load Accurator with the new locale
		String newURL = Window.Location.createUrlBuilder().setParameter(LocaleInfo.getLocaleQueryParam(), language).buildString();
		Window.Location.replace(newURL);
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
		getAnnotateScreen().loadResource(resourceURI, url);
	}

	protected void setPredefinedOrderPrints() {
		String user = Utility.getStoredUsername();
		hasPredefinedAnnotationOrder = true;
		String[] castles;
		String[] flowers;
		if ("user1".equals(user)) {
			castles = new String[] { "http://purl.org/collections/nl/rma/collection/r-342588",
					"http://purl.org/collections/nl/rma/collection/r-348117" };
			predefinedCastleURIs.addAll(Arrays.asList(castles));
			flowers = new String[] { "http://purl.org/collections/nl/rma/collection/r-122307",
					"http://purl.org/collections/nl/rma/collection/r-132364" };
			predefinedFloraURIs.addAll(Arrays.asList(flowers));
		}
		else if ("user2".equals(user)) {}
		else if ("user3".equals(user)) {

		}
		else if ("user4".equals(user)) {

		}
		else {
			hasPredefinedAnnotationOrder = false;
		}
	}

	public void userPropertyChanged(String dimension) {
		if ("expertise".equals(dimension)) {
			loadRecommendations();
			getRecommendationScreen().loadNextRecommendations();
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

	private final native JsArray<JsRecommendedItem> parseRecommendations(String json) /*-{
		return eval(json);
	}-*/;

	public void updateExpertise(String topic, double value) {
		if (expertise != null) expertise.put(topic, value);
	}

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
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "expertise", null, null, callback);
	}

	protected void loadFirstPrintForAnnotation() {
		// if we did not yet load the flowers and castles, wait and try again
		if (recommendedItems==null || expertise == null) {
			Timer t = new Timer() {
				@Override
				public void run() {
					loadFirstPrintForAnnotation();
				}
			};
			t.schedule(400);
		}
		else {
			// we have the prints and expertise
			List<String> uris = getNextPrintsToAnnotate(1);
			if (uris.size() > 0) {
				String uri = uris.get(0);
				// manually add the first print seen the to the seen list in the
				// recommendation screen
				getRecommendationScreen().addFirstSeenPrint(uri);
				annotate(uri);
			}
			else System.err.println("Could not annotate next print. No print available. Waiting and trying again...");
		}

	}

	/**
	 * Assumes that all data has been loaded (prints and expertise). If not it
	 * return an empty list
	 * 
	 * @param nrPrints
	 * @return
	 */
	public List<String> getNextPrintsToAnnotate(int nrPrints) {
		List<String> uris = new ArrayList<String>();
		if (expertise == null) {
			return uris;
		}
		// either a static order based on the person or based on UP and
		// recommendation
		if (hasPredefinedAnnotationOrder) {
			double castleExp = 0;
			double floraExp = 0;
			if (expertise.containsKey("castle")) castleExp = expertise.get("castle");
			if (expertise.containsKey("flora")) floraExp = expertise.get("flora");
			for (int i = 0; i < nrPrints; i++) {
				if (castleExp > 0.9 && predefinedCastleURIs.size() > i) uris.add(predefinedCastleURIs.get(i));
				else if (floraExp > 0.9 && predefinedFloraURIs.size() > i) uris.add(predefinedFloraURIs.get(i));
				else {
					// uris.add(recommendedItems.removeFirst().getURI());
				}
			}
		}
		else {
			if (recommendedItems == null) {
				return uris;
			}
			for (int i = 0; i < nrPrints; i++) {
				JsRecommendedItem rec = recommendedItems.removeFirst();
				Utility.setResourceTopic(rec.getURI(), rec.getScope());
				uris.add(rec.getURI());
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
					content.add(getAnnotateScreen());
					break;
				case Profile:
					content.add(getProfileScreen());
					getProfileScreen().loadUIThemeElements();
					getProfileScreen().loadData();
					break;
				case Quality:
					break;
				case Admin:
					break;
				case Recommendation:
					content.add(getRecommendationScreen());
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
		if (token == null || token.isEmpty()) {
			token = State.Annotate.toString();
		}
		LoadState(token);
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
