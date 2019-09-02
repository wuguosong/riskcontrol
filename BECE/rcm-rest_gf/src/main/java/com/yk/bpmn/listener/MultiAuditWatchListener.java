/**
 * 
 */
package com.yk.bpmn.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * @author 80845530
 *
 */
@Component("multiAuditWatchListener")
public class MultiAuditWatchListener implements ExecutionListener, TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String toTask = (String) execution.getVariable("toTask");
		if(!"1".equals(toTask)){
			execution.setVariable("isAgree", "0");
			String userId = (String) execution.getVariable("inputUser");
			execution.setVariable("backToUser", userId);
		}
		String isAgree = (String) execution.getVariable("isAgree");
		if("0".equals(isAgree)){
			execution.setVariable("inputUser", execution.getVariable("backToUser"));
		}
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		delegateTask.setVariable("isAgree", "1");
		delegateTask.setVariable("inputUser", "");
	}

}
