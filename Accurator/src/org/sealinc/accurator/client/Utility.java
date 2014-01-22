package org.sealinc.accurator.client;

import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.client.service.AdminComponentServiceAsync;
import org.sealinc.accurator.client.service.AssignComponentService;
import org.sealinc.accurator.client.service.AssignComponentServiceAsync;
import org.sealinc.accurator.client.service.ItemComponentService;
import org.sealinc.accurator.client.service.ItemComponentServiceAsync;
import org.sealinc.accurator.client.service.QualityComponentService;
import org.sealinc.accurator.client.service.QualityComponentServiceAsync;
import org.sealinc.accurator.client.service.UserComponentService;
import org.sealinc.accurator.client.service.UserComponentServiceAsync;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.AccuratorConstants;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;

public class Utility {
	public static AssignComponentServiceAsync assignService = (AssignComponentServiceAsync) GWT.create(AssignComponentService.class);
	public static UserComponentServiceAsync userService = (UserComponentServiceAsync) GWT.create(UserComponentService.class);
	public static ItemComponentServiceAsync itemService = (ItemComponentServiceAsync) GWT.create(ItemComponentService.class);
	public static QualityComponentServiceAsync qualityService = (QualityComponentServiceAsync) GWT.create(QualityComponentService.class);
	public static AdminComponentServiceAsync adminService = (AdminComponentServiceAsync) GWT.create(AdminComponentService.class);
	public static AccuratorConstants constants = GWT.create(AccuratorConstants.class);

	private static final String username = "username";
	private static final String password = "password";

	public static final RequestCallback noopCallback = new RequestCallback() {

		@Override
		public void onResponseReceived(Request request, Response response) {}

		@Override
		public void onError(Request request, Throwable exception) {
			System.err.println("Noop callback encountered error.");
			exception.printStackTrace();
		}
	};

	private Utility() {}
	
	public static native void hitURLWithJsonp(String urlToHit)/*-{
		$wnd.jQuery.ajax({
			type : 'GET',
			url : urlToHit,
			dataType : 'jsonp',
		});
	}-*/;

	public static String getLocalString(String toTranslate) {
		return constants.getString(toTranslate);
	}

	public static boolean setUser(String user, String pass) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			localStorage.setItem(username, user);
			localStorage.setItem(password, pass);
			return true;
		}
		else {
			return false;
		}
	}

	public static String getQualifiedUsername() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			return Config.userComponentUserURI + localStorage.getItem(username);
		}
		else {
			return null;
		}
	}

	public static String getStoredUsername() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			return localStorage.getItem(username);
		}
		else {
			return null;
		}
	}

	public static String getStoredPassword() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			return localStorage.getItem(password);
		}
		else {
			return null;
		}
	}

	public static void deleteStoredUserCredentials() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			localStorage.removeItem(username);
			localStorage.removeItem(password);
		}
	}

	public static void deleteUserProfileEntry(String user,String dimension, String scope,String provider){
		String loc = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/accurator/userprofile?";
		String data = "user=" + user;
		if (dimension != null) data += "&dimension=" + dimension;
		if (scope != null) data += "&scope=" + scope;
		if (provider != null) data += "&provider=" + provider;		

		RequestBuilder rb = new RequestBuilder(RequestBuilder.DELETE, loc + data);
		rb.setRequestData("");
		rb.setCallback(noopCallback);
		try {
			rb.send();
		}
		catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	public static void storeUserProfileEntry(String user, String dimension, String scope, String provider, String value, String valueType) {
		// value cannot be null
		if (value == null) return;

		String loc = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/accurator/userprofile?";
		String data = "user=" + user;
		if (dimension != null) data += "&dimension=" + dimension;
		if (scope != null) data += "&scope=" + scope;
		if (provider != null) data += "&provider=" + provider;
		if (valueType != null) data += "&valueType=" + valueType;
		// always add value
		data += "&value=" + value;

		RequestBuilder rb = new RequestBuilder(RequestBuilder.PUT, loc + data);
		rb.setRequestData("");
		rb.setCallback(noopCallback);
		try {
			rb.send();
		}
		catch (RequestException e) {
			e.printStackTrace();
		}
	}

	public static void getUserProfileEntry(String user, String dimension, String scope, String provider, RequestCallback callback) {
		String loc = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/accurator/userprofile?";
		String data = "user=" + user;
		if (dimension != null) data += "&dimension=" + dimension;
		if (scope != null) data += "&scope=" + scope;
		if (provider != null) {
			data += "&provider=" + provider;
		}

		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, loc + data);
		rb.setRequestData("");
		rb.setCallback(callback);
		try {
			rb.send();
		}
		catch (RequestException e) {
			e.printStackTrace();
		}
	}

	public static native final JsArray<JsUserProfileEntry> parseUserProfileEntry(String json)/*-{
		return eval(json);
	}-*/;

	public static String getResourceTopic(String resourceURI) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			return localStorage.getItem(resourceURI + "_topic");
		}
		else {
			return null;
		}
	}

	public static void setResourceTopic(String resourceURI, String topic) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null && topic != null) {
			localStorage.setItem(resourceURI + "_topic", topic);
		}
	}

	public static void clearLocalStorage() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			localStorage.clear();
		}
	}
}
