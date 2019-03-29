/**
 * 
 */
package com.yk.rcm.project.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.bson.Document;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;
import ws.client.Portal2Client;
import ws.client.portal.dto.PortalClientModel;

import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.exception.BusinessException;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IRoleService;
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
 * 发送待办的流程监听，挂在任务节点上，触发时机：assign
 * @author wufucan
 *
 */
@Component("projectWaitListener")
public class ProjectWaitListener implements TaskListener {

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
	private IUserService userService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IBaseMongo baseMongo;
	
	//新正式评审service
	@Resource
	private IFormalAssessmentInfoService FormalAssessmentInfoService;
	@Resource
	private IFormalAssessmentAuditService FormalAssessmentAuditService;
	
	//新投标评审
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IPreAuditService preAuditService;
	
	
	public Expression assignType;
	public Expression assignId;
	

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.delegate.DelegateTask)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		String taskId = delegateTask.getId();
		String userId = delegateTask.getAssignee();
		String businessId = execution.getProcessBusinessKey();
		
		String processInstanceId = delegateTask.getProcessInstanceId();
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId)
				.singleResult();
		String processKey = processDefinition.getKey();
		String formKey = delegateTask.getFormKey();
		String url = null;
		String title = "";
		if(Constants.FORMAL_ASSESSMENT.equals(processKey)){
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId;
			String name = (String) this.FormalAssessmentInfoService.getOracleByBusinessId(businessId).get("PROJECTNAME");
			title = name + "正式评审申请";
			String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
			if(taskDefinitionKey.equals("usertask3")){
				//评审负责人审批
				//修改阶段
				FormalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "3");
				Map<String, Object> formalAssessmentOld = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentOld);
				Document formalAssessment = Document.parse(json);
				Document approveAttachment = (Document) formalAssessment.get("approveAttachment");
				
				if(Util.isNotEmpty(approveAttachment)){
					List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
					if(Util.isNotEmpty(commentsList)){
						for (Document comments : commentsList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
					List<Document> attachmentNewList = (List<Document>) approveAttachment.get("attachmentNew");
					if(Util.isNotEmpty(attachmentNewList)){
						for (Document comments : attachmentNewList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
				}
				
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if(taskDefinitionKey.equals("usertask2")){
				//任务分配节点
				FormalAssessmentAuditService.updateAuditStageByBusinessId(businessId, "2");
			}else if(taskDefinitionKey.equals("usertask20")){
				//法律评审节点
				Map<String, Object> formalAssessmentOld = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentOld);
				Document formalAssessment = Document.parse(json);
				Document approveLegalAttachment = (Document) formalAssessment.get("approveLegalAttachment");
				
				if(Util.isNotEmpty(approveLegalAttachment)){
					List<Document> commentsList = (List<Document>) approveLegalAttachment.get("commentsList");
					if(Util.isNotEmpty(commentsList)){
						for (Document comments : commentsList) {
							comments.put("isLegalEdit", "0");
							comments.put("isGrassEdit", "0");
						}
					}
					
					List<Document> attachmentNewList = (List<Document>) approveLegalAttachment.get("attachmentNew");
					if(Util.isNotEmpty(attachmentNewList)){
						for (Document comments : attachmentNewList) {
							comments.put("isLegalEdit", "0");
							comments.put("isGrassEdit", "0");
						}
					}
				}
				
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if(taskDefinitionKey.equals("usertask3")){
				//专业评审节点
				Map<String, Object> formalAssessmentOld = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				String json = JsonUtil.toJson(formalAssessmentOld);
				Document formalAssessment = Document.parse(json);
				Document approveAttachment = (Document) formalAssessment.get("approveAttachment");
				
				if(Util.isNotEmpty(approveAttachment)){
					List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
					if(Util.isNotEmpty(commentsList)){
						for (Document comments : commentsList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
					
					List<Document> attachmentNewList = (List<Document>) approveAttachment.get("attachmentNew");
					if(Util.isNotEmpty(attachmentNewList)){
						for (Document comments : attachmentNewList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
				}
				
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
			}
			
		}else if(Constants.PROCESS_KEY_FormalAssessment.equals(processKey)){
			//新正式评审
			String oldurl = "#/IndividualTable";
			String ntUrl = Util.encodeUrl(oldurl);
//			url =  "/FormalAssessmentAuditDetailView/" + businessId + "/" + ntUrl;
			
			String description = delegateTask.getDescription();
			Map jsonObj = JsonUtil.fromJson(description, Map.class);
			String taskcode = (String) jsonObj.get("taskcode");
			url =  "/FormalAssessmentAuditDetailView/" + businessId + "/"+ taskcode + "/" + ntUrl;
			
			Map<String, Object> formalAssessmentByID = this.FormalAssessmentInfoService.getFormalAssessmentByID(businessId);
			Map<String, Object> formalAssessmentOracle = (Map<String, Object>) formalAssessmentByID.get("formalAssessmentOracle");
			String name = (String) formalAssessmentOracle.get("PROJECTNAME");
			
			//待办人查询
			String assignTypeStr = assignType.getExpressionText();
			String assignIdStr = (String) assignId.getValue(delegateTask);
			//查询待处理人
			if("userId".equals(assignTypeStr)){
				userId = assignIdStr;
			}else if("roleId".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}else if("roleConstant".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}
			
			title = name + "正式评审申请";
		}else if(Constants.PRE_ASSESSMENT.equals(processKey)){
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId;
			Map<String,Object> preOracle =  (Map<String, Object>) this.preInfoService.getPreInfoByID(businessId).get("oracle");
			
			String name = (String) preOracle.get("PROJECTNAME");
			title = name + "预评审申请";
			
			String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
			if(taskDefinitionKey.equals("usertask3") || taskDefinitionKey.equals("usertask6")){
				//评审负责人审批
				//修改阶段
				preInfoService.updateAuditStageByBusinessId(businessId, "3");
				Map<String, Object> formalAssessmentOld = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
				String json = JsonUtil.toJson(formalAssessmentOld);
				Document formalAssessment = Document.parse(json);
				Document approveAttachment = (Document) formalAssessment.get("approveAttachment");
				
				if(Util.isNotEmpty(approveAttachment)){
					List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
					if(Util.isNotEmpty(commentsList)){
						for (Document comments : commentsList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
					List<Document> attachmentNewList = (List<Document>) approveAttachment.get("attachmentNew");
					if(Util.isNotEmpty(attachmentNewList)){
						for (Document comments : attachmentNewList) {
							comments.put("isReviewLeaderEdit", "0");
							comments.put("isInvestmentManagerBackEdit", "0");
						}
					}
				}
				
				formalAssessment.remove("_id");
				baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_PRE_INFO);
			}else if(taskDefinitionKey.equals("usertask2")){
				//任务分配节点
				preInfoService.updateAuditStageByBusinessId(businessId, "2");
			}
		}else if(Constants.PROCESS_KEY_PREREVIEW.equals(processKey)){
			//新投标评审
			String oldurl = "#/IndividualTable";
			String ntUrl = Util.encodeUrl(oldurl);
			url =  "/PreAuditDetailView/" + businessId + "/" + ntUrl;
			Map<String, Object> preByID = this.preInfoService.getPreInfoByID(businessId);
			Map<String, Object> preOracle = (Map<String, Object>) preByID.get("oracle");
			String name = (String) preOracle.get("PROJECTNAME");
			
			//待办人查询
			String assignTypeStr = assignType.getExpressionText();
			String assignIdStr = (String) assignId.getValue(delegateTask);
			//查询待处理人
			if("userId".equals(assignTypeStr)){
				userId = assignIdStr;
			}else if("roleId".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}else if("roleConstant".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}
			title = name + "预评审申请";
		}else if(Constants.FORMAL_NOTICEOFDECSTION.equals(processKey)){
			url = formKey + "/" + businessId + "" + "@" + processDefinitionId + 
					"@" + processInstanceId + "@" + taskId+"@1";
			String name = (String) this.noticeOfDecisionService.queryOracleById(businessId).get("PROJECTNAME");
			title = name + "决策通知书申请";
		}else if(Constants.PROCESS_KEY_NOTICEDECISION.equals(processKey)){
			String oldurl = "#/IndividualTable";
			String ntUrl = Util.encodeUrl(oldurl);
			url =  "/noticeDecisionAuditView/toSubmit/" + businessId + "/" + ntUrl;
			Map<String,Object> mongoData = this.noticeDecisionInfoService.getNoticeDecstionByID(businessId);
			String name = (String) mongoData.get("projectName");
			title = name + "决策通知书申请";
		}else if(Constants.PROCESS_KEY_BULLETIN.equals(processKey)){
			String oldurl = "#/IndividualTable";
			String out = Util.encodeUrl(oldurl);
			url = "/BulletinMattersAuditView/"+businessId+"/"+out;
			String name = (String) this.bulletinInfoService.queryByBusinessId(businessId).get("BULLETINNAME");
			title = name;
			//待办人查询
			String assignTypeStr = assignType.getExpressionText();
			String assignIdStr = (String) assignId.getValue(delegateTask);
			//查询待处理人
			if("userId".equals(assignTypeStr)){
				userId = assignIdStr;
			}else if("roleId".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}else if("roleConstant".equals(assignTypeStr)){
				List<Map<String, Object>> users = this.roleService.queryUserById(assignIdStr);
				if(users.size() > 0){
					userId = (String) users.get(0).get("UUID");
				}else{
					throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
				}
			}
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
		model.setStatus("1");
		String senderId = ThreadLocalUtil.getUserId();
		Map<String, Object> sender = this.userService.queryById(senderId);
		model.setSender((String)sender.get("NAME"));
		
		model.setDepart((String)sender.get("ORGNAME"));
		
		Portal2Client ptClientProxy = (Portal2Client) SpringUtil.getBean("ptClient");
		ptClientProxy.setModel(model);
		Thread t = new Thread(ptClientProxy);
		t.start();
	}

}
