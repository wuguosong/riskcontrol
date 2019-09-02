package com.yk.rcm.formalAssessment.service.impl;

import com.yk.bpmn.entity.TaskInfo;
import com.yk.common.BaseMongo;
import com.yk.flow.dto.SingleProcessOption;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.ProcessResult;
import com.yk.power.service.IRoleService;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentAuditMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.sign.service.SelfApproval;
import common.Constants;
import common.PageAssistant;
import common.Result;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author yaphet
 */
@Service
@Transactional
public class FormalAssessmentAuditService implements IFormalAssessmentAuditService{
	
	@Resource
	private IFormalAssessmentAuditMapper formalAssessmentAuditMapper; 
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService; 
	@Resource
	private IBpmnAuditService bpmnAuditService;
	@Resource
	private IRoleService roleService;
	@Resource
	private BaseMongo baseMongo;
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;

	private static final String RCM_FORMALASSESSMENT_INFO = Constants.RCM_FORMALASSESSMENT_INFO;
	
	@Override
	public TaskInfo queryTaskInfoByBusinessId(String businessId,String processKey,String taskMark){
		String userId = ThreadLocalUtil.getUserId();

		List<Task> queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
		List<Map<String, Object>> logs = formalAssessmentAuditLogService.queryWaitingLogsById(businessId);
		/****Add By LiPan****/
		if(CollectionUtils.isEmpty(queryTaskInfo)){
			/**
			 * 1.依靠当前登录用已经不能从流程相关表中查询到任务记录
			 * 2.因为当前登录用户可能是转发用户(即被指定的加签用户)
			 * 3.此时需要使用加签用户的原始用户记录取查询任务信息
			 * 4.从每个业务日志表中获取当前用户的原始用户信息
			 * */
			if (CollectionUtils.isNotEmpty(logs)){
				for(Map<String, Object> log : logs){
					String audituserid = String.valueOf(log.get("AUDITUSERID"));
					String iswaiting = String.valueOf(log.get("ISWAITING"));
					if("1".equals(iswaiting) && userId.equals(audituserid)){
						userId = String.valueOf(log.get("OLDUSERID"));
						break;
					}
				}
				queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
			}
		}else{
			/**
			 * 1.即使当前用户在流程表中可以查到,也要预防另一种情况发生
			 * 2.当前用户为原始用户,但是当前用户进行了转办操作,已经转办给另一个用户,且操作完成
			 * 3.此时不因该被查到任务信息
			 * 4.对于页面来说,流程已经提交成功,就不能再进行提交操作了
			 */
			if (CollectionUtils.isNotEmpty(logs)){
				for(Map<String, Object> log : logs){
					String audituserid = String.valueOf(log.get("AUDITUSERID"));
					String iswaiting = String.valueOf(log.get("ISWAITING"));
					if("1".equals(iswaiting) && !userId.equals(audituserid)){
						userId = String.valueOf(log.get("AUDITUSERID"));
						break;
					}
				}
				queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
			}
		}
		/**Add By LiPan**/

		TaskInfo taskInfo = new TaskInfo();
		if(Util.isNotEmpty(queryTaskInfo) && queryTaskInfo.size()>0){
			taskInfo.setDescription(queryTaskInfo.get(0).getDescription());
			taskInfo.setAssignee(queryTaskInfo.get(0).getAssignee());
			taskInfo.setTaskId(queryTaskInfo.get(0).getId());;
			taskInfo.setTaskKey(queryTaskInfo.get(0).getTaskDefinitionKey());
		}
		return taskInfo;
	}

