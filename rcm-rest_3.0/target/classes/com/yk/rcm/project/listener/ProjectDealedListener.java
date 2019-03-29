/**
 * 
 */
package com.yk.rcm.project.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;
import ws.client.Portal2Client;
import ws.client.portal.dto.PortalClientModel;

import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IUserService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.noticeofdecision.service.INoticeOfDecisionService;
import com.yk.rcm.pre.service.IPreAuditLogService;
import com.yk.rcm.pre.service.IPreAuditService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IFormalAssesmentService;

import common.Constants;

/**
 * 发送已办的流程监听，挂在任务节点上，触发时机：complete
 * @author wufucan
 *
 */
@Component("projectDealedListener")
public class ProjectDealedListener implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private IFormalAssesmentService formalAssesmentService;
	@Resource
	private INoticeOfDecisionService noticeOfDecisionService;
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IUserService userService;
	
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IPreAuditService preAuditService;
	
	@Resource
	private IFormalAssessmentAuditService FormalAssessmentAuditService;
	
	//新正式评审service
	@Resource
	private IFormalAssessmentInfoService FormalAssessmentInfoService;

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.delegate.DelegateTask)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		String taskId = delegateTask.getId();
		String userId = ThreadLocalUtil.getUserId();
		String businessId = execution.getProcessBusinessKey();
		
		String processInstanceId = delegateTask.getProcessInstanceId();
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId)
				.singleResult();
		String processKey = processDefinition.getKey();
		String title = "";
		String formKey = "";
		String url = null;
		if(Constants.FORMAL_ASSESSMENT.equals(processKey)){
			Map<String, Object> queryOracleById = (Map<String, Object>) this.FormalAssessmentInfoService.getFormalAssessmentByID(businessId).get("formalAssessmentOracle");
			
			String name = (String) queryOracleById.get("PROJECTNAME");
			title = name + "正式评审申请";
			formKey = "/ProjectFormalReviewDetailView/View";
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId;
			String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
			if(taskDefinitionKey.equals("usertask2")){
				//任务分配节点
				FormalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "1");
			}else if(taskDefinitionKey.equals("usertask20")){
				//法律评审节点
				Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentold);
				Document formalAssessment = Document.parse(json);
				Map<String, Object> approveLegalAttachment = (Map<String, Object>) formalAssessment.get("approveLegalAttachment");
				if(Util.isNotEmpty(approveLegalAttachment)){
					List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveLegalAttachment.get("commentsList");
					//修改反馈信息
					if(Util.isNotEmpty(commentsList)){
						for (Map<String, Object> comments : commentsList) {
							comments.put("isLegalEdit", "0");
						}
					}
					List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveLegalAttachment.get("attachmentNew");
					//修改更新附件
					if(Util.isNotEmpty(attachmentNewList)){
						for (Map<String, Object> attachmentNew : attachmentNewList) {
							attachmentNew.put("isLegalEdit", "0");
						}
					}
				}
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if(taskDefinitionKey.equals("usertask3")){
				//专业评审节点
				Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentold);
				Document formalAssessment = Document.parse(json);
				Map<String, Object> approveAttachment = (Map<String, Object>) formalAssessment.get("approveAttachment");
				if(Util.isNotEmpty(approveAttachment)){
					List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveAttachment.get("commentsList");
					//修改反馈信息
					if(Util.isNotEmpty(commentsList)){
						for (Map<String, Object> comments : commentsList) {
							comments.put("isReviewLeaderEdit", "0");
						}
					}
					List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveAttachment.get("attachmentNew");
					//修改更新附件
					if(Util.isNotEmpty(attachmentNewList)){
						for (Map<String, Object> attachmentNew : attachmentNewList) {
							attachmentNew.put("isReviewLeaderEdit", "0");
						}
					}
				}
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if(taskDefinitionKey.equals("usertask4")){
				//保存固定小组成员意见
				Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentold);
				Document formalAssessment = Document.parse(json);
				String opinion = (String) execution.getVariable("opinion");
				formalAssessment.put("_id", new ObjectId(businessId));
				List<Map<String, Object>> fixGroupOption = (List<Map<String, Object>>) formalAssessment.get("fixGroupOption");
				if(Util.isNotEmpty(fixGroupOption)){
					int flag = 0;
					for (Map<String, Object> o : fixGroupOption) {
						//mongo中存在当前人的意见，替换掉
						if(o.get("UUID").toString().equals(userId)){
							o.put("opinion", opinion);
							flag = 1;
						}
					}
					//mongo中不存在当前人意见，新增
					if(flag == 0){
						Map<String, Object> optionItem = new HashMap<String,Object>();
						optionItem.put("NAME", userService.queryById(userId).get("NAME"));
						optionItem.put("VALUE", userId);
						optionItem.put("opinion", opinion);
						fixGroupOption.add(optionItem);
					}
				}else{
					fixGroupOption = new ArrayList<Map<String,Object>>();
					Map<String, Object> optionItem = new HashMap<String,Object>();
					optionItem.put("NAME", userService.queryById(userId).get("NAME"));
					optionItem.put("VALUE", userId);
					optionItem.put("opinion", opinion);
					fixGroupOption.add(optionItem);
				}
				Map<String, Object> data = new HashMap<String,Object>();
				data.put("fixGroupOption", fixGroupOption);
				baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
			}
		}else if(Constants.PROCESS_KEY_FormalAssessment.equals(processKey)){
			//新正是评审
			String oldurl = "#/FormalAssessmentAuditList/1";
			String out = Util.encodeUrl(oldurl);
			Map<String, Object> formalAssessmentByID = this.FormalAssessmentInfoService.getFormalAssessmentByID(businessId);
			Map<String, Object> formalAssessmentOracle = (Map<String, Object>) formalAssessmentByID.get("formalAssessmentOracle");
			url = "/FormalAssessmentAuditDetailView/"+businessId+"/"+out;
			String name = (String) formalAssessmentOracle.get("PROJECTNAME");
			title = name + "正式评审申请";
		}else if(Constants.PRE_ASSESSMENT.equals(processKey)){
			Map<String, Object> oracle = (Map<String, Object>) this.preInfoService.getPreInfoByID(businessId).get("oracle");
			String name = (String) oracle.get("PROJECTNAME");
			title = name + "预评审申请";
			formKey = "/ProjectPreReviewView";
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId;
			
			String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
			if(taskDefinitionKey.equals("usertask2")){
				//任务分配节点
				preInfoService.updateAuditStageByBusinessId(businessId, "1");
			}else if(taskDefinitionKey.equals("usertask3") || taskDefinitionKey.equals("usertask6")){
				//专业评审节点
				Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
				String json = JsonUtil.toJson(formalAssessmentold);
				Document formalAssessment = Document.parse(json);
				Map<String, Object> approveAttachment = (Map<String, Object>) formalAssessment.get("approveAttachment");
				if(Util.isNotEmpty(approveAttachment)){
					List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveAttachment.get("commentsList");
					//修改反馈信息
					if(Util.isNotEmpty(commentsList)){
						for (Map<String, Object> comments : commentsList) {
							comments.put("isReviewLeaderEdit", "0");
						}
					}
					List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveAttachment.get("attachmentNew");
					//修改更新附件
					if(Util.isNotEmpty(attachmentNewList)){
						for (Map<String, Object> attachmentNew : attachmentNewList) {
							attachmentNew.put("isReviewLeaderEdit", "0");
						}
					}
				}
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_PRE_INFO);
			}else if(taskDefinitionKey.equals("usertask4")){
				//保存固定小组成员意见
				Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
				String json = JsonUtil.toJson(formalAssessmentold);
				Document formalAssessment = Document.parse(json);
				String opinion = (String) execution.getVariable("opinion");
				formalAssessment.put("_id", new ObjectId(businessId));
				List<Map<String, Object>> fixGroupOption = (List<Map<String, Object>>) formalAssessment.get("fixGroupOption");
				if(Util.isNotEmpty(fixGroupOption)){
					int flag = 0;
					for (Map<String, Object> o : fixGroupOption) {
						//mongo中存在当前人的意见，替换掉
						if(o.get("VALUE").toString().equals(userId)){
							o.put("opinion", opinion);
							flag = 1;
						}
					}
					//mongo中不存在当前人意见，新增
					if(flag == 0){
						Map<String, Object> optionItem = new HashMap<String,Object>();
						optionItem.put("NAME", userService.queryById(userId).get("NAME"));
						optionItem.put("VALUE", userId);
						optionItem.put("opinion", opinion);
						fixGroupOption.add(optionItem);
					}
				}else{
					fixGroupOption = new ArrayList<Map<String,Object>>();
					Map<String, Object> optionItem = new HashMap<String,Object>();
					optionItem.put("NAME", userService.queryById(userId).get("NAME"));
					optionItem.put("VALUE", userId);
					optionItem.put("opinion", opinion);
					fixGroupOption.add(optionItem);
				}
				Map<String, Object> data = new HashMap<String,Object>();
				data.put("fixGroupOption", fixGroupOption);
				baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
			}
		}else if(Constants.PROCESS_KEY_PREREVIEW.equals(processKey)){
			//新正是评审
			String oldurl = "#/PreAuditList/1";
			String eUrl = Util.encodeUrl(oldurl);
			Map<String, Object> preByID = this.preInfoService.getPreInfoByID(businessId);
			Map<String, Object> preOracle = (Map<String, Object>) preByID.get("oracle");
			url = "/PreAuditDetailView/"+businessId+"/"+eUrl;
			String name = (String) preOracle.get("PROJECTNAME");
			title = name + "预评审申请";
		}else if(Constants.FORMAL_NOTICEOFDECSTION.equals(processKey)){
			String name = (String) this.noticeOfDecisionService.queryOracleById(businessId).get("PROJECTNAME");
			title = name + "决策通知书申请";
			formKey = "/NoticeOfDecision/view";
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId+"@6";
		}else if(Constants.PROCESS_KEY_NOTICEDECISION.equals(processKey)){
			String oldurl = "#/NoticeDecisionAuditList/1";
			String out = Util.encodeUrl(oldurl);
			Map<String,Object> mongoData = this.noticeDecisionInfoService.getNoticeDecstionByID(businessId);
			url = "/noticeDecisionAuditView/submitted/"+businessId+"/"+out;
			String name = (String) mongoData.get("projectName");
			title = name + "决策通知书申请";
		}else if(Constants.PROCESS_KEY_BULLETIN.equals(processKey)){
			String oldurl = "#/BulletinMattersAudit/1";
			String out = Util.encodeUrl(oldurl);
			url = "/BulletinMattersAuditView/"+businessId+"/"+out;
			String name = (String) this.bulletinInfoService.queryByBusinessId(businessId).get("BULLETINNAME");
			title = name;
		}else{
			return;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		
		PortalClientModel model = new PortalClientModel();
		model.setUniid(taskId);
		model.setTitle(title);
		model.setUrl(url);
		model.setMobileUrl(url);
		model.setOwner(userId);
		model.setCreated(Util.now());
		model.setType("1");
		model.setStatus("2");
		
		Portal2Client ptClientProxy = (Portal2Client) SpringUtil.getBean("ptClient");
		ptClientProxy.setModel(model);
		Thread t = new Thread(ptClientProxy);
		t.start();
	}
	
}
