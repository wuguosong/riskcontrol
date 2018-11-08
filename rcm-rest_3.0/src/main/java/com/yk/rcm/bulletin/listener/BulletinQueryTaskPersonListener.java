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
public class BulletinQueryTaskPersonListener implements ExecutionListener{

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
		//获取法律负责人
		Map<String, Object> legalLeader = (Map<String, Object>) taskallocation.get("legalLeader");
		//获取评审负责人
		Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
		
		if(Util.isEmpty(legalLeader)){
			execution.setVariable("legalLeader", null);
		}else{
			execution.setVariable("legalLeader", (String)legalLeader.get("VALUE"));
		}
		execution.setVariable("reviewLeader", (String)reviewLeader.get("VALUE"));
		
		this.bulletinInfoService.updateAuditStageByBusinessId(businessKey, "1.5");
		
		
		HashMap<String, Object> data = new HashMap<String,Object>();
		if(Util.isNotEmpty(legalLeader)){
			String legalLeaderId = (String) legalLeader.get("VALUE");
			data.put("legalLeaderId", legalLeaderId);
		}
		
		if(Util.isNotEmpty(reviewLeader)){
			String reviewLeaderId = (String) reviewLeader.get("VALUE");
			data.put("reviewLeaderId", reviewLeaderId);
		}
		data.put("businessId", businessKey);
		this.bulletinInfoService.updatePerson(data);
	}

}
