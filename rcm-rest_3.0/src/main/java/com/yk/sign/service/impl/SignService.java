package com.yk.sign.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.bpmn.service.IBpmnService;
import com.yk.common.BaseMongo;
import com.yk.exception.BusinessException;
import com.yk.process.service.IProcessService;
import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.bulletin.dao.IBulletinInfoMapper;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentAuditMapper;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.newPre.dao.IPreInfoCreateMapper;
import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import com.yk.sign.dao.ISignMapper;
import com.yk.sign.entity._ApprovalNode;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import util.ThreadLocalUtil;
import util.UserUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.*;

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
    private IPreInfoCreateMapper preInfoCreateMapper;
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
        if (Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(business_module)) {
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
        if (Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(business_module)) {
            return this.dealDate(this.preLogs(business_id));
        } else if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(business_module)) {
            return this.dealDate(this.formalLogs(business_id));
        } else if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            return this.dealDate(this.bulletLogs(business_id));
        } else {
            throw new BusinessException("BUSINESS_MODULE不在系统业务中!");
        }
    }

    private List<Map<String, Object>> dealDate(List<Map<String, Object>> logs){
        if(CollectionUtils.isNotEmpty(logs)){
            logs.get(logs.size() - 1).put("AUDITTIME", "");
        }
        return logs;
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
        try {
            if ("after".equalsIgnoreCase(changeType)) {
                // 所有正向的表达式
                Task curTask = taskService.createTaskQuery().taskId(task_id).singleResult();
                String curTaskKey = curTask.getTaskDefinitionKey();
                String nexTaskKey = this.getNextTaskInfo(Constants.PROCESS_KEY_BULLETIN, business_id).getKey();
                Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
                // 结束本次任务,开始下个节点
                variable.put("opinion", option);
                taskService.complete(task_id, variable);
                return;
            }
        } catch (Exception e) {
            // 此时发生时,是因为不确定下一个节点
            e.printStackTrace();
        }
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_BULLETIN).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        // 删除待办日志
        Map<String, Object> deleteLog = this.createDelLog(business_id, null, null, lastUserId);
        this.bulletinAuditMapper.deleteWaitlog(deleteLog);
        // 插入已办日志
        int orderByReady = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, ++orderByReady, task.getName(), executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), delMap.get("CHANGETYPE"));
        this.bulletinAuditMapper.save(alreadyLog);
        // 插入当前人待办日志
        int orderByAgency = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, delMap.get("OLDUSERID"), ++orderByAgency, task_id, task.getName(), executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), "");
        bulletinAuditMapper.save(agencyLog);
        String oldUrl = "#/BulletinMattersAudit/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/BulletinMattersAuditView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), String.valueOf(delMap.get("OLDUSERID")), "1", lastUserId);
    }

    public HashMap<String, Object> getCurrentTask(String business_module, String business_id) {
        List<HashMap> logs = signMapper.getCurrentTaskInfo(business_module, business_id);
        HashMap<String, Object> log = null;
        String curUser = UserUtil.getCurrentUserUuid();
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
                variable.put("opinion", option);
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
        deleteLog.put("currentUserId", lastUserId);
        this.formalAssessmentAuditMapper.deleteWaitlogs(deleteLog);
        // 插入已办日志
        int orderByReady = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, ++orderByReady, task.getName(), executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), delMap.get("CHANGETYPE"));
        this.formalAssessmentAuditMapper.save(alreadyLog);
        // 插入当前人待办日志
        int orderByAgency = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, delMap.get("OLDUSERID"), ++orderByAgency, task_id, task.getName(), executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), "");
        // 设置一些与业务有关的节点参数
        this.setBusinessSetting(task, agencyLog);
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
        String lastUserId = ThreadLocalUtil.getUserId();
        Map<String, Object> bulletinOracle = preInfoCreateMapper.getProjectByID(business_id);
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = preAuditLogMapper.queryAuditedLogsById(business_id);
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_PREREVIEW).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        String taskName = task.getName();
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        // 根据当前审批日志,判断该结束的是前加签还是后加签
        String changeType = String.valueOf(delMap.get("CHANGETYPE"));
        try {
            if ("after".equalsIgnoreCase(changeType)) {
                // 所有正向的表达式
                Task curTask = taskService.createTaskQuery().taskId(task_id).singleResult();
                String curTaskKey = curTask.getTaskDefinitionKey();
                String nexTaskKey = this.getNextTaskInfo(Constants.PROCESS_KEY_PREREVIEW, business_id).getKey();
                Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
                // 结束本次任务,开始下个节点
                variable.put("opinion", option);
                taskService.complete(task_id, variable);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 删除转办初始人待办日志
        HashMap<String, Object> deleteLog = (HashMap<String, Object>) this.createDelLog(business_id, executionId, null, lastUserId);
        preAuditLogMapper.deleteWaitlogs(deleteLog);
        // 插入转办初始人已办日志
        int orderByAlready = preAuditLogMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, ++orderByAlready, taskName, executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), delMap.get("CHANGETYPE"));
        preAuditLogMapper.save(alreadyLog);
        // 插入转办人待办日志
        int orderByAgency = preAuditLogMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, delMap.get("OLDUSERID"), ++orderByAgency, task_id, taskName, executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), "");
        preAuditLogMapper.save(agencyLog);
        // 发送待办
        String oldUrl = "#/PreAuditReportList/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/PreOtherReportView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("PROJECTNAME"), String.valueOf(delMap.get("OLDUSERID")), "1", lastUserId);
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
        if (Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(business_module)) {
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
        this.initDelMap(firMap, delMap, lastUserId);
        // 删除转办初始人待办日志
        Map<String, Object> deleteLog = this.createDelLog(business_id, null, task_id, lastUserId);
        formalAssessmentAuditMapper.deleteWaitlogs(deleteLog);
        // 插入转办初始人已办日志
        int orderByAlready = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, ++orderByAlready, taskName, executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), type);
        formalAssessmentAuditMapper.save(alreadyLog);
        // 插入转办人待办日志
        int orderByAgency = formalAssessmentAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, userId, ++orderByAgency, task_id, taskName, executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), type);
        // 设置一些与业务有关的节点参数
        this.setBusinessSetting(task, agencyLog);
        formalAssessmentAuditMapper.save(agencyLog);
        // 发送待办
        String oldUrl = "#/FormalAssessmentAuditList/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/FormalAssessmentAuditDetailView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("BULLETINNAME"), userId, "1", lastUserId);
    }

    private void setBusinessSetting(Task task, Map<String, Object> agencyLog) {
        if ("usertask19".equals(task.getTaskDefinitionKey())) {
            String desc = task.getDescription();
            JSONObject descJson = JSON.parseObject(desc, JSONObject.class);
            String taskcode = descJson.getString("taskcode");
            agencyLog.put("taskMark", taskcode);
        }
    }

    /**
     * 其它项目审批加签
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
        this.initDelMap(firMap, delMap, lastUserId);
        // 删除待办日志
        Map<String, Object> delLog = this.createDelLog(business_id, null, task_id, lastUserId);
        bulletinAuditMapper.deleteWaitlog(delLog);
        // 插入已办日志
        int orderByAlready = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, orderByAlready + 1, taskName, executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), type);
        bulletinAuditMapper.save(alreadyLog);
        // 插入待办日志
        int orderByAgency = bulletinAuditMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, userId, orderByAgency + 1, task_id, taskName, executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), type);
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
        Document userDoc = Document.parse(user_json);
        String userId = userDoc.getString("VALUE");
        Map<String, Object> bulletinOracle = preInfoCreateMapper.getProjectByID(business_id);
        String lastUserId = ThreadLocalUtil.getUserId();
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = preAuditLogMapper.queryAuditedLogsById(business_id);
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.PROCESS_KEY_PREREVIEW).processInstanceBusinessKey(business_id).list();
        Task task = list.get(0);
        String executionId = task.getExecutionId();
        String taskName = task.getName();
        // 查询转办初始人
        Map<String, Object> firMap = logs.get(0);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        initDelMap(firMap, delMap, lastUserId);
        // 删除转办初始人待办日志
        HashMap<String, Object> deleteLog = (HashMap<String, Object>) this.createDelLog(business_id, executionId, null, lastUserId);
        preAuditLogMapper.deleteWaitlogs(deleteLog);
        // 插入转办初始人已办日志
        int orderByAlready = preAuditLogMapper.queryMaxOrderNum(business_id);
        Map<String, Object> alreadyLog = this.createAlreadyLog(business_id, lastUserId, option, ++orderByAlready, taskName, executionId, delMap.get("LASTUSERID"), delMap.get("OLDUSERID"), type);
        preAuditLogMapper.save(alreadyLog);
        // 插入转办人待办日志
        int orderByAgency = preAuditLogMapper.queryMaxOrderNum(business_id);
        Map<String, Object> agencyLog = this.createAgencyLog(business_id, userId, ++orderByAgency, task_id, taskName, executionId, delMap.get("AUDITUSERID"), delMap.get("OLDUSERID"), type);
        preAuditLogMapper.save(agencyLog);
        // 发送待办
        String oldUrl = "#/PreAuditReportList/1";
        String out = Util.encodeUrl(oldUrl);
        String url = "/PreOtherReportView/" + business_id + "/" + out;
        bpmnService.sendWaitingToPotal(task_id, url, Util.format(Util.now()), (String) bulletinOracle.get("PROJECTNAME"), userId, "1", lastUserId);
    }

    private void initDelMap(Map<String, Object> firMap, Map<String, Object> delMap, String lastUserId) {
        if (delMap.get("LASTUSERID") == null) {
            delMap.put("LASTUSERID", firMap.get("AUDITUSERID"));
        }
        if (delMap.get("OLDUSERID") == null) {
            delMap.put("OLDUSERID", lastUserId);
        }
    }

    private Map<String, Object> createDelLog(String businessId, String executionId, String taskId, String currentUserId) {
        Map<String, Object> delLog = new HashMap<String, Object>();
        delLog.put("businessId", businessId);
        delLog.put("isWaiting", "1");
        if (StringUtils.isNotBlank(taskId)) {
            delLog.put("taskId", taskId);
        }
        if (StringUtils.isNotBlank(executionId)) {
            delLog.put("executionId", executionId);
        }
        delLog.put("currentUserId", currentUserId);
        return delLog;
    }

    private Map<String, Object> createAgencyLog(Object businessId, Object auditUserId, int orderBy, Object taskId, Object taskdesc, Object executionId, Object lastUserId, Object oldUserId, Object changeType) {
        Map<String, Object> agencyLog = new HashMap<String, Object>();
        agencyLog.put("businessId", businessId);
        agencyLog.put("auditUserId", auditUserId);
        agencyLog.put("auditTime", Util.now());
        agencyLog.put("opinion", "");
        agencyLog.put("auditStatus", "9");
        agencyLog.put("orderBy", orderBy);
        agencyLog.put("isWaiting", "1");
        agencyLog.put("taskId", taskId);
        agencyLog.put("taskdesc", taskdesc);
        agencyLog.put("executionId", executionId);
        agencyLog.put("lastUserId", lastUserId);
        agencyLog.put("oldUserId", oldUserId);
        agencyLog.put("changeType", changeType);
        return agencyLog;
    }

    private Map<String, Object> createAlreadyLog(Object businessId, Object auditUserId, Object option, int orderBy, Object taskdesc, Object executionId, Object lastUserId, Object oldUserId, Object changeType) {
        Map<String, Object> alreadyLog = new HashMap<String, Object>();
        alreadyLog.put("businessId", businessId);
        alreadyLog.put("auditUserId", auditUserId);
        alreadyLog.put("auditTime", Util.now());
        alreadyLog.put("opinion", option);
        alreadyLog.put("auditStatus", "B");
        alreadyLog.put("orderBy", orderBy);
        alreadyLog.put("isWaiting", "0");
        alreadyLog.put("taskdesc", taskdesc);
        alreadyLog.put("executionId", executionId);
        alreadyLog.put("lastUserId", lastUserId);
        alreadyLog.put("oldUserId", oldUserId);
        alreadyLog.put("changeType", changeType);
        return alreadyLog;
    }


    @Override
    public TaskDefinition getNextTaskInfo(String key, String business_id) {
        Map map = signMapper.getCurrentTaskInfo(key, business_id).get(0);
        // HashMap map = this.getCurrentTask(key, business_id);
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
        // 当前流程节点Id信息
        String activityId = task_def_key;
        //获取流程所有节点信息
        List<ActivityImpl> activityList = processDefinitionEntity.getActivities();
        //遍历所有节点信息
        task = this.getSubprocessTask(activityList, process_instance_id, activityId);
        return task;
    }

    /**
     * 迭代子流程
     *
     * @param activities
     * @param process_instance_id
     * @param activityId
     * @return
     */
    public TaskDefinition getSubprocessTask(List<ActivityImpl> activities, String process_instance_id, String activityId) {
        String id = null;
        TaskDefinition task = null;
        for (ActivityImpl activityImpl : activities) {
            id = activityImpl.getId();
            Object type = activityImpl.getProperty("type");
            if ("subProcess".equals(type)) {
                // 获取子流程节点,进行迭代
                task = this.getSubprocessTask(activityImpl.getActivities(), process_instance_id, activityId);
            } else {
                if (id.equals(activityId)) {
                    //获取下一个节点信息
                    task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, process_instance_id);
                    break;
                }
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

    @Override
    public HashMap<String, Object> validateSign(String key, String business_id) {
        HashMap<String, Object> validate = new HashMap<String, Object>();
        String code = null;
        String comment = null;
        TaskDefinition taskDefinition = null;
        try {
            taskDefinition = this.getNextTaskInfo(key, business_id);
            /**
             * 5.否则, 进行下一个判断
             * 6.判断下一个节点是否开始节点
             * 7.判断下一个节点是否结束节点
             */
            if (taskDefinition != null) {
                String beforeTaskKey = "";
                String startTaskKey = "";
                List<HashMap<String, Object>> history = processService.listProcessNode(key, business_id);
                if (CollectionUtils.isNotEmpty(history)) {
                    startTaskKey = String.valueOf(history.get(0).get("TASK_DEF_KEY_"));
                    if (history.size() > 1) {
                        beforeTaskKey = String.valueOf(history.get(history.size() - 2).get("TASK_DEF_KEY_"));
                    }
                }
                String nextTaskKey = taskDefinition.getKey();
                if (beforeTaskKey.equals(nextTaskKey)) {
                    code = "GMU";
                    comment = "下一步是网关,有多个任务节点,不能进行后加签操作!";
                } else if (startTaskKey.equals(nextTaskKey)) {
                    code = "SMP";
                    comment = "下一步回归主流程或子流程结束,不能进行后加签操作!";
                } else {
                    // 是否还有其它可能
                }
            } else {
                code = "CNP";
                comment = "当前或下一步是并行或结束节点,不能进行后加签操作!";
            }
        } catch (Exception ex) {
            /**
             * 1.执行getGatewayCondition(Object...)时抛出异常
             * 2.下一个节点蕴含不确定网关时,singleResult()获取的不止一个结果
             * 3.此时抛出异常
             * 4.不能进行本次任务的终结,不能进行下一步操作
             */
            code = "GMU";
            comment = "下一步是网关,有多个任务节点,不能进行后加签操作!";
        }
        validate.put("code", code);
        validate.put("comment", comment);
        return validate;
    }

    @Override
    public Map<String, Map<String, Object>> list2Map(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
        for (Map<String, Object> hashMap : list) {
            System.out.println(hashMap.get("TASKDESC") + " " + hashMap.get("AUDITUSERNAME") + " " + hashMap.get("AUDITTIME"));
            map.put(String.valueOf(hashMap.get("TASKDESC")), hashMap);
        }
        return map;
    }

    @Override
    public JSONObject put(Map<String, Map<String, Object>> map, JSONObject jsonObject) {
        String _approvalKey = jsonObject.getString(_ApprovalNode._approvalKey);
        Map<String, Object> hashMap = null;
        if (_approvalKey.contains(",")) {
            String[] _approvalKeyArray = _approvalKey.split(",");
            for (String key : _approvalKeyArray) {
                hashMap = map.get(key);
                if (hashMap != null) {
                    break;
                }
            }
        } else {
            hashMap = map.get(_approvalKey);
        }
        if (hashMap == null) {
            jsonObject.put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDo);
            jsonObject.put(_ApprovalNode._approvalUser, "");
            jsonObject.put(_ApprovalNode._approvalDate, "");
            jsonObject.put(_ApprovalNode._approvalStateCode, 0);// 还未执行
            return jsonObject;
        }
        jsonObject.put(_ApprovalNode._approvalDate, hashMap.get("AUDITTIME"));
        jsonObject.put(_ApprovalNode._approvalUser, hashMap.get("AUDITUSERNAME"));
        jsonObject.put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDone);
        jsonObject.put(_ApprovalNode._approvalStateCode, -1);// 已经执行
        return jsonObject;
    }

    @Override
    public _ApprovalNode getNewProcessImageStep(String processKey, String processId) {
        List<Map<String, Object>> list = signMapper.selectUniqueTasksForImageStep(processKey, processId);
        _ApprovalNode _approvalNode = new _ApprovalNode();
        Map<String, Map<String, Object>> map = this.list2Map(list);
        // 任务判断
        List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processKey).processInstanceBusinessKey(processId).list();
        // 退回等过滤操作
        map = this.judgeTaskMap(map, tasks);
        if (Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(processKey)) {
            // 投标评审
            _ApprovalNode._ReviewApproval _reviewApproval = new _ApprovalNode._ReviewApproval();
            _reviewApproval.set_drafting(this.put(map, _reviewApproval.get_drafting()));
            _reviewApproval.set_investmentManagerDrafting(this.put(map, _reviewApproval.get_investmentManagerDrafting()));
            _reviewApproval.set_businessAreaApproval(this.put(map, _reviewApproval.get_businessAreaApproval()));
            _reviewApproval.set_waterInvestmentCenter(this.put(map, _reviewApproval.get_waterInvestmentCenter()));
            _reviewApproval.set_assignmentTask(this.put(map, _reviewApproval.get_assignmentTask()));
            _reviewApproval.set_reviewChargeApproval(this.put(map, _reviewApproval.get_reviewChargeApproval()));
            _reviewApproval.set_reviewChargeConfirm(this.put(map, _reviewApproval.get_reviewChargeConfirm()));
            _reviewApproval.set_completed(this.put(map, _reviewApproval.get_completed()));
            for (Task task : tasks) {
                String name = task.getName();
                _reviewApproval.set_drafting(this.judgeTask(_reviewApproval.get_drafting(), name));
                _reviewApproval.set_investmentManagerDrafting(this.judgeTask(_reviewApproval.get_investmentManagerDrafting(), name));
                _reviewApproval.set_businessAreaApproval(this.judgeTask(_reviewApproval.get_businessAreaApproval(), name));
                _reviewApproval.set_waterInvestmentCenter(this.judgeTask(_reviewApproval.get_waterInvestmentCenter(), name));
                _reviewApproval.set_assignmentTask(this.judgeTask(_reviewApproval.get_assignmentTask(), name));
                _reviewApproval.set_reviewChargeApproval(this.judgeTask(_reviewApproval.get_reviewChargeApproval(), name));
                _reviewApproval.set_reviewChargeConfirm(this.judgeTask(_reviewApproval.get_reviewChargeConfirm(), name));
                _reviewApproval.set_completed(this.judgeTask(_reviewApproval.get_completed(), name));
            }
            _reviewApproval.execute();
            _approvalNode.set_reviewApproval(_reviewApproval);
            _approvalNode.set_processId(processId);
            _approvalNode.set_processKey(processKey);
        } else if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(processKey)) {
            // 正式评审
            _ApprovalNode._FormalApproval _formalApproval = new _ApprovalNode._FormalApproval();
            _formalApproval.set_drafting(this.put(map, _formalApproval.get_drafting()));
            _formalApproval.set_investmentManagerDrafting(this.put(map, _formalApproval.get_investmentManagerDrafting()));
            _formalApproval.set_businessAreaApproval(this.put(map, _formalApproval.get_businessAreaApproval()));
            _formalApproval.set_waterInvestmentCenter(this.put(map, _formalApproval.get_waterInvestmentCenter()));
            _formalApproval.set_assignmentTask(this.put(map, _formalApproval.get_assignmentTask()));
            _formalApproval.set_legalDistribution(this.put(map, _formalApproval.get_legalDistribution()));
            _formalApproval.set_lawChargeApproval(this.put(map, _formalApproval.get_lawChargeApproval()));
            _formalApproval.set_reviewChargeApproval(this.put(map, _formalApproval.get_reviewChargeApproval()));
            _formalApproval.set_reviewChargeConfirm(this.put(map, _formalApproval.get_reviewChargeConfirm()));
            _formalApproval.set_completed(this.put(map, _formalApproval.get_completed()));
            for (Task task : tasks) {
                String name = task.getName();
                _formalApproval.set_drafting(this.judgeTask(_formalApproval.get_drafting(), name));
                _formalApproval.set_investmentManagerDrafting(this.judgeTask(_formalApproval.get_investmentManagerDrafting(), name));
                _formalApproval.set_businessAreaApproval(this.judgeTask(_formalApproval.get_businessAreaApproval(), name));
                _formalApproval.set_waterInvestmentCenter(this.judgeTask(_formalApproval.get_waterInvestmentCenter(), name));
                _formalApproval.set_assignmentTask(this.judgeTask(_formalApproval.get_assignmentTask(), name));
                _formalApproval.set_legalDistribution(this.judgeTask(_formalApproval.get_legalDistribution(), name));
                _formalApproval.set_lawChargeApproval(this.judgeTask(_formalApproval.get_lawChargeApproval(), name));
                _formalApproval.set_reviewChargeApproval(this.judgeTask(_formalApproval.get_reviewChargeApproval(), name));
                _formalApproval.set_reviewChargeConfirm(this.judgeTask(_formalApproval.get_reviewChargeConfirm(), name));
                _formalApproval.set_completed(this.judgeTask(_formalApproval.get_completed(), name));
            }
            _formalApproval.execute();
            _approvalNode.set_formalApproval(_formalApproval);
            _approvalNode.set_processId(processId);
            _approvalNode.set_processKey(processKey);
        } else if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(processKey)) {
            // 其它评审
            _ApprovalNode._BulletinApproval _bulletinApproval = new _ApprovalNode._BulletinApproval();
            _bulletinApproval.set_drafting(this.put(map, _bulletinApproval.get_drafting()));
            _bulletinApproval.set_unitChargeApproval(this.put(map, _bulletinApproval.get_unitChargeApproval()));
            _bulletinApproval.set_businessLeaderApproval(this.put(map, _bulletinApproval.get_businessLeaderApproval()));
            _bulletinApproval.set_assignmentTask(this.put(map, _bulletinApproval.get_assignmentTask()));
            _bulletinApproval.set_lawChargeApproval(this.put(map, _bulletinApproval.get_lawChargeApproval()));
            _bulletinApproval.set_reviewChargeApproval(this.put(map, _bulletinApproval.get_reviewChargeApproval()));
            _bulletinApproval.set_completed(this.put(map, _bulletinApproval.get_completed()));
            for (Task task : tasks) {
                String name = task.getName();
                _bulletinApproval.set_drafting(this.judgeTask(_bulletinApproval.get_drafting(), name));
                _bulletinApproval.set_unitChargeApproval(this.judgeTask(_bulletinApproval.get_unitChargeApproval(), name));
                _bulletinApproval.set_businessLeaderApproval(this.judgeTask(_bulletinApproval.get_businessLeaderApproval(), name));
                _bulletinApproval.set_assignmentTask(this.judgeTask(_bulletinApproval.get_assignmentTask(), name));
                _bulletinApproval.set_lawChargeApproval(this.judgeTask(_bulletinApproval.get_lawChargeApproval(), name));
                _bulletinApproval.set_reviewChargeApproval(this.judgeTask(_bulletinApproval.get_reviewChargeApproval(), name));
                _bulletinApproval.set_completed(this.judgeTask(_bulletinApproval.get_completed(), name));
            }
            _bulletinApproval.execute();
            _approvalNode.set_bulletinApproval(_bulletinApproval);
            _approvalNode.set_processId(processId);
            _approvalNode.set_processKey(processKey);
        } else {
            return null;
        }
        return _approvalNode;
    }

    /**
     * 根据当前任务进行判断
     *
     * @param js
     * @param name
     * @return
     */
    private JSONObject judgeTask(JSONObject js, String name) {
        if (js.getString(_ApprovalNode._approvalKey).contains(name)) {
            js.put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDoing);
            js.put(_ApprovalNode._approvalDate, "");
            js.put(_ApprovalNode._approvalStateCode, 1);// 正在执行
        }
        return js;
    }

    /**
     * 进行重复性过滤
     *
     * @param map
     * @param tasks
     * @return
     */
    private Map<String, Map<String, Object>> judgeTaskMap(Map<String, Map<String, Object>> map, List<Task> tasks) {
        List<Integer> idx = new ArrayList<Integer>();// 存储当前任务出现的索引位置
        System.out.println("+++++++++++++++++++++++有序map开始++++++++++++++++++++++");
        for (Iterator<Map.Entry<String, Map<String, Object>>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Map<String, Object>> entry = iterator.next();
            String key = entry.getKey();
            Map<String, Object> value = entry.getValue();
            System.out.println(key + " " + JSON.toJSONString(value));
        }
        System.out.println("++++++++++++++++++++++有序map结束+++++++++++++++++++++++");
        List<Map<String, Object>> list = this.map2List(map);
        System.out.println("移除前：" + list.size() + " " + map.size());
        for (Task task : tasks) {
            String name = task.getName();
            for (Map<String, Object> obj : list) {
                if (obj.get("TASKDESC").equals(name)) {
                    idx.add(list.indexOf(obj));
                    System.out.println("name:" + name + " desc:" + obj.get("TASKDESC"));
                }
            }
        }
        Collections.sort(idx);// 排序
        System.out.println(JSON.toJSONString(idx));
        List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(idx)) {
            subList = list.subList(0, idx.get(0));// List.subList后不要试图操作父List，该方法返回的是一个视图
        } else {
            subList = list;
        }
        Map<String, Map<String, Object>> subMap = this.list2Map(subList);
        System.out.println("移除后：" + subList.size() + " " + subMap.size());
        return map;
    }

    /**
     * Map转换为List
     *
     * @param map
     * @return
     */
    private List<Map<String, Object>> map2List(Map<String, Map<String, Object>> map) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Iterator<Map.Entry<String, Map<String, Object>>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            list.add(iterator.next().getValue());
        }
        return list;
    }
}
