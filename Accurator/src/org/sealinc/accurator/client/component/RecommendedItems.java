package org.sealinc.accurator.client.component;

import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RecommendedItems extends Composite {

	private VerticalPanel mainContent;

	interface MyUiBinder extends UiBinder<Widget, RecommendedItems> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label lblCreator1, lblTitle1, lblCreator2, lblTitle2, lblCreator3,lblTitle3;
	@UiField
	Image img1,img2,img3;
	@UiField
	TextBox tbSearch;
	@UiField
	Button btnSearch;
	

	public native void openRecommendations()/*-{
		$wnd.jQuery("#dialog-recommendation").dialog({
			autoOpen : false,
			modal : true,
			draggable : false,
			resizable : false,
			closeOnEscape : false,
			width : 950,
			//position:"top",
		});
		$wnd.jQuery("#dialog-recommendation").dialog("open");
		$wnd.jQuery(".searchBox").attr("placeholder","zoekterm");
	}-*/;

	public void loadRecommendations() {
		// Get the recommended items
		Utility.assignService.getNextItemsToAnnotate(3, new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				Utility.itemService.getItems(result, new AsyncCallback<List<CollectionItem>>() {

					@Override
					public void onSuccess(List<CollectionItem> result) {
						if (result != null) {
							CollectionItem ci1 = result.get(0);
							CollectionItem ci2 = result.get(1);
							CollectionItem ci3 = result.get(2);

							if(ci1.maker!=null && ci1.maker.size()>0)
								lblCreator1.setText(ci1.maker.get(0));
							else
								lblCreator1.setText("Onbekend");
								
							
							lblTitle1.setText(ci1.title);
							img1.setUrl(ci1.imageURL + "&aria/maxwidth_288");
							if(ci2.maker!=null && ci2.maker.size()>0)
								lblCreator2.setText(ci2.maker.get(0));
							else
								lblCreator2.setText("Onbekend");
							
							lblTitle2.setText(ci2.title);
							img2.setUrl(ci2.imageURL + "&aria/maxwidth_288");
							if(ci3.maker!=null && ci3.maker.size()>0)
								lblCreator3.setText(ci3.maker.get(0));
							else
								lblCreator3.setText("Onbekend");
							lblTitle3.setText(ci3.title);
							img3.setUrl(ci3.imageURL + "&aria/maxwidth_288");
							
							openRecommendations();
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
		initWidget(uiBinder.createAndBindUi(this));
	}

}

