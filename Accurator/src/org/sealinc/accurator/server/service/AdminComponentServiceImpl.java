package org.sealinc.accurator.server.service;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.Configuration;
import org.sealinc.accurator.shared.ConfigurationSetting;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Ref;

public class AdminComponentServiceImpl extends RemoteServiceServlet implements AdminComponentService {

	private static final long serialVersionUID = 814007834668767239L;

	@Override
	public boolean register(String user, String password, String realName) {
		boolean success = Utility.register(user, realName, password);
		return success;
	}

	@Override
	public String getJSON(String url) {
		URL u;
		try {
			u = new URL(url);
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(u.getQuery()==null)
			url+= "?";
		else
			url+="&";
		url+="nocache="+new Random().nextInt();
			
		return Utility.getHTMLContent(url);
	}

	@Override
	public Map<String, ConfigurationSetting> getConfiguration(String version) {
		Map<String, ConfigurationSetting> settings = new HashMap<String, ConfigurationSetting>();
		// identified this application
		String applicationId = SystemProperty.applicationId.get();
		// get the Configuration if it exists
		Ref<Configuration> refConfig = ofy().load().type(Configuration.class).filter("version", version).filter("applicationId", applicationId).first();
		Configuration config = refConfig.getValue();

		// If there is a config
		if (config != null) {
			// get all the settings for this configuration
			List<ConfigurationSetting> listSettings = ofy().load().type(ConfigurationSetting.class).ancestor(config).list();
			for (ConfigurationSetting setting : listSettings) {
				settings.put(setting.name, setting);
			}
		}
		return settings;
	}

}
