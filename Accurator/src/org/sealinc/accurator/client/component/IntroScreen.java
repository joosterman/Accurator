package org.sealinc.accurator.client.component;

import org.sealinc.accurator.client.Accurator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class IntroScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, IntroScreen> {}

	Accurator accurator;

	@UiField
	InlineLabel lblLearnMore;

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
	}

	public IntroScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
	}

	@UiHandler("lblLearnMore")
	void lblLearnMoreClick(ClickEvent e) {
		scrollToBottom();
	}

	private native void scrollToBottom()/*-{
		$wnd.jQuery("div.bottom").scrollIntoView();
	}-*/;
}
