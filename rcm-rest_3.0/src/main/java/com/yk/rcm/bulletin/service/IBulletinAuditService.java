/**
 * 
 */
package com.yk.rcm.bulletin.service;

import java.util.Map;

import com.yk.bpmn.entity.TaskInfo;

import common.PageAssistant;
import common.Result;

/**
 * 通报事项Service
 * @author wufucan
 *
 */
public interface IBulletinAuditService {
	/**
	 * 启动单个流程
	 * @param businessId
	 * @return
	 */
	public Result startSingleFlow(String businessId);
	/**
	 * 启动批量流程
	 * @param businessId
	 * @return
	 */
	public Result startBatchFlow(String[] idsArr);
	/**
	 * 单个审核
	 * @param businessId
	 * @param processOption
	 * @param variables
	 * @return
	 */
	public Result auditSingle(String businessId, String processOption, Map<String, Object> variables);
	/**
	 * 查询待处理
	 * @param page
	 * @return
	 */
	public void queryWaitList(PageAssistant page);
	/**
	 * 查询已处理
	 * @param page
	 * @return
	 */
	public void queryAuditedList(PageAssistant page);
	/**
	 * 查询流程选项
	 * @param businessId
	 * @return
	 */
	public Result querySingleProcessOptions(String businessId);
	/**
	 * 查询节点信息
	 * @param businessId
	 * @param processKey
	 * @return
	 */
	public TaskInfo queryTaskInfoByBusinessId(String businessId, String processKey);
	/**
	 * 转办任务
	 * @param businessId
	 * @param user
	 * @param taskId
	 * @param option 
	 * @return
	 */
	public Result changeWork(String businessId, String user,String taskId, String option);
	/**
	 * 工作办结
	 * @param businessId
	 * @param user
	 * @param taskId
	 * @param option
	 * @return
	 */
	public Result workOver(String businessId, String user, String taskId, String option);
}
