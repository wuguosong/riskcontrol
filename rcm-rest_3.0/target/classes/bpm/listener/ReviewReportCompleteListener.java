/**
 * 
 */
package bpm.listener;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;

import com.yk.common.SpringUtil;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.pre.service.IPreInfoService;
import common.Constants;

/**
 * @Description: 
 * 1、提交决策委员会审阅任务结束后添加角色委员会成员到关系表<br>
 * 2、决策委员会审阅为评审最后一个节点，完成该节点标示评审审批通过，需要将评审单设置为通过
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class ReviewReportCompleteListener implements TaskListener{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		RepositoryService repositoryService = (RepositoryService) SpringUtil.getBean("repositoryService");
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId)
				.singleResult();
		String processKey = processDefinition.getKey();
		if(Constants.PRE_ASSESSMENT.equals(processKey)){
			this.preCall(delegateTask);
		}else if(Constants.FORMAL_ASSESSMENT.equals(processKey)){
			this.pfrCall(delegateTask);
		}
	}

	private void pfrCall(DelegateTask delegateTask) {
		try {
			String businessId = delegateTask.getExecution().getProcessBusinessKey();
			IFormalAssessmentAuditService formalAssessmentAuditService = (IFormalAssessmentAuditService) SpringUtil.getBean("formalAssessmentAuditService");
			formalAssessmentAuditService.updateAuditStatusByBusinessId(businessId, "2");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void preCall(DelegateTask delegateTask) {
		try {
			String businessId = delegateTask.getExecution().getProcessBusinessKey();
			IPreInfoService preInfoService = (IPreInfoService) SpringUtil.getBean("preInfoService");
			preInfoService.updateAuditStatusByBusinessId(businessId, "2");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
