package ws.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;
import ws.client.tz.RiskService;
import ws.client.tz.RiskServiceSoap;

import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IWsCallService;
/**
 * 投标评审后向投资系统推送评审相关信息
 * @author yaphet
 *
 */
public class TzAfterPreReviewClient implements Runnable, IWebClient {
	private static Logger logger = LoggerFactory.getLogger(TzClient.class);

	private String businessId;
	private String isNeedMeeting;
	private String decisionOpinion;
	private String remark;
	private String riskStatus;

	public TzAfterPreReviewClient(String businessId,String remark) {
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		IPreInfoService preInfoService = (IPreInfoService) SpringUtil.getBean("preInfoService");
		Map<String, Object> fOracle = preInfoService.getOracleByBusinessId(businessId);
		String needMeeting = (String) fOracle.get("NEED_MEETING");
		String wf_state = (String) fOracle.get("WF_STATE");
		if("1".equals(needMeeting)){
			needMeeting = "true";
		}else{
			needMeeting = "false";
		}
		String decisionOpinion = null;
		if(!"2".equals(wf_state)){
			if(Util.isEmpty(fOracle.get("DECISIONOPINION"))){
				decisionOpinion="2";
			}else{
				decisionOpinion = (String) fOracle.get("DECISIONOPINION");
			}
		}else{
			decisionOpinion = null;
		}
		this.businessId = businessId;
		this.isNeedMeeting = needMeeting;
		this.decisionOpinion = decisionOpinion;
		this.remark = remark;
	}
	
	public TzAfterPreReviewClient(String businessId,String decisionOpinion,String remark) {
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		IPreInfoService preInfoService = (IPreInfoService) SpringUtil.getBean("preInfoService");
		Map<String, Object> fOracle = preInfoService.getOracleByBusinessId(businessId);
		String needMeeting = (String) fOracle.get("NEED_MEETING");
		if("1".equals(needMeeting)){
			needMeeting = "true";
		}else{
			needMeeting = "false";
		}
		this.businessId = businessId;
		this.isNeedMeeting = needMeeting;
		this.decisionOpinion = decisionOpinion;
		this.remark = remark;
	}
	/**
	 * 只有流程评审负责人确认点的时候调用此方法
	 * @param businessId
	 * @param decisionOpinion
	 * @param remark
	 * @param riskStatus
	 */
	public TzAfterPreReviewClient(String businessId,String decisionOpinion,String remark,String riskStatus) {
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		IPreInfoService preInfoService = (IPreInfoService) SpringUtil.getBean("preInfoService");
		Map<String, Object> fOracle = preInfoService.getOracleByBusinessId(businessId);
		String needMeeting = (String) fOracle.get("NEED_MEETING");
		if("1".equals(needMeeting)){
			needMeeting = "true";
		}else{
			needMeeting = "false";
		}
		this.businessId = businessId;
		this.isNeedMeeting = needMeeting;
		this.decisionOpinion = decisionOpinion;
		this.remark = remark;
		this.riskStatus = riskStatus;
	}
	
	public boolean resend(String json) {
		String receive = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			receive = riskService.getPreDecisionInfo(json);
		} catch (Exception e) {
			success = false;
			logger.error(Util.parseException(e));
			return false;
		}
		data.put("receive", receive);
		data.put("content", json);
		data.put("success", success);
		data.put("type", "TZPTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
		return true;
	}

	@Override
	public void run() {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("CustomerId", businessId);
		content.put("IsHaveMeeting", isNeedMeeting);
		content.put("DecisionOpinion", decisionOpinion);
		content.put("Remark", remark);
		content.put("FinalFile", null);
		content.put("RiskStatus", riskStatus);
		
		String json = JsonUtil.toJson(content);
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			RiskService proxy = new RiskService();
			RiskServiceSoap riskService = proxy.getRiskServiceSoap();
			receive = riskService.getPreDecisionInfo(json);
		} catch (Throwable e) {
			logger.error(Util.parseException(e));
			receive = Util.parseException(e);
			success = false;
		}
		data.put("receive", receive);
		data.put("content", json);
		data.put("success", success);
		data.put("type", "TZPTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}

}
