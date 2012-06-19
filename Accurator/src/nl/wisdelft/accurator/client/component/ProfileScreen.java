package nl.wisdelft.accurator.client.component;

import java.beans.Beans;
import java.util.List;

import nl.wisdelft.accurator.client.service.UserInformationService;
import nl.wisdelft.accurator.client.service.UserInformationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProfileScreen extends Composite {
	private CaptionPanel pnlTagsUsed;
	private CaptionPanel captionPanel_1;
	private CaptionPanel captionPanel_2;
	private CaptionPanel captionPanel_3;
	private CaptionPanel captionPanel_4;
	private CaptionPanel captionPanel_5;

	private UserInformationServiceAsync userService = (UserInformationServiceAsync) GWT.create(UserInformationService.class);
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
			loadUserAnnotations();
	}

	private void loadUserAnnotations() {
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				for (String body : result) {
					//check if we have an URI or a literal
					if (UriUtils.isSafeUri(body) && body.startsWith("http")) {
						Anchor a = new Anchor(body,body);
						pnlAnnotations.add(a);
					}
					else {
						Label l = new Label(body);
						pnlAnnotations.add(l);
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

			}
		};
		userService.GetAnnotations(null, callback);
	}

}
