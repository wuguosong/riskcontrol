/**
 * 
 */
package com.yk.rcm.bulletin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yk.sign.service.SelfApproval;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.listener.DelegateExpressionTaskListener;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.bpmn.entity.TaskInfo;
import com.yk.bpmn.service.IBpmnService;
import com.yk.common.SpringUtil;
import com.yk.exception.wf.BulletinAuditRoleException;
import com.yk.flow.dto.SingleProcessOption;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.JsonUtil;
import com.yk.flow.util.ProcessResult;
import com.yk.power.service.IRoleService;
import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinAuditService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.project.listener.ProjectWaitListener;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class BulletinAuditService implements IBulletinAuditService {
	@Resource
	private IBulletinAuditMapper bulletinAuditMapper;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	@Resource
	private IBpmnAuditService bpmnAuditService;
	@Resource
	private TaskService taskService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IBpmnService bpmnService;
	
	@Override
	public TaskInfo queryTaskInfoByBusinessId(String businessId,String processKey){
		String userId = ThreadLocalUtil.getUserId();
		List<Task> queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
		TaskInfo taskInfo = new TaskInfo();
		if(queryTaskInfo.size()>0){
			try{
				JsonUtil.fromJson(queryTaskInfo.get(0).getDescription(),HashMap.class);
				taskInfo.setDescription(queryTaskInfo.get(0).getDescription());
			}catch(Exception e){
				taskInfo.setDescription(null);
			}
			
			taskInfo.setAssignee(queryTaskInfo.get(0).getAssignee());
			taskInfo.setTaskId(queryTaskInfo.get(0).getId());;
			taskInfo.setTaskKey(queryTaskInfo.get(0).getTaskDefinitionKey());
		}
		return taskInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinAuditService#startSingleFlow(java.lang.String)
	 */
	@Override
	public Result startSingleFlow(String businessId) {
		Result result = new Result();
		
		//查询通报信息
		Map<String, Object> bulletinInfo = this.bulletinInfoService.queryByBusinessId(businessId);
		Map<String, Object> variables = new HashMap<String, Object>();
		//单位负责人id
		String unitPersonId = (String)bulletinInfo.get("UNITPERSONID");
		variables.put("unitPerson", unitPersonId);
		String bulletinTypeCode = (String) bulletinInfo.get("BULLETINTYPECODE");
		if("TBSX_BUSINESS_SUBCOMPANYTZ".equals(bulletinTypeCode)){
			//如果业务类型是北控城服的，设置isCityService流程变量为1
			variables.put("isCityService", "1");
		}else{
			variables.put("isCityService", "0");
			//业务负责人角色
			String businessPersonRole = this.bulletinAuditMapper.queryBusinessRole(businessId);
			if(businessPersonRole == null){
				throw new BulletinAuditRoleException("未找到对应的业务审核角色！");
			}
			variables.put("businessPersonRole", businessPersonRole);
			List<Map<String, Object>> users = this.roleService.queryUserById(businessPersonRole);
			/*if(users == null || users.size()!=1){
				throw new BulletinAuditRoleException("对应的业务审核角色没有人！");
			}	
			String uid = (String) users.get(0).get("UUID");
			String isSkipUnitAudit = "0";
			if(uid.equals(unitPersonId)){
				//如果单位负责人和业务负责人是同一人，那么跳过单位负责人审批
				isSkipUnitAudit = "1";
			}*/
			String isSkipUnitAudit = "0";
			if(CollectionUtils.isEmpty(users)){
				throw new BulletinAuditRoleException("对应的业务审核角色没有人！");
			}
			for(Map<String, Object> user : users){
				String uid = (String) user.get("UUID");
				if(uid.equals(unitPersonId)){
					//如果单位负责人和业务负责人是同一人，那么跳过单位负责人审批
					isSkipUnitAudit = "1";
					break;
				}
			}

			variables.put("isSkipUnitAudit", isSkipUnitAudit);
		}
		ProcessResult<ProcessInstance> pr = new SelfApproval().startFlow(Constants.PROCESS_KEY_BULLETIN, businessId, variables);
		//this.bpmnAuditService.startFlow(Constants.PROCESS_KEY_BULLETIN, businessId, variables);
		if(pr.isSuccess()){
			//启动流程成功，修改bulletin状态
			this.bulletinInfoService.updateAfterStartflow(businessId);
		}else{
			result.setResult_name(pr.getMessage());
		}
		return result;
	}

	@Override
	public Result auditSingle(String businessId, String processOption, Map<String, Object> variables) {
		Result result = new Result();
		List<Task> tasks = this.bpmnAuditService.queryTaskInfo(Constants.PROCESS_KEY_BULLETIN, businessId, ThreadLocalUtil.getUserId());
		if(tasks.size()>0){
			Task task = tasks.get(0);
			this.sendNotice(task);
		}else{
			result.setSuccess(false)
				.setResult_name("该任务已经处理过了!");
			return result;
		}
		ProcessResult<String> pr = this.bpmnAuditService.auditSingle(processOption, variables);
		if(!pr.isSuccess()){
			result.setSuccess(false)
				.setResult_name(pr.getMessage());
		}
		return result;
	}

	@Override
	public void queryWaitList(PageAssistant page) {
		//查询待办的sql
		String sql = this.bpmnAuditService.queryWaitdealSql(Constants.PROCESS_KEY_BULLETIN, ThreadLocalUtil.getUserId()).getData();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			sql = null;
			params.put("auditUserId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " applytime desc ";
		}
		params.put("assignSql", sql);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinAuditMapper.queryWaitList(params);
		page.setList(list);
		
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
			orderBy = " applytime desc ";
		}
		params.put("assignUserId", assignUserId);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinAuditMapper.queryAuditedList(params);
		page.setList(list);
	}

	@Override
	public Result querySingleProcessOptions(String businessId) {
		Result result = new Result();
		ProcessResult<List<SingleProcessOption>> pr = this.bpmnAuditService.querySingleSequenceFlow(Constants.PROCESS_KEY_BULLETIN, businessId, ThreadLocalUtil.getUserId());
		result.setResult_data(pr.getData());
		return result;
	}

	@Override
	public Result startBatchFlow(String[] idsArr) {
		Result result = new Result();
		int total = idsArr.length;
		int success = 0;
		for(int i = 0; i < idsArr.length; i++){
			Result r = this.startSingleFlow(idsArr[i]);
			if(r.isSuccess()){
				success++;
			}
		}
		String msg = "执行[ "+total+" ]条，成功[ "+success+" ]条！";
		result.setResult_name(msg);
		return result;
	}
	
	private void sendNotice(Task task) {
		String definId = task.getProcessDefinitionId();
		RepositoryService rs = (RepositoryService) SpringUtil
				.getBean("repositoryService");
		ProcessDefinition pd = rs.getProcessDefinition(definId);
		// 如果没有发待办的Listener,那么给所有节点都加上发待办的listener
		List<ActivityImpl> activitys = ((ProcessDefinitionImpl) pd)
				.getActivities();
		for (int i = 0; i < activitys.size(); i++) {
			boolean isNotSendToPortal = true;
			ActivityImpl activity = activitys.get(i);
			String eleType = (String) activity.getProperties().get("type");
			if ("userTask".equals(eleType)) {
				TaskDefinition td = (TaskDefinition) activity
						.getProperty(UserTaskParseHandler.PROPERTY_TASK_DEFINITION);
				List<TaskListener> taskListeners = td
						.getTaskListener("create");
				for (TaskListener taskListener : taskListeners) {
					if (taskListener instanceof DelegateExpressionTaskListener) {
						String exepression = ((DelegateExpressionTaskListener) taskListener)
								.getExpressionText();
						if ("${projectWaitListener}".equals(exepression)) {
							isNotSendToPortal = false;
							break;
						}
					} else if (taskListener instanceof ProjectWaitListener) {
						isNotSendToPortal = false;
						break;
					}
				}
				if (isNotSendToPortal) {
					td.addTaskListener("create", (TaskListener) SpringUtil
							.getBean("projectWaitListener"));
					td.addTaskListener("complete", (TaskListener) SpringUtil
							.getBean("projectDealedListener"));
				}
			} else if ("subProcess".equals(eleType)) {
				List<ActivityImpl> subActivitys = activity.getActivities();
				for (int m = 0; m < subActivitys.size(); m++) {
					ActivityImpl subActivity = subActivitys.get(m);
					String subEleType = (String) subActivity.getProperties()
							.get("type");
					if ("userTask".equals(subEleType)) {
						TaskDefinition td = (TaskDefinition) subActivity
								.getProperty(UserTaskParseHandler.PROPERTY_TASK_DEFINITION);
						List<TaskListener> taskListeners = td
								.getTaskListener("assignment");
						for (TaskListener taskListener : taskListeners) {
							if (taskListener instanceof ProjectWaitListener) {
								isNotSendToPortal = false;
								break;
							}
						}
						if (isNotSendToPortal) {
							td.addTaskListener("assignment",
									(TaskListener) SpringUtil
											.getBean("projectWaitListener"));
							td.addTaskListener("complete",
									(TaskListener) SpringUtil
											.getBean("projectDealedListener"));
						}
					}
				}
			}
		}

	}

	@Override
	public Result changeWork(String businessId, String user,String taskId, String option) {
		Result result = new Result();
		
		Document userDoc = Document.parse(user);
		String name = userDoc.getString("NAME");
		String userId = userDoc.getString("VALUE");
		
		Map<String, Object> bulletinOracle = this.bulletinInfoService.queryByBusinessId(businessId);
		
		String lastUserId = ThreadLocalUtil.getUserId();
		
		//获取节点日志描述信息
		List<Map<String, Object>> logs = bulletinAuditLogService.queryAuditLogs(businessId);
		
		//将当前人待办变已办，新增待办日志
		List<Task> list = taskService.createTaskQuery()
									.taskId(taskId)
									.processDefinitionKey(Constants.PROCESS_KEY_BULLETIN)
									.processInstanceBusinessKey(businessId)
									.list();
		Task task = list.get(0);
		String executionId = task.getExecutionId();
		String taskName = task.getName();
		//查询转办初始人
		String oldWorkUser = null;
		for (Map<String, Object> map : logs) {
			if("1".equals((String) map.get("ISWAITING"))){
				if(executionId.equals((String) map.get("EXECUTIONID"))){
					if(Util.isNotEmpty(map.get("OLDUSERID"))){
						oldWorkUser = (String) map.get("OLDUSERID");
					}else{
						oldWorkUser = lastUserId;
					}
				}
			}
		}
		//删除待办日志
		this.bulletinAuditLogService.deleteWaitlog(businessId, null,null,taskId);
		//插入已办日志
		Map<String, Object> log = new HashMap<String, Object>();
		log.put("businessId", businessId);
		log.put("auditUserId", lastUserId);
		log.put("auditTime", Util.now());
		log.put("opinion", option);
		log.put("auditStatus", "B");
		int orderBy = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", ++orderBy);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskName);
		log.put("executionId", executionId);
		log.put("oldUserId", oldWorkUser);
		this.bulletinAuditLogService.save(log);
		//插入当前人待办日志
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("auditUserId", userId);
		data.put("auditTime", Util.now());
		data.put("opinion", "");
		data.put("auditStatus", "9");
		int orderBy2 = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		data.put("orderBy", orderBy2);
		data.put("isWaiting", "1");
		data.put("taskId", taskId);
		data.put("taskdesc", taskName);
		data.put("executionId", executionId);
		data.put("lastUserId", lastUserId);
		data.put("oldUserId", oldWorkUser);
		this.bulletinAuditLogService.save(data);
		//发送待办
		String oldurl = "#/BulletinMattersAudit/1";
		String out = Util.encodeUrl(oldurl);
		String url = "/BulletinMattersAuditView/"+businessId+"/"+out;
		bpmnService.sendWaitingToPotal(taskId, url, Util.format(Util.now()), (String)bulletinOracle.get("BULLETINNAME"), userId, "1", lastUserId);
		return result;
	}

	@Override
	public Result workOver(String businessId, String oldUserId, String taskId, String option) {
		
		Result result = new Result();
		String lastUserId = ThreadLocalUtil.getUserId();
		Map<String, Object> bulletinOracle = this.bulletinInfoService.queryByBusinessId(businessId);
		
		//获取节点日志描述信息
		//将当前人待办变已办，新增待办日志
		List<Task> list = taskService.createTaskQuery()
									.taskId(taskId)
									.processDefinitionKey(Constants.PROCESS_KEY_BULLETIN)
									.processInstanceBusinessKey(businessId)
									.list();
		Task task = list.get(0);
		
		//删除待办日志
		this.bulletinAuditLogService.deleteWaitlog(businessId, null,null,taskId);
		//插入已办日志
		Map<String, Object> log = new HashMap<String, Object>();
		log.put("businessId", businessId);
		log.put("auditUserId", lastUserId);
		log.put("auditTime", Util.now());
		log.put("opinion", option);
		log.put("auditStatus", "B");
		int orderBy = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", ++orderBy);
		log.put("isWaiting", "0");
		log.put("taskdesc", task.getName());
		String executionId = task.getExecutionId();
		log.put("executionId", executionId);
		log.put("oldUserId", oldUserId);
		this.bulletinAuditLogService.save(log);
		
		//插入当前人待办日志
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("auditUserId", oldUserId);
		data.put("auditTime", Util.now());
		data.put("opinion", "");
		data.put("auditStatus", "9");
		int orderBy2 = this.bulletinAuditLogService.queryMaxOrderNum(businessId);
		data.put("orderBy", orderBy2);
		data.put("isWaiting", "1");
		data.put("taskId", taskId);
		data.put("taskdesc", task.getName());
		data.put("executionId", executionId);
		data.put("lastUserId", lastUserId);
		this.bulletinAuditLogService.save(data);
		String oldurl = "#/BulletinMattersAudit/1";
		String out = Util.encodeUrl(oldurl);
		String url = "/BulletinMattersAuditView/"+businessId+"/"+out;
		bpmnService.sendWaitingToPotal(taskId, url, Util.format(Util.now()), (String)bulletinOracle.get("BULLETINNAME"), oldUserId, "1", lastUserId);
		return result;
	}

}
