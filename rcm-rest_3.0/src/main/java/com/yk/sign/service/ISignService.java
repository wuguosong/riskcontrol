package com.yk.sign.service;

import com.alibaba.fastjson.JSONObject;
import com.yk.sign.entity._ApprovalNode;
import common.PageAssistant;
import org.activiti.engine.impl.task.TaskDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/6.
 */
public interface ISignService {

    /**
     * 加签
     *
     * @param type
     * @param business_module
     * @param business_id
     * @param user_json
     * @param task_id
     * @param option
     */
    void doSign(String type, String business_module, String business_id, String user_json, String task_id, String option);

    /**
     * 获取审批日志记录
     *
     * @param business_module
     * @param business_id
     * @return
     */
    List<Map<String, Object>> listLogs(String business_module, String business_id);

    /**
     * 查询代办列表
     *
     * @param key
     * @param page
     * @return
     */
    PageAssistant queryAgencyList(String key, PageAssistant page);

    /**
     * 结束加签
     *
     * @param business_module
     * @param business_id
     * @param task_id
     * @param option
     */
    void endSign(String business_module, String business_id, String task_id, String option);

    /**
     * 获取下一个审批节点
     *
     * @param key
     * @param business_id
     * @return
     */
    TaskDefinition getNextTaskInfo(String key, String business_id);

    /**
     * 本步是否可以进行后加签操作
     *
     * @param key
     * @param business_id
     * @return
     */
    HashMap<String, Object> validateSign(String key, String business_id);

    /**
     * 获取当前任务信息
     *
     * @param business_module
     * @param business_id
     * @return
     */
    HashMap<String, Object> getCurrentTask(String business_module, String business_id);

    ///////////////////////////////新流程图专用////////////////////////////////
    Map<String, Map<String, Object>> list2Map(List<Map<String, Object>> list);

    JSONObject put(Map<String, Map<String, Object>> map, JSONObject jsonObject);

    _ApprovalNode getNewProcessImageStep(String processKey, String processId);
    ///////////////////////////////新流程图专用////////////////////////////////

    /**
     * 流程节点跳转
     *
     * @param taskId     跳转开始节点
     * @param activityId 跳转结束节点
     * @retrun List<String> 跳转成功后的任务实例Id列表
     * @notice 不支持并行流程
     */
    List<String> jumpTask(String taskId, String activityId);

    /**
     * 执行驳回操作
     * @param processKey 流程Key
     * @param businessKey 业务Key
     * @param taskId 当前任务Id
     * @param activityId 要驳回的节点Key
     */
    void executeBreak(String processKey, String businessKey, String taskId, String activityId, String comments);

    /**
     * 删除不在运行时任务中的代办
     * @param processKey
     * @param businessKey
     */
    void deleteLogsNotInRunTask(String processKey, String businessKey);

    /**
     * 保存基层法务意见到Mongo中
     * @param processKey 流程Key
     * @param businessKey 业务Id
     * @param mongoData 要保存的Mongo数据
     */
    void saveGrassrootsLegalStaffOpinionMongo(String processKey, String businessKey, String mongoData);
}
