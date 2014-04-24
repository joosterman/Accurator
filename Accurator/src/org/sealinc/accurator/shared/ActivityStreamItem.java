package org.sealinc.accurator.shared;

import java.io.Serializable;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ActivityStreamItem implements IsSerializable, Serializable {

	private static final long serialVersionUID = 4817564674629988968L;
	private String text;

	public ActivityStreamItem() {

	}

	public ActivityStreamItem(String text) {
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
