package org.sealinc.accurator.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUserProfileEntry extends JavaScriptObject{
	protected JsUserProfileEntry(){}
	
	public final native String getDimension() /*-{ return this.dimension; }-*/;
	public final native String getScope() /*-{ return this.scope; }-*/;
	public final native String getProvider() /*-{ return this.provider; }-*/;
	public final native String getValueAsString() /*-{ return this.value; }-*/;
	public final native double getValueAsDouble() /*-{ return this.value; }-*/;
	public final native int getValueAsInt() /*-{ return this.value; }-*/;
	
}
