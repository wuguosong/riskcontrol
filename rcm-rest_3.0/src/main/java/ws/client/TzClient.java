/**
 * 
 */
package ws.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;
import ws.client.tz.RiskService;
import ws.client.tz.RiskServiceSoap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;

/**
 * 投资接口异步调用
 * @author 80845530
 *
 */
public class TzClient implements Runnable, IWebClient {
	private static Logger logger = LoggerFactory.getLogger(TzClient.class);
	
	private String businessId;
	private String status;
	private String location;
	
	public TzClient(){
	}
	public TzClient(String businessId, String status, String location){
		this.businessId = businessId;
		this.status = status;
		this.location = location;
	}
	
	public boolean resend(String json){
		Gson gs = new Gson();
		Map<String, Object> map = gs.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
		this.businessId = (String) map.get("CustomerId");
		this.status = (String) map.get("RiskStatus");
		this.location = (String) map.get("AuditReport");
		try {
//			RiskServiceSoap tzClientProxy = (RiskServiceSoap) SpringUtil.getBean("tzClientProxy");
//			tzClientProxy.updateRiskAuditInfo(businessId, status, location);
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			riskService.updateRiskAuditInfo(businessId, status, location);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		Map<String, Object> content = new HashMap<String, Object>();
		if("".equals(status)){
			return;
		}
		content.put("CustomerId", businessId);
		content.put("RiskStatus", status);
		content.put("AuditReport", location);
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			receive = riskService.updateRiskAuditInfo(businessId, status, location);
//			Map<String, Object> requestContext = ((BindingProvider)riskService).getRequestContext();
			// Timeout in millis
//			requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 4000); 
//			requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 1500); 
//			receive = riskService.updateRiskAuditInfo(businessId, status, location);
//			RiskServiceSoap tzClientProxy =  (RiskServiceSoap) SpringUtil.getBean("tzClientProxy");
//			receive = tzClientProxy.updateRiskAuditInfo(businessId, status, location);
		} catch (Throwable e) {
			logger.error(Util.parseException(e));
			receive = Util.parseException(e);
			success = false;
		}
		ObjectMapper mapper = new ObjectMapper();
		String contentStr = null;
		try {
			contentStr = mapper.writeValueAsString(content);
		} catch (JsonProcessingException e) {
			contentStr = "businessId:"+businessId+";status:"+status+";location:"+location;
		}
		data.put("receive", receive);
		data.put("content", contentStr);
		data.put("success", success);
		data.put("type", "TZPS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}
	
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
