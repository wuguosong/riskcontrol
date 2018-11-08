package com.yk.rcm.formalAssessment.listener;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.flow.util.JsonUtil;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
/**
 * 
 * @author yaphet
 * 2017年6月27日
 * 删除待办
 */
@Component
public class FormalAssessmentDeleteWaitLogListener implements TaskListener{
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService; 
	private static final long serialVersionUID = 1L;
	
	public Expression assignType;
	public Expression assignId;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		String assignTypeStr = assignType.getExpressionText();
		
		String assignIdStr = (String) assignId.getValue(delegateTask);
		
		DelegateExecution execution = delegateTask.getExecution();
		//业务id
		String businessId = execution.getProcessBusinessKey();
		//executionId
		String executionId = execution.getId();
		Map<String,Object> taskMap = new HashMap<String,Object>();
		//获取当前人id
		String userId = ThreadLocalUtil.getUserId();
		taskMap.put("businessId", businessId);
		taskMap.put("isWaiting", "1");
		taskMap.put("currentUserId", userId);
		taskMap.put("executionId", executionId);
		formalAssessmentAuditLogService.deleteWaitlogs(taskMap);
		
		String description = delegateTask.getDescription();
		if(Util.isNotEmpty(description)){
			Map<String, Object> fromJson = JsonUtil.fromJson(description, Map.class);
			if("1".equals(fromJson.get("isHuiqian"))){
				String sequenceFlow = (String) execution.getVariable("sequenceFlow");
				if("1".equals(sequenceFlow)){
					this.formalAssessmentAuditLogService.deleteHuiQianTaskByExecutionId(executionId);
				}
			}
		}
	}
}
