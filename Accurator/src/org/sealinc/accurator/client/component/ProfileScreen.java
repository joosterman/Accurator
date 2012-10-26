package org.sealinc.accurator.client.component;

import java.util.List;
import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.JsUserProfileEntry;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ProfileScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	VerticalPanel pnlAnnotated;
	@UiField
	Label lblTotalAnnotated;

	Accurator accurator;

	DateTimeFormat df = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

	public void loadData() {
		// total number of annotated prints
		Utility.userService.getTotalAnnotatedPrints(Utility.getStoredUsername(), null, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				lblTotalAnnotated.setText("Total: " + result);

			}

			@Override
			public void onFailure(Throwable caught) {}
		});

		// get annotated prints
		Utility.userService.getLastAnnotatedItems(Utility.getStoredUsername(), 10, new AsyncCallback<List<CollectionItem>>() {

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

	private native void setExpertiseSlider(String topic, double value) /*-{
		$wnd.jQuery("#" + topic + "Slider").slider("value", value);
	}-*/;

	private void getExpertise(final String topic) {
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				double expertise = 0;
				if (entries.length() > 0) {
					expertise = entries.get(0).getValueAsDouble();					
				}
				initSlider(topic, expertise);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub

			}
		};
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "expertise", topic, Utility.getQualifiedUsername(), callback);
	}

	private void getLanguage() {
		// create callback function
		RequestCallback callback = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(json);
				if (entries.length() > 0) {
					String language = entries.get(0).getValueAsString();
					updateLanguageButtons(language);
					// set the annotation component to the locale
					RequestBuilder req = new RequestBuilder(RequestBuilder.GET, Config.getAnnotationComponentChangePreferenceURL() + "?user:lang="
							+ language);
					req.setRequestData("");
					req.setCallback(Utility.noopCallback);
					try {
						req.send();
					}
					catch (Exception ex) {}
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub

			}
		};
		// Execute the call
		Utility.getUserProfileEntry(Utility.getStoredUsername(), "languagePreference", null, null, callback);
	}

	private native void updateLanguageButtons(String language) /*-{
		$wnd.jQuery("#language" + language.toUpperCase()).attr("checked", true);
		$wnd.jQuery("#language").buttonset("refresh");
	}-*/;

	
	private native void initSlider(String topic, double value)/*-{
		var user = @org.sealinc.accurator.client.Utility::getQualifiedUsername()();
		$wnd
			.jQuery("#"+topic+"Slider")
			.slider({
				min : 0,
				max : 1,
				step : 0.1,
				range : "min",
				value : value,
				change : function(event, ui) {
					@org.sealinc.accurator.client.Utility::storeUserProfileEntry(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(user,"expertise",topic,user,""+ui.value,"double");
				},
			});
	}-*/;
	
	public native void loadUIThemeElements()/*-{
		var pscreen = this;

		pscreen.@org.sealinc.accurator.client.component.ProfileScreen::getExpertise(Ljava/lang/String;)("flora");
		pscreen.@org.sealinc.accurator.client.component.ProfileScreen::getExpertise(Ljava/lang/String;)("castle");

		//load language buttons
		$wnd.jQuery("#language").buttonset();
		$wnd.jQuery(".languageButton").click(function() {
			var val = $wnd.jQuery(this).val();
			pscreen.@org.sealinc.accurator.client.component.ProfileScreen::setLanguagePreference(Ljava/lang/String;)(val);
		});
		pscreen.@org.sealinc.accurator.client.component.ProfileScreen::getLanguage()();
	}-*/;

	private void setLanguagePreference(String preference) {
		// store the new preference
		Utility.storeUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(), preference,
				"string");
		// load Accurator with the new locale
		String newURL = Window.Location.createUrlBuilder().setParameter(LocaleInfo.getLocaleQueryParam(), preference).buildString();
		Window.Location.replace(newURL);
	}

	public ProfileScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
		loadData();
	}
}
