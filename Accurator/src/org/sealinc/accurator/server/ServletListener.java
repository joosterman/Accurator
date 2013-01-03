package org.sealinc.accurator.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.sealinc.accurator.shared.Configuration;
import org.sealinc.accurator.shared.ConfigurationSetting;

import com.googlecode.objectify.ObjectifyService;

public class ServletListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ObjectifyService.register(UserProfileEntry.class);		
		ObjectifyService.register(User.class);		
		ObjectifyService.register(Configuration.class);		
		ObjectifyService.register(ConfigurationSetting.class);		
	}
}
