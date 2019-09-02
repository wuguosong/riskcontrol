/**
 * 
 */
package com.yk.util;

import javax.xml.ws.Endpoint;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.yk.common.SpringUtil;
import com.yk.rcm.ws.server.impl.ProjectForTzService;

import util.PropertiesUtil;

/**
 * @author wufucan
 *
 */
public class InstantiationTracingBeanPostProcessor implements
		ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){
			//root application context 没有parent，他就是老大.
	           //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法
			String domain = PropertiesUtil.getProperty("domain.allow");
			String path = PropertiesUtil.getProperty("ws_tz_server_addr");
			ProjectForTzService instance = (ProjectForTzService) SpringUtil.getBean("projectForTzService");
			Endpoint.publish(domain + path,  
	                instance);  
	    }
	}

}
