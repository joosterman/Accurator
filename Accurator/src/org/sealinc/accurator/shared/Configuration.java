package org.sealinc.accurator.shared;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Configuration {
	@Id
	Long id;
	@Index
	public String version;
	@Index
	public String applicationId;
}