	@Override
	public void queryAuditedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		String assignUserId = ThreadLocalUtil.getUserId();
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			assignUserId = null;
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("assignUserId", assignUserId);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.formalAssessmentAuditMapper.queryAuditedList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
	}
	@Override
	public void queryWaitList(PageAssistant page) {
		//查询待办的sql
		String newWaitingsql = this.bpmnAuditService.queryWaitdealSql(Constants.PROCESS_KEY_FormalAssessment, ThreadLocalUtil.getUserId()).getData();
		String oldWaitingsql = this.bpmnAuditService.queryWaitdealSql(Constants.FORMAL_ASSESSMENT, ThreadLocalUtil.getUserId()).getData();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			sql = null;
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
//		if(orderBy == null){
//			orderBy = " apply_date desc ";
//		}
		params.put("assignSql1", newWaitingsql);
		params.put("assignSql2", oldWaitingsql);
//		params.put("orderBy", orderBy);
		params.put("userId", ThreadLocalUtil.getUserId());
		List<Map<String, Object>> list = this.formalAssessmentAuditMapper.queryWaitList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		
	}
	
	@Override
	public Result querySingleProcessOptions(String businessId,String taskMark) {
		Result result = new Result();
		ProcessResult<List<SingleProcessOption>> pr = null;
		if(Util.isNotEmpty(taskMark)){
			List<Map<String, Object>> queryWaitingLogsById = formalAssessmentAuditLogService.queryWaitingLogsById(businessId);
			for (Map<String, Object> map : queryWaitingLogsById) {
				if(Util.isNotEmpty(map.get("TASKMARK"))){
					if(map.get("TASKMARK").equals(taskMark) && map.get("AUDITUSERID").equals(ThreadLocalUtil.getUserId())){
						String taskId = (String) map.get("TASKID");
						pr = this.bpmnAuditService.querySingleSequenceFlow(taskId);
					}
				}
			}
		}else{
			pr = this.bpmnAuditService.querySingleSequenceFlow(Constants.PROCESS_KEY_FormalAssessment, businessId, ThreadLocalUtil.getUserId());
		}
		
		List<SingleProcessOption> data = pr.getData();
		/*String flowId = "flow40";
		List<SingleProcessOption> data = pr.getData();
		
		for(int i = 0 ;i<data.size();i++){
			if(data.get(i).getFlowId().equals(flowId)){
				
				String documentation = data.get(i).getDocumentation();
				if(Util.isEmpty(documentation)){
					//查询固定小组成员
					Map<String, Object> formalAssessment = this.formalAssessmentInfoService.getFormalAssessmentByID(businessId);
					Map<String, Object> formalAssessmentOracle = (Map<String, Object>) formalAssessment.get("formalAssessmentOracle");
					
					String fixedGroupPersonIds = (String) formalAssessmentOracle.get("FIXEDGROUPPERSONIDS");
					if(Util.isEmpty(fixedGroupPersonIds)){
						data.remove(i);
					}
				}
			}
		}*/
		//屏蔽专业评审的流程选项 	start
		for(int i = 0 ;i<data.size();i++){
			if(data.get(i).getFlowId().equals("flow40")){
				data.remove(i);
			}
		}
		//屏蔽专业评审的流程选项 	end
		result.setResult_data(data);
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Result startSingleFlow(String businessId) {
		Result result = new Result();
		
		//查mongo根据id查formalAssessment
		Map<String, Object> formalAssessmentMongo = baseMongo.queryById(businessId, RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> apply = (Map<String, Object>) formalAssessmentMongo.get("apply");
		
		//投资经理
		Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
		String investmentManagerValue = (String) investmentManager.get("VALUE");
		
		//大区companyHeader
		Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
		String companyHeaderValue = (String)companyHeader.get("VALUE");
		//验证水环境
		List<Map<String, Object>> serviceTypeList = (List<Map<String, Object>>) apply.get("serviceType");
		String isSkipServiceTypeAudit = "1";
		
		for (Map<String, Object> serviceType : serviceTypeList) {
			String serviceTypeKey = (String) serviceType.get("KEY");
			//水环境1402
			//传统水务1401
			if(serviceTypeKey.equals("1402") || serviceTypeKey.equals("1401")){
				isSkipServiceTypeAudit ="0";
			}
		}
		
		//区域负责人——投资中心/水环境
		Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
		String serviceTypeValue = null;
		if(investmentPerson != null){
			serviceTypeValue = (String) investmentPerson.get("VALUE");
		}
		
		//基层法务人员
		Map<String, Object> grassrootsLegalStaff = (Map<String, Object>) apply.get("grassrootsLegalStaff");
		String grassrootsLegalStaffValue = null;
		if(grassrootsLegalStaff != null){
			grassrootsLegalStaffValue = (String) grassrootsLegalStaff.get("VALUE");
		}

		//流程变量赋值
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("investmentManager", investmentManagerValue);
		variables.put("largeArea", companyHeaderValue);
		variables.put("isSkipServiceTypeAudit", isSkipServiceTypeAudit);
		variables.put("serviceType", serviceTypeValue);
		variables.put("grassRootsLegal", grassrootsLegalStaffValue);
		
		//启动流程/*=======自审批方式启动流程=====*/
		ProcessResult<ProcessInstance> pr = new SelfApproval().startFlow(Constants.PROCESS_KEY_FormalAssessment, businessId, variables);
		//this.bpmnAuditService.startFlow(Constants.PROCESS_KEY_FormalAssessment, businessId, variables);
		
		if(pr.isSuccess()){
			//启动流程成功，修改formalAssessment状态
			this.formalAssessmentInfoService.updateAfterStartflow(businessId);
		}else{
			result.setResult_name(pr.getMessage());
		}
		return result;
	}



	@Override
	public Result auditSingle(String businessId, String processOption,
			Map<String, Object> variables ,String documentation) {
//		if(Util.isNotEmpty(documentation)){
//			Document documentationDoc = Document.parse(documentation);
//			Object object = documentationDoc.get("preAction");
//			String string = object.toString();
//			int indexOf = string.indexOf("validCheckedMajor");
//			if(Util.isNotEmpty(indexOf)){
//				Map<String, Object> fromalAssessmentMongo = baseMongo.queryById(businessId, RCM_FORMALASSESSMENT_INFO);
//				Map<String, Object> taskallocation = (Map<String, Object>) fromalAssessmentMongo.get("taskallocation");
//				
//				List<Document> professionalReviewers = (List<Document>) taskallocation.get("professionalReviewers");
//				if(Util.isNotEmpty(professionalReviewers)){
//					List<String> majorMembersList = new ArrayList<String>();
//					for (Document ll : professionalReviewers) {
//						String id = ll.getString("VALUE");
//						majorMembersList.add(id);
//					}
//					variables.put("majorMembers", majorMembersList);
//				}
//			}
//		}
		
		Result result = new Result();
		ProcessResult<String> pr = this.bpmnAuditService.auditSingle(processOption, variables);
		if(!pr.isSuccess()){
			result.setSuccess(false)
				.setResult_name(pr.getMessage());
		}
		
//		this.bpmnAuditService.queryTaskInfo(Constants.PROCESS_KEY_FormalAssessment, businessId, "");
		return result;
	}
	@Override
	public void updateAuditStatusByBusinessId(String businessId, String wf_state) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("wf_state", wf_state);
		this.formalAssessmentAuditMapper.updateAuditStatusByBusinessId(map);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void saveTaskToMongo(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		Document reviewLeader = (Document) doc.get("reviewLeader");
//		Document reviewLeader = Document.parse(reviewLeaderStr);
		Document legalReviewLeader = (Document) doc.get("legalReviewLeader");
		List<Document> fixedGroup = (List<Document>) doc.get("fixedGroup");
		
		//专业评审人员
		List<Document> professionalReviewers = (List<Document>) doc.get("professionalReviewers");
		Map<String, Object> taskallocation = new HashMap<String,Object>();
		if(Util.isNotEmpty(reviewLeader)){
			taskallocation.put("reviewLeader", reviewLeader);
		}
		if(Util.isNotEmpty(legalReviewLeader)){
			taskallocation.put("legalReviewLeader",legalReviewLeader);
		}
		if(Util.isNotEmpty(fixedGroup)){
			taskallocation.put("fixedGroup", fixedGroup);
		}
		
		if(Util.isNotEmpty(professionalReviewers)){
			if(Util.isNotEmpty(professionalReviewers)){
				for (Document comments : professionalReviewers) {
					comments.remove("$$hashKey");
				}
			}
			taskallocation.put("professionalReviewers", professionalReviewers);
		}
		//查mongo根据id查formalAssessment
		Map<String, Object> formalAssessmentMongo = baseMongo.queryById(businessId, RCM_FORMALASSESSMENT_INFO);
		formalAssessmentMongo.put("taskallocation", taskallocation);
		baseMongo.updateSetByObjectId(businessId, formalAssessmentMongo, RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("businessId", businessId);
		if(Util.isNotEmpty(reviewLeader)){
			params.put("reviewLeader", (String) reviewLeader.get("VALUE"));
		}
		if(Util.isNotEmpty(legalReviewLeader)){
			params.put("legalReviewLeader", (String) legalReviewLeader.get("VALUE"));
		}
		String fixedGroupIds = "";
		if(Util.isNotEmpty(fixedGroup)){
			for (Document person : fixedGroup) {
				fixedGroupIds += ","+person.get("VALUE");
			}
			fixedGroupIds = fixedGroupIds.substring(1);
		}
		params.put("fixedGroupIds", fixedGroupIds);
		
		String professionalReviewersIds = "";
		if(Util.isNotEmpty(professionalReviewers)){
			for (Document person : professionalReviewers) {
				professionalReviewersIds += ","+person.get("VALUE");
			}
			professionalReviewersIds = professionalReviewersIds.substring(1);
		}
		params.put("professionalReviewersIds", professionalReviewersIds);
		this.formalAssessmentInfoService.updatePersonById(params);
		this.formalAssessmentInfoService.updateFixGroupIds(businessId,fixedGroupIds);
	}
	@Override
	public List<String> queryFirstLevelLawyersIdsByServiceTypeIds(
			HashMap<String, Object> params) {
		return this.formalAssessmentAuditMapper.queryFirstLevelLawyersIdsByServiceTypeIds(params);
	}
	
	@Override
	public void updateAuditStageByBusinessId(String businessId,String stage) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("stage", stage);
		this.formalAssessmentAuditMapper.updateAuditStageByBusinessId(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addNewAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String uuid = doc.get("UUID").toString();
		
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		Document item = (Document) doc.get("item");
		String fileName = (String) item.get("fileName");
		String filePath = (String) item.get("filePath");
		//申请人信息
		Document programmed = (Document) item.get("programmed");
		//审核人信息
		Document approved = (Document) item.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("version", version);
					file.put("approved", approved);
					file.put("upload_date", approved);
					file.put("upload_date", Util.format(Util.now()));
					filesList.add(file);
				}else{
					
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("approved", approved);
					file.put("version", version);
					file.put("upload_date", Util.format(Util.now()));
					List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
					files.add(file);
					attachment.put("files", files);
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = (String) doc.get("version");
		String businessId = (String) doc.get("businessId");
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for(int i = 0 ; i < filesList.size() ; i++){
						if(filesList.get(i).get("version").toString().equals(version)){
							filesList.remove(i);
						}
					}
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		
		Document programmed = (Document) doc.get("programmed");
		
		Document approved = (Document) doc.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for (Map<String, Object> files : filesList) {
						if(files.get("version").toString().equals(version)){
							files.put("fileName", fileName);
							files.put("filePath", filePath);
							files.put("approved", approved);
							files.put("programmed", programmed);
						}
					}
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	
}
