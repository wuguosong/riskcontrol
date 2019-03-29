/**
 * 
 */
package com.yk.bpmn.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.HpgClient;
import ws.client.Portal2Client;
import ws.client.TzAfterNoticeClient;
import ws.client.TzAfterPreReviewClient;
import ws.client.portal.dto.PortalClientModel;

import com.yk.bpmn.dao.IBpmnMapper;
import com.yk.bpmn.entity.TaskInfo;
import com.yk.bpmn.service.IBpmnService;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IPertainAreaService;
import com.yk.power.service.IRoleService;
import com.yk.power.service.IUserService;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.pre.service.IPreAuditLogService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IFormalAssesmentService;
import com.yk.rcm.project.service.IWsCallService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class BpmnService implements IBpmnService {
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private HistoryService historyService;
	@Resource
	private TaskService taskService;
	@Resource
	private IFormalAssesmentService formalAssesmentService;  
	@Resource
	private IBpmnMapper bpmnMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private IPreAuditLogService preAuditLogService;
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	@Resource
	private IUserService userService;
	@Resource
	private IWsCallService wscallService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IPertainAreaService pertainAreaService;
	@Resource
	private IFormalAssessmentInfoService FormalAssessmentInfoService;
	@Override
	public Result deploy(String processKey) {
		Result result = new Result();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream bpmn, png;
	    try {
			bpmn = classLoader.getResource("diagrams/"+processKey+".bpmn").openStream();
			png = classLoader.getResource("diagrams/"+processKey+".png").openStream();
			
		} catch (Exception e) {
			result.setSuccess(false);
			result.setResult_name("流程资源文件不存在！！！");
			return result;
		}
	    this.repositoryService
			.createDeployment()
			.addInputStream(processKey+".bpmn", bpmn)
			.addInputStream(processKey+".png", png)
			.category(processKey)
			.name(processKey)
			.deploy();
		result.setResult_name("流程部署成功！");
		return result;
	}


	@Override
	public Result stopProcess(String bpmnType, String businessKey) {
		Result result = new Result();
		ProcessInstance processInstance = this.runtimeService
				.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey, bpmnType)
				.singleResult();
		List<HistoricProcessInstance> historyList = this.historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(bpmnType)
				.processInstanceBusinessKey(businessKey)
				.list();
		if(processInstance == null && (historyList==null||historyList.size()==0)){
			result.setSuccess(false);
			result.setResult_name("流程已经结束！");
		}else if(processInstance == null && historyList!=null&&historyList.size()>0){
			for(int i = 0; i < historyList.size(); i++){
				HistoricProcessInstance hpi = historyList.get(i);
				this.historyService.deleteHistoricProcessInstance(hpi.getId());
			}
			result.setSuccess(false);
			result.setResult_name("清除了历史流程，执行成功！");
		}else if(processInstance.isEnded()){
			result.setSuccess(false);
			result.setResult_name("流程已经结束！");
		}else{
			this.runtimeService.deleteProcessInstance(processInstance.getId(), "流程不对，要结束");
			this.historyService.deleteHistoricProcessInstance(processInstance.getId());
			result.setResult_name("执行成功！");
		}
		return result;
		/*String userId = ThreadLocalUtil.getUserId();
		this.querySequenceFlows(bpmnType, businessKey, userId);
		return new Result();*/
	}
	
	public List<SequenceFlow> querySequenceFlows(String bpmnType, String businessId, String userId) {
//		userId = "0001N6100000000JJ4DD";
		Task task = this.taskService.createTaskQuery()
			.processDefinitionKey(bpmnType)
			.processInstanceBusinessKey(businessId)
			.taskCandidateOrAssigned(userId)
			.singleResult();
		if(task == null){
			return new ArrayList<SequenceFlow>();
		}
		return this.querySequenceFlowsByTask(task);
	}
	
	public List<SequenceFlow> querySequenceFlowsByTask(Task task){
		List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();
		String processDefId = task.getProcessDefinitionId();
		BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefId);
		if(bpmnModel != null) {  
		    Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();  
		    for(FlowElement e : flowElements) {  
		    	if(e.getId().equals(task.getTaskDefinitionKey())){
		    		sequenceFlows = ((UserTask)e).getOutgoingFlows();
		    		return sequenceFlows;
		    	}
		    }  
		}  
		return sequenceFlows;
	}


	@Override
	public void testData() {
//		String businessId = "584fc1d335445d3e92b6d9da";
//		Map<String, Object> doc = this.formalAssementService.queryById(businessId);
//		this.updateAttachmentPath((Document) doc);
//		System.out.println(doc);
	}
	
	private void updateAttachmentPath(Document doc){
		if(!doc.containsKey("attachment")){
			return;
		}
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		if(attachmentList == null || attachmentList.isEmpty()){
			return;
		}
		for(int i = 0; i < attachmentList.size(); i++){
			Document attachment = attachmentList.get(i);
			if(attachment != null && attachment.containsKey("files") && attachment.get("files")!=null){
				List<Document> files = (List<Document>) attachment.get("files");
				if(files.isEmpty()){
					continue;
				}
				for(int m = 0; m < files.size(); m++){
					Document file = files.get(m);
					if(file==null || !file.containsKey("filePath") || file.getString("filePath")==null){
						continue;
					}
					String filePath = file.getString("filePath");
					file.put("filePath", filePath.replaceAll("\\\\", "/"));
				}
			}
		}
	}


	@Override
	public Result queryTaskInfoById(String taskId) {
		Result result = new Result();
		Task task = this.taskService.createTaskQuery().taskId(taskId)
			.singleResult();
		if(task == null){
			return result.setResult_data(null);
		}
		TaskInfo taskInfo = new TaskInfo();
		String assignee = task.getAssignee();
		String formKey = task.getFormKey();
		String taskKey = task.getTaskDefinitionKey();
		String description = task.getDescription();
		List<SequenceFlow> outSequences = this.querySequenceFlowsByTask(task);
		taskInfo.setTaskId(taskId);
		taskInfo.setTaskKey(taskKey);
		taskInfo.setDescription(description);
		taskInfo.setAssignee(assignee);
		taskInfo.setFormKey(formKey);
		//对sequenceFlow按id进行排序
		List<SequenceFlow> flows = new ArrayList<SequenceFlow>();
		List<String> ids = new ArrayList<String>();
		int count = outSequences.size();
		
		for(int m = 0; m < count; m++){
			String minId = null;
			int index = 0;
			for(int i = 0; i < outSequences.size(); i++){
				if(ids.contains(outSequences.get(i).getId())){
					continue;
				}
				if(minId == null){
					minId = outSequences.get(i).getId();
					continue;
				}
				Integer minIdInt = Integer.parseInt(minId.substring(4));
				Integer cidInt = Integer.parseInt(outSequences.get(i).getId().substring(4));
				if(minIdInt > cidInt){
					minId = outSequences.get(i).getId();
					index = i;
				}
			}
			flows.add(outSequences.get(index));
			ids.add(minId);
		}
		taskInfo.setOutSequences(flows);
		result.setResult_data(taskInfo);
		return result;
	}


	@Override
	public List<Map<String, Object>> queryProjectList(Map<String, Object> params) {
		return this.bpmnMapper.queryProjectList(params);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Result queryProjectList(String flow) {
		Result result = new Result();
		Document doc = Document.parse(flow);
		Document project =  (Document) doc.get("project");
		String type = (String) project.get("PROJECT_TYPE");
		String businessId = (String) project.get("BUSINESS_ID");
		Document user = (Document) doc.get("user");
		String newUserId = (String) user.get("VALUE");
		String task = (String) doc.get("task");
		
		if("pre".equals(type)){
			Map<String, Object> preMongo = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
			Map<String, Object> apply = (Map<String, Object>) preMongo.get("apply");
			//投资经理
			Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
			//双投investmentPerson
			Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
			Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
			Map<String, Object> pertainArea = (Map<String, Object>) apply.get("pertainArea");
			Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
			//任务人员信息
			Map<String, Object> taskallocation = (Map<String, Object>) preMongo.get("taskallocation");
			List<Map<String, Object>> queryAuditLogs = this.preAuditLogService.queryAuditLogs(businessId);
			
			if("task".equals(task)){
				this.changeTaskPerson(newUserId);
			}else if("businessLeader".equals(task)){
				//判断所有项目是否有当前节点待办，有则处理，无则修改
				List<Map<String, Object>> preWaitingLogs = this.preAuditLogService.queryWaitingLogs(null);
				//获取当前项目所属大区
				for (Map<String, Object> log : preWaitingLogs) {
					if(Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						Document description = Document.parse(documation);
						if("1".equals(description.get("isBusinessLeader"))){
							
							Map<String, Object> fMongo = this.baseMongo.queryById((String)log.get("BUSINESSID"), Constants.RCM_PRE_INFO);
							Map<String, Object> fapply = (Map<String, Object>) fMongo.get("apply");
							//发送待办信息
							this.reduceWaiting("pre", (String)log.get("BUSINESSID"), newUserId, (String)fapply.get("projectName"),(String)log.get("TASKID"));
							//修改审核日志
							this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
						}
					}
				}
				//修改对大区人员信息
				this.pertainAreaService.updatePersonByPertainAreaId((String)pertainArea.get("KEY"),newUserId,"1");
				
			}else if("businessArea".equals(task)){
				List<Map<String, Object>> queryPertainAreaByOrgPkValue = this.pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
				if(queryPertainAreaByOrgPkValue.size()>0){
					Map<String, Object> pMap = queryPertainAreaByOrgPkValue.get(0);
					
					//判断所有项目是否有当前节点待办，有则处理，无则修改
					List<Map<String, Object>> preWaitingLogs = this.preAuditLogService.queryWaitingLogs((String)pMap.get("LEADERID"));
					//获取当前项目所属大区
					for (Map<String, Object> log : preWaitingLogs) {
						if(Util.isNotEmpty(log.get("TASKID"))){
							String taskId = (String)log.get("TASKID");
							Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
							String documation = (String) ruTask.get("DESCRIPTION_");
							if(Util.isNotEmpty(documation)){
								Document description = Document.parse(documation);
								if("1".equals(description.get("isBusinessArea"))){
									Map<String, Object> fMongo = this.baseMongo.queryById((String)log.get("BUSINESSID"), Constants.RCM_PRE_INFO);
									Map<String, Object> fapply = (Map<String, Object>) fMongo.get("apply");
									//发送待办信息
									this.reduceWaiting("pre", (String)log.get("BUSINESSID"), newUserId, (String)fapply.get("projectName"),(String)log.get("TASKID"));
									//修改审核日志
									this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
								}
							}
						}
					}
					//修改对大区人员信息
					this.pertainAreaService.updateById((String)pMap.get("ID"),newUserId);
				}else{
					result.setSuccess(false);
					result.setResult_name("该项目没有业务区负责人！");
					return result;
				}
			}else if("createBy".equals(task)){
				if(Util.isEmpty(investmentManager)){
					result.setSuccess(false);
					result.setResult_name("该项目没有投资经理！");
					return result;
				}
				//获取投资经理id
				String investmentManagerId = (String) investmentManager.get("VALUE");
				if(Util.isEmpty(investmentManagerId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有投资经理！");
					return result;
				}
				
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						Document description = Document.parse(documation);
						if("1".equals(description.get("isInvestmentManager")) || "1".equals(description.get("isInvestmentManagerBack"))){
							//发送待办信息
							this.reduceWaiting("pre", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
							
							//修改审核日志
							this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"investmentManager","isInvestmentManager");
				this.changeActPerson(businessId,newUserId,"investmentManager","isInvestmentManagerBack");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("createBy", newUserId);
				data.put("businessId", businessId);
				this.preInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				investmentManager.put("NAME", (String)newUser.get("NAME"));
				investmentManager.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_PRE_INFO);		
				
			}else if("largeAreaLeader".equals(task)){
				if(Util.isEmpty(companyHeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
				//获取双投负责人id
				String companyHeaderId = (String) companyHeader.get("VALUE");
				if(Util.isEmpty(companyHeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
				
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						Document description = Document.parse(documation);
						if("1".equals(description.get("isLargeArea")) || "1".equals(description.get("isLargeAreaBack"))){
							//发送待办信息
							this.reduceWaiting("pre", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
							//修改审核日志
							this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"largeArea","isLargeArea");
				this.changeActPerson(businessId,newUserId,"largeArea","isLargeAreaBack");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("largeAreaPersonId", newUserId);
				data.put("businessId", businessId);
				this.preInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				companyHeader.put("NAME", (String)newUser.get("NAME"));
				companyHeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_PRE_INFO);
				
			}else if("serviceTypePerson".equals(task)){
				if(Util.isEmpty(investmentPerson)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
				//获取双投负责人id
				String investmentPersonId = (String) investmentPerson.get("VALUE");
				if(Util.isEmpty(investmentPersonId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
					for (Map<String, Object> log : queryAuditLogs) {
						if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
							String taskId = (String)log.get("TASKID");
							Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
							String documation = (String) ruTask.get("DESCRIPTION_");
							Document description = Document.parse(documation);
							if("1".equals(description.get("isServiewType"))){
								//发送待办信息
								this.reduceWaiting("pre", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								
								//修改审核日志
								this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				this.changeActPerson(businessId,newUserId,"serviceType","isServiewType");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("serviceTypePersonId", newUserId);
				data.put("businessId", businessId);
				this.preInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				investmentPerson.put("NAME", (String)newUser.get("NAME"));
				investmentPerson.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_PRE_INFO);
				
			}else if("reviewLeader".equals(task)){
				if(Util.isEmpty(taskallocation)){
					result.setSuccess(false);
					result.setResult_name("该项目没有分配任务，暂无评审负责人！");
					return result;
				}
				//获取评审负责人id
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				if(Util.isEmpty(reviewLeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				String reviewLeaderId = (String) reviewLeader.get("VALUE");
				if(Util.isEmpty(reviewLeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						Document description = Document.parse(documation);
						if("1".equals(description.get("isReviewLeader"))){
							//发送待办信息
							this.reduceWaiting("pre", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
							
							//修改审核日志
							this.preAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"reviewLeader","isReviewLeader");
				//修改基本信息表
				Map<String, Object> data = new HashMap<String,Object>();
				data.put("reviewLeader", newUserId);
				data.put("businessId", businessId);
				this.preInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				reviewLeader.put("NAME", (String)newUser.get("NAME"));
				reviewLeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("taskallocation", taskallocation);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_PRE_INFO);
				
			}else{
				result.setSuccess(false);
				result.setResult_name("请选择正确的角色！");
				return result;
			}
		}else if("pfr".equals(type)){
			Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			Map<String, Object> apply = (Map<String, Object>) formalMongo.get("apply");
			//投资经理
			Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
			//双投investmentPerson
			Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
			Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
			Map<String, Object> grassrootsLegalStaff = (Map<String, Object>) apply.get("grassrootsLegalStaff");
			Map<String, Object> pertainArea = (Map<String, Object>) apply.get("pertainArea");
			Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
			//任务人员信息
			Map<String, Object> taskallocation = (Map<String, Object>) formalMongo.get("taskallocation");
			List<Map<String, Object>> queryAuditLogs = this.formalAssessmentAuditLogService.queryAuditLogs(businessId);
			
			if("task".equals(task)){
				this.changeTaskPerson(newUserId);
			}else if("businessLeader".equals(task)){
//				//判断所有项目是否有当前节点待办，有则处理，无则修改
//				List<Map<String, Object>> formalWaitingLogs = this.formalAssessmentAuditLogService.queryWaitingLogs(null);
//				//获取当前项目所属大区
//				for (Map<String, Object> log : formalWaitingLogs) {
//					if(Util.isNotEmpty(log.get("TASKID"))){
//						String taskId = (String)log.get("TASKID");
//						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
//						String documation = (String) ruTask.get("DESCRIPTION_");
//						Document description = Document.parse(documation);
//						if("1".equals(description.get("isBusinessLeader"))){
//							
//							Map<String, Object> fMongo = this.baseMongo.queryById((String)log.get("BUSINESSID"), Constants.RCM_FORMALASSESSMENT_INFO);
//							Map<String, Object> fapply = (Map<String, Object>) fMongo.get("apply");
//							//发送待办信息
//							this.reduceWaiting("pfr", (String)log.get("BUSINESSID"), newUserId, (String)fapply.get("projectName"),(String)log.get("TASKID"));
//							//修改审核日志
//							this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
//						}
//					}
//				}
//				//修改对大区人员信息
//				this.pertainAreaService.updatePersonByPertainAreaId((String)pertainArea.get("KEY"),newUserId,"2");
//				
			}else if("businessArea".equals(task)){
				List<Map<String, Object>> queryPertainAreaByOrgPkValue = this.pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
				if(queryPertainAreaByOrgPkValue.size()>0){
					Map<String, Object> pMap = queryPertainAreaByOrgPkValue.get(0);
					
					//判断所有项目是否有当前节点待办，有则处理，无则修改
					List<Map<String, Object>> preWaitingLogs = this.preAuditLogService.queryWaitingLogs((String)pMap.get("LEADERID"));
					//获取当前项目所属大区
					for (Map<String, Object> log : preWaitingLogs) {
						if(Util.isNotEmpty(log.get("TASKID"))){
							String taskId = (String)log.get("TASKID");
							Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
							String documation = (String) ruTask.get("DESCRIPTION_");
							if(Util.isNotEmpty(documation)){
								Document description = Document.parse(documation);
								if("1".equals(description.get("isBusinessArea"))){
									Map<String, Object> fMongo = this.baseMongo.queryById((String)log.get("BUSINESSID"), Constants.RCM_FORMALASSESSMENT_INFO);
									Map<String, Object> fapply = (Map<String, Object>) fMongo.get("apply");
									//发送待办信息
									this.reduceWaiting("pfr", (String)log.get("BUSINESSID"), newUserId, (String)fapply.get("projectName"),(String)log.get("TASKID"));
									//修改审核日志
									this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
								}
							}
						}
					}
					//修改对大区人员信息
					this.pertainAreaService.updateById((String)pMap.get("ID"),newUserId);
				}else{
					result.setSuccess(false);
					result.setResult_name("该项目没有业务区负责人！");
					return result;
				}
			}else if("createBy".equals(task)){
				if(Util.isEmpty(investmentManager)){
					result.setSuccess(false);
					result.setResult_name("该项目没有投资经理！");
					return result;
				}
				//获取投资经理id
				String investmentManagerId = (String) investmentManager.get("VALUE");
				//基层法务id
				String grassrootsLegalStaffId = (String) grassrootsLegalStaff.get("VALUE");
				if(Util.isEmpty(investmentManagerId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有投资经理！");
					return result;
				}
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							Document description = Document.parse(documation);
							if("1".equals(description.get("isInvestmentManager")) || "1".equals(description.get("isInvestmentManagerBack"))){
								//发送待办信息
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
							//如果投资经理与基层法务人员相同，则将继承法务同时换掉
							if(grassrootsLegalStaffId.equals(investmentManagerId)){
								if("1".equals(description.get("isGrassRootsLegal"))){
									//发送待办信息
									this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
									//修改审核日志
									this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
								}
							}
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"investmentManager","isInvestmentManager");
				this.changeActPerson(businessId,newUserId,"investmentManager","isInvestmentManagerBack");
				//如果投资经理与基层法务人员相同，则将继承法务同时换掉
				if(grassrootsLegalStaffId.equals(investmentManagerId)){
					this.changeActPerson(businessId,newUserId,"grassRootsLegal","isGrassRootsLegal");
				}
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("createBy", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				investmentManager.put("NAME", (String)newUser.get("NAME"));
				investmentManager.put("VALUE", (String)newUser.get("UUID"));
				
				//如果投资经理与基层法务人员相同，则将继承法务同时换掉
				if(grassrootsLegalStaffId.equals(investmentManagerId)){
					grassrootsLegalStaff.put("NAME", (String)newUser.get("NAME"));
					grassrootsLegalStaff.put("VALUE", (String)newUser.get("UUID"));
				}
				
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);		
			}else if("largeAreaLeader".equals(task)){
				if(Util.isEmpty(companyHeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有大区责人！！");
					return result;
				}
				//获取双投负责人id
				String companyHeaderId = (String) companyHeader.get("VALUE");
				if(Util.isEmpty(companyHeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有大区责人！");
					return result;
				}
				//新项目
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							Document description = Document.parse(documation);
							if("1".equals(description.get("isLargeArea"))){
								//发送待办信息
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"largeArea","isLargeArea");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("largeAreaPersonId", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				companyHeader.put("NAME", (String)newUser.get("NAME"));
				companyHeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if("grassLegal".equals(task)){
				if(Util.isEmpty(grassrootsLegalStaff)){
					result.setSuccess(false);
					result.setResult_name("该项目没有基层法务人员！");
					return result;
				}
				//获取基层法务id
				String grassrootsLegalStaffId = (String) grassrootsLegalStaff.get("VALUE");
				if(Util.isEmpty(grassrootsLegalStaffId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有基层法务人员！");
					return result;
				}
				//新项目
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							
							Document description = Document.parse(documation);
							if("1".equals(description.get("isGrassRootsLegal"))){
								//发送待办信息
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				}
				
				this.changeActPerson(businessId,newUserId,"grassRootsLegal","isGrassRootsLegal");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("grassRootsLegalPersonId", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				grassrootsLegalStaff.put("NAME", (String)newUser.get("NAME"));
				grassrootsLegalStaff.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if("serviceTypePerson".equals(task)){
				if(Util.isEmpty(investmentPerson)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
				//获取双投负责人id
				String investmentPersonId = (String) investmentPerson.get("VALUE");
				if(Util.isEmpty(investmentPersonId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有双投负责人！");
					return result;
				}
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							
							Document description = Document.parse(documation);
							if("1".equals(description.get("isServiewType"))){
								//发送待办信息
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"serviceType","isServiewType");
				
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("serviceTypePersonId", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				investmentPerson.put("NAME", (String)newUser.get("NAME"));
				investmentPerson.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("apply", apply);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);
			}else if("reviewLeader".equals(task)){
				if(Util.isEmpty(taskallocation)){
					result.setSuccess(false);
					result.setResult_name("该项目没有分配任务，暂无评审负责人！");
					return result;
				}
				//获取评审负责人id
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				if(Util.isEmpty(reviewLeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				String reviewLeaderId = (String) reviewLeader.get("VALUE");
				if(Util.isEmpty(reviewLeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							Document description = Document.parse(documation);
							if("1".equals(description.get("isReviewLeader"))){
								//发送待办信息
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"reviewLeader","isReviewLeader");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("reviewLeader", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				reviewLeader.put("NAME", (String)newUser.get("NAME"));
				reviewLeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("taskallocation", taskallocation);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);
				
				HpgClient hpgClient = new HpgClient(businessId,newUserId);
				Thread thread = new Thread(hpgClient);
				thread.start();
				
			}else if("legalLeader".equals(task)){
				if(Util.isEmpty(taskallocation)){
					result.setSuccess(false);
					result.setResult_name("该项目没有分配任务，暂无法律负责人！");
					return result;
				}
				//获取评审负责人id
				Map<String, Object> legalReviewLeader = (Map<String, Object>) taskallocation.get("legalReviewLeader");
				if(Util.isEmpty(legalReviewLeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有法律负责人！");
					return result;
				}
				String legalReviewLeaderId = (String) legalReviewLeader.get("VALUE");
				if(Util.isEmpty(legalReviewLeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有法律负责人！");
					return result;
				}
				
				//新项目
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS")) && Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							
							Document description = Document.parse(documation);
							if("1".equals(description.get("isLegalReviewLeader"))){
								//发送待办信息
								//新评审
								this.reduceWaiting("pfr", businessId, newUserId, (String)apply.get("projectName"),(String)log.get("TASKID"));
								//修改审核日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"),newUserId);
							}
						}
					}
				}
				this.changeActPerson(businessId,newUserId,"legalReviewLeader","isLegalReviewLeader");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("legalReviewLeader", newUserId);
				data.put("businessId", businessId);
				this.formalAssessmentInfoService.updatePersonById(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				legalReviewLeader.put("NAME", (String)newUser.get("NAME"));
				legalReviewLeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("taskallocation", taskallocation);
				this.baseMongo.updateSetByObjectId(businessId, mongoData, Constants.RCM_FORMALASSESSMENT_INFO);
				
			}else if("legalTask".equals(task)){
				//获取旧人员id
				Map<String, Object> roleByCode = roleService.queryRoleByCode(Constants.ROLE_FORMAL_LEGALTASK);
				String roleId = (String) roleByCode.get("ROLE_ID");
				Map<String, Object> userMap = roleService.queryRoleUserByRoleId(roleId);
				
				String oldUserId = (String) userMap.get("UUID");
				
				List<Map<String, Object>> formalWaitingLogs = formalAssessmentAuditLogService.queryWaitingLogs(oldUserId);
				for (Map<String, Object> log : formalWaitingLogs) {
					if(Util.isNotEmpty((String)log.get("TASKID"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							
							Document description = Document.parse(documation);
							if("1".equals(description.get("isSelectLegalLeader"))){
								String pBusinessId = (String) log.get("BUSINESSID");
								//发送待办信息
								String oldurl = "#/FormalAssessmentAuditList/1";
								String eUrl = Util.encodeUrl(oldurl);
								String url = "/FormalAssessmentAuditDetailView/"+pBusinessId+"/"+eUrl;
								Map<String, Object> fMongo = this.baseMongo.queryById(pBusinessId, Constants.RCM_FORMALASSESSMENT_INFO);
								Map<String, Object> fApply = (Map<String, Object>) fMongo.get("apply");
								this.sendWaitingToPotal((String)log.get("TASKID"), url, Util.format(Util.now()), (String)fApply.get("projectName"), newUserId, "1", "未知");;
								//修改待办日志
								this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"), newUserId);
							}
						}
					}
				
				}
				//修改人员
				roleService.updateUserIdByRoleCode(newUserId, Constants.ROLE_FORMAL_LEGALTASK);
			}else{
				result.setSuccess(false);
				result.setResult_name("请选择正确的角色！");
				return result;
			}
		}else if("bulletin".equals(type)){
			Map<String, Object> bulletinMongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
			//任务人员信息
			Map<String, Object> taskallocation = (Map<String, Object>) bulletinMongo.get("taskallocation");
			List<Map<String, Object>> queryAuditLogs = this.bulletinAuditLogService.queryAuditLogs(businessId);
			if("task".equals(task)){
				this.changeTaskPerson(newUserId);
			}else if("reviewLeader".equals(task)){
				if(Util.isEmpty(taskallocation)){
					result.setSuccess(false);
					result.setResult_name("该项目没有分配任务，暂无评审负责人！");
					return result;
				}
				//获取评审负责人id
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				Map<String, Object> riskLeader = (Map<String, Object>) taskallocation.get("riskLeader");
				if(Util.isEmpty(reviewLeader) && Util.isEmpty(riskLeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				Map<String, Object> newUser = userService.queryById(newUserId);
				String oldUserId = "";
				String valKey = "";
				String desc = "";
				if(Util.isNotEmpty(reviewLeader)){
					valKey = "reviewLeader";
					desc = "isReviewLeader";
					oldUserId = (String) reviewLeader.get("VALUE");
					reviewLeader.put("NAME", (String)newUser.get("NAME"));
					reviewLeader.put("VALUE", (String)newUser.get("UUID"));
					
				}else if(Util.isNotEmpty(riskLeader)){
					valKey = "riskLeader";
					desc = "isRiskLeader";
					oldUserId = (String) riskLeader.get("VALUE");
					riskLeader.put("NAME", (String)newUser.get("NAME"));
					riskLeader.put("VALUE", (String)newUser.get("UUID"));
				}
				if(Util.isEmpty(oldUserId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有评审负责人！");
					return result;
				}
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							
							Document description = Document.parse(documation);
							if("1".equals(description.get("isRiskLeader")) || "1".equals(description.get("isReviewLeader"))){
								//发送待办信息
								this.reduceWaiting("bulletin", businessId, newUserId, (String)bulletinMongo.get("bulletinName"),(String)log.get("TASKID"));
								//修改审核日志
								this.bulletinAuditLogService.updateAuditUserIdById(newUserId,(String)log.get("ID"));
								this.bulletinAuditLogService.updateOldPerson((String)log.get("ID"), newUserId);
							}
						}
					}
				}
				this.changeActPerson(businessId, newUserId,valKey,desc);
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("reviewLeaderId", newUserId);
				data.put("businessId", businessId);
				this.bulletinInfoService.updatePerson(data);
				//修改mongo
				Map<String, Object> mongoData = new HashMap<String,Object>();
				mongoData.put("taskallocation", taskallocation);
				this.baseMongo.updateSetById(businessId, mongoData, Constants.RCM_BULLETIN_INFO);
			}else if("legalLeader".equals(task)){
				if(Util.isEmpty(taskallocation)){
					result.setSuccess(false);
					result.setResult_name("该项目没有分配任务，暂无法律负责人！");
					return result;
				}
				//获取评审负责人id
				Map<String, Object> legalLeader = (Map<String, Object>) taskallocation.get("legalLeader");
				if(Util.isEmpty(legalLeader)){
					result.setSuccess(false);
					result.setResult_name("该项目没有法律负责人！");
					return result;
				}
				String legalLeaderId = (String) legalLeader.get("VALUE");
				if(Util.isEmpty(legalLeaderId)){
					result.setSuccess(false);
					result.setResult_name("该项目没有法律负责人！");
					return result;
				}
				
				for (Map<String, Object> log : queryAuditLogs) {
					if("9".equals((String)log.get("AUDITSTATUS"))){
						String taskId = (String)log.get("TASKID");
						Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
						String documation = (String) ruTask.get("DESCRIPTION_");
						if(Util.isNotEmpty(documation)){
							Document description = Document.parse(documation);
							if("1".equals(description.get("isLegalLeader"))){
								//发送待办信息
								this.reduceWaiting("bulletin", businessId, newUserId, (String)bulletinMongo.get("bulletinName"),(String)log.get("TASKID"));
								//修改审核日志
								if(Util.isEmpty((String)log.get("OLDUSERID"))){
									this.bulletinAuditLogService.updateAuditUserIdById(newUserId,(String)log.get("ID"));
								}
								if(Util.isNotEmpty((String)log.get("OLDUSERID"))){
									this.bulletinAuditLogService.updateOldPerson((String)log.get("ID"), newUserId);
								}
							}
						}
					}
				}
				this.changeActPerson(businessId, newUserId,"legalLeader","isLegalLeader");
				//修改基本信息表
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("legalLeaderId", newUserId);
				data.put("businessId", businessId);
				this.bulletinInfoService.updatePerson(data);
				//修改mongo
				Map<String, Object> newUser = userService.queryById(newUserId);
				Map<String, Object> mongoData = new HashMap<String,Object>();
				legalLeader.put("NAME", (String)newUser.get("NAME"));
				legalLeader.put("VALUE", (String)newUser.get("UUID"));
				mongoData.put("taskallocation", taskallocation);
				this.baseMongo.updateSetById(businessId, mongoData, Constants.RCM_BULLETIN_INFO);
			}else if("businessPerson".equals(task)){
				Map<String, Object> execution = this.bpmnMapper.getProcInstIdByBusinessId(businessId);
				
				String procInstId = (String) execution.get("PROC_INST_ID_");
				
				List<Map<String, Object>> variableList = this.bpmnMapper.queryVariableByProcInstId(procInstId);
				
				for (Map<String, Object> v : variableList) {
					if("businessPersonRole".equals(v.get("NAME_"))){
						String roleId = (String) v.get("TEXT_");
						Map<String, Object> oldUser = this.roleService.queryUserById(roleId).get(0);
						
						String oldUserId = (String) oldUser.get("UUID");
						List<Map<String, Object>> bulletinWaitingLogs = bulletinAuditLogService.queryWaitingLogs(oldUserId );
						

						for (Map<String, Object> log : bulletinWaitingLogs) {
							
							if(Util.isNotEmpty((String)log.get("TASKID"))){
								String taskId = (String)log.get("TASKID");
								Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
								String documation = (String) ruTask.get("DESCRIPTION_");
								if(Util.isEmpty(documation)){
									result.setSuccess(false);
									result.setResult_name("此项目为旧项目无法修改业务负责人！");
									return result;
								}
								Document description = Document.parse(documation);
								if("1".equals(description.get("isBusiness"))){
									String pBusinessId = (String) log.get("BUSINESSID");
									//发送待办信息
									String oldurl = "#/BulletinMattersAudit/1";
									String out = Util.encodeUrl(oldurl);
									String url = "/BulletinMattersAuditView/"+pBusinessId+"/"+out;
									Map<String, Object> bMongo = this.baseMongo.queryById(pBusinessId, Constants.RCM_BULLETIN_INFO);
									this.sendWaitingToPotal((String)log.get("TASKID"), url, Util.format(Util.now()), (String)bMongo.get("bulletinName"), newUserId, "1", "未知");;
									this.bulletinAuditLogService.updateWaitingPerson((String)log.get("ID"), newUserId);
								}
							}
							
						}
						
						
						//处理待办日志
						this.roleService.updateOneUserByRoleId(roleId,newUserId);
					}
				}
				
			}else{
				result.setSuccess(false);
				result.setResult_name("请选择正确的角色！");
				return result;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Result changeTaskPerson(String newUserId){

		//获取旧人员id
		Map<String, Object> userMap = roleService.queryRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER).get(0);
		String oldUserId = (String) userMap.get("VALUE");
		
		List<Map<String, Object>> formalWaitingLogs = formalAssessmentAuditLogService.queryWaitingLogs(oldUserId);
		List<Map<String, Object>> preWaitingLogs = preAuditLogService.queryWaitingLogs(oldUserId);
		List<Map<String, Object>> bulletinWaitingLogs = bulletinAuditLogService.queryWaitingLogs(oldUserId);
		for (Map<String, Object> log : formalWaitingLogs) {
			if(Util.isNotEmpty((String)log.get("TASKID"))){
				String taskId = (String)log.get("TASKID");
				Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
				String documation = (String) ruTask.get("DESCRIPTION_");
				Document description = Document.parse(documation);
				if("1".equals(description.get("isTask"))){
					String pBusinessId = (String) log.get("BUSINESSID");
					//发送待办信息
					String oldurl = "#/FormalAssessmentAuditList/1";
					String eUrl = Util.encodeUrl(oldurl);
					String url = "/FormalAssessmentAuditDetailView/"+pBusinessId+"/"+eUrl;
					Map<String, Object> fMongo = this.baseMongo.queryById(pBusinessId, Constants.RCM_FORMALASSESSMENT_INFO);
					Map<String, Object> fApply = (Map<String, Object>) fMongo.get("apply");
					this.sendWaitingToPotal((String)log.get("TASKID"), url, Util.format(Util.now()), (String)fApply.get("projectName"), newUserId, "1", "未知");;
					//修改待办日志
					this.formalAssessmentAuditLogService.updateWaitingPerson((String)log.get("ID"), newUserId);
				}
			}
		
		}
		for (Map<String, Object> log : preWaitingLogs) {
			
			if(Util.isNotEmpty((String)log.get("TASKID"))){
				String taskId = (String)log.get("TASKID");
				Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
				String documation = (String) ruTask.get("DESCRIPTION_");
				Document description = Document.parse(documation);
				if("1".equals(description.get("isTask"))){
					String pBusinessId = (String) log.get("BUSINESSID");
					//发送待办信息
					String oldurl = "#/PreAuditList/1";
					String eUrl = Util.encodeUrl(oldurl);
					String url = "/PreAuditDetailView/"+pBusinessId+"/"+eUrl;
					Map<String, Object> pMongo = this.baseMongo.queryById(pBusinessId, Constants.RCM_PRE_INFO);
					Map<String, Object> pApply = (Map<String, Object>) pMongo.get("apply");
					this.sendWaitingToPotal((String)log.get("TASKID"), url, Util.format(Util.now()), (String)pApply.get("projectName"), newUserId, "1", "未知");;
					this.preAuditLogService.updateWaitingPerson((String)log.get("ID"), newUserId);
				}
			}
		
		}
		for (Map<String, Object> log : bulletinWaitingLogs) {
			
			if(Util.isNotEmpty((String)log.get("TASKID"))){
				String taskId = (String)log.get("TASKID");
				Map<String, Object> ruTask = this.bpmnMapper.queryActRuTaskByTaskId(taskId);
				String documation = (String) ruTask.get("DESCRIPTION_");
				Document description = Document.parse(documation);
				if("1".equals(description.get("isTask")) || "1".equals(description.get("isRiskTask"))){
					String pBusinessId = (String) log.get("BUSINESSID");
					//发送待办信息
					String oldurl = "#/BulletinMattersAudit/1";
					String out = Util.encodeUrl(oldurl);
					String url = "/BulletinMattersAuditView/"+pBusinessId+"/"+out;
					Map<String, Object> bMongo = this.baseMongo.queryById(pBusinessId, Constants.RCM_BULLETIN_INFO);
					this.sendWaitingToPotal((String)log.get("TASKID"), url, Util.format(Util.now()), (String)bMongo.get("bulletinName"), newUserId, "1", "未知");;
					this.bulletinAuditLogService.updateWaitingPerson((String)log.get("ID"), newUserId);
				}
			}
			
		}
		//修改人员
		roleService.updateUserIdByRoleCode(newUserId, Constants.TASK_ASSIGNEE_MANAGER);
		roleService.updateUserIdByRoleCode(newUserId, Constants.ROLE_TBSX_ASSIGNEE_MANAGER);
		return null;
	}
	public void reduceWaiting(String type,String businessId,String newUserId,String pName,String taskId){
		if("pre".equals(type)){
			//新评审
			String oldurl = "#/PreAuditList/1";
			String eUrl = Util.encodeUrl(oldurl);
			String url = "/PreAuditDetailView/"+businessId+"/"+eUrl;
			this.sendWaitingToPotal(taskId, url, Util.format(Util.now()), pName, newUserId, "1", "未知");
		}else if("pfr".equals(type)){
			String oldurl = "#/FormalAssessmentAuditList/1";
			String eUrl = Util.encodeUrl(oldurl);
			String url = "/FormalAssessmentAuditDetailView/"+businessId+"/"+eUrl;
			this.sendWaitingToPotal(taskId, url, Util.format(Util.now()), pName, newUserId, "1", "未知");
		}else if("bulletin".equals(type)){
			String oldurl = "#/BulletinMattersAudit/1";
			String out = Util.encodeUrl(oldurl);
			String url = "/BulletinMattersAuditView/"+businessId+"/"+out;
			this.sendWaitingToPotal(taskId, url, Util.format(Util.now()), pName, newUserId, "1", "未知");
		}
	}
	@Override
	public void sendWaitingToPotal(String taskId,String url,String createDate,String title,String owner,String status,String sender) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("taskId", taskId);
		map.put("url", url);
		map.put("createDate", createDate);
		map.put("sender", sender);
		map.put("title", title);
		map.put("owner", owner);
		map.put("status", status);
		String json = JsonUtil.toJson(map);
		this.wscallService.sendTask(json);
	}
	private void changeActPerson(String businessId,String newUser,String variableKey,String description){
		//查询实例id
		Map<String, Object> execution = this.bpmnMapper.getProcInstIdByBusinessId(businessId);
		
		String procInstId = (String) execution.get("PROC_INST_ID_");
		
		List<Map<String, Object>> taskList = this.bpmnMapper.queryActTaskByProcInstId(procInstId);
		
		for (Map<String, Object> ruTask : taskList) {
			String documation = (String) ruTask.get("DESCRIPTION_");
			Document descriptionDoc = Document.parse(documation);
			if("1".equals(descriptionDoc.get(description))){
				String execution_id_ = (String) ruTask.get("EXECUTION_ID_");
				
				this.bpmnMapper.updateActTask(execution_id_,newUser);
				
			}
		}
		
		//查询task、修改task
		//查询变量，修改变量
		this.bpmnMapper.updateActVariable(procInstId,newUser,variableKey);
	}


	@Override
	public List<Map<String, Object>> queryProjectListByPage(PageAssistant page) {
		
		HashMap<String, Object> hashMap = new HashMap<String,Object>();
		
		if(Util.isNotEmpty(page.getParamMap())){
			
			hashMap.putAll(page.getParamMap());
			
		}
		
		hashMap.put("page", page);
		
		return this.bpmnMapper.queryProjectList(hashMap);
		
	}


	@Override
	public Result endFlow(String bpmnType, String businessKey,String reason) {
		if("pre".equals(bpmnType)){
			bpmnType = "preReview";
		}else if("pfr".equals(bpmnType)){
			bpmnType = "formalReview";
		}else if("bulletin".equals(bpmnType)){
			bpmnType = "bulletin";
		}
		Result result = new Result();
		ProcessInstance processInstance = this.runtimeService
				.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey, bpmnType)
				.singleResult();
		List<HistoricProcessInstance> historyList = this.historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey(bpmnType)
				.processInstanceBusinessKey(businessKey)
				.list();
		if(processInstance == null && (historyList==null||historyList.size()==0)){
			result.setSuccess(false);
			result.setResult_name("流程已经结束！");
		}else if(processInstance == null && historyList!=null&&historyList.size()>0){
			for(int i = 0; i < historyList.size(); i++){
				HistoricProcessInstance hpi = historyList.get(i);
				this.historyService.deleteHistoricProcessInstance(hpi.getId());
			}
			result.setSuccess(false);
			result.setResult_name("清除了历史流程，执行成功！");
		}else if(processInstance.isEnded()){
			result.setSuccess(false);
			result.setResult_name("流程已经结束！");
		}else{
			//发送已办
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("businessId", businessKey);
			List<Map<String, Object>> queryAuditLogs = null;
			if("preReview".equals(bpmnType)){
				queryAuditLogs = this.preAuditLogService.queryWaitingLogsById(businessKey);
			}else if("formalReview".equals(bpmnType)){
				queryAuditLogs = this.formalAssessmentAuditLogService.queryWaitingLogsById(businessKey);
			}else if("bulletin".equals(bpmnType)){
				queryAuditLogs = this.bulletinAuditLogService.queryWaitingLogsById(businessKey);
			}
			if(Util.isNotEmpty(queryAuditLogs)){
				for (Map<String, Object> log : queryAuditLogs) {
					if(Util.isNotEmpty(log.get("TASKID"))){
						String taskId = (String) log.get("TASKID");
						String userId = (String) log.get("AUDITUSERID");
						String url = "";
						String title = "";
						if("preReview".equals(bpmnType)){
							//新正是评审
							String oldurl = "#/PreAuditList/1";
							String eUrl = Util.encodeUrl(oldurl);
							Map<String, Object> preByID = this.preInfoService.getPreInfoByID(businessKey);
							Map<String, Object> preOracle = (Map<String, Object>) preByID.get("oracle");
							url = "/PreAuditDetailView/"+businessKey+"/"+eUrl;
							String name = (String) preOracle.get("PROJECTNAME");
							title = name + "投标评审申请";
						}else if("formalReview".equals(bpmnType)){
							String oldurl = "#/FormalAssessmentAuditList/1";
							String out = Util.encodeUrl(oldurl);
							Map<String, Object> formalAssessmentByID = this.FormalAssessmentInfoService.getFormalAssessmentByID(businessKey);
							Map<String, Object> formalAssessmentOracle = (Map<String, Object>) formalAssessmentByID.get("formalAssessmentOracle");
							url = "/FormalAssessmentAuditDetailView/"+businessKey+"/"+out;
							String name = (String) formalAssessmentOracle.get("PROJECTNAME");
							title = name + "正式评审申请";
						}else if("bulletin".equals(bpmnType)){
							String oldurl = "#/BulletinMattersAudit/1";
							String out = Util.encodeUrl(oldurl);
							url = "/BulletinMattersAuditView/"+businessKey+"/"+out;
							String name = (String) this.bulletinInfoService.queryByBusinessId(businessKey).get("BULLETINNAME");
							title = name;
						}
						PortalClientModel model = new PortalClientModel();
						model.setUniid(taskId);
						model.setTitle(title);
						model.setUrl(url);
						model.setMobileUrl(url);
						model.setOwner(userId);
						model.setCreated(Util.now());
						model.setType("1");
						model.setStatus("5");
						Portal2Client ptClientProxy = (Portal2Client) SpringUtil.getBean("ptClient");
						ptClientProxy.setModel(model);
						Thread t = new Thread(ptClientProxy);
						t.start();
					}
				}
			}
			
			
			this.runtimeService.deleteProcessInstance(processInstance.getId(), "流程不对，要结束");
			//更改日志
			if("preReview".equals(bpmnType)){
				Map<String, Object> log = this.preAuditLogService.queryWaitingLogsById(businessKey).get(0);
				this.preAuditLogService.updateOptionById((String)log.get("ID"), reason);
				//推项目数据
				TzAfterPreReviewClient tzAfterPreReviewClient = new TzAfterPreReviewClient(businessKey, reason);
				Thread t = new Thread(tzAfterPreReviewClient);
				t.start();
			}else if("formalReview".equals(bpmnType)){
				Map<String, Object> log = this.formalAssessmentAuditLogService.queryWaitingLogsById(businessKey).get(0);
				this.formalAssessmentAuditLogService.updateOptionById((String)log.get("ID"), reason);
				
				TzAfterNoticeClient tzAfterNoticeClient = new TzAfterNoticeClient(businessKey, "2",null);
				Thread t = new Thread(tzAfterNoticeClient);
				t.start();
				
			}else if("bulletin".equals(bpmnType)){
				Map<String, Object> log = this.bulletinAuditLogService.queryWaitingLogsById(businessKey).get(0);
				this.bulletinAuditLogService.updateOptionById((String)log.get("ID"), reason);
			}
//			this.historyService.deleteHistoricProcessInstance(processInstance.getId());
			result.setResult_name("执行成功！");
		}
		return result;
	}


	@Override
	public Map<String, Object> getProcessInstanceId(String businessId) {
		return this.bpmnMapper.getHiProcInstIdByBusinessId(businessId);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Result getTaskPerson(String flow) {
		Result result = new Result();
		Document doc = Document.parse(flow);
		Document project =  (Document) doc.get("project");
		String type = (String) project.get("PROJECT_TYPE");
		String businessId = (String) project.get("BUSINESS_ID");
		String task = (String) doc.get("task");
		
		if("pre".equals(type)){
			Map<String, Object> preMongo = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
			Map<String, Object> apply = (Map<String, Object>) preMongo.get("apply");
			Map<String, Object> taskallocation = (Map<String, Object>) preMongo.get("taskallocation");
			Map<String, Object> preOracle = this.preInfoService.getOracleByBusinessId(businessId);
			if("createBy".equals(task)){
				//投资经理
				Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
				result.setResult_data(investmentManager);
			}else if("largeAreaLeader".equals(task)){
				//大区负责人
				Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
				result.setResult_data(companyHeader);
			}else if("task".equals(task)){
				//任务分配人
				Map<String, Object> userMap = roleService.queryRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER).get(0);
				Map<String,Object> taskPerson= new HashMap<String,Object>();
				taskPerson.put("NAME", (String)userMap.get("NAME"));
				taskPerson.put("VALUE", (String)userMap.get("UUID"));
				result.setResult_data(taskPerson);
			}else if("serviceTypePerson".equals(task)){
				//双投中心负责人
				String serviceTypeKey = (String) preOracle.get("SERVICETYPE_ID");
				if("1402".equals(serviceTypeKey) || "1401".equals(serviceTypeKey)){
					String servicePersonId = (String) preOracle.get("SERVICETYPEPERSONID");
					Map<String, Object> servicePerson = this.userService.queryById(servicePersonId);
					
					Map<String,Object> serviceTypePerson= new HashMap<String,Object>();
					serviceTypePerson.put("NAME", (String)servicePerson.get("NAME"));
					serviceTypePerson.put("VALUE", (String)servicePerson.get("UUID"));
					result.setResult_data(serviceTypePerson);
				}else{
					result.setSuccess(false);
					result.setResult_name("项目业务类型非传统水务和水环境项目！");
				}
			}else if("reviewLeader".equals(task)){
				//评审负责人
				if(Util.isEmpty(taskallocation)){
					result.setResult_name("暂未分配任务，无评审负责人！");
					result.setSuccess(false);
				}
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				result.setResult_data(reviewLeader);
			}else if("businessArea".equals(task)){
				//业务区负责人
				Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
				List<Map<String, Object>> queryPertainAreaByOrgPkValue = this.pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
				if(queryPertainAreaByOrgPkValue.size()>0){
					Map<String,Object> businessArea= new HashMap<String,Object>();
					String leaderId = (String)queryPertainAreaByOrgPkValue.get(0).get("leaderid");
					Map<String, Object> userByID = this.userService.getSysUserByID(leaderId);
					
					businessArea.put("NAME",(String)userByID.get("NAME") );
					businessArea.put("VALUE", leaderId);
					result.setResult_data(businessArea);
				}else{
					result.setResult_name("此单位无需走业务区节点！");
					result.setSuccess(false);
				}
			}
		}else if("pfr".equals(type)){
			Map<String, Object> pfrMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			Map<String, Object> pfrOracle = this.formalAssessmentInfoService.getOracleByBusinessId(businessId);
			Map<String, Object> apply = (Map<String, Object>) pfrMongo.get("apply");
			Map<String, Object> taskallocation = (Map<String, Object>) pfrMongo.get("taskallocation");
			if("createBy".equals(task)){
				//投资经理
				Map<String, Object> investmentManager = (Map<String, Object>) apply.get("investmentManager");
				result.setResult_data(investmentManager);
			}else if("largeAreaLeader".equals(task)){
				//大区负责人
				Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
				result.setResult_data(companyHeader);
			}else if("grassLegal".equals(task)){
				//基层法务人员
				Map<String, Object> grassrootsLegalStaff = (Map<String, Object>) apply.get("grassrootsLegalStaff");
				result.setResult_data(grassrootsLegalStaff);
			}else if("task".equals(task)){
				//任务分配人
				Map<String, Object> userMap = roleService.queryRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER).get(0);
				Map<String,Object> taskPerson= new HashMap<String,Object>();
				taskPerson.put("NAME", (String)userMap.get("NAME"));
				taskPerson.put("VALUE", (String)userMap.get("UUID"));
				result.setResult_data(taskPerson);
			}else if("serviceTypePerson".equals(task)){
				//双投中心负责人
				String serviceTypeKey = (String) pfrOracle.get("SERVICETYPE_ID");
				if("1402".equals(serviceTypeKey) || "1401".equals(serviceTypeKey)){
					String servicePersonId = (String) pfrOracle.get("SERVICETYPEPERSONID");
					Map<String, Object> servicePerson = this.userService.queryById(servicePersonId);
					
					Map<String,Object> serviceTypePerson= new HashMap<String,Object>();
					serviceTypePerson.put("NAME", (String)servicePerson.get("NAME"));
					serviceTypePerson.put("VALUE", (String)servicePerson.get("UUID"));
					result.setResult_data(serviceTypePerson);
				}else{
					result.setSuccess(false);
					result.setResult_name("项目业务类型非传统水务和水环境项目！");
				}
			}else if("reviewLeader".equals(task)){
				//评审负责人
				if(Util.isEmpty(taskallocation)){
					result.setResult_name("暂未分配任务，无评审负责人！");
					result.setSuccess(false);
				}
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				result.setResult_data(reviewLeader);
			}else if("legalLeader".equals(task)){
				//法律负责人
				if(Util.isEmpty(taskallocation)){
					result.setResult_name("暂未分配任务，无法律负责人！");
					result.setSuccess(false);
					return result;
				}
				Map<String, Object> legalReviewLeader = (Map<String, Object>) taskallocation.get("legalReviewLeader");
				if(Util.isEmpty(legalReviewLeader)){
					result.setResult_name("暂未分配任务，无法律负责人！");
					result.setSuccess(false);
					return result;
				}
				result.setResult_data(legalReviewLeader);
			}else if("legalTask".equals(task)){
				//法律任务分配人
				Map<String, Object> roleByCode = roleService.queryRoleByCode(Constants.ROLE_FORMAL_LEGALTASK);
				String roleId = (String) roleByCode.get("ROLE_ID");
				
				Map<String, Object> userMap = roleService.queryRoleUserByRoleId(roleId);
				
				Map<String,Object> taskPerson= new HashMap<String,Object>();
				taskPerson.put("NAME", (String)userMap.get("NAME"));
				taskPerson.put("VALUE", (String)userMap.get("UUID"));
				result.setResult_data(taskPerson);
			}else if("businessArea".equals(task)){
				//业务区负责人
				Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
				List<Map<String, Object>> queryPertainAreaByOrgPkValue = this.pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
				if(queryPertainAreaByOrgPkValue.size()>0){
					Map<String,Object> businessArea= new HashMap<String,Object>();
					String leaderId = (String)queryPertainAreaByOrgPkValue.get(0).get("leaderid");
					Map<String, Object> userByID = this.userService.getSysUserByID(leaderId);
					
					businessArea.put("NAME",(String)userByID.get("NAME") );
					businessArea.put("VALUE", leaderId);
					result.setResult_data(businessArea);
				}else{
					result.setResult_name("此单位无需走业务区节点！");
					result.setSuccess(false);
				}
			}
		}else if("bulletin".equals(type)){
			Map<String, Object> bulletinMongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
			Map<String, Object> bulletinOracle = this.bulletinInfoService.queryByBusinessId(businessId);
			Map<String, Object> taskallocation = (Map<String, Object>) bulletinMongo.get("taskallocation");
			if("businessPerson".equals(task)){
				//业务负责人
				Map<String, Object> execution = this.bpmnMapper.getProcInstIdByBusinessId(businessId);
				
				String procInstId = (String) execution.get("PROC_INST_ID_");
				
				List<Map<String, Object>> variableList = this.bpmnMapper.queryVariableByProcInstId(procInstId);
				
				for (Map<String, Object> v : variableList) {
					if("businessPersonRole".equals(v.get("NAME_"))){
						String roleId = (String) v.get("TEXT_");
						Map<String, Object> oldUser = this.roleService.queryUserById(roleId).get(0);
						
						Map<String,Object> businessAreaPerson= new HashMap<String,Object>();
						businessAreaPerson.put("NAME", (String)oldUser.get("NAME"));
						businessAreaPerson.put("VALUE", (String)oldUser.get("UUID"));
						result.setResult_data(businessAreaPerson);
					}else{
						return result.setResult_name("无业务负责人！").setSuccess(false);
					}
				}
			}else if("task".equals(task)){
				//任务分配人
				Map<String, Object> userMap = roleService.queryRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER).get(0);
				Map<String,Object> taskPerson= new HashMap<String,Object>();
				taskPerson.put("NAME", (String)userMap.get("NAME"));
				taskPerson.put("VALUE", (String)userMap.get("UUID"));
				result.setResult_data(taskPerson);
			}else if("reviewLeader".equals(task)){
				//评审负责人
				if(Util.isEmpty(taskallocation)){
					return result.setResult_name("未分配任务，暂无评审负责人！").setSuccess(false);
				}
				Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
				Map<String, Object> riskLeader = (Map<String, Object>) taskallocation.get("riskLeader");
				Map<String, Object> reviewLeaderUser = null;
				if(Util.isNotEmpty(reviewLeader)){
					reviewLeaderUser = reviewLeader;
					result.setResult_data(reviewLeaderUser);
				}else if(Util.isNotEmpty(riskLeader)){
					reviewLeaderUser =  riskLeader;
					result.setResult_data(reviewLeaderUser);
				}else{
					result.setResult_name("未分配任务，暂无评审负责人！").setSuccess(false);
				}
			}else if("legalLeader".equals(task)){
				//法律负责人
				if(Util.isEmpty(taskallocation)){
					return result.setResult_name("未分配任务，暂无法律负责人！").setSuccess(false);
				}
				Map<String, Object> legalLeader = (Map<String, Object>) taskallocation.get("legalLeader");
				if(Util.isEmpty(legalLeader)){
					return result.setResult_name("无法律负责人！").setSuccess(false);
				}
				result.setResult_data(legalLeader);
			}
		}
		
		return result;
	}
	
}
