package nl.wisdelft.accurator.client.component;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.widget.client.TextButton;

public class Header extends Composite {

	public Header() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("header");
		horizontalPanel.setSpacing(5);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setBorderWidth(0);
		initWidget(horizontalPanel);
		horizontalPanel.setWidth("100%");
		horizontalPanel.setHeight("100%");
		
		TextButton textButton = new TextButton("Skip");
		horizontalPanel.add(textButton);
		
		TextButton textButton_1 = new TextButton("Done");
		horizontalPanel.add(textButton_1);
		
		Label label = new Label("RMA Accurator");
		label.setStyleName("gwt-Label-Title");
		label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(label);
		label.setSize("538px", "");
		
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
