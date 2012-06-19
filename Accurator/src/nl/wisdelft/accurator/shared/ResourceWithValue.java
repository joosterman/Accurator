package nl.wisdelft.accurator.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResourceWithValue<T extends Serializable> implements IsSerializable {
	private String resource;
	private T objectValue;
		
	private ResourceWithValue(){}
	
	public ResourceWithValue(String resource){
		this.resource = resource;
		this.objectValue = null;
	}
	
	public ResourceWithValue(String resource, T value){
		this.resource = resource;
		this.objectValue = value;
	}

	public String getObjectURI() {
		return resource;
	}

	public T getObjectValue() {
		return objectValue;
	}	
}
