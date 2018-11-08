package com.yk.rcm.formalAssessment.listener;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
/**
 * 
 * @author yaphet
 * 2018年3月29日
 * 验证分管领导意见是否同意
 */
@Component
public class FormalCheckBusinessAgreeListener implements TaskListener{
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		String flowId = (String) execution.getVariable("sequenceFlow");
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		if("1".equals(flowId)){
			execution.setVariable(taskDefinitionKey+"_huiqianBack", flowId);
		}
	}
}
