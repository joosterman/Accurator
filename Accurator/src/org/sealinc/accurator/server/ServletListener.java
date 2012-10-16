package org.sealinc.accurator.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.sealinc.accurator.shared.UserProfileEntry;

import com.googlecode.objectify.ObjectifyService;

public class ServletListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ObjectifyService.register(UserProfileEntry.class);		
	}
}
