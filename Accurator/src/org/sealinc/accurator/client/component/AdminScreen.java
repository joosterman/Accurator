package org.sealinc.accurator.client.component;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminScreen extends Composite {
	private ListBox activityStreamStrategy;

	public AdminScreen() {
		
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		
		activityStreamStrategy = new ListBox();
		verticalPanel.add(activityStreamStrategy);
		fillActivityStreamStrategies();
	}

	private void fillActivityStreamStrategies() {
		activityStreamStrategy.setVisibleItemCount(5);
		
	}

}
