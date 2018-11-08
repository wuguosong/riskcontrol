package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;

@Component
public class FormalAssessmentAuditStageListener implements TaskListener , ExecutionListener{
	public Expression stage;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	
	private static final long serialVersionUID = 1L;

	/**
	 * 节点
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		
		DelegateExecution execution = delegateTask.getExecution();
		
		String businessKey = execution.getProcessBusinessKey();
		//阶段
		String stageStr = stage.getExpressionText();
		
		this.updateAuditStageByBusinessId(businessKey, stageStr);
	}
	/**
	 * 线
	 */
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessKey = execution.getProcessBusinessKey();
		//阶段
		String stageStr = (String)stage.getValue(execution);
		this.updateAuditStageByBusinessId(businessKey, stageStr);
	}
	
	private void updateAuditStageByBusinessId(String businessId,String stage){
		Map<String, Object> data = new HashMap<String,Object>();
		formalAssessmentAuditService.updateAuditStageByBusinessId(businessId,stage);
	}
	
	
}
