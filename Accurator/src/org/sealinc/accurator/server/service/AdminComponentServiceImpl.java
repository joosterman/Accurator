package org.sealinc.accurator.server.service;

import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.server.Utility;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminComponentServiceImpl extends RemoteServiceServlet implements AdminComponentService {

	private static final long serialVersionUID = 814007834668767239L;

	@Override
	public boolean register(String user, String password, String realName) {
		boolean success = Utility.register(user, realName, password);
		return success;
	}

	@Override
	public String getJSON(String url) {
		return Utility.getHTMLContent(url);
	}

}
