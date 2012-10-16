package org.sealinc.accurator.client.component;

import java.util.List;
import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ProfileScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Button refresh;
	@UiField
	VerticalPanel pnlAnnotated;
	@UiField
	Label lblTotalAnnotated;

	Accurator accurator;

	DateTimeFormat df = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

	@UiHandler("refresh")
	void handleClick(ClickEvent e) {
		loadData();
	}

	private void loadData() {
		// total number of annotated prints
		Utility.userService.getTotalAnnotatedPrints(Utility.getUser(), null, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				lblTotalAnnotated.setText("Total: " + result);

			}

			@Override
			public void onFailure(Throwable caught) {}
		});

		// get annotated prints
		Utility.userService.getLastAnnotatedItems(Utility.getUser(), 10, new AsyncCallback<List<CollectionItem>>() {

			@Override
			public void onSuccess(List<CollectionItem> result) {
				pnlAnnotated.clear();
				Anchor a;
				// load prints annotated
				for (final CollectionItem ci : result) {
					a = new Anchor(ci.title);
					a.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							accurator.annotate(ci.uri);
						}
					});
					pnlAnnotated.add(a);
				}

			}

			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	
	private static native void loadExpertiseSlider(String topic,String json)	/*-{
		$wnd.jQuery("#"+topic+"Slider").slider("value",eval(json));
	}-*/;
		
	public static void getExpertise(final String topic){
		String loc = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/accurator/userprofile?";
		String data = "user=" + Utility.getUser() + "&type=expertise&topic=" + topic;
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, loc+data);
		rb.setRequestData("");
		rb.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				loadExpertiseSlider(topic, response.getText());				
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub
				
			}
		});
		try {
			rb.send();
		}
		catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void storeExpertise(String topic, int value) {
		String loc = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/accurator/userprofile?";
		String data = "user=" + Utility.getUser() + "&type=expertise&topic=" + topic + "&value=" + value;		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.PUT, loc+data);
		rb.setRequestData("");
		rb.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {		
				
			}
			
			@Override
			public void onError(Request request, Throwable exception) {				
			}
		});
		try {
			rb.send();
		}
		catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	public native void loadUIThemeElements()/*-{
		$wnd.jQuery("#floraSlider").slider({
			min : 0,
			max : 10,
			range : "min",
			slide : function(event, ui) {
				@org.sealinc.accurator.client.component.ProfileScreen::storeExpertise(Ljava/lang/String;I)("flora",ui.value);
			},
		});
		@org.sealinc.accurator.client.component.ProfileScreen::getExpertise(Ljava/lang/String;)("flora");
		
		$wnd.jQuery("#castleSlider").slider({
			min : 0,
			max : 10,
			range : "min",
			slide : function(event, ui) {
				@org.sealinc.accurator.client.component.ProfileScreen::storeExpertise(Ljava/lang/String;I)("castle",ui.value);
			},
		});
		@org.sealinc.accurator.client.component.ProfileScreen::getExpertise(Ljava/lang/String;)("castle");
	}-*/;

	public ProfileScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
		loadData();
	}
}
