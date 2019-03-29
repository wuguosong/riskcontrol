/**
 * 
 */
package ws.service;

import javax.xml.ws.Endpoint;

import util.PropertiesUtil;

/**
 * @Description: jax-ws发布webservice
 * @Author zhangkewei
 * @Date 2016年11月23日 下午4:31:07  
 */
public class WebserviceContext {
	
	private final static String wsPort = "9070";
	
	public static void publish(){
		String domain = PropertiesUtil.getProperty("domain.allow").replaceFirst(":\\d*$", "");
		String preFix = domain+":"+wsPort+"/WebService";
		Endpoint.publish(preFix+"/WebServiceForTZ", new WebServiceForTZ());
	}
}
