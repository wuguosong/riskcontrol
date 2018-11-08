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
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;
/**
 * 正式评审过会后向投资系统推送评审相关信息
 * @author yaphet
 *
 */
public class TzAfterNoticeClient implements Runnable, IWebClient {
	private static Logger logger = LoggerFactory.getLogger(TzClient.class);

	private String businessId;
	private String decisionOpinion;
	private String decisionReport;


	public TzAfterNoticeClient(String businessId, String decisionOpinion,
			String decisionReport) {
		this.businessId = businessId;
		this.decisionOpinion = decisionOpinion;
		this.decisionReport = decisionReport;
	}

	public boolean resend(String json) {
		String receive = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			receive = riskService.getPfrDecisionInfo(json);
		} catch (Exception e) {
			success = false;
			logger.error(Util.parseException(e));
			return false;
		}
		data.put("receive", receive);
		data.put("content", json);
		data.put("success", success);
		data.put("type", "TZFTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
		return true;
	}
	
	@Override
	public void run() {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("CustomerId", businessId);
		content.put("DecisionOpinion", decisionOpinion);
		content.put("DecisionReport", decisionReport);
		content.put("FinalFile", null);
		ObjectMapper mapper = new ObjectMapper();
		String contentStr = null;
		try {
			contentStr = mapper.writeValueAsString(content);
			
		} catch (JsonProcessingException e) {
			contentStr = "{\"CustomerId\":\""+businessId+"\",\"DecisionOpinion\":\""+decisionOpinion+"\",\"DecisionReport\":\""+
					decisionReport+"\"}";
		}
		
		
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			receive = riskService.getPfrDecisionInfo(contentStr);
		} catch (Throwable e) {
			logger.error(Util.parseException(e));
			receive = Util.parseException(e);
			success = false;
		}
		data.put("receive", receive);
		data.put("content", contentStr);
		data.put("success", success);
		data.put("type", "TZFTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}

}
