/**
 * 
 */
package com.yk.rcm.pre.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import util.PropertiesUtil;
import util.Util;
import ws.client.TzClient;

import com.yk.common.IBaseMongo;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;

/**
 * 流程启动监听器
 * @author yaphet
 */
@Component
public class PreAuditStartListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Resource 
	private IPreInfoService preInfoService;
	@Resource 
	private IBaseMongo baseMongo;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessId = execution.getProcessBusinessKey();
		//修改申请时间
		this.preInfoService.updateApplyDate(businessId);
		//修改状态
		this.preInfoService.updateAuditStatusByBusinessId(businessId, "1");
		
		//添加流程变量
		//查mongo根据id查formalAssessment
		Map<String, Object> preMongo = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		Map<String, Object> apply = (Map<String, Object>) preMongo.get("apply");
		//投资经理
		Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
		String investmentManagerValue = (String) investmentManager.get("VALUE");
		
		//大区companyHeader
		Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
		String companyHeaderValue = (String)companyHeader.get("VALUE");
		
		//验证水环境
		List<Map<String, Object>> serviceTypeList = (List<Map<String, Object>>) apply.get("serviceType");
		String isSkipServiceTypeAudit = "1";
		if(Util.isNotEmpty(serviceTypeList)){
			for (Map<String, Object> serviceType : serviceTypeList) {
				String serviceTypeKey = (String) serviceType.get("KEY");
				//水环境1402
				//传统水务1401
				if("1402".equals(serviceTypeKey) || "1401".equals(serviceTypeKey)){
					isSkipServiceTypeAudit ="0";
				}
			}
		}
		
		//区域负责人——投资中心/水环境
		Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
		String serviceTypeValue = null;
		if(investmentPerson != null){
			serviceTypeValue = (String) investmentPerson.get("VALUE");
		}
		//流程变量赋值
		execution.setVariable("investmentManager", investmentManagerValue);
		execution.setVariable("largeArea", companyHeaderValue);
		execution.setVariable("isSkipServiceTypeAudit", isSkipServiceTypeAudit);
		execution.setVariable("serviceType", serviceTypeValue);
		
		String serverPath = PropertiesUtil.getProperty("domain.allow");
		StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
		url.append("PreAuditDetailView/");
		url.append(businessId);
		String oldurl = "#/";
		String ntUrl = Util.encodeUrl(oldurl);
		url.append("/"+ntUrl);
		//调用投资接口，通知他们更新项目状态
//		TzClient client = new TzClient();
//		client.setBusinessId(businessId);
//		client.setStatus("1");
//		client.setLocation(url.toString());
//		Thread t = new Thread(client);
//		t.start();
	}

}
