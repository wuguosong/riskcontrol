package com.yk.rcm.noticeofdecision.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.exception.BusinessException;
import com.yk.power.service.IRoleService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionAuditLogService;
/**
 * 决策通知书
 * 流程待处理监听
 * @author wufucan
 */
@Component
public class NoticeDecisionUpdateWaitLogListener implements TaskListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private INoticeDecisionAuditLogService noticeDecisionAuditLogService;
	@Resource
	private IRoleService roleService;
	
	
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
			List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}else if("roleConstant".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}
		
		boolean isExistWaitLog = this.noticeDecisionAuditLogService.isExistWaitLog(map);
		
		if(isExistWaitLog){
			this.updateExist(delegateTask, nextUserId, taskId, executionId);
		}else{
			String taskdesc = delegateTask.getDescription();
			Map<String, Object> data = new HashMap<String, Object>();
			
			data.put("businessId", businessId);
			data.put("auditUserId", nextUserId);
			data.put("auditTime", Util.now());
			data.put("opinion", "");
			data.put("auditStatus", "9");
			int orderBy = this.noticeDecisionAuditLogService.queryMaxOrderNum(businessId);
			if(orderBy == 1){
				orderBy = 2;
			}
			data.put("orderBy", orderBy);
			data.put("isWaiting", "1");
			data.put("taskdesc", taskdesc);
			data.put("taskId", taskId);
			data.put("executionId", executionId);
			this.noticeDecisionAuditLogService.save(data);
		}
	}

	
	private void updateExist(DelegateTask delegateTask, String userId, String taskId, String executionId){
		String businessId = delegateTask.getExecution().getProcessBusinessKey();
		String taskdesc = delegateTask.getDescription();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("nextUserId", userId);
		data.put("orderBy", this.noticeDecisionAuditLogService.queryNextOrderNum(businessId));
		data.put("taskdesc", taskdesc);
		data.put("taskId", taskId);
		data.put("executionId", executionId);
		this.noticeDecisionAuditLogService.updateWaitLog(data);
	}
}
