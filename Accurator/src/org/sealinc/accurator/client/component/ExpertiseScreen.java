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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ExpertiseScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ExpertiseScreen> {}
	
	@UiField
	Button btnExpertise;
	@UiField
	FlowPanel pnlExpertiseLeft, pnlExpertiseRight;
	
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	Accurator accurator;
	
	public ExpertiseScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
		initFields();
	}
	
	@UiHandler("btnExpertise")
	void btnExpertise(ClickEvent e) {
		System.out.println("show me your expertise!");
	}
	
	private void initFields() {
		Label label = new Label("Chicken");
		pnlExpertiseLeft.add(label);
		Label label2 = new Label("Koe");
		pnlExpertiseRight.add(label2);
	}
}
