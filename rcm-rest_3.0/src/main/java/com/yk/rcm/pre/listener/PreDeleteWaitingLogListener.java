package com.yk.rcm.pre.listener;

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
import com.yk.rcm.pre.service.IPreAuditLogService;

/**
 * @author yaphet
 * 出节点删除待办日志
 */
@Component
public class PreDeleteWaitingLogListener implements TaskListener{

	private static final long serialVersionUID = 1L;
	@Resource
	private IPreAuditLogService preAuditLogService;
	
	public Expression assignType;
	public Expression assignId;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		//业务id
		String businessId = execution.getProcessBusinessKey();
		//executionId
		String executionId = execution.getId();
		//获取当前人id
		String userId = ThreadLocalUtil.getUserId();
		preAuditLogService.deleteWaitlog(businessId,userId,executionId);
		
		String description = delegateTask.getDescription();
		if(Util.isNotEmpty(description)){
			Map<String, Object> fromJson = JsonUtil.fromJson(description, Map.class);
			if("1".equals(fromJson.get("isHuiqian"))){
				String sequenceFlow = (String) execution.getVariable("sequenceFlow");
				if("1".equals(sequenceFlow)){
					this.preAuditLogService.deleteHuiQianTaskByExecutionId(executionId);
				}
			}
		}
	}

}
