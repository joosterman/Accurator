package org.sealinc.accurator.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.sealinc.accurator.client.component.AnnotateScreen;
import org.sealinc.accurator.client.component.ProfileScreen;
import org.sealinc.accurator.client.component.RecommendedItems;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
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

	private Map<String, Integer> expertise;
	private Queue<String> castlesURIs;
	private Queue<String> floraURIs;
	private Map<String, Boolean> printURIIsCastle = new HashMap<String, Boolean>();

	private Storage localStorage;
	private AnnotateScreen annotateScreen;
	private ProfileScreen profileScreen;
	private RecommendedItems recommendationScreen;
	String username = null;
	Timer renewLoginTimer = null;

	private enum State {
		Annotate, Profile, Quality, Admin, Recommendation
	};

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
		closeLogin();
		openRegister(this);
	}

	private native void loadUIThemeElements()/*-{
		$wnd.jQuery("button").button();
		$wnd.jQuery(".button").button();
	}-*/;

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		Widget w = uiBinder.createAndBindUi(this);
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

		// check if we have a user data
		localStorage = Storage.getLocalStorageIfSupported();
		String password = null;
		if (localStorage != null) {
			username = localStorage.getItem("username");
			password = localStorage.getItem("password");
		}
		if (username == null || password == null) openLogin(this);
		else {
			// loginGWT(username,password);
			login(this, username, password);
		}
	}

	public void annotate(String resourceURI) {
		if (annotateScreen == null) {
			annotateScreen = new AnnotateScreen(this, resourceURI);
		}
		else {
			annotateScreen.loadResource(resourceURI);
		}
		if (!History.getToken().equals(State.Annotate.toString())) {
			History.newItem(State.Annotate.toString());
		}
	}

	public void register(final String user, final String password, String realName) {
		final Accurator acc = this;
		Utility.adminService.register(user, password, realName, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					lblRegisterMessage.setText("");
					closeRegister();
					login(acc, user, password);
				}
				else {
					lblRegisterMessage.setText("Account kon niet aangemaakt worden. Probeer nogmaals of neem contact op met de beheerder.");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				lblRegisterMessage.setText("Account kon niet aangemaakt worden. Probeer nogmaals of neem contact op met de beheerder.");
			}
		});
	}

	public native void openRegister(Accurator acc)/*-{
		$wnd
			.jQuery("#dialog-register")
			.dialog({
				autoOpen : false,
				modal : true,
				draggable : false,
				resizable : false,
				closeOnEscape : false,
				buttons : {
					"Annuleer" : function() {
						$wnd.jQuery("#dialog-register").dialog("close");
						$wnd.jQuery("#dialog-login").dialog("open");
					},
					"Registreer" : function() {
						var user = $wnd.jQuery("#regname").val();
						var password = $wnd.jQuery("#regpassword").val();
						var realName = $wnd.jQuery("#regrealName").val();
						acc.@org.sealinc.accurator.client.Accurator::register(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(user,password,realName);
					},
				}
			});
		$wnd.jQuery("#dialog-register").dialog("open");
		$wnd.jQuery(".ui-dialog-titlebar-close").hide();
	}-*/;

	public static native void closeLogin()/*-{
		$wnd.jQuery("#dialog-login").dialog("close");
	}-*/;

	public static native void closeRegister()/*-{
		$wnd.jQuery("#dialog-register").dialog("close");
	}-*/;

	public void renewLogin() {
		localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			String username = localStorage.getItem("username");
			String password = localStorage.getItem("password");
			if (username != null && password != null) {
				login(this, username, password);
				System.out.println("Login renewed");
			}
		}
	}

	public native void openLogin(Accurator acc)/*-{
		$wnd
			.jQuery("#dialog-login")
			.dialog({
				autoOpen : false,
				modal : true,
				draggable : false,
				resizable : false,
				closeOnEscape : false,
				buttons : {
					"Log in" : function() {
						var user = $wnd.jQuery("#name").val();
						var password = $wnd.jQuery("#password").val();
						acc.@org.sealinc.accurator.client.Accurator::login(Lorg/sealinc/accurator/client/Accurator;Ljava/lang/String;Ljava/lang/String;)(acc,user,password);
					},
				}
			});
		$wnd.jQuery("#dialog-login").dialog("open");
		$wnd.jQuery(".ui-dialog-titlebar-close").hide();
	}-*/;

	private void loadExpertise() {
		Utility.userService.getExpertise(Utility.getUser(), new AsyncCallback<Map<String, Integer>>() {

			@Override
			public void onSuccess(Map<String, Integer> result) {
				expertise = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public String getUIFieldsParam(String resourceURI){
		if(printURIIsCastle.get(resourceURI)){
			return "&ui=http://semanticweb.cs.vu.nl/annotate/nicheAccuratorCastleDemoUi";
		}
		else{
			return "&ui=http://semanticweb.cs.vu.nl/annotate/nicheAccuratorFlowerDemoUi";
		}
	}

	public List<String> getNextPrintsToAnnotate(int nrPrints) {
		List<String> uris = new ArrayList<String>();
		if (expertise == null || castlesURIs == null || floraURIs == null) {
			return uris;
		}

		int castleExp = 0;
		int floraExp = 0;
		if (expertise.containsKey("castle")) castleExp = expertise.get("castle");
		if (expertise.containsKey("flora")) floraExp = expertise.get("flora");

		if (castlesURIs != null && floraURIs != null) {
			for (int i = 0; i < nrPrints; i++) {
				boolean addFlora = false;
				// random number if needed
				int r = Random.nextInt(10);
				// equal exp? determine random
				if (castleExp == floraExp) {
					if (r > 5) {
						addFlora = false;
					}
					else{
						addFlora = true;						
					}
				}
				else if (castleExp == 0 && floraExp > 0 && floraExp > 4) {
					addFlora = true;
				}
				else if (castleExp > 0 && castleExp > 4 && floraExp == 0) {
					addFlora =false;
				}
				else {
					double castle = Math.abs(castleExp - r);
					double flora = Math.abs(floraExp - r);
					if (castle > flora){
						addFlora = false;
					}
					else{
						addFlora = true;
					}
				}
				
				if(addFlora){
					printURIIsCastle.put(floraURIs.peek(),false);
					uris.add(floraURIs.poll());
					
				}
				else{
					printURIIsCastle.put(castlesURIs.peek(), true);
					uris.add(castlesURIs.poll());
				}
			}
		}
		return uris;
	}

	public void loginSuccessful(String username, String password) {
		// check if this is the first login
		// new login
		if (renewLoginTimer == null) {

			loadCurrentHistory();
			Utility.setUser(username, password);
			renewLoginTimer = new Timer() {
				@Override
				public void run() {
					renewLogin();
				}
			};
			// renew login every 4 minutes
			renewLoginTimer.scheduleRepeating(1000 * 60 * 4);
			System.out.println("First login");
			loadExpertise();
		}
	}

	public native void login(Accurator acc, String username, String password)/*-{
		loginURL = @org.sealinc.accurator.shared.Config::getLoginURL()();
		$wnd.jQuery.ajax({
			type : 'GET',
			url : loginURL,
			dataType : 'jsonp',
			data : {
				"user" : username,
				"password" : password
			},
			statusCode : {
				200 : function() {
					try {
						$wnd.jQuery("#dialog-login").dialog("close");
					} catch (err) {
					}
					acc.@org.sealinc.accurator.client.Accurator::loginSuccessful(Ljava/lang/String;Ljava/lang/String;)(username,password);
				},
			},
		});
	}-*/;

	private void LoadState(String token) {
		State state = null;
		try {
			state = State.valueOf(token);
			btnDone.setVisible(false);
			content.clear();

			switch (state) {

				case Annotate:
					btnDone.setVisible(true);
					if (annotateScreen == null) annotateScreen = new AnnotateScreen(this);
					content.add(annotateScreen);
					loadExpertise();
					break;
				case Profile:
					if (profileScreen == null) profileScreen = new ProfileScreen(this);
					content.add(profileScreen);
					profileScreen.loadUIThemeElements();
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

	private void loadCurrentHistory() {
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
