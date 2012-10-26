package org.sealinc.accurator.client.component;

import java.util.ArrayList;
import java.util.List;
import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RecommendedItems extends Composite {

	interface MyUiBinder extends UiBinder<Widget, RecommendedItems> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	Accurator accurator;

	@UiField
	Label lblCreator1, lblTitle1, lblCreator2, lblTitle2, lblCreator3, lblTitle3, lblPrintmaker1,lblPrintmaker2,lblPrintmaker3;
	@UiField
	Image img1, img2, img3;
	@UiField
	TextBox tbSearch;
	@UiField
	Button btnSearch;

	Label[] titles;
	Label[] creators;
	Image[] images;
	Label[] printmakers;
	List<HandlerRegistration> regs = new ArrayList<HandlerRegistration>();

	@UiHandler("imgNext")
	void nextClick(ClickEvent e) {
		loadRecommendations();
	}

	@UiHandler("btnSearch")
	void btnSearchClickHandler(ClickEvent e) {
		String text = tbSearch.getText();
		Utility.assignService.search(text, new AsyncCallback<List<CollectionItem>>() {

			@Override
			public void onSuccess(List<CollectionItem> result) {
				loadItems(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void loadItems(List<CollectionItem> items) {
		for(HandlerRegistration reg:regs){
			reg.removeHandler();
		}
		regs.clear();
		for (int i = 0; i < titles.length; i++) {
			if (items.size() > i) {
				final CollectionItem ci = items.get(i);
				if (ci.maker != null && ci.maker.size() > 0) creators[i].setText(ci.maker.get(0));
				else creators[i].setText("Onbekend");
				titles[i].setText(ci.title);
				images[i].setUrl(ci.imageURL + "&aria/maxwidth_288");
				images[i].setVisible(true);
				printmakers[i].setVisible(true);
				regs.add(images[i].addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						accurator.annotate(ci.uri);
						loadRecommendations();
					}
				}));
			}
			else {
				creators[i].setText("");
				titles[i].setText("");
				images[i].setVisible(false);
				printmakers[i].setVisible(false);
				
			}
		}
	}

	public void loadRecommendations() {
		// clear the existing items
		for (int i = 0; i < titles.length; i++) {
			creators[i].setText("");
			titles[i].setText("");
			images[i].setVisible(false);
			printmakers[i].setVisible(false);
		}

		// Get the recommended items
		List<String> uris = accurator.getNextPrintsToAnnotate(3);
		if (uris.size() == 0) {
			Timer t = new Timer() {
				@Override
				public void run() {
					loadRecommendations();
				}
			};
			t.schedule(200);
		}
		else {
			Utility.itemService.getItems(uris, new AsyncCallback<List<CollectionItem>>() {

				@Override
				public void onSuccess(List<CollectionItem> result) {
					loadItems(result);
				}

				@Override
				public void onFailure(Throwable caught) {

				}
			});
		}
	}

	public RecommendedItems(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
		titles = new Label[] { lblTitle1, lblTitle2, lblTitle3 };
		creators = new Label[] { lblCreator1, lblCreator2, lblCreator3 };
		images = new Image[] { img1, img2, img3 };
		printmakers = new Label[] {lblPrintmaker1,lblPrintmaker2,lblPrintmaker3};
		loadRecommendations();
	}

}
