package com.yk.rcm.pre.listener;

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
		/*=====法律负责人审批完成后，代办没有转为已办，而是直接删除了代办，造成了审批记录中缺少了一个节点====*/
		String taskDefKey = delegateTask.getTaskDefinitionKey();
		if("usertask10".equals(taskDefKey)){
			Map<String, Object> log = new HashMap<String, Object>();
			String opinion = (String) execution.getVariable("opinion");
			log.put("currentUserId", userId);
			log.put("businessId", businessId);
			log.put("auditUserId", userId);
			log.put("auditTime", Util.now());
			log.put("opinion", opinion);
			int orderBy = this.preAuditLogService.queryMaxOrderNum(businessId);
			log.put("orderBy", orderBy+1);
			log.put("isWaiting", "0");
			log.put("taskdesc", delegateTask.getName());
			log.put("auditStatus", "B");
			log.put("executionId", execution.getId());
			this.preAuditLogService.save(log);
		}
		/*=====法律负责人审批完成后，代办没有转为已办，而是直接删除了代办，造成了审批记录中缺少了一个节点====*/
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
