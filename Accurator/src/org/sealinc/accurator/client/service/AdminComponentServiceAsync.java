package org.sealinc.accurator.client.service;

import java.util.Map;
import org.sealinc.accurator.shared.ConfigurationSetting;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface AdminComponentServiceAsync {

	void register(String user, String password, String realName, AsyncCallback<Boolean> callback);

	void getJSON(String url, AsyncCallback<String> callback);

	void getConfiguration(String version, AsyncCallback<Map<String, ConfigurationSetting>> callback);

	
}
