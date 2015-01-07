package org.sealinc.accurator.client.component;


import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
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
		
//		intAge.getElement().setAttribute("type", "number");
		intAge.setMaxLength(3);
		intAge.setVisibleLength(3);
//		txtEMail.getElement().setAttribute("type", "email");
	}
	
	public void setInfoFailureText(String text) {
		// clear the current
		pnlInfoWell.clear();
		Label lblFailInfo = new Label(text);
		pnlInfoWell.setStyleName("well");
		pnlInfoWell.add(lblFailInfo);
	}
}
