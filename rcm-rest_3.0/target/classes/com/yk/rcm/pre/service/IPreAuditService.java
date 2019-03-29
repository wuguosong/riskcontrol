package com.yk.rcm.pre.service;

import java.util.Map;
import com.yk.bpmn.entity.TaskInfo;
import common.PageAssistant;
import common.Result;

public interface IPreAuditService {
	/**
	 * 查询已办列表
	 * @param page
	 */
	void queryAuditedList(PageAssistant page);
	/**
	 * 查询待办列表
	 * @param page
	 */
	void queryWaitList(PageAssistant page);
	/**
	 * 查询流程选项
	 * @param businessId
	 * @return
	 */
	Result querySingleProcessOptions(String businessId);
	/**
	 * 启动流程
	 * @param businessId
	 * @return
	 */
	Result startSingleFlow(String businessId);
	/**
	 * 流程审核
	 * @param businessId
	 * @param processOption
	 * @param variables
	 * @return
	 */
	Result auditSingle(String businessId, String processOption, Map<String, Object> variables);
	/**
	 * 查询节点信息
	 * @param businessId
	 * @param processKey
	 * @return
	 */
	TaskInfo queryTaskInfoByBusinessId(String businessId, String processKey);

}
