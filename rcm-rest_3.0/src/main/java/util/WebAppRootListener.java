package util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.util.StringUtils;

public class WebAppRootListener implements ServletContextListener {
	public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";
	public static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.setProperty("DEBUG.MONGO", "true");
		ServletContext servletContext = sce.getServletContext();
		String root = servletContext.getRealPath("/");
		if (root == null) {
			throw new IllegalStateException(
				"Cannot set web app root system property when WAR file is not expanded");
		}
		String param = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		String key = (param != null ? param : DEFAULT_WEB_APP_ROOT_KEY);
		String oldValue = System.getProperty(key);
		if (oldValue != null && !StringUtils.pathEquals(oldValue, root)) {
			throw new IllegalStateException(
				"Web app root system property already set to different value: '" +
				key + "' = [" + oldValue + "] instead of [" + root + "] - " +
				"Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
		}
		System.setProperty(key, root);
		System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String param = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		String key = (param != null ? param : DEFAULT_WEB_APP_ROOT_KEY);
		System.getProperties().remove(key);
	}

}
