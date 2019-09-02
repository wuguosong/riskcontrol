package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;

import util.Util;
@Component
public class FormalAssessmentAddWaitForReviewLeaderListener implements ExecutionListener{

	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	public Expression taskdesc;
	
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String taskdescStr = (String)taskdesc.getValue(execution);
		
		String businessId = execution.getProcessBusinessKey();
		//为评审负责人提供一条待办以供修改
		HashMap<String, Object> reviewLeaderParams = new HashMap<String,Object>();
		String revieweaderId = (String) execution.getVariable("reviewLeader");
		reviewLeaderParams.put("businessId", businessId);
		reviewLeaderParams.put("auditUserId", revieweaderId);
		reviewLeaderParams.put("auditTime", Util.now());
		reviewLeaderParams.put("opinion", null);
		reviewLeaderParams.put("auditStatus", "9");
		reviewLeaderParams.put("orderBy", this.formalAssessmentAuditLogService.queryMaxOrderNum(businessId)+1);
		reviewLeaderParams.put("isWaiting", "1");
		reviewLeaderParams.put("taskdesc", taskdescStr);
		
		reviewLeaderParams.put("executionId", execution.getId());
		this.formalAssessmentAuditLogService.save(reviewLeaderParams);
	}

}
