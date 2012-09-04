package org.sealinc.accurator.client;

import org.sealinc.accurator.client.component.AdminScreen;
import org.sealinc.accurator.client.component.AnnotateScreen;
import org.sealinc.accurator.client.component.Header;
import org.sealinc.accurator.client.component.ProfileScreen;
import org.sealinc.accurator.client.component.QualityScreen;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
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
	private Storage localStorage;
	String username = null;

	private enum State {
		Annotate, Profile, Quality, Admin
	};

	public void loadApplication() {
		RootPanel rootPanel = RootPanel.get("accuratorMain");
		verticalPanel_3 = new VerticalPanel();
		verticalPanel_3.setStyleName("page");
		rootPanel.add(verticalPanel_3);
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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// check if we have a user data
		localStorage = Storage.getLocalStorageIfSupported();
		String password = null;
		if (localStorage != null) {
			username = localStorage.getItem("username");
			password = localStorage.getItem("password");
		}
		if (username == null || password == null) openLogin(this);
		else {
			login(this, username, password);
		}
	}
	
	public native void openLogin(Accurator acc)/*-{
		$wnd
			.jQuery("#dialog-form")
			.dialog({
				autoOpen : false,
				height : 300,
				width : 350,
				modal : true,
				draggable : false,
				resizable : false,
				closeOnEscape : false,
				buttons : {
					"Log in" : function() {
						user = $wnd.jQuery("#name").val();
						password = $wnd.jQuery("#password").val();
						$wnd.jQuery(".validateTips").text("We checken je gegevens...");
						acc.@org.sealinc.accurator.client.Accurator::login(Lorg/sealinc/accurator/client/Accurator;Ljava/lang/String;Ljava/lang/String;)(acc,user,password);
					},
				}
			});
		$wnd.jQuery("#dialog-form").dialog("open");
		$wnd.jQuery(".ui-dialog-titlebar-close").hide();
	}-*/;

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
					$wnd.jQuery("#dialog-form").dialog("close");
					@org.sealinc.accurator.client.Utility::setUser(Ljava/lang/String;Ljava/lang/String;)(username,password);
					acc.@org.sealinc.accurator.client.Accurator::loadApplication()();
				}
			},
		});
	}-*/;

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
		if (token != null && token.length() > 0) LoadState(token);

		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				LoadState(token);
			}
		});
	}
}
