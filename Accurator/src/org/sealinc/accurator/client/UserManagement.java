package org.sealinc.accurator.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserManagement {
	private Accurator acc;
	Timer renewLoginTimer = null;
	private Storage localStorage;

	protected UserManagement(Accurator acc) {
		this.acc = acc;
	}

	private void loginSuccessful(String username, String password) {
		// store the credentials
		Utility.setUser(username, password);
		// get the current (URL) locale
		final String locale = Window.Location.getParameter(LocaleInfo.getLocaleQueryParam());
		// get the users language preference
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(json);
				String language = null;
				if (entries.length() > 0) {
					language = entries.get(0).getValueAsString();
				}

				if (language != null && !language.isEmpty()) {
					// the user has a preference
					// check if that is the same as the current language
					if (!language.equals(locale)) {
						// change to the users preference (page is reloaded)
						acc.changeLanguage(language);
						return;
					}
				}
				// either the user does not have a preference or the preference matches
				// the current locale
				// normal login procedure: either a renewed login or a first login

				// clear login status message
				acc.lblLoginMessage.setText("");
				if (renewLoginTimer == null) {
					// first login
					acc.setPredefinedOrderPrints();
					acc.loadFirstPrintForAnnotation();					
					acc.loadCurrentHistory();
					
					// renew login every 4 minutes
					renewLoginTimer = new Timer() {
						@Override
						public void run() {
							renewLogin();
						}
					};

					renewLoginTimer.scheduleRepeating(1000 * 60 * 4);
					// now we are logged in show the logout button
					acc.lnkLogout.setVisible(true);
				}
				// needs to be refreshed every new session/login
				acc.updateLanguageForAnnotationComponent();

			}

			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub

			}
		};
		// Execute the call
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(), callback);
	}

	private void loginFailed() {
		acc.lblLoginMessage.setText(Utility.constants.loginFailed());
		Utility.deleteStoredUserCredentials();
		openLogin();
	}

	protected native void logout()/*-{
		logoutURL = @org.sealinc.accurator.shared.Config::getLogoutURL();
		$wnd.jQuery.ajax({
			type : 'GET',
			url : logoutURL,
			dataType : 'jsonp',
			timeout : 1000,
			error : function(xhr, ajaxOptions, thrownError) {
				if (xhr.status === 200) {

				} else {

				}
			}
		});
	}-*/;

	protected void login() {
		String user = Utility.getStoredUsername();
		String pass = Utility.getStoredPassword();
		if (user != null && pass != null) {
			login(user, pass);
		}
		else {
			openLogin();
		}
	}

	private native void login(String username, String password)/*-{
		var management = this;
		var loginURL = @org.sealinc.accurator.shared.Config::getLoginURL()();
		//this works for all browser execpt IE
		$wnd.jQuery
			.ajax({
				type : 'GET',
				url : loginURL,
				dataType : 'jsonp',
				data : {
					"user" : username,
					"password" : password
				},
				timeout : 10000,
				error : function(xhr, ajaxOptions, thrownError) {
					if (xhr.status === 200) {
						try {
							$wnd.jQuery("#dialog-login").dialog("close");
						} catch (err) {
						}
						management.@org.sealinc.accurator.client.UserManagement::loginSuccessful(Ljava/lang/String;Ljava/lang/String;)(username,password);
					} else {
						//show failed!
						management.@org.sealinc.accurator.client.UserManagement::loginFailed()();
					}
				}
			});
	}-*/;

	protected native void closeLogin()/*-{
		$wnd.jQuery("#dialog-login").dialog("close");
	}-*/;

	private void renewLogin() {
		localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			String username = localStorage.getItem("username");
			String password = localStorage.getItem("password");
			if (username != null && password != null) {
				login(username, password);
				System.out.println("Login renewed");
			}
		}
	}

	private native void openLogin()/*-{
		var management = this;
		var logIn = @org.sealinc.accurator.client.Utility::getLocalString(Ljava/lang/String;)("logIn");
		var localButtons = {};
		localButtons[logIn] = function() {
			var user = $wnd.jQuery("#name").val();
			var password = $wnd.jQuery("#password").val();
			management.@org.sealinc.accurator.client.UserManagement::login(Ljava/lang/String;Ljava/lang/String;)(user,password);
		};
		$wnd.jQuery("#dialog-login").dialog({
			autoOpen : false,
			modal : true,
			draggable : false,
			resizable : false,
			closeOnEscape : false,
			buttons : localButtons,
		});
		$wnd.jQuery("#dialog-login").dialog("open");
		$wnd.jQuery(".ui-dialog-titlebar-close").hide();
	}-*/;

	private void register(final String user, final String password, String realName) {
		Utility.adminService.register(user, password, realName, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isSuccess) {
				if (isSuccess) {
					setRegistrationFailed(false);
					closeRegister();
					login(user, password);
				}
				else {
					setRegistrationFailed(true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				acc.lblRegisterMessage.setText("Account kon niet aangemaakt worden. Probeer nogmaals of neem contact op met de beheerder.");
			}
		});
	}

	protected native void openRegister()/*-{
		var management = this;
		var cancel = @org.sealinc.accurator.client.Utility::getLocalString(Ljava/lang/String;)("cancel");
		var register = @org.sealinc.accurator.client.Utility::getLocalString(Ljava/lang/String;)("register");

		var localButtons = {};
		localButtons[cancel] = function() {
			$wnd.jQuery("#dialog-register").dialog("close");
			$wnd.jQuery("#dialog-login").dialog("open");
		};
		localButtons[register] = function() {
			var user = $wnd.jQuery("#regname").val();
			var password = $wnd.jQuery("#regpassword").val();
			var realName = $wnd.jQuery("#regrealName").val();
			management.@org.sealinc.accurator.client.UserManagement::register(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(user,password,realName);
		};

		$wnd.jQuery("#dialog-register").dialog({
			autoOpen : false,
			modal : true,
			draggable : false,
			resizable : false,
			closeOnEscape : false,
			buttons : localButtons,
		});
		$wnd.jQuery("#dialog-register").dialog("open");
		$wnd.jQuery(".ui-dialog-titlebar-close").hide();
	}-*/;

	private native void closeRegister()/*-{
		$wnd.jQuery("#dialog-register").dialog("close");
	}-*/;

	private void setRegistrationFailed(boolean failed) {
		if (failed) acc.lblRegisterMessage.setText(Utility.constants.registrationFailed());
		else acc.lblRegisterMessage.setText("");
	}
}
