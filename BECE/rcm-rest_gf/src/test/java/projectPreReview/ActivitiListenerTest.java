/**
 * 
 */
package projectPreReview;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年9月13日 下午1:27:45  
 */
public class ActivitiListenerTest implements JavaDelegate{
	private Expression formKey;
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		String fk = (String)formKey.getValue(execution);
		System.out.println(fk);
		//System.out.println(execution.getVariable("someVariableInMainProcess"));
		System.out.println(execution.getVariables().toString());
	}
	
	public void setFormKey(Expression formKey) {
		this.formKey = formKey;
	}
}
