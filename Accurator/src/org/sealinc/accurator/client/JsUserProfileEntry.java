package org.sealinc.accurator.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUserProfileEntry extends JavaScriptObject{
	protected JsUserProfileEntry(){}
	
	public final native String getUser() /*-{ return this.user; }-*/;
	public final native String getType() /*-{ return this.type; }-*/;
	public final native String getTopic() /*-{ return this.topic; }-*/;
	public final native String getValueAsString() /*-{ return this.value; }-*/;
	public final native double getValueAsDouble() /*-{ return this.value; }-*/;
	
}
