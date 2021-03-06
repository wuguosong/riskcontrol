/**
 * 
 */
package ws.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;
import ws.client.evaluation.TaskManger;
import ws.client.evaluation.TaskMangerPortType;
import ws.client.tz.RiskService;
import ws.client.tz.RiskServiceSoap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;

import common.Constants;

/**
 * 后评估接口异步调用
 * @author yaphet
 *
 */
public class HpgClient implements Runnable, IWebClient {
	private static Logger logger = LoggerFactory.getLogger(HpgClient.class);
	
	private String projectNo;
	private String projectName;
	private String reviewLeaderId;
	
	public HpgClient(String businessId, String reviewLeaderId){
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> apply = (Map<String, Object>) queryById.get("apply");
		this.projectNo = (String) apply.get("projectNo");
		this.projectName = (String) apply.get("projectName");
		this.reviewLeaderId = reviewLeaderId;
	}
	public boolean resend(String json){
		Gson gs = new Gson();
		Map<String, Object> map = gs.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
		this.projectNo = (String) map.get("revcode");
		this.projectName = (String) map.get("rev_name");
		this.reviewLeaderId = (String) map.get("review");
		try {
			TaskManger proxy = new TaskManger();
			TaskMangerPortType taskMangerHttpPort = proxy.getTaskMangerHttpPort();
			taskMangerHttpPort.aftReview(projectNo, projectName, reviewLeaderId);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		Map<String, Object> content = new HashMap<String, Object>();
		if("".equals(projectNo)){
			return;
		}
		content.put("revcode", projectNo);
		content.put("rev_name", projectName);
		content.put("review", reviewLeaderId);
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			TaskManger proxy = new TaskManger();
			TaskMangerPortType taskMangerHttpPort = proxy.getTaskMangerHttpPort();
			receive = taskMangerHttpPort.aftReview(projectNo, projectName, reviewLeaderId);
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
			contentStr = "projectNo:"+projectNo+";projectName:"+projectName+";reviewLeaderId:"+reviewLeaderId;
		}
		data.put("receive", receive);
		data.put("content", contentStr);
		data.put("success", success);
		data.put("type", "HPGTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}
	
}
