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
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class Utility {
	public static AssignComponentServiceAsync assignService = (AssignComponentServiceAsync) GWT.create(AssignComponentService.class);
	public static UserComponentServiceAsync userService = (UserComponentServiceAsync) GWT.create(UserComponentService.class);
	public static ItemComponentServiceAsync itemService = (ItemComponentServiceAsync) GWT.create(ItemComponentService.class);
	public static QualityComponentServiceAsync qualityService = (QualityComponentServiceAsync) GWT.create(QualityComponentService.class);
	public static AdminComponentServiceAsync adminService = (AdminComponentServiceAsync) GWT.create(AdminComponentService.class);

	private Utility() {}

	public static boolean setUser(String username, String password) {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			localStorage.setItem("username", username);
			localStorage.setItem("password", password);
			return true;
		}
		else {
			return false;
		}
	}

	public static String getUser() {
		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			return localStorage.getItem("username");
		}
		else {
			return null;
		}
	}
}
