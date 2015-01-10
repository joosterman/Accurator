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
	IntegerBox intAge;
	@UiField
	RadioButton chkGenderMale, chkGenderFemale;
	@UiField
	ListBox lbxCountry, lbxEducation, lbxIncome, lbxInternetUsage;
	@UiField
	FlowPanel pnlInfoWell;
	
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	Accurator accurator;
	
	@UiHandler("btnAddInfo")
	void btnRegisterClick(ClickEvent e) {
		// get the entered fields
		String mail = txtEMail.getText();
		System.out.println(mail);
	}
	
	public AddInfoScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
		intAge.setMaxLength(3);
		addCountries();
		addEducationLevels();
		addIncomeLevels();
		addInternetUseLevels();
//		txtEMail.getElement().setAttribute("type", "email");
	}
	
	private void addCountries() {
		Utility.adminService.getCountries(new AsyncCallback<List<String>>() {
			public void onSuccess(List<String> countries) {
				for (String country : countries) {
					lbxCountry.addItem(country);
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
