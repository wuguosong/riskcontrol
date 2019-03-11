package com.yk.sign.service.impl;

import com.yk.bpmn.service.IBpmnService;
import com.yk.common.BaseMongo;
import com.yk.exception.BusinessException;
import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.bulletin.dao.IBulletinInfoMapper;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentAuditMapper;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import com.yk.sign.dao.ISignMapper;
import com.yk.sign.service.ISignService;
import common.Constants;
import common.PageAssistant;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/6.
 */
@Service
public class SignService implements ISignService {
    String after = "after";
    String before = "before";
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
    private TaskService taskService;
    @Resource
    private BaseMongo baseMongo;
    @Resource
    private ISignMapper signMapper;


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
        if (after.equalsIgnoreCase(type)) {
            this.afterSign(business_module, business_id, user_json, task_id, option);
        }
        if (before.equalsIgnoreCase(type)) {
            this.beforeSign(business_module, business_id, user_json, task_id, option);
        }
    }


    @Override
    public void endSign(String type, String business_module, String business_id, String task_id, String option) {
        // 校验主要参数
        if (StringUtils.isBlank(type)) {
            throw new BusinessException("结束失败,TYPE为空!");
        }
        if (StringUtils.isBlank(business_module)) {
            throw new BusinessException("结束失败,BUSINESS_MODULE为空!");
        }
        if (StringUtils.isBlank(business_id)) {
            throw new BusinessException("结束失败,BUSINESS_ID为空!");
        }
        if (StringUtils.isBlank(task_id)) {
            throw new BusinessException("结束失败,TASK_ID为空!");
        }
        if (after.equalsIgnoreCase(type)) {
            this.endAfterSign(business_module, business_id, task_id, option);
        }
        if (before.equalsIgnoreCase(type)) {
            this.endBeforeSign(business_module, business_id, task_id, option);
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
        } else if (Constants.FORMAL_ASSESSMENT.equalsIgnoreCase(business_module)) {
            return this.formalLogs(business_id);
        } else if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
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
     * 结束前加签
     *
     * @param business_module
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endBeforeSign(String business_module, String business_id, String task_id, String option) {
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.endBulleBeforeSign(business_id, task_id, option);
        }
        if (Constants.FORMAL_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.endFormalBeforeSign(business_id, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.endPreBeforeSign(business_id, task_id, option);
        }
    }

    /**
     * 结束后加签
     *
     * @param business_module
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endAfterSign(String business_module, String business_id, String task_id, String option) {
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.endBulletAfterSign(business_id, task_id, option);
        }
        if (Constants.FORMAL_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.endFormalAfterSign(business_id, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.endPreAfterSign(business_id, task_id, option);
        }
    }

    /**
     * 结束其它项目审批前加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endBulleBeforeSign(String business_id, String task_id, String option) {
        String lastUserId = ThreadLocalUtil.getUserId();
        Map<String, Object> bulletinOracle = bulletinInfoMapper.queryByBusinessId(business_id);
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = bulletinAuditMapper.queryAuditLogs(business_id);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
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

    /**
     * 结束正式项目审批前加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endFormalBeforeSign(String business_id, String task_id, String option) {
        String lastUserId = ThreadLocalUtil.getUserId();
        Map<String, Object> bulletinOracle = formalAssessmentInfoCreateMapper.getProjectByID(business_id);
        // 获取节点日志描述信息
        List<Map<String, Object>> logs = formalAssessmentAuditMapper.queryAuditedLogsById(business_id);
        Map<String, Object> delMap = logs.get(logs.size() - 1);
        // 将当前人待办变已办，新增待办日志
        List<Task> list = taskService.createTaskQuery().taskId(task_id).list();//taskService.createTaskQuery().taskId(task_id).processDefinitionKey(Constants.FORMAL_ASSESSMENT).processInstanceBusinessKey(business_id).list();
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
        alreadyLog.put("changeType", "before");
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
    private void endPreBeforeSign(String business_id, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 结束其它项目审批后加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endBulletAfterSign(String business_id, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 结束正式项目审批后加签
     *
     * @param business_id
     * @param task_id
     * @param option
     */
    private void endFormalAfterSign(String business_id, String task_id, String option) {
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
    private void beforeSign(String business_module, String business_id, String user_json, String task_id, String option) {
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.bulletBeforeSign(business_id, user_json, task_id, option);
        }
        if (Constants.FORMAL_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.formalBeforeSign(business_id, user_json, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.preBeforeSign(business_id, user_json, task_id, option);
        }
    }

    /**
     * 后加签
     *
     * @param business_module
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void afterSign(String business_module, String business_id, String user_json, String task_id, String option) {
        if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(business_module)) {
            this.bulletAfterSign(business_id, user_json, task_id, option);
        }
        if (Constants.FORMAL_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.formalAfterSign(business_id, user_json, task_id, option);
        }
        if (Constants.PRE_ASSESSMENT.equalsIgnoreCase(business_module)) {
            this.preAfterSign(business_id, user_json, task_id, option);
        }
    }

    /**
     * 正式项目审批后加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void formalAfterSign(String business_id, String user_json, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 其它项目审批后加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void bulletAfterSign(String business_id, String user_json, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 投标项目审核后加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void preAfterSign(String business_id, String user_json, String task_id, String option) {
        // TODO 实现代码
    }

    /**
     * 正式项目审批前加签
     *
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    private void formalBeforeSign(String business_id, String user_json, String task_id, String option) {
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
        if(delMap.get("LASTUSERID") == null){
            delMap.put("LASTUSERID", firMap.get("AUDITUSERID"));
        }
        if(delMap.get("OLDUSERID") == null){
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
        alreadyLog.put("changeType", "before");
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
        agencyLog.put("changeType", "before");
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
    private void bulletBeforeSign(String business_id, String user_json, String task_id, String option) {
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
        if(delMap.get("LASTUSERID") == null){
            delMap.put("LASTUSERID", firMap.get("AUDITUSERID"));
        }
        if(delMap.get("OLDUSERID") == null){
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
        alreadyLog.put("changeType", "before");
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
        agencyLog.put("changeType", "before");
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
    private void preBeforeSign(String business_id, String user_json, String task_id, String option) {
        // TODO 实现代码
    }
}
