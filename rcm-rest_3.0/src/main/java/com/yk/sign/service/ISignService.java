package com.yk.sign.service;

import common.PageAssistant;
import org.activiti.engine.impl.task.TaskDefinition;

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
     * @param business_module
     * @param business_id
     * @param task_id
     * @param option
     */
    void endSign(String business_module, String business_id, String task_id, String option);

    /**
     * 获取下一个审批节点
     * @param key
     * @param business_id
     * @return
     */
    TaskDefinition getNextTaskInfo(String key, String business_id);
}
