package com.yk.sign.service.impl;

import com.yk.bpmn.service.IBpmnService;
import com.yk.common.BaseMongo;
import com.yk.exception.BusinessException;
import com.yk.process.service.IProcessService;
import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.bulletin.dao.IBulletinInfoMapper;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentAuditMapper;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import com.yk.sign.dao.ISignMapper;
import com.yk.sign.service.ISignService;
import common.Constants;
import common.PageAssistant;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/6.
 */
@Service
public class SignService implements ISignService {
    @Resource
    private IBulletinAuditMapper bulletinAuditMapper;
    @Resource
    private IBulletinInfoMapper bulletinInfoMapper;
    @Resource
    private IFormalAssessmentAuditMapper formalAssessmentAuditMapper;
    @Resource
    private IFormalAssessmentInfoCreateMapper formalAssessmentInfoCreateMapper;
    @Resource
    private IPreAuditLogMapper preAuditLogMapper;
    @Resource
    private IBpmnService bpmnService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private BaseMongo baseMongo;
    @Resource
    private ISignMapper signMapper;
    @Resource
    private IProcessService processService;


    @Override
    public void doSign(String type, String business_module, String business_id, String user_json, String task_id, String option) {
        // 校验主要参数
        if (StringUtils.isBlank(type)) {
            throw new BusinessException("加签失败,TYPE为空!");
        }
        if (StringUtils.isBlank(business_module)) {
            throw new BusinessException("加签失败,BUSINESS_MODULE为空!");
        }
        if (StringUtils.isBlank(business_id)) {
            throw new BusinessException("加签失败,BUSINESS_ID为空!");
        }
        if (StringUtils.isBlank(user_json)) {
            throw new BusinessException("加签失败,USER_JSON为空!");
        }
        if (StringUtils.isBlank(task_id)) {
            throw new BusinessException("加签失败,TASK_ID为空!");
        }
        this.startSign(type, business_module, business_id, user_json, task_id, option);
    }


