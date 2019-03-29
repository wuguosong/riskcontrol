/**
 * 
 */
package com.yk.common;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import sys.filter.DispatcherFilter;
import util.Util;

/**
 * @author 80845530
 *
 */
public class SpringUtil implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(DispatcherFilter.class);
	
	private static ApplicationContext applicationContext;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringUtil.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}
	/**
	 * 获取spring核心容器中的bean,如果没有，会尝试采用参数name去注册bean
	 * @param name
	 * @return
	 */
	public static Object getBean(String name){
		Object obj = null;
		if(null != name && applicationContext.containsBean(name)){
			obj = applicationContext.getBean(name);
		}else if(null != name && name.indexOf(".") > 0){
			//判断name中有没有点，如果有，说明是旧请求，要判断类是否存在
			try {
				Class.forName(name);
			} catch (ClassNotFoundException e) {
				logger.error(Util.parseException(e));
				return null;
			}
			obj = registerMyBean(name);
		}else{
			throw new RuntimeException("找不到beanId为["+name+"]对象!");
		} 
		return obj;
	}
	
	public static Object getBean(Class<?> cla){
		return applicationContext.getBean(cla);
	}
	
	private synchronized static Object registerMyBean(String name){
		ConfigurableApplicationContext cac = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) cac.getBeanFactory();
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(name);
		beanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getRawBeanDefinition());
		return beanFactory.getBean(name);
	}
	
}
