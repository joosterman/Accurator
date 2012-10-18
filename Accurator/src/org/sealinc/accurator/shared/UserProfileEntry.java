package org.sealinc.accurator.shared;

import java.io.Serializable;
import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Unindex;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
public class UserProfileEntry implements Serializable,IsSerializable {

	private static final long serialVersionUID = 8917703718174701287L;
	@Id
	public Long id;
	@Index
	public String user;
	@Index
	public String type;
	@Index(IfNotNull.class)
	public String topic;
	@Unindex
	public Object value;
	@Index
	public Date date;
	
	@OnSave
	public void onSave(){
		date = new Date();
	}
}
