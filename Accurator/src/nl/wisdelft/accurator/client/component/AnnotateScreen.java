package nl.wisdelft.accurator.client.component;


import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.NamedFrame;

public class AnnotateScreen extends Composite {

	public AnnotateScreen() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(10);
		initWidget(horizontalPanel);
		horizontalPanel.setSize("100%", "100%");
		
		NamedFrame annotationFrame = new NamedFrame("annotationFrame");
		annotationFrame.setStyleName("gwt-Frame-Annotate");
		annotationFrame.setUrl("http://e-culture.multimedian.nl/sealinc/annotate");
		horizontalPanel.add(annotationFrame);
		annotationFrame.setSize("800px", "800px");
		
		RecommendedItems recommendedItems = new RecommendedItems();
		horizontalPanel.add(recommendedItems);
		recommendedItems.setSize("100%", "");
	}

}
