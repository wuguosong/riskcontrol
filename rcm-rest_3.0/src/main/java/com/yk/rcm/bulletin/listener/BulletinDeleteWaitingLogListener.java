package com.yk.rcm.bulletin.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;

import com.yk.rcm.bulletin.service.IBulletinAuditLogService;

/**
 * 
 * @author yaphet
 * 出节点删除待办日志
 *
 */
@Component
public class BulletinDeleteWaitingLogListener implements TaskListener{

	private static final long serialVersionUID = 1L;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		//业务id
		String businessId = execution.getProcessBusinessKey();
		//executionId
		String executionId = execution.getId();
		//获取当前人id
		String userId = ThreadLocalUtil.getUserId();
		bulletinAuditLogService.deleteWaitlog(businessId,userId,executionId,null);
	}

}
