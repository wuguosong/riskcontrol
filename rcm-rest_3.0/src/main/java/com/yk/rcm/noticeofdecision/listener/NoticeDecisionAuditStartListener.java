package com.yk.rcm.noticeofdecision.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;
/**
 * 决策通知书
 * 流程开始监听
 * @author wufucan
 */
@Component
public class NoticeDecisionAuditStartListener implements ExecutionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		
		
	}

}
