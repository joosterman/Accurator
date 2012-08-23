package org.sealinc.accurator.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActivityStreamItem implements IsSerializable {
	private String text;	
	
	public ActivityStreamItem(){
		
	}
	public ActivityStreamItem(String text){
		this.setText(text);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
