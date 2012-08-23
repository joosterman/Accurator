package org.sealinc.accurator.client.component;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

public class Header extends Composite {

	
	public Header() {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		initWidget(horizontalPanel);
		
		Label label = new Label("Accurator");
		label.setStyleName("applicationTitle");
		horizontalPanel.add(label);
		
		Hyperlink hprlnkNewHyperlink_1 = new Hyperlink("Annotate Screen", false, "Annotate");
		horizontalPanel.add(hprlnkNewHyperlink_1);
		
		Hyperlink hprlnkNewHyperlink = new Hyperlink("Profile Screen", false, "Profile");
		horizontalPanel.add(hprlnkNewHyperlink);
		
		Hyperlink hprlnkNewHyperlink_2 = new Hyperlink("Quality Screen", false, "Quality");
		horizontalPanel.add(hprlnkNewHyperlink_2);
		
		Hyperlink hprlnkNewHyperlink_3 = new Hyperlink("Admin Screen", false, "Admin");
		horizontalPanel.add(hprlnkNewHyperlink_3);
	}

}
