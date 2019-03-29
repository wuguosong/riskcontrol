package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.exception.BusinessException;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;

/**
 * 
 * @author yaphet
 * 
 * 2018年3月30日
 *
 */
@Component
public class HuiqianTaskCreateListener  implements TaskListener {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		execution.setVariable(taskDefinitionKey+"_huiqianBack", "0");
	}
	
}
