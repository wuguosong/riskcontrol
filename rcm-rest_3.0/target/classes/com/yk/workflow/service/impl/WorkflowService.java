package com.yk.workflow.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.listener.DelegateExpressionTaskListener;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yk.bpmn.entity.TaskInfo;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.exception.BusinessException;
import com.yk.exception.wf.WorkflowException;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.power.service.IRoleService;
import com.yk.rcm.project.listener.ProjectWaitListener;
import com.yk.workflow.dao.IWorkFlowMapper;
import com.yk.workflow.service.IWorkflowService;

import bpm.BpmFactory;
import common.Constants;
import common.PageAssistant;
import common.Result;
import rcm.ProjectInfo;
import util.DbUtil;
import util.ThreadLocalUtil;
import util.Util;
/**
 * @author yaphet
 */
@Service
@Transactional
public class WorkflowService implements IWorkflowService {
	@Resource
	private ProcessEngine e;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private TaskService taskService;
	@Resource
	private IWorkFlowMapper workFlowMapper;
	@Resource
	private IBpmnAuditService bpmnAuditService;
	@Resource
	private IRoleService roleService;
	
	
	/**
	 *流程启动
	 * {
	 * 	startVar:{processKey:'流程ID',businessId:"业务ID",subject:"主题",inputUser:"发起人"},
	 * 	runtimeVar:{businessId:业务ID,subject:工单主题,inputUser:下一环节处理人,assigneeList:[xx,xx],conditionName:conditionValue)},
	 *  currentTaskVar:{opinion:审批意见,[key1:value1]},
	 * 	newTaskVar:{
	 * 		submitBy:上一环节处理人中文名称,
	 * 		[key1:value1]
	 *  }
	 * }
	 * runtimeVar:会影响流程流转的变量
	 * newTaskVar:会绑定到下一任务上的变量
	 * conditionName表示条件名称,conditionValue表示条件值
	 * @param json
	 */
	public void startProcess(String json){
		Map<String, Object> map = Util.parseJson2Map(json);
		Map<String, Object> startVar = (Map<String, Object>)map.get(Constants.START_VAR);
		if(Util.isNotEmpty(startVar) && startVar.containsKey(Constants.PROCESS_KEY)){
			String processKey = (String)startVar.get(Constants.PROCESS_KEY);
			String businessKey = (String)startVar.get(Constants.BUSINESS_ID);
			//校验该业务ID所属流程是否已经启动
			ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
			Boolean isStarted = prj.isAllreadyStartWithBusiness(businessKey);
			if(isStarted) throw new BusinessException("流程已经启动");
			startVar.remove(Constants.PROCESS_KEY);
			//设置流程启动人
			e.getIdentityService().setAuthenticatedUserId((String)startVar.get("inputUser"));
			//启动流程
			ProcessInstance pi = e.getRuntimeService().startProcessInstanceByKey(processKey, businessKey, startVar);
			Map<String, Object> currentTaskVar = (Map<String, Object>)map.get(Constants.CURRENTTASK_VAR);
			if(currentTaskVar != null){
				setVariablesLocal2Task(pi.getId(), currentTaskVar);
			}
			//如果起草环节也设计为一个usertask，那么需要将runtimeVar传入新创建起草环节task
			Task task = e.getTaskService().createTaskQuery().processInstanceId(pi.getId()).singleResult();
			Map<String, Object> runtimeVar = (Map<String, Object>)map.get(Constants.RUNTIME_VAR);
			e.getTaskService().complete(task.getId(), runtimeVar);
			Map<String, Object> newTaskVar =(Map<String, Object>) map.get(Constants.NEWTASK_VAR);
			if(newTaskVar != null){
				setVariablesLocal2TaskWithPreTask(task.getId(), newTaskVar);
			}
			//如果需要发通知或者待阅
//			sendNotice(map, pi.getId());
		}else{
			throw new BusinessException("启动参数缺失");
		}
	}

