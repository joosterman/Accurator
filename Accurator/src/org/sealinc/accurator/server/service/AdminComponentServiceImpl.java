package org.sealinc.accurator.server.service;

import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.server.Utility;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminComponentServiceImpl extends RemoteServiceServlet implements AdminComponentService {

	private static final long serialVersionUID = 814007834668767239L;

	@Override
	public int register(String user, String password, String realName) {
		String url = String.format("%s?user=%s&password=%s&realname=%s", Config.getAdminRegisterUserURL(), user, password, realName);
		int retCode = Utility.getStatusCode(url);
		return retCode;
	}

}
