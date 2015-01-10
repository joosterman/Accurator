package org.sealinc.accurator.client.component;

import java.util.List;

import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.Utility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddInfoScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, AddInfoScreen> {}
	
	@UiField
	Button btnAddInfo;
	@UiField
	TextBox txtEMail;
	@UiField
	IntegerBox intAge, intMuseumVisits;
	@UiField
	RadioButton chkGenderMale, chkGenderFemale, chkUrban, chkSubUrban, chkRural, 
				chkYesEmployee, chkNoEmployee, chkYesTagging, chkNoTagging,
				chkTaggingExpert, chkTaggingIntermediate, chkTaggingNovice;
	@UiField
	CheckBox chkSocialFacebook, chkSocialTwitter, chkSocialLinkedIn, chkSocialNone,
			 chkTagSiteWaIsDa, chkTagSiteSpotvogel, chkTagSiteSteve, chkTagNone;
	@UiField
	ListBox lbxCountry, lbxLanguage, lbxEducation, lbxIncome, lbxInternetUsage;
	@UiField
	FlowPanel pnlInfoWell;
	
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	Accurator accurator;
	
	private static List<String> countries, languages;
	
	public AddInfoScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
		initFields();
	}
	
	@UiHandler("btnAddInfo")
	void btnAddInfoClick(ClickEvent e) {
		// get the entered fields
		int age = getAge();
		String gender = getGender();
		String country = getCountry();
		String community = getCommunity();
		String language = getLanguage();
		String education = getEducation();
		String income = getIncome();
		String mail = getMail(); 
		String socialMedia = getSocialMedia();
		String internetUsage = getInternetUsage();
		int museumVisits = getMuseumVisits();
		String museumEmployee = getMuseumEmployee();
		String taggingExperience = getTaggingExperience();
		String taggingLevel = getTaggingLevel();
		String tagSites = getTagSites();
		
		System.out.println(age);
		System.out.println(gender);
		System.out.println(country);
		System.out.println(community);
		System.out.println(language);
		System.out.println(education);
		System.out.println(income);
		System.out.println(mail);
		System.out.println(socialMedia);
		System.out.println(internetUsage);
		System.out.println(museumVisits);
		System.out.println(museumEmployee);
		System.out.println(taggingExperience);
		System.out.println(taggingLevel);
		System.out.println(tagSites);
	}
	
	private int getAge() {
		int age = -1;
		if(intAge.getValue() != null) {
			age = intAge.getValue();
		}
		return age;
	}
	
	private String getGender() {
		String gender = "unknown";
		if(chkGenderMale.getValue() == true) {
			gender = "male";
		} else if(chkGenderFemale.getValue() == true){
			gender = "female";
		}
		return gender;
	}
	
	private String getCountry() {
		String country = "unknown";
		int countryIndex = lbxCountry.getSelectedIndex();
		if(countryIndex != 0) {
			country = lbxCountry.getItemText(countryIndex);
		}
		return country;
	}
	
	private String getCommunity() {
		String community = "unknown";
		if(chkUrban.getValue() == true) {
			community = "male";
		} else if(chkSubUrban.getValue() == true){
			community = "sub-urban";
		} else if(chkRural.getValue() == true){
			community = "rural";
		}
		return community;
	}
	
	private String getLanguage() {
		String language = "unknown";
		int languageIndex = lbxLanguage.getSelectedIndex();
		if(languageIndex != 0) {
			language = lbxLanguage.getItemText(languageIndex);
		}
		return language;
	}
	
	private String getEducation() {
		String education;
		int educationIndex = lbxEducation.getSelectedIndex();
		switch (educationIndex) {
	        case 1:  education = "primary school";
	                 break;
	        case 2:  education = "high school";
	                 break;
	        case 3:  education = "college";
	                 break;
	        case 4:  education = "bachelor";
	                 break;
	        case 5:  education = "master";
	                 break;
	        case 6:  education = "doctorate";
	                 break;
	        default: education = "unknown";
	                 break;
		}
		return education;
	}
	
	private String getIncome() {
		String income;
		int incomeIndex = lbxIncome.getSelectedIndex();
		switch (incomeIndex) {
	        case 1:  income = "<20";
	                 break;
	        case 2:  income = "20 to 35";
	                 break;
	        case 3:  income = "35 to 50";
	                 break;
	        case 4:  income = "50 to 75";
	                 break;
	        case 5:  income = "75 to 100";
	                 break;
	        case 6:  income = "100 to 150";
	                 break;
	        case 7:  income = "150 to 200";
            		 break;
	        case 8:  income = ">200";
            		 break;
	        default: income = "unknown";
	                 break;
		}
		return income;
	}
	
	private String getMail() {
		String mail;
		if (txtEMail.getText().isEmpty()) {
			mail = "unknown";
		} else {
			mail = txtEMail.getText();
		}
		return mail;
	}
	
	private String getSocialMedia() {
		String socialMedia = "unknown";
		if(chkSocialFacebook.getValue() == true) {
			socialMedia = "facebook";
		} else if(chkSocialLinkedIn.getValue() == true){
			if (chkSocialFacebook.getValue() == true) {
				socialMedia += " linkedin";
			} else {
				socialMedia = "linkedin";
			}
		} else if(chkSocialTwitter.getValue() == true){
			if (chkSocialFacebook.getValue() == true || chkSocialLinkedIn.getValue() == true) {
				socialMedia += " twitter";
			} else {
				socialMedia = "twitter";
			}
		}
		if(chkSocialNone.getValue() == true)
			socialMedia = "none";
		
		return socialMedia;
	}
	
	private String getInternetUsage() {
		String internetUsage;
		int internetUsageIndex = lbxInternetUsage.getSelectedIndex();
		switch (internetUsageIndex) {
	        case 1:  internetUsage = "always";
	                 break;
	        case 2:  internetUsage = "once a day";
	                 break;
	        case 3:  internetUsage = "3 to 5 times a week";
	                 break;
	        case 4:  internetUsage = "1 to 2 times a week";
	                 break;
	        case 5:  internetUsage = "less than once a week";
	                 break;
	        default: internetUsage = "unknown";
	                 break;
		}
		return internetUsage;
	}
	
	private int getMuseumVisits() {
		int visits = -1;
		if(intMuseumVisits.getValue() != null) {
			visits = intMuseumVisits.getValue();
		}
		return visits;
	}
	
	private String getMuseumEmployee() {
		String museumEmployee = "unknown";
		if(chkYesEmployee.getValue() == true) {
			museumEmployee = "true";
		} else if(chkNoEmployee.getValue() == true){
			museumEmployee = "false";
		}
		return museumEmployee;
	}
	
	private String getTaggingExperience() {
		String taggingExperience = "unknown";
		if(chkYesTagging.getValue() == true) {
			taggingExperience = "true";
		} else if(chkNoTagging.getValue() == true){
			taggingExperience = "false";
		}
		return taggingExperience;
	}
	
	private String getTaggingLevel() {
		String taggingLevel = "unknown";
		if(chkTaggingNovice.getValue() == true) {
			taggingLevel = "novice";
		} else if(chkTaggingIntermediate.getValue() == true){
			taggingLevel = "intermediate";
		} else if(chkTaggingExpert.getValue() == true){
			taggingLevel = "expert";
		}
		return taggingLevel;
	}
	
	private String getTagSites() {
		String taggingSites = "unknown";
		if(chkTagSiteWaIsDa.getValue() == true) {
			taggingSites = "waisda";
		} else if(chkTagSiteSpotvogel.getValue() == true){
			if (chkTagSiteWaIsDa.getValue() == true) {
				taggingSites += " spotvogel";
			} else {
				taggingSites = "spotvogel";
			}
		} else if(chkTagSiteSteve.getValue() == true){
			if (chkTagSiteWaIsDa.getValue() == true || chkTagSiteSpotvogel.getValue() == true) {
				taggingSites += " steve";
			} else {
				taggingSites = "steve";
			}
		}
		if(chkTagNone.getValue() == true)
			taggingSites = "none";
		
		return taggingSites;
	}
	
	private void initFields() {
		intAge.setMaxLength(3);
		intMuseumVisits.setMaxLength(3);
		addCountries();
		addLanguages();
		addEducationLevels();
		addIncomeLevels();
		addInternetUseLevels();
	}
	
	private void addCountries() {
		lbxCountry.addItem("");
		Utility.adminService.getCountries(new AsyncCallback<List<String>>() {
			public void onSuccess(List<String> serverCountries) {
				countries = serverCountries;
				for (String country : countries) {
					lbxCountry.addItem(country);
				}
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void addLanguages() {
		lbxLanguage.addItem("");
		Utility.adminService.getLanguages(new AsyncCallback<List<String>>() {
			public void onSuccess(List<String> serverLanguages) {
				languages = serverLanguages;
				for (String language : languages) {
					lbxLanguage.addItem(language);
				}
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void addEducationLevels() {
		String[] educationLevels = {"",
				Utility.constants.primarySchool(),
				Utility.constants.highSchool(),
				Utility.constants.college(),
				Utility.constants.bachelor(),
				Utility.constants.master(),
				Utility.constants.doctorate()};
		
		for(int i=0; i<educationLevels.length; i++) {
			lbxEducation.addItem(educationLevels[i]);
		}
	}
	
	private void addIncomeLevels() {
		String[] incomeLevels = {"",
								 Utility.constants.income20(),
								 Utility.constants.income20to35(),
								 Utility.constants.income35to50(),
								 Utility.constants.income50to75(),
								 Utility.constants.income75to100(),
								 Utility.constants.income100to150(),
								 Utility.constants.income150to200(),
								 Utility.constants.income200()};
		
		for(int i=0; i<incomeLevels.length; i++) {
			lbxIncome.addItem(incomeLevels[i]);
		}
	}
	
	private void addInternetUseLevels() {
		String[] internetUseLevels = {"",
								 Utility.constants.internetAlways(),
								 Utility.constants.internetOnceADay(),
								 Utility.constants.internet3to5(),
								 Utility.constants.internet1to2(),
								 Utility.constants.internetLessThan1()};
		
		for(int i=0; i<internetUseLevels.length; i++) {
			lbxInternetUsage.addItem(internetUseLevels[i]);
		}
	}
	
	public void setInfoFailureText(String text) {
		// clear the current
		pnlInfoWell.clear();
		Label lblFailInfo = new Label(text);
		pnlInfoWell.setStyleName("well");
		pnlInfoWell.add(lblFailInfo);
	}
}
