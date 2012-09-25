package org.sealinc.accurator.client.component;

import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Annotation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileScreen extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ProfileScreen> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	VerticalPanel tagsUsed;
	@UiField
	Button refresh;

	@UiHandler("refresh")
	void handleClick(ClickEvent e) {
		loadData();
	}

	private void loadData() {
		tagsUsed.clear();
		Utility.userService.getAnnotations(Utility.getUser(), new AsyncCallback<List<Annotation>>() {

			@Override
			public void onSuccess(List<Annotation> result) {
				Label l = null;
				for (Annotation a : result) {
					l = new Label(a.hasBody);
					tagsUsed.add(l);
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

	}

	public ProfileScreen() {
		initWidget(uiBinder.createAndBindUi(this));
		loadData();
	}
}
