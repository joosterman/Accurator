package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RegisterScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, RegisterScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	Accurator accurator;

	public RegisterScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
	}
}
