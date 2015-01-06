package org.sealinc.accurator.client;

import org.sealinc.accurator.client.Accurator.State;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
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

	private void loginSuccessful(final String username, final String password) {
		// store the credentials
		Utility.setUser(username, password);
		// get the current (URL) locale
		final String locale = Window.Location.getParameter(LocaleInfo.getLocaleQueryParam());
		// get the users language preference
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(),
				new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						String json = response.getText();
						JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(json);
						String language = null;
						if (entries.length() > 0) {
							language = entries.get(0).getValueAsString();
						}

						// if the user indicated a preference
						if (language != null && !language.isEmpty()) {
							// check if that is the same as the current language
							if (!language.equals(locale)) {
								// change to the users preference (page is reloaded)
								acc.changeLanguage(language);
								return;
							}
						}
						// either the user does not have a preference or the preference
						// matches the current locale

						// clear login status message
						acc.lblLoginMessage.setText("");

						// normal login procedure: first login
						if (renewLoginTimer == null) {
							// clear the datastore
							Utility.clearLocalStorage();
							// re-add the username/password
							Utility.setUser(username, password);

							acc.loadExpertise();
							acc.loadRecommendations();
							acc.loadFirstPrintForAnnotation();

							// renew login every 4 minutes
							renewLoginTimer = new Timer() {
								@Override
								public void run() {
									renewLogin();
								}
							};
							renewLoginTimer.scheduleRepeating(1000 * 60 * 4);
							// User is logged in, show the menu button
							DOM.getElementById("menuButton").removeClassName("hide");

							// check whether this is the first visit ever for this user
							determineLandingPage();
						}
						// needs to be refreshed every login
						acc.updateLanguageForAnnotationComponent();
					}

					@Override
					public void onError(Request request, Throwable exception) {
						// TODO Auto-generated method stub

					}
				});

	}

	protected void determineLandingPage() {
		// if this is the first time the user visits accurator show the about box
		// and the profile page
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "firstVisit", null, "Accurator", new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				if (entries.length() == 0) {
					// this is the first visit
					History.newItem(State.Profile.toString());
					acc.openAboutDialog();
				}
				else {
					acc.loadCurrentHistory();
				}
				Utility.storeUserProfileEntry(Utility.getQualifiedUsername(), "firstVisit", null, "Accurator", Boolean.FALSE.toString(), null);
			}

			@Override
			public void onError(Request request, Throwable exception) {}
		});
	}

	private void loginFailed() {
		acc.lblLoginMessage.setText(Utility.constants.loginFailed());
		Utility.deleteStoredUserCredentials();
		// TODO Handle login failed
	}

	private void resetLoginTimer() {
		if (renewLoginTimer != null) renewLoginTimer.cancel();
	}

	protected native void logout()/*-{
		var um = this;
		this.@org.sealinc.accurator.client.UserManagement::resetLoginTimer();
		logoutURL = @org.sealinc.accurator.shared.Config::logoutURL;
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
			// TODO Handle login not available in local storage
		}
	}

	protected native void login(String username, String password)/*-{
		var management = this;
		var loginURL = @org.sealinc.accurator.shared.Config::loginURL;
		$wnd.jQuery
			.ajax({
				type : 'GET',
				url : loginURL,
				dataType : 'jsonp',
				data : {
					"user" : username,
					"password" : password
				},
				timeout : 5000,
				error : function(xhr, ajaxOptions, thrownError) {
					if (xhr.status === 200) {
						try {
							$wnd.jQuery("#dialog-login").dialog("close");
						} catch (err) {
						}
						//close the login box
						$wnd.jQuery('#modalLogin').modal('hide')
						management.@org.sealinc.accurator.client.UserManagement::loginSuccessful(Ljava/lang/String;Ljava/lang/String;)(username,password);
					} else {
						//show failed!
						management.@org.sealinc.accurator.client.UserManagement::loginFailed()();
					}
				}
			});
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

	protected void register(final String user, final String password, String realName) {
		Utility.adminService.register(user, password, realName, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isSuccess) {
				if (isSuccess) {
					login(user, password);
				}
				else {
					acc.registrationFailed(Utility.constants.registrationFailed());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				acc.registrationFailedMessage(Utility.constants.registrationFailed(), caught.toString());
			}
		});
	}
}
