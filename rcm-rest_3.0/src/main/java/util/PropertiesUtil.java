package util;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertiesUtil extends PropertyPlaceholderConfigurer {
	
	private static Properties sysProperties;
	
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory,
			Properties props) throws BeansException {
		super.processProperties(beanFactory, props);
		sysProperties = props;
	}
	
	public static String getProperty(String key){
		return sysProperties.getProperty(key);
	}
}
