/**
 * 
 */
package bpm.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;

import rcm.ProjectRelation;

/**
 * @Description: 评审分配评审任务后添加评审负责人，评审组长，评审小组固定成员到关系表
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class ReviewLeaderCompleteListener implements TaskListener{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ReviewLeaderCompleteListener.class);

	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			String businessId = delegateTask.getExecution().getProcessBusinessKey();
			String processInstanceId = delegateTask.getProcessInstanceId();
			String processDefId = delegateTask.getProcessDefinitionId();
			logger.debug("businessId:"+businessId+" processInstanceId:"+processInstanceId+" processDefId:"+processDefId);
			ProjectRelation pr = new ProjectRelation();
			pr.insertWhenReviewLeaderComplete(businessId, processDefId);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

}
