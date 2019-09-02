package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzAfterNoticeClient;
import ws.client.TzAfterPreReviewClient;
import ws.client.TzClient;

import com.daxt.service.IDaxtService;
import com.yk.common.IBaseMongo;
import com.yk.exception.BusinessException;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
/**
 * 
 * @author yaphet
 * 2017年5月31日
 *
 */
@Component
public class FormalAssessmentAuditEndListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource 
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IDaxtService daxtService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//流程结束时需要做的事
		//1、修改正式评审的审核状态
		String businessId = execution.getProcessBusinessKey();
		String isAgree = (String) execution.getVariable("isAgree");
		String taskdesc = "";
		String auditUserId = "";
		if("1".equals(isAgree)){
			this.formalAssessmentAuditService.updateAuditStatusByBusinessId(businessId, "2");
			taskdesc = "通过结束";
//			this.updateStatusAndUrlForTZ(businessId,"2");
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "2", "");
			
		}else if("0".equals(isAgree)){
			this.formalAssessmentAuditService.updateAuditStatusByBusinessId(businessId, "3");
			this.formalAssessmentAuditService.updateAuditStageByBusinessId(businessId,"1");
			taskdesc = "不通过结束";
//			this.updateStatusAndUrlForTZ(businessId,"3");
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "3", "");
			//给投资推项目信息数据
//			TzAfterNoticeClient tzAfterPreReviewClient = new TzAfterNoticeClient(businessId, "2",null);
//			Thread tt = new Thread(tzAfterPreReviewClient);
//			tt.start();
//			
			this.daxtService.prfStart(businessId);
		}else if(isAgree == null){
			this.formalAssessmentAuditService.updateAuditStatusByBusinessId(businessId, "3");
			this.formalAssessmentAuditService.updateAuditStageByBusinessId(businessId,"1");
			taskdesc = "管理员终止";
			auditUserId = ThreadLocalUtil.getUserId();
//			this.updateStatusAndUrlForTZ(businessId,"3");
			this.formalAssessmentInfoService.updateRiskAuditInfo(businessId, "3", "");
			
			this.daxtService.prfStart(businessId);
		}else {
			throw new BusinessException("流程结束时，审核状态不明确！");
		}
		
		//修改完成时间
		this.formalAssessmentInfoService.updateCompleteDateById(businessId,Util.now());
		
		Map<String, Object> taskMap = new HashMap<String,Object>();
		taskMap.put("businessId", businessId);
		taskMap.put("isWaiting", "1");
		this.formalAssessmentAuditLogService.deleteWaitlogs(taskMap);
		
		//2、修改待审核日志信息
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("orderBy", this.formalAssessmentAuditLogService.queryNextOrderNum(businessId));
		data.put("nextUserId", "");
		data.put("taskdesc", taskdesc);
		data.put("auditUserId", auditUserId);
		//删除所有待办
		Map<String, Object> log = new HashMap<String, Object>();
		log.put("businessId", businessId);
		log.put("isWaiting", "1");
		this.formalAssessmentAuditLogService.deleteWaitlogs(log);
		//新增结束日志
		data.put("auditTime", Util.now());
		data.put("isWaiting", "1");
		data.put("auditStatus", "9");
		this.formalAssessmentAuditLogService.save(data);
	}
	
	private void updateStatusAndUrlForTZ(String businessId,String status){
		
		if(status.equals("2")){
//			BasicDBObject queryWhere = new BasicDBObject();
//			queryWhere.put("projectFormalId", businessId);
//			Map<String, Object> formalReport = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_FORMALREPORT_INFO).get(0);
//			
//			String filepath = "", ext = "";
//			if(Util.isNotEmpty(formalReport)){
//				filepath = (String) formalReport.get("filePath");
//				if(filepath!=null){
//					ext = filepath.substring(filepath.lastIndexOf("."));
//				}
//			}
//			//调用投资接口，把结果反馈给投资系统
//			String url = PropertiesUtil.getProperty("domain.allow")+PropertiesUtil.getProperty("contextPath");
//			url = url + "/common/RcmFile/downLoad?filePath=" +filepath+"&fileName=formalReport"+ext;
//			
//			TzClient client = new TzClient();
//			client.setBusinessId(businessId);
//			client.setStatus(status);
//			client.setLocation(url.toString());
//			Thread t = new Thread(client);
//			t.start();
			
		}else if(status.equals("3")){
			
			String serverPath = PropertiesUtil.getProperty("domain.allow");
			StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
			url.append("FormalAssessmentAuditDetailView/");
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
		}
	}
}
