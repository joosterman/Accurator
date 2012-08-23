package org.sealinc.accurator.client.component;

import java.beans.Beans;
import java.util.List;

import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Annotation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProfileScreen extends Composite {
	private CaptionPanel pnlTagsUsed;
	private CaptionPanel captionPanel_1;
	private CaptionPanel captionPanel_2;
	private CaptionPanel captionPanel_3;
	private CaptionPanel captionPanel_4;
	private CaptionPanel captionPanel_5;

	private VerticalPanel pnlAnnotations;

	public ProfileScreen() {
		Grid grid = new Grid(2, 3);
		initWidget(grid);
		grid.setSize("100%", "");

		pnlTagsUsed = new CaptionPanel("Tags used");
		grid.setWidget(0, 0, pnlTagsUsed);

		pnlAnnotations = new VerticalPanel();
		pnlTagsUsed.setContentWidget(pnlAnnotations);
		pnlAnnotations.setSize("", "");

		captionPanel_1 = new CaptionPanel("Statistics");
		grid.setWidget(0, 1, captionPanel_1);

		captionPanel_2 = new CaptionPanel("We think you like");
		grid.setWidget(0, 2, captionPanel_2);

		captionPanel_3 = new CaptionPanel("New panel");
		captionPanel_3.setCaptionHTML("Objects annotated");
		grid.setWidget(1, 0, captionPanel_3);

		captionPanel_4 = new CaptionPanel("?");
		grid.setWidget(1, 1, captionPanel_4);

		captionPanel_5 = new CaptionPanel("New panel");
		captionPanel_5.setCaptionHTML("We think you dislike");
		grid.setWidget(1, 2, captionPanel_5);

		if (!Beans.isDesignTime())
		{
			Utility.userService.getAnnotations("http://sealincmedia.project.cwi.nl/ns/exp/user_1", new AsyncCallback<List<Annotation>>() {
				
				@Override
				public void onSuccess(List<Annotation> result) {
					
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		}
	}



}
