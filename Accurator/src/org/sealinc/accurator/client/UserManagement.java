package org.sealinc.accurator.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserManagement {
	private Accurator acc;
	Timer renewLoginTimer = null;
	private Storage localStorage;
	
	protected UserManagement(Accurator acc){
		this.acc = acc;		
	}
	

	private void loginSuccessful(String username, String password) {
		// check if this is the first login
		// new login
		acc.lblLoginMessage.setText("");
		if (renewLoginTimer == null) {
			Utility.setUser(username, password);
			acc.loadCurrentHistory();
			
			renewLoginTimer = new Timer() {
				@Override
				public void run() {
					renewLogin();
				}
			};
			// renew login every 4 minutes
			renewLoginTimer.scheduleRepeating(1000 * 60 * 4);
			System.out.println("First login");
			acc.lnkLogout.setVisible(true);
		}
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

	protected void login(){
		String user = Utility.getStoredUsername();
		String pass = Utility.getStoredPassword();
		if(user!=null && pass!=null){
			login(user,pass);
		}
		else{
			openLogin();
		}
	}
	private native void login(String username, String password)/*-{
		var management = this;
		loginURL = @org.sealinc.accurator.shared.Config::getLoginURL()();
		$wnd.jQuery.ajax({
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
