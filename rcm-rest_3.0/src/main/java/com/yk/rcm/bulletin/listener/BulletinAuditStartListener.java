/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import com.yk.rcm.bulletin.service.IBulletinAuditLogService;

/**
 * 流程启动监听器
 * @author wufucan
 */
@Component
public class BulletinAuditStartListener implements ExecutionListener {
	
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService; 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.ExecutionListener#notify(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessId = execution.getProcessBusinessKey();
		bulletinAuditLogService.deleteWaitlog(businessId, null, null,null);
	}

}
