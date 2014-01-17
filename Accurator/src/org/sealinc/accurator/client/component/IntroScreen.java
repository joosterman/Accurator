package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class IntroScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, IntroScreen> {}
	Accurator accurator;
		
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@Override
	public void setHeight(String height){
		super.setHeight(height);
	}

	public IntroScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
	}

}