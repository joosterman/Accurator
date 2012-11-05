package org.sealinc.accurator.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRecommendedItem extends JavaScriptObject{
	protected JsRecommendedItem(){}
	
	public final native String getScope() /*-{ return this.scope; }-*/;
	public final native String getURI() /*-{ return this.uri; }-*/;

}
