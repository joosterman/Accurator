package org.sealinc.accurator.server.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.server.Utility;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminComponentServiceImpl extends RemoteServiceServlet implements AdminComponentService {

	private static final long serialVersionUID = 814007834668767239L;

	@Override
	public boolean register(String user, String password, String realName) {
		Utility.login();
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
		if (u.getQuery() == null) url += "?";
		else url += "&";
		url += "nocache=" + new Random().nextInt();

		return Utility.getHTMLContent(url);
	}

}
