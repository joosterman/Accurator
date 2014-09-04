package org.sealinc.accurator.client.component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RecommendedItems extends Composite {

	interface MyUiBinder extends UiBinder<Widget, RecommendedItems> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	Accurator accurator;

	@UiField
	Label lblCreator1, lblTitle1, lblCreator2, lblTitle2, lblCreator3,
			lblTitle3, lblPrintmaker1, lblPrintmaker2, lblPrintmaker3;
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
	LinkedList<String> seenPrints = new LinkedList<String>();
	LinkedList<String> backlog = new LinkedList<String>();

	@UiHandler("imgNext")
	void nextClick(ClickEvent e) {
		loadNextRecommendations();
	}

	@UiHandler("imgPrevious")
	void previousClick(ClickEvent e) {
		loadPreviousRecommendations();
	}

	@UiHandler("btnSearch")
	void btnSearchClickHandler(ClickEvent e) {
		String text = tbSearch.getText();
		Utility.assignService.search(text,
				new AsyncCallback<List<CollectionItem>>() {

					@Override
					public void onSuccess(List<CollectionItem> result) {
						loadItems(result);
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
	}

	public RecommendedItems(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		accurator = acc;
		titles = new Label[] { lblTitle1, lblTitle2, lblTitle3 };
		creators = new Label[] { lblCreator1, lblCreator2, lblCreator3 };
		images = new Image[] { img1, img2, img3 };
		printmakers = new Label[] { lblPrintmaker1, lblPrintmaker2,
				lblPrintmaker3 };
		clearRecommendation();
	}

	public void addFirstSeenPrint(String uri) {
		seenPrints.addFirst(uri);
		System.out.println("SeenPrints: " + seenPrints);
		System.out.println("Backlog: " + backlog);
	}

	private void loadItems(List<CollectionItem> items) {
		for (HandlerRegistration reg : regs) {
			reg.removeHandler();
		}
		regs.clear();
		for (int i = 0; i < titles.length; i++) {
			if (items.size() > i) {
				final CollectionItem ci = items.get(i);
				if (ci.maker != null)
					creators[i].setText(ci.maker);
				else
					creators[i].setText("Onbekend");
				titles[i].setText(ci.title);
				images[i].setUrl(Config.imageCacheThumbURL+"?uri="+ ci.imageURL);
				images[i].setVisible(true);
				printmakers[i].setVisible(true);
				regs.add(images[i].addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						accurator.annotate(ci.uri);
						History.newItem(Accurator.State.Annotate.toString());
						loadNextRecommendations();
					}
				}));
			} else {
				creators[i].setText("");
				titles[i].setText("");
				images[i].setVisible(false);
				printmakers[i].setVisible(false);

			}
		}
	}

	public void loadPreviousRecommendations() {
		// check if we can go back
		if (seenPrints.size() <= 3)
			return;

		clearRecommendation();
		accurator.showLoading(true);
		// put the currently visible prints in the backlog
		for (int i = 0; i < 3 && seenPrints.size() > 0; i++) {
			backlog.addFirst(seenPrints.removeLast());
		}

		// get the last three from the seen items
		List<String> uris = new ArrayList<String>();
		for (int i = 0; i < 3 && seenPrints.size() > i; i++) {
			int index = seenPrints.size() - i - 1;
			uris.add(seenPrints.get(index));
		}

		Utility.itemService.getItems(uris,
				new AsyncCallback<List<CollectionItem>>() {
					@Override
					public void onSuccess(List<CollectionItem> result) {
						loadItems(result);
						accurator.showLoading(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						System.err.println(caught.toString());
					}
				});
	}

	public void loadNextRecommendations() {
		clearRecommendation();
		accurator.showLoading(true);
		// Either get the recommended items from the backlog or, when empty, new
		// ones
		List<String> uris = new ArrayList<String>();
		if (backlog.size() > 0) {
			uris = new ArrayList<String>();
			for (int i = 0; i < 3 && backlog.size() > 0; i++) {
				String elem = backlog.removeLast();
				uris.add(elem);
				seenPrints.addLast(elem);
			}
		} else {
			uris = accurator.getNextPrintsToAnnotate(3);
			// retry if there are at this moment no next prints
			if (uris.size() == 0) {
				Timer t = new Timer() {
					@Override
					public void run() {
						loadNextRecommendations();
					}
				};
				t.schedule(200);
				return;
			}
			// store that we have seen these items
			for (int i = 0; i < uris.size(); i++) {
				int index = uris.size() - i - 1;
				seenPrints.addLast(uris.get(index));
			}
		}
		// get the data and show the items
		Utility.itemService.getItems(uris,
				new AsyncCallback<List<CollectionItem>>() {
					@Override
					public void onSuccess(List<CollectionItem> result) {
						loadItems(result);
						accurator.showLoading(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						System.err.println(caught.toString());
					}
				});
	}

	private void clearRecommendation() {
		// clear the existing items
		for (int i = 0; i < titles.length; i++) {
			creators[i].setText("");
			titles[i].setText("");
			images[i].setVisible(false);
			printmakers[i].setVisible(false);
		}
	}

}
