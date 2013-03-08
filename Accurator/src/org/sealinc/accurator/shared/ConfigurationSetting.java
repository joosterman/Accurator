package org.sealinc.accurator.shared;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ConfigurationSetting {
	@Id
	Long id;
	@Parent  
	public Key<Configuration> configuration;
	@Index
	public String name;
	public String description;
	public String value;
}
