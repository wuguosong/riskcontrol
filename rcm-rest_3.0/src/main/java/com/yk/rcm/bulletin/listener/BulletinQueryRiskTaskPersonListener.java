package com.yk.rcm.bulletin.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;
/**
 * 
 * @author yaphet
 * 查询任务分配人，添加到流程变量中
 *
 */
@Component
public class BulletinQueryRiskTaskPersonListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateExecution execution) throws Exception {
		//taskallocation任务人员字段
		String businessKey = execution.getProcessBusinessKey();
		Map<String, Object> bulletinInfo = this.baseMongo.queryById(businessKey, Constants.RCM_BULLETIN_INFO);
		
		Map<String, Object> taskallocation = (Map<String, Object>) bulletinInfo.get("taskallocation");
		//获取评审负责人
		Map<String, Object> riskLeader = (Map<String, Object>) taskallocation.get("riskLeader");
		
		execution.setVariable("riskLeader", (String)riskLeader.get("VALUE"));
		
		this.bulletinInfoService.updateAuditStageByBusinessId(businessKey, "1.5");
		
		HashMap<String, Object> data = new HashMap<String,Object>();
		data.put("businessId", businessKey);
		if(Util.isNotEmpty(riskLeader)){
			String reviewLeaderId = (String) riskLeader.get("VALUE");
			data.put("reviewLeaderId", reviewLeaderId);
		}
		this.bulletinInfoService.updatePerson(data);
	}

}
