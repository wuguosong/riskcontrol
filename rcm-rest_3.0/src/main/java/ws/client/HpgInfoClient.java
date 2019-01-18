/**
 * 
 */
package ws.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import util.Util;
import ws.client.evaluation.TaskManger;
import ws.client.evaluation.TaskMangerPortType;
import ws.client.tz.RiskService;
import ws.client.tz.RiskServiceSoap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.project.service.IWsCallService;

import common.Constants;

/**
 * 后评估接口异步调用
 * @author yaphet
 *
 */
public class HpgInfoClient implements Runnable, IWebClient {
	
	private static Logger logger = LoggerFactory.getLogger(HpgInfoClient.class);
	
	private String projectcode;
	private String bin_pattern;
	private String pro_size;
	private String pro_type;
	private String pro_unit;
	private String investtime;
	private String investopinion;
	private String investdept;
	private String investmanager;
	private String pre_matter;
	private String after_matter;
	
	public HpgInfoClient(String businessId) {
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		IFormalAssessmentInfoService formalAssessmentInfoService = (IFormalAssessmentInfoService) SpringUtil.getBean("formalAssessmentInfoService");
		
		BasicDBObject filter =new BasicDBObject();
		filter.put("projectFormalId", businessId);
		Map<String, Object> oracleByBusinessId = formalAssessmentInfoService.getOracleByBusinessId(businessId);
		Map<String, Object> formalInfo = baseMongo.queryById(businessId,Constants.RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> noticeDecision = baseMongo.queryByCondition(filter, Constants.RCM_NOTICEDECISION_INFO).get(0);
		Map<String, Object> apply = (Map<String, Object>) formalInfo.get("apply");
		this.projectcode = apply.get("projectNo") == null?"":(String) apply.get("projectNo");
		ArrayList<Map<String, Object>> projectModel = (ArrayList<Map<String, Object>>) apply.get("projectModel");
		String bin_pattern = "";
		if(Util.isNotEmpty(projectModel) && projectModel.size()>0){
			for (Map<String, Object> p : projectModel) {
				bin_pattern += ","+p.get("VALUE");
			}
			bin_pattern = bin_pattern.substring(1);
		}
		this.pro_size = noticeDecision.get("evaluationScale") == null?"":(String) noticeDecision.get("evaluationScale"); 
		List<Map<String, Object>> rlist = baseMongo.queryByCondition(filter,Constants.RCM_FORMALREPORT_INFO);
		String pro_type= "";
		// 去掉原因，修改版本后，正式评审-提交决策会材料，projectType字段去掉了 2019-01-18
		/*if(rlist.size()>0){
			Map<String, Object> report = rlist.get(0);
			Map<String, Object> policyDecision =(Map<String, Object>) report.get("policyDecision");
			Map<String, Object> projectType =(Map<String, Object>) policyDecision.get("projectType");
			String item_name =(String) projectType.get("ITEM_NAME");
			pro_type =item_name;
		}*/
		
		Map<String, Object> subjectOfImplementation = (Map<String, Object>) noticeDecision.get("subjectOfImplementation");
		this.pro_unit = subjectOfImplementation == null?"":(String) subjectOfImplementation.get("value");
		String investtime = "";
		if(Util.isNotEmpty(oracleByBusinessId.get("NOTICE_CONFIRM_TIME"))){
			Date notice_confirm_time = (Date) oracleByBusinessId.get("NOTICE_CONFIRM_TIME");
			investtime = Util.format(notice_confirm_time);
		}
		
		this.investtime = investtime;
		String consentToInvestment = (String) noticeDecision.get("consentToInvestment");
		if(consentToInvestment == null){
			this.investopinion = "";
		}else if("1".equals(consentToInvestment)){
			this.investopinion = "同意投资";
		}else if("2".equals(consentToInvestment)){
			this.investopinion = "不同意投资";
		}else if("3".equals(consentToInvestment)){
			this.investopinion = "同意有条件投资";
		}else if("4".equals(consentToInvestment)){
			this.investopinion = "择期决议";
		}
		Map<String, Object> responsibilityUnit = (Map<String, Object>) noticeDecision.get("responsibilityUnit");
		this.investdept = responsibilityUnit == null?"":(String) responsibilityUnit.get("value");
		
		ArrayList<Map<String, Object>> personLiable = (ArrayList<Map<String, Object>>) noticeDecision.get("personLiable");
		String investmanager = "";
		if(Util.isNotEmpty(personLiable)){
			for (Map<String, Object> p : personLiable) {
				investmanager += ","+p.get("value");
			}
			investmanager = investmanager.substring(1);
		}
		this.pre_matter = (String) noticeDecision.get("implementationMatters"); 
		this.after_matter = (String) noticeDecision.get("implementationRequirements"); 
		
		this.bin_pattern = bin_pattern;
		this.pro_type = pro_type;
		this.investmanager = investmanager;
	}

	public boolean resend(String json){
		try {
			TaskManger proxy = new TaskManger();
			TaskMangerPortType taskMangerHttpPort = proxy.getTaskMangerHttpPort();
			taskMangerHttpPort.aftHand(json);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		Map<String, Object> content = new HashMap<String, Object>();
		
		content.put("projectcode", projectcode);
		content.put("bin_pattern", bin_pattern);
		content.put("pro_size", pro_size);
		content.put("pro_type", pro_type);
		content.put("pro_unit", pro_unit);
		content.put("investtime", investtime);
		content.put("investopinion", investopinion);
		content.put("Investdept", investdept);
		content.put("investmanager", investmanager);
		content.put("pre_matter", pre_matter);
		content.put("after_matter", after_matter);
		String json = JsonUtil.toJson(content);
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		try {
			TaskManger proxy = new TaskManger();
			TaskMangerPortType taskMangerHttpPort = proxy.getTaskMangerHttpPort();
			receive = taskMangerHttpPort.aftHand(json);
		} catch (Throwable e) {
			logger.error(Util.parseException(e));
			receive = Util.parseException(e);
			success = false;
		}
		data.put("receive", receive);
		data.put("content", json);
		data.put("success", success);
		data.put("type", "HPGTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}
}