	/**
	 * {
	 * 	taskId:待办任务ID,
	 * 	runtimeVar:{inputUser:下一环节处理人,assigneeList:[xx,xx],conditionName:conditionValue)},
	 * 	currentTaskVar:{opinion:审批意见,[key1:value1]},
	 * 	newTaskVar:{
	 * 		submitBy:上一环节处理人中文名称,
	 * 		[key1:value1]
	 *  }
	 * }
	 * runtimeVar:会影响流程流转的变量
	 * currentTaskVar:会绑定到当前任务上的变量
	 * newTaskVar:会绑定到下一任务上变量
	 * conditionName表示条件名称,conditionValue表示条件值
	 * @param json
	 */
	@SuppressWarnings("unchecked")
	public Result approve(String json){
		Result result = new Result();
		TaskService ts= e.getTaskService();
		Map<String, Object> map = Util.parseJson2Map(json);
		String taskId = (String)map.get("taskId");
		Map<String, Object> currentTaskVar = (Map<String, Object>)map.get(Constants.CURRENTTASK_VAR);
		String cesuanFileOpinion = null;
		if(currentTaskVar != null){
			cesuanFileOpinion = (String) currentTaskVar.get("cesuanFileOpinion");
		}
		Task task = e.getTaskService().createTaskQuery().taskId(taskId).singleResult();
		if(task == null){
			result.setSuccess(false)
				.setResult_name("该任务已经处理过了!");
			return result;
		}
		//如果需要发通知
		sendNotice(task);
		if(currentTaskVar != null){
			ts.setVariablesLocal(taskId, currentTaskVar);
		}
		Map<String, Object> newTaskVar = (Map<String, Object>)map.get(Constants.NEWTASK_VAR);
		String processInstanceId = null;
		if(newTaskVar != null){
			//获取流程实例ID
			processInstanceId = task.getProcessInstanceId();
		}
		e.getTaskService().complete(taskId, (Map<String, Object>)map.get(Constants.RUNTIME_VAR));
		
		if(processInstanceId != null){
			//给新生成的任务设置变量
			setVariablesLocal2TaskWithPreTask(taskId, newTaskVar);
		}
		//如果需要发通知
//		sendNotice(map, processInstanceId);
		//如果是一级业务部门审核，需要保存测算文件意见和投资协议意见
		if(cesuanFileOpinion != null){
			String tzProtocolOpinion = (String) currentTaskVar.get("tzProtocolOpinion");
			currentTaskVar.remove("cesuanFileOpinion");
			currentTaskVar.remove("tzProtocolOpinion");
			String defId = task.getProcessDefinitionId();
			RepositoryService repositoryService = e.getRepositoryService();
			ProcessDefinition def = repositoryService.getProcessDefinition(defId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("cesuanFileOpinion", cesuanFileOpinion);
			data.put("tzProtocolOpinion", tzProtocolOpinion);
			String businessId = (String) map.get("businessId");
			if(Constants.PRE_ASSESSMENT.equals(def.getKey())){
				//预评审
				IBaseMongo preAssementService = (IBaseMongo) SpringUtil.getBean("baseMongo");
				preAssementService.updateSetByObjectId(businessId, data, Constants.PREASSESSMENT);
			}else if(Constants.FORMAL_ASSESSMENT.equals(def.getKey())){
				//正式评审
				IBaseMongo preAssementService = (IBaseMongo) SpringUtil.getBean("baseMongo");
				preAssementService.updateSetByObjectId(businessId, data, Constants.RCM_PROJECTFORMALREVIEW_INFO);
			}
		}
		//如果是分配评审任务，那么需要保存项目的紧急程度
		String emergencyLevel = (String)map.get("emergencyLevel");
		if(emergencyLevel != null){
			String defId = task.getProcessDefinitionId();
			ProcessDefinition def = repositoryService.getProcessDefinition(defId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("emergencyLevel", emergencyLevel);
			String businessId = (String) map.get("businessId");
			if(Constants.PRE_ASSESSMENT.equals(def.getKey())){
				//预评审
				IBaseMongo preAssementService = (IBaseMongo) SpringUtil.getBean("baseMongo");
				preAssementService.updateSetByObjectId(businessId, data, Constants.PREASSESSMENT);
			}else if(Constants.FORMAL_ASSESSMENT.equals(def.getKey())){
				//正式评审
				IBaseMongo preAssementService = (IBaseMongo) SpringUtil.getBean("baseMongo");
				preAssementService.updateSetByObjectId(businessId, data, Constants.RCM_PROJECTFORMALREVIEW_INFO);
			}
			//将项目紧急程序存在oracle数据库中
			ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("emergencyLevel", emergencyLevel);
			prj.updateProjectInfo(businessId, params);
		}
		result.setSuccess(true)
			.setResult_code("S")
			.setResult_name("执行成功！");
		return result;
	}
	
	/**
	 * 发待阅或者通知
	 * @param map
	 */
	/*private void sendNotice(Map<String, Object> map, String processInstanceId) {
		Object notice = map.get("noticeInfo");
		if(notice != null){
			NoticeInfo ns = new NoticeInfo();
			Map<String, Object> noticeMap = (Map<String, Object>)notice;
			noticeMap.put("processInstanceId", processInstanceId);
			noticeMap.put("taskId", map.get("taskId"));
			List<Map> readers = (List<Map>)noticeMap.get("reader");
			if(Util.isEmpty(readers)) return;
			readers = Util.toLowerList(readers);
			for(Map rd : readers){
				noticeMap.put("reader",rd.get("value"));
				noticeMap.put("readerName",rd.get("name"));
				ns.insert(noticeMap);
			}
			//同步给门户系统
			PortalClient pc = new PortalClient((String)map.get("taskId"), "");
			pc.start();
		}
	}*/
	
	/**
	 * 绑定任务变量
	 * @param id
	 * @param taskVar
	 */
	private void setVariablesLocal2Task(String processInstanceId, Map<String, Object> taskVar) {
		//查找当前运行时任务
		List<Task> tasks = e.getTaskService().createTaskQuery().processInstanceId(processInstanceId).list();
		//设置任务变量
		if(Util.isNotEmpty(tasks) && Util.isNotEmpty(taskVar)){
			for(Task t : tasks){
				e.getTaskService().setVariablesLocal(t.getId(), taskVar);
			}
		}
	}
	
	/**
	 * 1、绑定任务变量，用于准确获取由preTaskId新产生的任务
	 * 2、调用门户接口传递已办和待办
	 * @param id
	 * @param taskVar
	 */
	private void setVariablesLocal2TaskWithPreTask(String preTaskId, Map<String, Object> taskVar){
		HistoricTaskInstance hisTask = e.getHistoryService().createHistoricTaskInstanceQuery().taskId(preTaskId).singleResult();
		Date completeDate = hisTask.getEndTime();
		List<Task> tasks = e.getTaskService().createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).list();
		
		/*同步任务到门户*/
		List<String> currentTaskIds = new ArrayList<String>();
		currentTaskIds.add("'blank'");//没有新的待办时保证sql不出错
		/*同步任务到门户*/
		
		//设置任务变量
		if(Util.isNotEmpty(tasks) && Util.isNotEmpty(taskVar)){
			for(Task t : tasks){
				if(t.getCreateTime().compareTo(completeDate)!=-1){
					e.getTaskService().setVariablesLocal(t.getId(), taskVar);
					currentTaskIds.add("'"+t.getId()+"'");
				}
			}
		}
		
//		PortalClient pc = new PortalClient(preTaskId, currentTaskIds);
//		pc.start();
	}
	/**
	 * 获取流程定义ID，如果有流程实例，根据流程实例查询， 如果没有流程实例，根据流程定义Key查询
	 * @param json {processInstanceId：xx,processDefinitionKey:xx}
	 * @return
	 */
	public Document getProcessDefinitionId(String json){
		Document params = Document.parse(json);
		String processInstanceId  = params.getString("processInstanceId");
		String processDefinitionKey  = params.getString("processKey");
		ProcessEngine e = BpmFactory.getInstance();
		String processDefinitionId = "";
		if(StringUtils.isNotBlank(processInstanceId)){
			HistoricProcessInstance hp = e.getHistoryService().createHistoricProcessInstanceQuery().
						processInstanceId(processInstanceId).singleResult();
			processDefinitionId = hp.getProcessDefinitionId();
		}else if(StringUtils.isNotBlank(processDefinitionKey)){
			 List<ProcessDefinition> list = e.getRepositoryService().createProcessDefinitionQuery().
					 	processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().desc().listPage(0, 1);
			 if(Util.isEmpty(list)) throw new BusinessException("流程未部署:"+processDefinitionKey);
			 processDefinitionId = list.get(0).getId();
		}
		Document doc = new Document();
		doc.put("processDefinitionId", processDefinitionId);
		return doc;
	}

	/**
	 * 获取流程图(未框选),流程图是多版本的,所以要根据流程定义ID查询：
	 * @param json {processDefinitionId:"processDefinitionId"}
	 * @return
	 */
	public InputStream getBpmImg(String json){
		ProcessEngine e = BpmFactory.getInstance();
		RepositoryService repositoryService = e.getRepositoryService();
		Map<String, Object> map = Util.parseJson2Map(json);
		String processDefinitionId = (String)map.get("processDefinitionId");
		if(processDefinitionId == null || "".equals(processDefinitionId.trim())){
			throw new WorkflowException("流程定义id不能为空");
		}
		//查看流程图
		InputStream is = repositoryService.getProcessDiagram(processDefinitionId);
		return is;
	}
	
	/**
	 * 获取流程活动节点</br>
	 * 首先要找到流程定义的所有活动，然后判断该流程是否结束
	 * 如果未结束要找到当前正在执行的活动，如果已结束直在所有活动中找到结束活动
	 * 找到结束节点
	 * @param json {processInstanceId:xx}
	 * @return
	 */
	public JsonArray getActiveActivityIds(String json){
		Map<String, Object> map = Util.parseJson2Map(json);
		String processInstanceId = (String)map.get("processInstanceId");
		String businessId = (String) map.get("businessId");
		if(businessId != null){
			String processKey = (String) map.get("processKey");
			RuntimeService runtimeService = e.getRuntimeService();
			HistoryService historyService = e.getHistoryService();
			List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessId, processKey)
				.list();
			if(piList == null ||piList.size() == 0){
				List<HistoricProcessInstance> hlist = historyService.createHistoricProcessInstanceQuery()
					.processDefinitionKey(Constants.PROCESS_KEY_BULLETIN)
					.processInstanceBusinessKey(businessId)
					.finished()
					.list();
				processInstanceId = hlist.get(0).getId();
			}else{
				processInstanceId = piList.get(0).getId(); 
			}
		}
		//如果还没有创建流程历史， 那么直接return
		if(StringUtils.isBlank(processInstanceId)) return null;
		ProcessEngine e = BpmFactory.getInstance();
		RuntimeService runtimeService = e.getRuntimeService();
		RepositoryService repositoryService = e.getRepositoryService();
		HistoryService hisService = e.getHistoryService();
		//只要启动流程就会有流程实例历史
		HistoricProcessInstance hp = hisService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String processDefinitionId = hp.getProcessDefinitionId();
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).
				getDeployedProcessDefinition(processDefinitionId);
		//根据流程定义查找到该流程的所有活动
		List<ActivityImpl> defActs = pde.getActivities();
		JsonArray array = new JsonArray();
		if(hp.getEndTime() == null){
			//流程没有结束,找到当前活动的ID，此处应该传入processInstanceId, 官方API的描述是错误的
			List<String> curActIds = runtimeService.getActiveActivityIds(processInstanceId);
			//通过当前活动ID列表获取活动实例
			for(ActivityImpl act : defActs){
				if("subProcess".equals(act.getProperty("type"))){
					List<ActivityImpl> subActs = act.getActivities();
					for(ActivityImpl subAct : subActs){
						if(curActIds.contains(subAct.getId())){
							addCurrentAct(array, subAct);
						}
					}
				}else{
					if(curActIds.contains(act.getId())){
						addCurrentAct(array, act);
					}
				}
			}
		}else{
			//流程已结束，直接找到流程定义中的结束活动即可
			for(ActivityImpl act : defActs){
				Map<String, Object> props = act.getProperties();
				String type = (String)props.get("type");
				if("endEvent".equals(type)){
					addCurrentAct(array, act);
					break;
				}
			}
		}
		return array;
	}
	
	private void addCurrentAct(JsonArray array, ActivityImpl act) {
		JsonObject js = new JsonObject();
		js.addProperty("left", act.getX());
		js.addProperty("top", act.getY());
		js.addProperty("width", act.getWidth());
		js.addProperty("height", act.getHeight());
		array.add(js);
	}
	
	/**
	 * @param id
	 * @param gs
	 * @param strings
	 */
	private void addVarLocalProperty(String taskId, JsonObject gs, String[] variableNames) {
		Map<String, Object> varMap = e.getTaskService().getVariablesLocal(taskId, Arrays.asList(variableNames));
		addToGson(varMap, gs);
	}

	/**
	 * @param string
	 * @param gs
	 * @param strings
	 */
	private void addVarProperty(String processInstance, JsonObject gs, String[] variableNames) {
		Map<String, Object> varMap = e.getRuntimeService().getVariables(processInstance, Arrays.asList(variableNames));
		addToGson(varMap, gs);
	}
	/**
	 * @param varMap
	 * @param gs
	 */
	private void addToGson(Map<String, Object> varMap, JsonObject gs) {
		if(Util.isNotEmpty(varMap)){
			for(String key : varMap.keySet()){
				gs.addProperty(key, varMap.get(key).toString());
			}
		}
	}

	/**
	 * 通过sql查询流程审批历史
	 * @param json {processInstanceId:xx}
	 */
	public JsonArray getProcessInstanceApproveHistory(String json){
		SqlSession session = DbUtil.openSession();
		Map<String, Object> map = Util.parseJson2Map(json);
//		List<Map<String, Object>> list = session.selectList("workflow.selectProcessInstanceApproveHistory", map);
		List<Map<String, Object>> list = this.workFlowMapper.selectProcessInstanceApproveHistory(map);
		
		JsonArray array = new JsonArray();
		if(Util.isNotEmpty(list)){
			for(Map<String, Object> hi : list){
				JsonObject js = new JsonObject();
				array.add(js);
				js.addProperty("approver", (String)hi.get("USERNAME"));
				js.addProperty("taskName", (String)hi.get("TASKNAME"));
				js.addProperty("beginDate", (String)hi.get("STARTTIME"));
				js.addProperty("endDate", (String)hi.get("ENDTIME"));
				js.addProperty("opinion", (String)hi.get("OPINION"));
				js.addProperty("emergencyLevel", (String)hi.get("EMERGENCYLEVEL"));
			}
		}
		return array;
	}
	
	/**
	 * 结束流程,找到当前活动并清除它的所有连线， 然后创建指向结束活动的连线，完成任务-最好还是通过流程图画线来顺序执行
	 * @param json {taskId:'', processInstanceId:'',processDefinitionId:'', deleteVar:{}}
	 */	
	@Deprecated
	public void completeProcess(String json){
		Map params = Util.parseJson2Map(json);
		String taskId = (String)params.get("taskId");
		String processInstanceId = (String)params.get("processInstanceId");
		String processDefinitionId = (String)params.get("processDefinitionId");
		//找到当前活动和结束活动
		List<String> curActIds = runtimeService.getActiveActivityIds(processInstanceId);
		if(Util.isEmpty(curActIds)) return;
		if(curActIds.size()>1) throw new BusinessException("当前节点不能结束流程");
		String currentActId = curActIds.get(0);
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).
				getDeployedProcessDefinition(processDefinitionId);
		//根据流程定义查找到该流程的所有活动
		List<ActivityImpl> defActs = pde.getActivities();
		ActivityImpl currentAct = null, endAct = null;
		for(ActivityImpl act : defActs){
			String type = (String)act.getProperties().get("type");
			if(currentActId.equals(act.getId())){
				currentAct = act;
			}else if("endEvent".equals(type)){
				endAct = act;
			}
		}
		//清空当前活动的所有连线
		if(currentAct == null || endAct == null) throw new BusinessException("没有找到当前节点或结束节点");
		currentAct.getOutgoingTransitions().clear();
		// 创建新流向  
        TransitionImpl newTransition = currentAct.createOutgoingTransition();  
        newTransition.setDestination(endAct);
        taskService.complete(taskId, (Map<String, Object>)params.get("deleteVar"));
        //endAct.getIncomingTransitions().remove(newTransition);
        // 还原以前流向
        //restoreTransition(currentAct, oriPvmTransitionList);
	}
	
	/** 
     * 还原指定活动节点流向 
     *  
     * @param activityImpl 
     *            活动节点 
     * @param oriPvmTransitionList 
     *            原有节点流向集合 
     */  
    private void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向  
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();  
        pvmTransitionList.clear();  
        // 还原以前流向  
        for (PvmTransition pvmTransition : oriPvmTransitionList) {  
            pvmTransitionList.add(pvmTransition);  
        }
    }
    
    
    /** 
     * 清空指定活动节点流向 
     *  
     * @param activityImpl 
     *            活动节点 
     * @return 节点流向集合 
     */  
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl){
        // 存储当前节点所有流向临时变量  
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
        // 获取当前节点所有流向，存储到临时变量，然后清空  
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }  
        pvmTransitionList.clear();
        return oriPvmTransitionList;
    }
    
    
    
    /**
     * 根据流程实例Id获取流程实例
     * @param json
     * @return
     */
    public ProcessInstance getProcessInstance(String json){
    	String processInstanceId = (String)Util.parseJson2Map(json).get("processInstanceId");
    	return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }
    
    /*
     * 查询流程节点定义
     */
    public List<ActivityImpl> getProcessActivityImpl(String json){
    	String processDefinitionId = (String)Util.parseJson2Map(json).get("processDefinitionId");
    	ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).
				getDeployedProcessDefinition(processDefinitionId);
		//根据流程定义查找到该流程的所有活动
		List<ActivityImpl> defActs = pde.getActivities();
		return defActs;
    }
    
    
    
    private void sendNotice(Task task){
    	String definId = task.getProcessDefinitionId();
		RepositoryService rs = (RepositoryService) SpringUtil.getBean("repositoryService");
		ProcessDefinition pd = rs.getProcessDefinition(definId);
			//如果没有发待办的Listener,那么给所有节点都加上发待办的listener
			List<ActivityImpl> activitys = ((ProcessDefinitionImpl)pd).getActivities();
			for(int i = 0; i < activitys.size(); i++){
				boolean isNotSendToPortal = true;
				ActivityImpl activity = activitys.get(i);
				String eleType = (String) activity.getProperties().get("type");
				if("userTask".equals(eleType)){
					TaskDefinition td = (TaskDefinition)activity.getProperty(UserTaskParseHandler.PROPERTY_TASK_DEFINITION);
					List<TaskListener> taskListeners = td.getTaskListener("assignment");
					for(TaskListener taskListener : taskListeners){
						if(taskListener instanceof DelegateExpressionTaskListener){
							String exepression = ((DelegateExpressionTaskListener)taskListener).getExpressionText();
							if("${projectWaitListener}".equals(exepression)){
								isNotSendToPortal = false;
								break;
							}
						}else if(taskListener instanceof ProjectWaitListener){
							isNotSendToPortal = false;
							break;
						}
					}
					if(isNotSendToPortal){
						td.addTaskListener("assignment", (TaskListener)SpringUtil.getBean("projectWaitListener"));
						td.addTaskListener("complete", (TaskListener)SpringUtil.getBean("projectDealedListener"));
					}
				}else if("subProcess".equals(eleType)){
					List<ActivityImpl> subActivitys = activity.getActivities();
					for(int m = 0; m < subActivitys.size(); m++){
						ActivityImpl subActivity = subActivitys.get(m);
						String subEleType = (String)subActivity.getProperties().get("type");
						if("userTask".equals(subEleType)){
							TaskDefinition td = (TaskDefinition)subActivity.getProperty(UserTaskParseHandler.PROPERTY_TASK_DEFINITION);
							List<TaskListener> taskListeners = td.getTaskListener("assignment");
							for(TaskListener taskListener : taskListeners){
								if(taskListener instanceof ProjectWaitListener){
									isNotSendToPortal = false;
									break;
								}
							}
							if(isNotSendToPortal){
								td.addTaskListener("assignment", (TaskListener)SpringUtil.getBean("projectWaitListener"));
								td.addTaskListener("complete", (TaskListener)SpringUtil.getBean("projectDealedListener"));
							}
						}
					}
				}
			}
			
    }

	@Override
	public TaskInfo getTaskInfo(String processKey, String businessId) {
		List<Task> tasks = this.bpmnAuditService.queryTaskInfo(processKey, businessId, ThreadLocalUtil.getUserId());
		if(tasks!=null && tasks.size() > 0){
			TaskInfo taskInfo = new TaskInfo();
			Task t = tasks.get(0);
			taskInfo.setAssignee(t.getAssignee());
			taskInfo.setDescription(t.getDescription());
			taskInfo.setFormKey(t.getFormKey());
			taskInfo.setTaskId(t.getId());
			taskInfo.setTaskKey(t.getTaskDefinitionKey());
			return taskInfo;
		}
		return null;
	}

	@Override
	public int getMyTaskCount(String userId) {
		return workFlowMapper.getMyTaskCount(userId);
	}

	@Override
	public int getOverTaskCount(String userId) {
		return workFlowMapper.getOverOrCompletedTaskCount(userId, null);
	}
	
	@Override
	public int getCompletedTaskCount(String userId) {
		return workFlowMapper.getOverOrCompletedTaskCount(userId, "1");
	}

	@Override
	public PageAssistant queryMyTaskByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = workFlowMapper.queryMyTaskByPage(params);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant queryOverTaskByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = workFlowMapper.queryOverOrCompletedTaskByPage(params);
		page.setList(list);
		return page;
	}
	
	@Override
	public PageAssistant queryCompletedTaskByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		params.put("isCompleted", "1");
		List<Map<String, Object>> list = workFlowMapper.queryOverOrCompletedTaskByPage(params);
		page.setList(list);
		return page;
	}
	
	@Override
	public PageAssistant queryMyProjectInfoByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		//判断当前是否为投资经理 
		if(roleService.ifRoleContainUser(Constants.ROLE_CODE_INVESTMENT_MANAGER)){
			params.put("createby", ThreadLocalUtil.getUserId());
		}
		else if(roleService.ifRoleContainUser(Constants.ROLE_CODE_REVIEWFZR)){
			params.put("reviewpersonId", ThreadLocalUtil.getUserId());
		}else{
			return page;
		}
		
		List<Map<String, Object>> list = workFlowMapper.queryMyProjectInfoByPage(params);
		page.setList(list);
		return page;
	}
}
