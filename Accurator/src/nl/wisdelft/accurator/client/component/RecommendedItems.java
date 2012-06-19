package nl.wisdelft.accurator.client.component;

import java.beans.Beans;
import java.util.List;

import nl.wisdelft.accurator.client.service.RecommenderService;
import nl.wisdelft.accurator.client.service.RecommenderServiceAsync;
import nl.wisdelft.accurator.client.service.ResourceDetailService;
import nl.wisdelft.accurator.client.service.ResourceDetailServiceAsync;
import nl.wisdelft.accurator.shared.ResourceDetail;
import nl.wisdelft.accurator.shared.Utility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecommendedItems extends Composite {

	private RecommenderServiceAsync recommender = (RecommenderServiceAsync) GWT.create(RecommenderService.class);
	private ResourceDetailServiceAsync details = (ResourceDetailServiceAsync) GWT.create(ResourceDetailService.class);
	private VerticalPanel mainContent;

	public void getRecommendations() {
		// Get the recommended items
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<String> result) {
				AsyncCallback<List<ResourceDetail>> callback = new AsyncCallback<List<ResourceDetail>>() {

					@Override
					public void onSuccess(List<ResourceDetail> result) {
						for (ResourceDetail val : result) {
							HorizontalPanel horizontalPanel = new HorizontalPanel();
							mainContent.add(horizontalPanel);
							Image image = new Image(val.getImageURL());
							horizontalPanel.add(image);
							image.setSize("100px", "");

							VerticalPanel verticalPanel = new VerticalPanel();
							horizontalPanel.add(verticalPanel);
							verticalPanel.setHeight("100%");

							Label label = new Label(val.getTitle());
							verticalPanel.add(label);
							Label label2 = new Label(val.getDescription());
							verticalPanel.add(label2);

							FormPanel formPanel = new FormPanel("annotationFrame");
							formPanel.setAction(Utility.getAnnotationComponentURL());
							formPanel.setMethod("GET");
							verticalPanel.add(formPanel);
							verticalPanel.setCellVerticalAlignment(formPanel, HasVerticalAlignment.ALIGN_BOTTOM);

							VerticalPanel fields = new VerticalPanel();
							formPanel.add(fields);

							SubmitButton submitButton = new SubmitButton();
							submitButton.setText("Annotate!");
							fields.add(submitButton);

							Hidden target = new Hidden();
							target.setValue(val.getURI());
							target.setName("target");
							fields.add(target);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				};
				details.GetResourcesDetail(result, callback);
			}

		};
		recommender.getRecommendedItems(null, callback);
	}

	public RecommendedItems() {
		// use a vertical stacked layout for the recommendations
		mainContent = new VerticalPanel();
		initWidget(mainContent);
		if (!Beans.isDesignTime())
			getRecommendations();
	}

}
