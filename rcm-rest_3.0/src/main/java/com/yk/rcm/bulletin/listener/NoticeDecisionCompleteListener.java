/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ws.client.contract.SendProjectThread;

/**
 * @Description: 
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
@Component
public class NoticeDecisionCompleteListener implements ExecutionListener{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NoticeDecisionCompleteListener.class);
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessId = execution.getProcessBusinessKey();
		SendProjectThread sendProject = new SendProjectThread(businessId);
		new Thread(sendProject).start();
	}

}
