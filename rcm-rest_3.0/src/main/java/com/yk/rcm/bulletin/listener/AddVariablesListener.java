/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;
import com.yk.flow.util.JsonUtil;

/**
 * @author wufucan
 *
 */
@Component
public class AddVariablesListener implements ExecutionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Expression variablesJson;

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.ExecutionListener#notify(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String variablesJsonStr = (String) variablesJson.getValue(execution);
		Map<String, Object> variables = JsonUtil.fromJson(variablesJsonStr, new TypeToken<Map<String, Object>>(){}.getType());
		execution.setVariables(variables);
	}

}
