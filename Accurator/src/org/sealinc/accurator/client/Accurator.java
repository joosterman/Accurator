package org.sealinc.accurator.client;

import org.sealinc.accurator.client.component.AnnotateScreen;
import org.sealinc.accurator.client.component.ProfileScreen;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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

	private Storage localStorage;
	private AnnotateScreen annotateScreen;
	private ProfileScreen profileScreen;
	String username = null;

	private enum State {
		Annotate, Profile, Quality, Admin
	};

	@UiHandler("lnkRegister")
	void clickHandler(ClickEvent e) {
		closeLogin();
		openRegister(this);
	}

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		Widget w = uiBinder.createAndBindUi(this);
		rootPanel.add(w);
		initHistorySupport();

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

	public void register(final String user, final String password, String realName) {
		final Accurator acc = this;
		Utility.adminService.register(user, password, realName, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (result == 200) {
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
					"Annuleer": function(){
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
					$wnd.jQuery("#dialog-login").dialog("close");
					@org.sealinc.accurator.client.Utility::setUser(Ljava/lang/String;Ljava/lang/String;)(username,password);
					acc.@org.sealinc.accurator.client.Accurator::loadCurrentHistory()();
				}
			},
		});
	}-*/;

	private void LoadState(String token) {
		State state = null;
		try {
			state = State.valueOf(token);
			content.clear();
			switch (state) {
				case Annotate:
					if (annotateScreen == null) annotateScreen = new AnnotateScreen();
					content.add(annotateScreen);
					break;
				case Profile:
					if (profileScreen == null) profileScreen = new ProfileScreen();
					content.add(profileScreen);
					break;
				case Quality:

					break;
				case Admin:
					break;
			}
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
			LoadState("Annotate");
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
