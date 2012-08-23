package org.sealinc.accurator.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class RDFObject implements IsSerializable {
	public String uri;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RDFObject)
			return ((RDFObject) o).uri.equals(this.uri);
		else
			return false;
	}
	
	@Override
	public int hashCode(){
		return uri.hashCode();
	}

	@Override
	public String toString(){
		return uri;
	}
}
