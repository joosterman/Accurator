package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, RegisterScreen> {}
	
	@UiField
	Button btnRegister;
	@UiField
	TextBox txtRegisterUserName, txtRegisterPassword, txtRegisterFullName;
	
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	Accurator accurator;
	
	@UiHandler("btnRegister")
	void btnRegisterClick(ClickEvent e) {
		// get the entered fields
		String user = txtRegisterUserName.getText();
		String pass = txtRegisterPassword.getText();
		String fullName = txtRegisterFullName.getText();
		accurator.registerUser(user, pass, fullName);
	}
	
	public RegisterScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
	}
}
