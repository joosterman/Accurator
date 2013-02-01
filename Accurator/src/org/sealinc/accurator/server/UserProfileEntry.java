package org.sealinc.accurator.server;

import java.io.Serializable;
import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindex;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
public class UserProfileEntry implements Serializable,IsSerializable {

	private static final long serialVersionUID = 8917703718174701287L;
	@Id
	public transient Long id;
	@Parent  
	public transient Key<User> user;
	@Index
	public String dimension;
	@Index(IfNotNull.class)
	public String scope;
	@Index
	public Object value;
	@Index
	public String provider;
	@Index
	public Date lastChanged;
	
	@OnSave
	public void onSave(){
		lastChanged = new Date();
	}
}
