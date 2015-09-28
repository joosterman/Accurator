package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class IntroScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, IntroScreen> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private Accurator accurator;
	private final String imagePathPrefix = "./images/background/";
	private final String[] backgroundImages = { "1.jpg", "2.jpg", "3.jpg", "4.jpg" };

	@UiField
	InlineLabel lblLearnMore;
	@UiField
	Image imgBackground;

	/*@UiField
	Anchor btnLogin;

	@UiHandler("btnLogin")
	void btnLoginClick(ClickEvent e) {
		accurator.login_demo();
	}*/

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
	}

	public IntroScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
		int index = (int) Math.round(Math.random() * backgroundImages.length);
		// correct for zero based index
		if (index > 0)
			index--;
		imgBackground.setUrl(imagePathPrefix + backgroundImages[index]);
	}

	@UiHandler("lblLearnMore")
	void lblLearnMoreClick(ClickEvent e) {
		scrollToBottom();
	}

	private native void scrollToBottom()/*-{
		var container = $wnd.jQuery('body');
		var scrollTo = $wnd.jQuery('#divider');

		container.animate({
			scrollTop : scrollTo.offset().top - container.offset().top
					+ container.scrollTop()
		});

	}-*/;
}
