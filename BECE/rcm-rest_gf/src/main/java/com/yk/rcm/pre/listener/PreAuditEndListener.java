/**
 * 
 */
package com.yk.rcm.pre.listener;

import com.daxt.service.IDaxtService;
import com.yk.exception.BusinessException;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionConfirmInfoService;
import com.yk.rcm.pre.service.IPreAuditLogService;
import com.yk.rcm.pre.service.IPreAuditService;
import com.yk.rcm.pre.service.IPreInfoService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;
import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzAfterPreReviewClient;
import ws.client.TzClient;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程结束监听
 * @author yaphet
 */
@Component
public class PreAuditEndListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;
	@Resource
	private IPreAuditService preAuditService;
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IPreAuditLogService preAuditLogService;
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private INoticeDecisionConfirmInfoService noticeDecisionConfirmInfoService;
	@Resource
	private IDaxtService daxtService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessId = execution.getProcessBusinessKey();
		String isAgree = (String) execution.getVariable("isAgree");
		String taskdesc = "";
		String auditUserId = "";
		if("1".equals(isAgree)){
			this.preInfoService.updateAuditStatusByBusinessId(businessId, "2");
			taskdesc = "通过结束";
//			this.updateStatusAndUrlForTZ(businessId,"2",execution);
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "2", "");
			Map<String, Object> oracleByBusinessId = preInfoService.getOracleByBusinessId(businessId);
			
			//归档
			if(Util.isNotEmpty(oracleByBusinessId.get("NEED_MEETING")) && "0".equals(oracleByBusinessId.get("NEED_MEETING"))){
				noticeDecisionConfirmInfoService.getYesPfrDecisionInfo(businessId, false, "", "");
				this.daxtService.preStart(businessId);
			} else if(Util.isNotEmpty(oracleByBusinessId.get("NEED_MEETING")) && "1".equals(oracleByBusinessId.get("NEED_MEETING"))){
				noticeDecisionConfirmInfoService.getYesPfrDecisionInfo(businessId, true, "", "");
			}
		}else if("0".equals(isAgree)){
			this.preInfoService.updateAuditStatusByBusinessId(businessId, "3");
			this.preInfoService.updateAuditStageByBusinessId(businessId,"1");
			taskdesc = "不通过结束";
//			this.updateStatusAndUrlForTZ(businessId,"3",execution);
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "2", "");
			//归档
			this.daxtService.preStart(businessId);
		}else if(isAgree == null){
			this.preInfoService.updateAuditStatusByBusinessId(businessId, "3");
			this.preInfoService.updateAuditStageByBusinessId(businessId,"1");
			auditUserId = ThreadLocalUtil.getUserId();
			taskdesc = "管理员终止终止";
//			this.updateStatusAndUrlForTZ(businessId,"3",execution);
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "2", "");
			//归档
			this.daxtService.preStart(businessId);
		}else {
			throw new BusinessException("流程结束时，审核状态不明确！");
		}
		
		//修改完成时间
		this.preInfoService.updateCompleteDate(businessId,Util.now());
		this.preAuditLogService.deleteWaitlog(businessId, null, null);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("orderBy", this.preAuditLogService.queryMaxOrderNum(businessId));
		data.put("nextUserId", "");
		data.put("auditUserId", auditUserId);
		data.put("taskdesc", taskdesc);
		//新增结束日志
		data.put("auditTime", Util.now());
		data.put("isWaiting", "1");
		data.put("auditStatus", "9");
		this.preAuditLogService.save(data);
		
	}
	
	private void updateStatusAndUrlForTZ(String businessId,String status,DelegateExecution execution){
		if("3".equals(status)){
			String serverPath = PropertiesUtil.getProperty("domain.allow");
			StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
			url.append("PreAuditDetailView/");
			url.append(businessId);
			String oldurl = "#/";
			String ntUrl = Util.encodeUrl(oldurl);
			url.append("/"+ntUrl);
			TzClient client = new TzClient();
			client.setBusinessId(businessId);
			client.setStatus(status);
			client.setLocation(url.toString());
			Thread t = new Thread(client);
			t.start();
		}else if("2".equals(status)){
			
			String opinion = (String) execution.getVariable("opinion");
			
			Map<String, Object> oracle = preInfoService.getOracleByBusinessId(businessId);
			String decisionopinion = (String) oracle.get("DECISIONOPINION");
			//给投资推项目信息数据
			TzAfterPreReviewClient tzAfterPreReviewClient = new TzAfterPreReviewClient(businessId, decisionopinion ,opinion,"2");
			Thread t = new Thread(tzAfterPreReviewClient);
			t.start();
		}
			
	}
}
