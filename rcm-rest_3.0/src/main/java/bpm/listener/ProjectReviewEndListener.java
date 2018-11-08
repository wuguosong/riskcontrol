/**
 * 
 */
package bpm.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

import util.PropertiesUtil;
import util.Util;
import ws.client.TzClient;

import com.yk.common.SpringUtil;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;

/**
 * @Description: 
 * 更新评审申请单状态,如果状态为审批通过(wfState=2)，那么此处不做处理，如果状态为
 * 审批中(wfState=1)，则说明未经过出具评审报告环节，直接从其它环节跳转到结束的，此时
 * 需要把状态设置为项目终止状态(wfState=3)
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class ProjectReviewEndListener implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
			
			//流程结束时，需要调用投资系统接口，把评审结果反馈给投资系统，在此先把要反馈的内容存入数据库中
			String bpmnKey = ((ExecutionEntity)execution).getProcessDefinitionKey();
			if(Constants.PRE_ASSESSMENT.equals(bpmnKey)){
				this.preCall(execution);
			}else if(Constants.FORMAL_ASSESSMENT.equals(bpmnKey)){
				this.pfrCall(execution);
			}
	}
	
	private void preCall(DelegateExecution execution){
		String businessId = execution.getProcessBusinessKey();
		IPreInfoService preInfoService = (IPreInfoService) SpringUtil.getBean("preInfoService");
		Map<String, Object> map = preInfoService.getPreInfoByID(businessId);
		Map<String, Object> oracle = (Map<String, Object>) map.get("oracle");
		String wfState = (String)oracle.get("WF_STATE");
		String riskStatus = "";
		if("1".equals(wfState)){
			//更新预评审申请单状态为审批中状态wfState=3
			preInfoService.updateAuditStageByBusinessId(businessId, "1");
			preInfoService.updateAuditStatusByBusinessId(businessId, "3");
			//流程结束了，但是没有获得通过
			riskStatus = "3";
		}else if("2".equals(wfState)){
			//审批通过，流程正常结束
			riskStatus = "2";
		}else if("3".equals(wfState)){
			//流程结束了，但是没有获得通过
			preInfoService.updateAuditStageByBusinessId(businessId, "1");
			riskStatus = "3";
		}
		preInfoService.updateCompleteDate(businessId, Util.now());
		
		String serverPath = PropertiesUtil.getProperty("domain.allow");
		StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
		url.append("ProjectPreReviewView/view/");
		url.append(businessId);
		String processDefinitionId = execution.getProcessDefinitionId();
		String processInstanceId = execution.getProcessInstanceId();
		url.append("@"+processDefinitionId+"@"+processInstanceId+"@@");
		
		TzClient client = new TzClient();
		client.setBusinessId(businessId);
		client.setStatus(riskStatus);
		client.setLocation(url.toString());
		
		Thread t = new Thread(client);
		t.start();
	}
	
	private void pfrCall(DelegateExecution execution){
		//String bpmnKey = ((ExecutionEntity)execution).getProcessDefinitionKey();
		String businessId = execution.getProcessBusinessKey();
		//ProjectInfo prj = new ProjectInfo();
		IFormalAssessmentInfoService formalAssessmentInfoService = (IFormalAssessmentInfoService) SpringUtil.getBean("formalAssessmentInfoService");
		Map<String, Object> map = formalAssessmentInfoService.getOracleByBusinessId(businessId);
		String wfState = (String)map.get("WF_STATE");
		Map<String, Object> params = new HashMap<String, Object>();
		String riskStatus = "";
		
		IFormalAssessmentAuditService formalAssessmentAuditService = (IFormalAssessmentAuditService) SpringUtil.getBean("formalAssessmentAuditService");
		
		if("1".equals(wfState)){
			//更新预评审申请单状态为审批中状态wfState=3
			params.put("wfState", "3");
			formalAssessmentAuditService.updateAuditStatusByBusinessId(businessId, "3");
			formalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "1");
			//流程结束了，但是没有获得通过
			riskStatus = "3";
		}else if("2".equals(wfState)){
			//审批通过，流程正常结束
//			riskStatus = "2";
		}else if("3".equals(wfState)){
			//流程结束了，但是没有获得通过
			formalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "1");
			riskStatus = "3";
			
		}
		
		if("3".equals(riskStatus)){
			String serverPath = PropertiesUtil.getProperty("domain.allow");
			StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
			url.append("ProjectFormalReviewDetailView/view/");
			url.append(businessId);
			String processDefinitionId = execution.getProcessDefinitionId();
			String processInstanceId = execution.getProcessInstanceId();
			url.append("@"+processDefinitionId+"@"+processInstanceId+"@@");
			
			TzClient client = new TzClient();
			client.setBusinessId(businessId);
			client.setStatus(riskStatus);
			client.setLocation(url.toString());
			Thread t = new Thread(client);
			t.start();
		}
		formalAssessmentInfoService.updateCompleteDateById(businessId, new Date());
	}
}
