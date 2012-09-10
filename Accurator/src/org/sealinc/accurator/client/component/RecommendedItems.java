package org.sealinc.accurator.client.component;

import java.beans.Beans;
import java.util.Date;
import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.View;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecommendedItems extends Composite {

	private VerticalPanel mainContent;

	public void updateRecommendations() {
		// Get the recommended items
		Utility.assignService.getNextItemsToAnnotate(3, new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				Utility.itemService.getItems(result, new AsyncCallback<List<CollectionItem>>() {

					@Override
					public void onSuccess(List<CollectionItem> result) {
						if (result != null) {
							for (final CollectionItem val : result) {
								HorizontalPanel recommendationRow = new HorizontalPanel();
								recommendationRow.setStyleName("recommendationRow");
								mainContent.add(recommendationRow);

								Image image = new Image(val.thumbnailURL);
								recommendationRow.add(image);

								VerticalPanel verticalPanel = new VerticalPanel();
								verticalPanel.setStyleName("recommendationText");
								recommendationRow.add(verticalPanel);

								Label label = new Label(val.title);
								verticalPanel.add(label);
								Label label2 = new Label(val.description);
								verticalPanel.add(label2);

								FormPanel formPanel = new FormPanel("annotationFrame");
								formPanel.setAction(Config.getAnnotationComponentURL());
								formPanel.setMethod("GET");
								verticalPanel.add(formPanel);

								VerticalPanel fields = new VerticalPanel();
								formPanel.add(fields);

								SubmitButton submitButton = new SubmitButton();
								submitButton.setText("Annoteer!");
								submitButton.addClickHandler(new ClickHandler() {

									@Override
									public void onClick(ClickEvent event) {
										View view = new View();
										view.date = new Date();
										view.fromRecommendation = true;
										view.viewer = Config.getUserComponentUserURI() + Utility.getUser();
										Utility.userService.setViewed(val.uri, view, new AsyncCallback<Boolean>() {

											@Override
											public void onSuccess(Boolean result) {
												System.out.println("View result: " + result);
											}

											@Override
											public void onFailure(Throwable caught) {}
										});
									}
								});
								fields.add(submitButton);

								Hidden target = new Hidden();
								target.setValue(val.uri);
								target.setName("target");
								fields.add(target);
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	public RecommendedItems() {
		// use a vertical stacked layout for the recommendations
		mainContent = new VerticalPanel();
		initWidget(mainContent);
		if (!Beans.isDesignTime()) updateRecommendations();
	}

}
