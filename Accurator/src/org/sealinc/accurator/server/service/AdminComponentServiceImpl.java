package org.sealinc.accurator.server.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	 * @return List<String> of countries
	 */
	@Override
	public List<String> getCountries() {
		Locale[] locales = Locale.getAvailableLocales();
		List<Country> countries = new ArrayList<Country>();
		List<String> countryNames = new ArrayList<String>();
		
		for(Locale locale : locales) {
			String name = locale.getDisplayCountry();
			String language = locale.getDisplayCountry();

			if (!"".equals(name) && !"".equals(language)) {
		        countries.add(new Country(name, language));
		    }
		}
		
		Collections.sort(countries, new CountryComparator());
		
		for(Country country : countries) {
			String name = country.name;
			if(!countryNames.contains(name))
				countryNames.add(country.name); 
		}
		
		return countryNames;
	}
	
	/**
	 * Get list of languages.
	 * 
	 * @return List<String> of Languages
	 */
	@Override
	public List<String> getLanguages() {
		Locale[] locales = Locale.getAvailableLocales();
		List<Country> countries = new ArrayList<Country>();
		List<String> languages = new ArrayList<String>();
		
		for(Locale locale : locales) {
			String name = locale.getDisplayCountry();
			String language = locale.getDisplayLanguage();

			if (!"".equals(name) && !"".equals(language)) {
		        countries.add(new Country(name, language));
		    }
		}
		
		for(Country country : countries) {
			String language = country.language;
			if(!languages.contains(language))
				languages.add(country.language);
		}
		
		Collections.sort(languages);
		return languages;
	}

	
	class CountryComparator implements Comparator<Country> {
		  
		@SuppressWarnings("rawtypes")
		private Comparator comparator;
		CountryComparator() {
			comparator = Collator.getInstance();
		}

		@SuppressWarnings("unchecked")
		public int compare(Country o1, Country o2) {
			return comparator.compare(o1.name, o2.name);
		}
	}
	
	class Country {
		public String name;
		public String language;
		
		Country(String name, String language) {
		    this.name = name;
		    this.language = language;
		}
	}
}
