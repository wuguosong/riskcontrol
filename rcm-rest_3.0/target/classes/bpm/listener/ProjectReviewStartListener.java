/**
 * 
 */
package bpm.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

import com.yk.common.SpringUtil;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;

import common.Constants;
import rcm.ProjectInfo;
import util.PropertiesUtil;
import ws.client.TzClient;

/**
 * @Description: 
 * 评审流程启动后，更新评审申请单状态为审批中状态wfState=1, applyDate=new Date()
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class ProjectReviewStartListener implements TaskListener{
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		
		DelegateExecution execution = delegateTask.getExecution();
		String businessId = execution.getProcessBusinessKey();
		//更新预评审申请单状态为审批中状态state=1
		ProjectInfo prj = new ProjectInfo();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wfState", "1");
		params.put("applyDate", new Date());
		prj.updateProjectInfo(businessId, params);
		
		//修改阶段1
		IFormalAssessmentAuditService formalAssessmentAuditService = (IFormalAssessmentAuditService) SpringUtil.getBean("formalAssessmentAuditService");
		formalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "1");
		
		String processDefinitionId = execution.getProcessDefinitionId();
		String processInstanceId = execution.getProcessInstanceId();
		
		String bpmnKey = ((ExecutionEntity)execution).getProcessDefinitionKey();
		if(bpmnKey == null){
			bpmnKey = processDefinitionId.split(":")[0];
		}
		String serverPath = PropertiesUtil.getProperty("domain.allow");
		StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
		if(Constants.PRE_ASSESSMENT.equals(bpmnKey)){
			//预评审
			url.append("ProjectPreReviewView/");
		}else if(Constants.FORMAL_ASSESSMENT.equals(bpmnKey)){
			//正式评审
			url.append("ProjectFormalReviewDetailView/View/");
		}
		url.append(businessId).append("@").append(processDefinitionId)
				.append("@").append(processInstanceId);
		//调用投资接口，通知他们更新项目状态
		TzClient client = new TzClient();
		client.setBusinessId(businessId);
		client.setStatus("1");
		client.setLocation(url.toString());
		Thread t = new Thread(client);
		t.start();
	}

}
