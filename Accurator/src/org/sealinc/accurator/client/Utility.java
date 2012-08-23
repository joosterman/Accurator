package org.sealinc.accurator.client;

import org.sealinc.accurator.client.service.AssignComponentService;
import org.sealinc.accurator.client.service.AssignComponentServiceAsync;
import org.sealinc.accurator.client.service.ItemComponentService;
import org.sealinc.accurator.client.service.ItemComponentServiceAsync;
import org.sealinc.accurator.client.service.QualityComponentService;
import org.sealinc.accurator.client.service.QualityComponentServiceAsync;
import org.sealinc.accurator.client.service.UserComponentService;
import org.sealinc.accurator.client.service.UserComponentServiceAsync;

import com.google.gwt.core.client.GWT;

public class Utility {
	public static AssignComponentServiceAsync assignService = (AssignComponentServiceAsync) GWT.create(AssignComponentService.class);
	public static UserComponentServiceAsync userService = (UserComponentServiceAsync) GWT.create(UserComponentService.class);
	public static ItemComponentServiceAsync itemService = (ItemComponentServiceAsync) GWT.create(ItemComponentService.class);
	public static QualityComponentServiceAsync qualityService = (QualityComponentServiceAsync) GWT.create(QualityComponentService.class);
	
	private Utility() {}
	
	

}
