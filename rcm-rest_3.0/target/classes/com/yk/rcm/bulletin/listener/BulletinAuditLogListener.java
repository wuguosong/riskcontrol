/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.rcm.bulletin.service.IBulletinAuditLogService;

/**
 * 审核日志监听器
 * @author wufucan
 */
@Component
public class BulletinAuditLogListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	public Expression status;
	public Expression taskdesc;

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.ExecutionListener#notify(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String statusStr = (String) status.getValue(execution);
		String taskdescStr = (String)taskdesc.getValue(execution);
		
		Map<String, Object> log = new HashMap<String, Object>();
		
		String businessId = execution.getProcessBusinessKey();
		String opinion = (String) execution.getVariable("opinion");
		String auditUserId = ThreadLocalUtil.getUserId();
		
		log.put("businessId", businessId);
		log.put("auditUserId", auditUserId);
		log.put("auditTime", Util.now());
		log.put("opinion", opinion);
		log.put("auditStatus", statusStr);
		int orderBy = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", ++orderBy);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskdescStr);
		log.put("executionId", execution.getId());
		this.bulletinAuditLogService.save(log);
	}

}
