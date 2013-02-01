package org.sealinc.accurator.client.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.sealinc.accurator.client.Accurator;
import org.sealinc.accurator.client.JsUserProfileEntry;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.CollectionItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ProfileScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private int latestPrintColumns = 5;
	private int nrLatestPrint = 10;
	private int latestPrintRows = nrLatestPrint / latestPrintColumns + 1;
	private List<String> interests = new ArrayList<String>();
	Accurator accurator;
	List<HandlerRegistration> regs = new ArrayList<HandlerRegistration>();

	DateTimeFormat df = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

	@UiField
	Grid grAnnotatedPrints;
	@UiField
	Label lblTotalAnnotated;
	@UiField
	TextBox tbInterest;
	@UiField
	Button btnAddInterest;
	@UiField
	FlowPanel pnlInterests;

	@UiHandler("tbInterest")
	void tbInterest_KeyPressed(KeyPressEvent e) {
		if (e.getCharCode() == 13) {
			btnAddInterest.click();
		}
	}

	@UiHandler("btnAddInterest")
	void btnAddInterest_Click(ClickEvent e) {
		final String interest = tbInterest.getText();
		// check if we already know the interest for this user
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				if (entries.length() == 0) {
					// store the interest
					Utility.storeUserProfileEntry(Utility.getQualifiedUsername(), "interest", interest, Utility.getQualifiedUsername(),
							Boolean.TRUE.toString(), null);
					// add the interest to the interest list
					interests.add(interest);
					// sort the interest list
					Collections.sort(interests, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareToIgnoreCase(o2);
						}
					});
					// reload the interest block
					loadInterests();
				}
				tbInterest.setText("");
			}

			@Override
			public void onError(Request request, Throwable exception) {

			}
		};
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "interest", interest, Utility.getQualifiedUsername(), callback);
	}


	private void loadInterests() {
		// clear the current
		pnlInterests.clear();
		// add all the interest as a link button
		for (final String interest : interests) {
			Anchor a = new Anchor(interest);
			a.setStyleName("interest button");
			pnlInterests.add(a);
			a.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Utility.storeUserProfileEntry(Utility.getQualifiedUsername(), "interest", interest, Utility.getQualifiedUsername(),
							Boolean.FALSE.toString(), null);
					interests.remove(interest);
					loadInterests();
				}
			});
		}
		// reload theme element
		loadUIThemeElements();
	}

	public void loadData() {
		// total number of annotated prints
		Utility.userService.getTotalAnnotatedPrints(Utility.getStoredUsername(), null, new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				lblTotalAnnotated.setText(result.toString());
			}

			@Override
			public void onFailure(Throwable caught) {}
		});

		// get annotated prints
		Utility.userService.getLastAnnotatedItems(Utility.getStoredUsername(), 10, new AsyncCallback<List<CollectionItem>>() {

			@Override
			public void onSuccess(List<CollectionItem> result) {
				// clear all handlers
				for (HandlerRegistration reg : regs) {
					reg.removeHandler();
				}
				regs.clear();

				grAnnotatedPrints.clear();
				grAnnotatedPrints.resize(latestPrintRows, latestPrintColumns);
				Image im;
				int col = 0;
				int row = 0;
				// load prints annotated
				for (int i = 0; i < result.size() && i < nrLatestPrint; i++) {
					final CollectionItem ci = result.get(i);
					im = new Image(ci.thumbnailURL);
					im.setStyleName("imageButton");
					HandlerRegistration reg = im.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							accurator.annotate(ci.uri);
							History.newItem(Accurator.State.Annotate.toString());
						}
					});
					regs.add(reg);
					grAnnotatedPrints.setWidget(row, col, im);
					col++;
					if (col == latestPrintColumns) {
						col = 0;
						row++;
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {}
		});

		// get interests
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "interest", null, Utility.getQualifiedUsername(), new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				interests.clear();
				// parse the interests from the UP
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				for (int i = 0; i < entries.length(); i++) {
					JsUserProfileEntry entry = entries.get(i);
					if (Boolean.TRUE.toString().equals(entry.getValueAsString())) {
						interests.add(entry.getScope());
					}
				}
				loadInterests();
			}

			@Override
			public void onError(Request request, Throwable exception) {

			}
		});

		// get expertise
		getExpertise("flora");
		getExpertise("castle");
		/*Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "expertise", null, Utility.getQualifiedUsername(), new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				JsArray<JsUserProfileEntry> entries = Utility.parseUserProfileEntry(response.getText());
				for (int i = 0; i < entries.length(); i++) {
					JsUserProfileEntry entry = entries.get(i);
					updateExpertise(entry.getScope(), entry.getValueAsDouble());
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {}
		});*/
		/*
		 * // for each expertise topic create a slider for (final String topic :
		 * Config.profileExpertises) { // used for the class of the slider String
		 * sanitizedTopic = topic.replace(" ", ""); // the translated version of the
		 * topic String localTopic = null; try { localTopic =
		 * Utility.constants.getString(topic); } catch (MissingResourceException ex)
		 * { localTopic = topic; } // capitalize the first letter of the topic
		 * localTopic = localTopic.substring(0, 1); // first add the header with the
		 * expertise Label header = new Label("<h3>" + localTopic + " " +
		 * "expertise</h3>"); pnlExpertises.add(header); // add a vertical panel
		 * that includes the lay/expert and slider HorizontalPanel pnlSlider = new
		 * HorizontalPanel(); Label lay = new Label(Utility.constants.lay());
		 * SimplePanel slider = new SimplePanel();
		 * slider.setStyleName(sanitizedTopic); Label expert = new
		 * Label(Utility.constants.expert()); pnlSlider.add(lay);
		 * pnlSlider.add(slider); pnlSlider.add(expert);
		 * pnlExpertises.add(pnlSlider); }
		 */
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
				if (entries != null && entries.length() > 0) {
					expertise = entries.get(0).getValueAsDouble();
				}
				initSlider(topic, expertise);
			}

			@Override
			public void onError(Request request, Throwable exception) {}
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
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub

			}
		};
		// Execute the call
		Utility.getUserProfileEntry(Utility.getQualifiedUsername(), "languagePreference", null, Utility.getQualifiedUsername(), callback);
	}

	private native void updateLanguageButtons(String language) /*-{
		$wnd.jQuery("#language" + language.toUpperCase()).attr("checked", true);
		$wnd.jQuery("#language").buttonset("refresh");
	}-*/;

	private void updateExpertise(String topic, double value) {
		accurator.updateExpertise(topic, value);
	}

	private void userPropertyChanged(String dimension) {
		accurator.userPropertyChanged(dimension);
	}

	private native void initSlider(String topic, double val)/*-{
		var user = @org.sealinc.accurator.client.Utility::getQualifiedUsername()();
		var pscreen = this;
		$wnd
			.jQuery("#" + topic + "Slider")
			.slider({
				min : 0,
				max : 1,
				step : 0.1,
				range : "min",
				value : val,
				stop : function(event, ui) {
					@org.sealinc.accurator.client.Utility::storeUserProfileEntry(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(user,"expertise",topic,user,""+ui.value,"double");
					pscreen.@org.sealinc.accurator.client.component.ProfileScreen::updateExpertise(Ljava/lang/String;D)(topic,ui.value);
					pscreen.@org.sealinc.accurator.client.component.ProfileScreen::userPropertyChanged(Ljava/lang/String;)("expertise");
				},
			});
	}-*/;

	private void changeLanguage(String language) {
		accurator.changeLanguage(language);
	}

	public native void loadUIThemeElements()/*-{
		var pscreen = this;

		$wnd.jQuery("a.interest").button({
			icons : {
				secondary : "ui-icon-close"
			}
		});

		//load language buttons
		$wnd.jQuery("#language").buttonset();
		$wnd.jQuery(".languageButton").click(function() {
			var val = $wnd.jQuery(this).val();
			pscreen.@org.sealinc.accurator.client.component.ProfileScreen::changeLanguage(Ljava/lang/String;)(val);
		});
		pscreen.@org.sealinc.accurator.client.component.ProfileScreen::getLanguage()();
	}-*/;

	public ProfileScreen(Accurator acc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.accurator = acc;
	}
}
