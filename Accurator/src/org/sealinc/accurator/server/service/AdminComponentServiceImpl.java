package org.sealinc.accurator.server.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
			e.printStackTrace();
			return null;
		}
		if (u.getQuery() == null) url += "?";
		else url += "&";
		url += "nocache=" + new Random().nextInt();

		return Utility.getHTMLContent(url);
	}
	
	/**
	 * Get list of countries.
	 * 
	 * @return String[] of countries
	 */
	@Override
	public List<String> getCountries() {
		List<String> countryList = Arrays.asList(Locale.getISOCountries());		
		return countryList;
	}

}
