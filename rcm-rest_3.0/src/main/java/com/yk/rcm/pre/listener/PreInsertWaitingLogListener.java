package com.yk.rcm.pre.listener;

import com.yk.exception.BusinessException;
import com.yk.rcm.pre.service.IPreAuditLogService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author yaphet
 * 进入节点增加待办日志
 *
 */
@Component
public class PreInsertWaitingLogListener implements TaskListener {

	private static final long serialVersionUID = 1L;
	@Resource
	private IPreAuditLogService preAuditLogService;
	public Expression assignType;
	public Expression assignId;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		String assignTypeStr = assignType.getExpressionText();
		String assignIdStr = (String) assignId.getValue(delegateTask);
		DelegateExecution execution = delegateTask.getExecution();
		
		String taskId = delegateTask.getId();
		String executionId = execution.getId();
		
		String businessId = execution.getProcessBusinessKey();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		//查询待处理人
		String nextUserId = null;
		if("userId".equals(assignTypeStr)){
			nextUserId = assignIdStr;
		}else if("roleId".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.preAuditLogService.queryUsersByRoleId(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}else if("roleConstant".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.preAuditLogService.queryUsersByRoleId(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}
		

		//新增待办
		String taskName = delegateTask.getExecution().getCurrentActivityName();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("auditUserId", nextUserId);
		data.put("auditTime", Util.now());
		data.put("opinion", "");
		data.put("auditStatus", "9");
		int orderBy = this.preAuditLogService.queryMaxOrderNum(businessId);
		data.put("orderBy", orderBy);
		data.put("isWaiting", "1");
		data.put("taskdesc", taskName);
		data.put("taskId", taskId);
		data.put("executionId", executionId);
		String userId = ThreadLocalUtil.getUserId();
		data.put("lastUserId", userId);
		
		/****************************************************/
		//阶段修改
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		String stage = null;
		//大区审批usertask2
		if(taskDefinitionKey.equals("usertask2")){
			stage = "1.3";
		}
		//投资经理起草usertask1
		else if(taskDefinitionKey.equals("usertask1")){
			stage = "1";
		}
		//投资中心/水环境usertask3
		else if(taskDefinitionKey.equals("usertask3")){
			stage = "1.5";
		}
		//分配任务usertask4
		else if(taskDefinitionKey.equals("usertask4")){
			stage = "2";
		}
		//法律评审负责人
		else if(taskDefinitionKey.equals("usertask10")){
			stage = "2.7";
		}
		//大区
		else if(taskDefinitionKey.equals("usertask10")){
			stage = "2.3";
		}
		//评审负责人||评审负责人确认
		else if(taskDefinitionKey.equals("usertask5") || taskDefinitionKey.equals("usertask8")){
			stage = "2.8";
		}
		//专业评审人员
		else if(taskDefinitionKey.equals("usertask6")){
			stage = "3";
		}
		//投资经理反馈
		else if(taskDefinitionKey.equals("usertask9")){
			stage = "2.1";
		}
		//投资中心/水环境反馈
		else if(taskDefinitionKey.equals("usertask11")){
			stage = "2.5";
		}
		data.put("stage", stage);
		/****************************************************/
		this.preAuditLogService.save(data);
	}
}