    @Override
    public void endSign(String business_module, String business_id, String task_id, String option) {
        if (StringUtils.isBlank(business_module)) {
            throw new BusinessException("结束失败,BUSINESS_MODULE为空!");
        }
        if (StringUtils.isBlank(business_id)) {
            throw new BusinessException("结束失败,BUSINESS_ID为空!");
        }
        if (StringUtils.isBlank(task_id)) {
            throw new BusinessException("结束失败,TASK_ID为空!");
        }
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.endBulleSign(business_id, task_id, option);
        }
        if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(business_module)) {
            this.endFormalSign(business_id, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.endPreSign(business_id, task_id, option);
        }
    }


    @Override
    public List<Map<String, Object>> listLogs(String business_module, String business_id) {
        if (StringUtils.isBlank(business_module)) {
            throw new BusinessException("BUSINESS_MODULE不能为空!");
        }
        if (StringUtils.isBlank(business_id)) {
            throw new BusinessException("BUSINESS_ID不能为空!");
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            return this.preLogs(business_id);
        } else if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(business_module)) {
            return this.formalLogs(business_id);
        } else if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            return this.bulletLogs(business_id);
        } else {
            throw new BusinessException("BUSINESS_MODULE不在系统业务中!");
        }
    }


    @Override
    public PageAssistant queryAgencyList(String key, PageAssistant page) {
        if (StringUtils.isBlank(key)) {
            throw new BusinessException("KEY不能为空!");
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("key", key);
        params.put("userId", ThreadLocalUtil.getUserId());
        params.put("page", page);
        if (page.getParamMap() != null) {
            params.putAll(page.getParamMap());
        }
        List<HashMap> list = signMapper.queryAgencyList(params);
        for (Map<String, Object> map : list) {
            String id = String.valueOf(map.get("business_id"));
            Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
            map.put("mongoDate", mongoDate);
        }
        page.setList(list);
        return page;
    }


    /**
     * 结束其它项目审批前加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endBulleSign(String business_id, String task_id, String option) {
        String lastUserId = ThreadLocalUtil.getUserId();
        Map<String, Object> bulletinOracle = bulletinInfoMapper.queryByBusinessId(business_id);
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = bulletinAuditMapper.queryAuditLogs(business_id);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        // 根据当前审批日志,判断该结束的是前加签还是后加签
        String changeType = String.valueOf(delMap.get("CHANGETYPE"));
        try{
            if ("after".equalsIgnoreCase(changeType)) {
                // 所有正向的表达式
                Task curTask = taskService.createTaskQuery().taskId(task_id).singleResult();
                String curTaskKey = curTask.getTaskDefinitionKey();
                String nexTaskKey = this.getNextTaskInfo(Constants.PROCESS_KEY_BULLETIN, business_id).getKey();
                Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
                // 结束本次任务,开始下个节点
                taskService.complete(task_id, variable);
                return;
            }
        }catch(Exception e){
            // 此时发生时,是因为不确定下一个节点
            e.printStackTrace();
        }
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_BULLETIN).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        // 删除待办日志
        Map<String, Object> deleteLog = new HashMap<String, Object>();
        deleteLog.put("businessId", business_id);
        deleteLog.put("isWaiting", "1");
        this.bulletinAuditMapper.deleteWaitlog(deleteLog);
        // 插入已办日志
        int orderByReady = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = new HashMap<String, Object>();
        alreadyLog.put("businessId", business_id);
        alreadyLog.put("auditUserId", lastUserId);
        alreadyLog.put("auditTime", Util.now());
        alreadyLog.put("opinion", option);
        alreadyLog.put("auditStatus", "B");
        alreadyLog.put("orderBy", ++orderByReady);
        alreadyLog.put("isWaiting", "0");
        alreadyLog.put("taskdesc", task.getName());
        alreadyLog.put("executionId", executionId);
        alreadyLog.put("lastUserId", delMap.get("LASTUSERID"));
        alreadyLog.put("oldUserId", delMap.get("OLDUSERID"));
        alreadyLog.put("changeType", delMap.get("CHANGETYPE"));
        this.bulletinAuditMapper.save(alreadyLog);
        // 插入当前人待办日志
        Map<String, Object> agencyLog = new HashMap<String, Object>();
        int orderByAgency = bulletinAuditMapper.queryMaxOrderNum(business_id);
        agencyLog.put("businessId", business_id);
        agencyLog.put("auditUserId", delMap.get("OLDUSERID"));
        agencyLog.put("auditTime", Util.now());
        agencyLog.put("opinion", "");
        agencyLog.put("auditStatus", "9");
        agencyLog.put("orderBy", ++orderByAgency);
        agencyLog.put("isWaiting", "1");
        agencyLog.put("taskId", task_id);
        agencyLog.put("taskdesc", task.getName());
        agencyLog.put("executionId", executionId);
        agencyLog.put("lastUserId", delMap.get("AUDITUSERID"));
        agencyLog.put("oldUserId", delMap.get("OLDUSERID"));
        agencyLog.put("changeType", "");
        bulletinAuditMapper.save(agencyLog);
        String oldUrl = "#/BulletinMattersAudit/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/BulletinMattersAuditView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), String.valueOf(delMap.get("OLDUSERID")), "1", lastUserId);
    }

    private HashMap<String, Object> getCurrentTask(String business_module, String business_id) {
        List<HashMap> logs = signMapper.getCurrentTaskInfo(business_module, business_id);
        HashMap<String, Object> log = null;
        String curUser = ThreadLocalUtil.getUserId();
        for (HashMap map : logs) {
            if (String.valueOf(map.get("AUDITUSERID")).equalsIgnoreCase(curUser)) {
                log = map;
                break;
            }
        }
        return log;
    }

    /**
     * 结束正式项目审批前加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endFormalSign(String business_id, String task_id, String option) {
        String lastUserId = ThreadLocalUtil.getUserId();
        Map<String, Object> bulletinOracle = formalAssessmentInfoCreateMapper.getProjectByID(business_id);
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = formalAssessmentAuditMapper.queryAuditedLogsById(business_id);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        // 根据当前审批日志,判断该结束的是前加签还是后加签
        String changeType = String.valueOf(delMap.get("CHANGETYPE"));
        try {
            if ("after".equalsIgnoreCase(changeType)) {
                // 所有正向的表达式
                Task curTask = taskService.createTaskQuery().taskId(task_id).singleResult();
                String curTaskKey = curTask.getTaskDefinitionKey();
                String nexTaskKey = this.getNextTaskInfo(Constants.PROCESS_KEY_FormalAssessment, business_id).getKey();
                Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
                // 结束本次任务,开始下个节点
                taskService.complete(task_id, variable);
                return;
            }
        } catch (Exception e) {
            // 此时发生时,是因为不确定下一个节点
            e.printStackTrace();
        }
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).list();//taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_FormalAssessment).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        // 删除待办日志
        Map<String, Object> deleteLog = new HashMap<String, Object>();
        deleteLog.put("businessId", business_id);
        deleteLog.put("isWaiting", "1");
        this.formalAssessmentAuditMapper.deleteWaitlogs(deleteLog);
        // 插入已办日志
        int orderByReady = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = new HashMap<String, Object>();
        alreadyLog.put("businessId", business_id);
        alreadyLog.put("auditUserId", lastUserId);
        alreadyLog.put("auditTime", Util.now());
        alreadyLog.put("opinion", option);
        alreadyLog.put("auditStatus", "B");
        alreadyLog.put("orderBy", ++orderByReady);
        alreadyLog.put("isWaiting", "0");
        alreadyLog.put("taskdesc", task.getName());
        alreadyLog.put("executionId", executionId);
        alreadyLog.put("lastUserId", delMap.get("LASTUSERID"));
        alreadyLog.put("oldUserId", delMap.get("OLDUSERID"));
        alreadyLog.put("changeType", delMap.get("CHANGETYPE"));
        this.formalAssessmentAuditMapper.save(alreadyLog);
        // 插入当前人待办日志
        Map<String, Object> agencyLog = new HashMap<String, Object>();
        int orderByAgency = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        agencyLog.put("businessId", business_id);
        agencyLog.put("auditUserId", delMap.get("OLDUSERID"));
        agencyLog.put("auditTime", Util.now());
        agencyLog.put("opinion", "");
        agencyLog.put("auditStatus", "9");
        agencyLog.put("orderBy", ++orderByAgency);
        agencyLog.put("isWaiting", "1");
        agencyLog.put("taskId", task_id);
        agencyLog.put("taskdesc", task.getName());
        agencyLog.put("executionId", executionId);
        agencyLog.put("lastUserId", delMap.get("AUDITUSERID"));
        agencyLog.put("oldUserId", delMap.get("OLDUSERID"));
        agencyLog.put("changeType", "");
        formalAssessmentAuditMapper.save(agencyLog);
        String oldUrl = "#/FormalAssessmentAuditList/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/FormalAssessmentAuditView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), String.valueOf(delMap.get("OLDUSERID")), "1", lastUserId);
    }

    /**
     * 结束投标项目审批前加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endPreSign(String business_id, String task_id, String option) {
        // TODO 实现代码
    }


    /**
     * 结束投标项目审批后加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endPreAfterSign(String business_id, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 正式项目审批日志
     *
     * @param business_id
     * @return
     */
    private List<Map<String, Object>> formalLogs(String business_id) {
        return formalAssessmentAuditMapper.queryAuditedLogsById(business_id);
    }

    /**
     * 其它项目审批日志
     *
     * @param business_id
     * @return
     */
    private List<Map<String, Object>> bulletLogs(String business_id) {
        return bulletinAuditMapper.queryAuditLogs(business_id);
    }

    /**
     * 投标项目审批日志
     *
     * @param business_id
     * @return
     */
    private List<Map<String, Object>> preLogs(String business_id) {
        return preAuditLogMapper.queryAuditedLogsById(business_id);
    }

    /**
     * 前加签
     *
     * @param business_module
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void startSign(String type, String business_module, String business_id, String user_json, String task_id, String option) {
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.bulletSign(type, business_id, user_json, task_id, option);
        }
        if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(business_module)) {
            this.formalSign(type, business_id, user_json, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.preSign(type, business_id, user_json, task_id, option);
        }
    }

    /**
     * 正式项目审批前加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void formalSign(String type, String business_id, String user_json, String task_id, String option) {
        Document userDoc = Document.parse(user_json);
        String userId = userDoc.getString("VALUE");
        Map<String, Object> bulletinOracle = formalAssessmentInfoCreateMapper.getProjectByID(business_id);
        String lastUserId = ThreadLocalUtil.getUserId();
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = formalAssessmentAuditMapper.queryAuditedLogsById(business_id);
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_FormalAssessment).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        String taskName = task.getName();
        // 查询转办初始人
        Map<String, Object> firMap = logs.get(0);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        if (delMap.get("LASTUSERID") == null) {
            delMap.put("LASTUSERID", firMap.get("AUDITUSERID"));
        }
        if (delMap.get("OLDUSERID") == null) {
            delMap.put("OLDUSERID", lastUserId);
        }
        // 删除转办初始人待办日志
        Map<String, Object> deleteLog = new HashMap<String, Object>();
        deleteLog.put("businessId", business_id);
        deleteLog.put("isWaiting", "1");
        deleteLog.put("taskId", task_id);
        formalAssessmentAuditMapper.deleteWaitlogs(deleteLog);
        // 插入转办初始人已办日志
        int orderByAlready = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = new HashMap<String, Object>();
        alreadyLog.put("businessId", business_id);
        alreadyLog.put("auditUserId", lastUserId);
        alreadyLog.put("auditTime", Util.now());
        alreadyLog.put("opinion", option);
        alreadyLog.put("auditStatus", "B");
        alreadyLog.put("orderBy", ++orderByAlready);
        alreadyLog.put("isWaiting", "0");
        alreadyLog.put("taskdesc", taskName);
        alreadyLog.put("executionId", executionId);
        alreadyLog.put("lastUserId", delMap.get("LASTUSERID"));
        alreadyLog.put("oldUserId", delMap.get("OLDUSERID"));
        alreadyLog.put("changeType", type);
        formalAssessmentAuditMapper.save(alreadyLog);
        // 插入转办人待办日志
        int orderByAgency = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = new HashMap<String, Object>();
        agencyLog.put("businessId", business_id);
        agencyLog.put("auditUserId", userId);
        agencyLog.put("auditTime", Util.now());
        agencyLog.put("opinion", "");
        agencyLog.put("auditStatus", "9");
        agencyLog.put("orderBy", ++orderByAgency);
        agencyLog.put("isWaiting", "1");
        agencyLog.put("taskId", task_id);
        agencyLog.put("taskdesc", taskName);
        agencyLog.put("executionId", executionId);
        agencyLog.put("lastUserId", delMap.get("AUDITUSERID"));
        agencyLog.put("oldUserId", delMap.get("OLDUSERID"));
        agencyLog.put("changeType", type);
        formalAssessmentAuditMapper.save(agencyLog);
        // 发送待办
        String oldUrl = "#/FormalAssessmentAuditList/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/FormalAssessmentAuditDetailView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), userId, "1", lastUserId);
    }

    /**
     * 其它项目审批前加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void bulletSign(String type, String business_id, String user_json, String task_id, String option) {
        Document userDoc = Document.parse(user_json);
        String userId = userDoc.getString("VALUE");
        Map<String, Object> bulletinOracle = bulletinInfoMapper.queryByBusinessId(business_id);
        String lastUserId = ThreadLocalUtil.getUserId();
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = bulletinAuditMapper.queryAuditLogs(business_id);
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_BULLETIN).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        String taskName = task.getName();
        Map<String, Object> firMap = logs.get(0);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        if (delMap.get("LASTUSERID") == null) {
            delMap.put("LASTUSERID", firMap.get("AUDITUSERID"));
        }
        if (delMap.get("OLDUSERID") == null) {
            delMap.put("OLDUSERID", lastUserId);
        }
        // 删除待办日志
        Map<String, Object> delLog = new HashMap<String, Object>();
        delLog.put("businessId", business_id);
        delLog.put("isWaiting", "1");
        delLog.put("taskId", task_id);
        bulletinAuditMapper.deleteWaitlog(delLog);
        // 插入已办日志
        int orderByAlready = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = new HashMap<String, Object>();
        alreadyLog.put("businessId", business_id);
        alreadyLog.put("auditUserId", lastUserId);
        alreadyLog.put("auditTime", Util.now());
        alreadyLog.put("opinion", option);
        alreadyLog.put("auditStatus", "B");
        alreadyLog.put("orderBy", orderByAlready + 1);
        alreadyLog.put("isWaiting", "0");
        alreadyLog.put("taskdesc", taskName);
        alreadyLog.put("executionId", executionId);
        alreadyLog.put("lastUserId", delMap.get("LASTUSERID"));
        alreadyLog.put("oldUserId", delMap.get("OLDUSERID"));
        alreadyLog.put("changeType", type);
        bulletinAuditMapper.save(alreadyLog);
        // 插入待办日志
        int orderByAgency = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = new HashMap<String, Object>();
        agencyLog.put("businessId", business_id);
        agencyLog.put("auditUserId", userId);
        agencyLog.put("auditTime", Util.now());
        agencyLog.put("opinion", "");
        agencyLog.put("auditStatus", "9");
        agencyLog.put("orderBy", orderByAgency + 1);
        agencyLog.put("isWaiting", "1");
        agencyLog.put("taskId", task_id);
        agencyLog.put("taskdesc", taskName);
        agencyLog.put("executionId", executionId);
        agencyLog.put("lastUserId", delMap.get("AUDITUSERID"));
        agencyLog.put("oldUserId", delMap.get("OLDUSERID"));
        agencyLog.put("changeType", type);
        bulletinAuditMapper.save(agencyLog);
        // 转办人发送待办
        String oldUrl = "#/BulletinMattersAudit/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/BulletinMattersAuditView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), userId, "1", lastUserId);
    }

    /**
     * 投标项目审核前加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void preSign(String type, String business_id, String user_json, String task_id, String option) {
        // TODO 实现代码
    }

    @Override
    public TaskDefinition getNextTaskInfo(String key, String business_id) {
        // Map map = signMapper.getCurrentTaskInfo(key, business_id).get(0);
        HashMap map = this.getCurrentTask(key, business_id);
        if (map != null) {
            return this.getNextTask(String.valueOf(map.get("PROC_INST_ID_")), String.valueOf(map.get("TASK_DEF_KEY_")));
        }
        return null;
    }

    /**
     * @param process_instance_id
     * @param task_def_key
     * @return
     */
    private TaskDefinition getNextTask(String process_instance_id, String task_def_key) {
        TaskDefinition task = null;
        //获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(process_instance_id).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(definitionId);
        // ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 当前流程节点Id信息
        String activityId = task_def_key;//execution.getCurrentActivityId();
        //获取流程所有节点信息
        List<ActivityImpl> activityList = processDefinitionEntity.getActivities();
        //遍历所有节点信息
        String id = null;
        for (ActivityImpl activityImpl : activityList) {
            id = activityImpl.getId();
            if (id.equals(activityId)) {
                //获取下一个节点信息
                task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, process_instance_id);
                break;
            }
        }
        return task;
    }


    /**
     * 下一个任务节点信息
     * 如果下一个节点为用户任务则直接返回,
     * 如果下一个节点为排他网关, 获取排他网关Id信息,
     * 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式,
     * 并找到el表达式通过的线路后的用户任务
     *
     * @param activityImpl      流程节点信息
     * @param activityId        当前流程节点Id信息
     * @param elString          排他网关顺序流线段判断条件
     * @param processInstanceId 流程实例Id信息
     * @return
     */
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString, String processInstanceId) {
        PvmActivity ac = null;
        Object object = null;
        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            return taskDefinition;
        } else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))) {// 当前节点为exclusiveGateway
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            // 获取流程启动时设置的网关判断条件信息
            elString = getGatewayCondition(activityImpl.getId(), processInstanceId);
            // 如果排他网关只有一条线路信息
            if (outTransitions.size() == 1) {
                return nextTaskDefinition((ActivityImpl) outTransitions.get(0).getDestination(), activityId, elString, processInstanceId);
            } else if (outTransitions.size() > 1) { // 如果排他网关有多条线路信息
                for (PvmTransition tr1 : outTransitions) {
                    object = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                    // 判断el表达式是否成立
                    if (isCondition(activityImpl.getId(), StringUtils.trim(object.toString()), elString)) {
                        return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString, processInstanceId);
                    } else {
                        //  Noting can be done
                    }
                }
            } else {
                //  Noting can be done
            }
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    // 如果网关路线判断条件为空信息
                    if (StringUtils.isEmpty(elString)) {
                        // 获取流程启动时设置的网关判断条件信息
                        elString = getGatewayCondition(ac.getId(), processInstanceId);
                    }
                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, elString, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            object = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            // 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(object.toString()), elString)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString, processInstanceId);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                    // noting can be done
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 查询流程启动时设置排他网关判断条件信息
     *
     * @param gatewayId         排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息
     * @param processInstanceId 流程实例Id信息
     * @return
     */
    private String getGatewayCondition(String gatewayId, String processInstanceId) {
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
        Object object = runtimeService.getVariable(execution.getId(), gatewayId);
        return object == null ? "" : object.toString();
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     *
     * @param key   el表达式key信息
     * @param el    el表达式信息
     * @param value el表达式传入值信息
     * @return
     */
    private boolean isCondition(String key, String el, String value) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }
}
