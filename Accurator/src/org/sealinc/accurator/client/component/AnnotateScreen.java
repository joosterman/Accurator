package org.sealinc.accurator.client.component;


import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Config;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.SubmitButton;

public class AnnotateScreen extends Composite {
	private Hidden target;
	public AnnotateScreen() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("annotate");
		initWidget(horizontalPanel);
		
		NamedFrame annotationFrame = new NamedFrame("annotationFrame");
		annotationFrame.setStyleName("annotationFrame");
		//annotationFrame.setUrl(Config.getAnnotationComponentURL());
		horizontalPanel.add(annotationFrame);
		
		RecommendedItems recommendedItems = new RecommendedItems();
		horizontalPanel.add(recommendedItems);
		recommendedItems.setStyleName("recommendationBlock");
		
		FormPanel formPanel = new FormPanel("annotationFrame");
		formPanel.setStyleName("doneButton");
		horizontalPanel.add(formPanel);
		formPanel.setAction(Config.getAnnotationComponentURL());
		formPanel.setMethod("GET");
		horizontalPanel.add(formPanel);
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		formPanel.setWidget(horizontalPanel_1);
		
		SubmitButton tbDone = new SubmitButton("Volgende");
		horizontalPanel_1.add(tbDone);
		tbDone.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Utility.assignService.getNextItemsToAnnotate(1, new AsyncCallback<List<String>>() {
					
					@Override
					public void onSuccess(List<String> result) {
						target.setValue(result.get(0));						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		});
		
		target = new Hidden();
		target.setName("target");
		horizontalPanel_1.add(target);
	}

}
