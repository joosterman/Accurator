package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class IntroScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, IntroScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private final String imagePathPrefix = "./images/background/";
	private final String[] backgroundImages = { "1.jpg" };

	@UiField
	Image imgBackground;

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
	}

	public IntroScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		int index = (int) Math.round(Math.random() * backgroundImages.length);
		// correct for zero based index
		if (index > 0) index--;
		imgBackground.setUrl(imagePathPrefix + backgroundImages[index]);
	}
}
