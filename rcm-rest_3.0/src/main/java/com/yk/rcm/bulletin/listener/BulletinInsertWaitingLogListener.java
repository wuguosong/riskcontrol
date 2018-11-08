package com.yk.rcm.bulletin.listener;

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
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
/**
 * 
 * @author yaphet
 * 进入节点增加待办日志
 *
 */
@Component
public class BulletinInsertWaitingLogListener implements TaskListener {

	private static final long serialVersionUID = 1L;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
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
			List<Map<String, Object>> users = this.bulletinAuditLogService.queryUsersByRoleId(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}else if("roleConstant".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.bulletinAuditLogService.queryUsersByRoleId(assignIdStr);
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
		int orderBy = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		data.put("orderBy", orderBy);
		data.put("isWaiting", "1");
		data.put("taskdesc", taskName);
		data.put("taskId", taskId);
		data.put("executionId", executionId);
		String userId = ThreadLocalUtil.getUserId();
		data.put("lastUserId", userId);
		this.bulletinAuditLogService.save(data);
	}
}
