/**
 * 
 */
package com.yk.bpmn.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
public interface IBpmnService {
	/**
	 * 根据key部署流程
	 * @param key
	 * @return
	 */
	public Result deploy(String processKey);
	/**
	 * 直接结束流程
	 * @param bpmnType
	 * @param businessKey
	 * @return
	 */
	public Result stopProcess(String bpmnType, String businessKey);
	/**
	 * 根据任务id查询
	 * @param taskId
	 * @return
	 */
	public Result queryTaskInfoById(String taskId);
	
	public void testData();
	/**
	 * 查询所有评审列表
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryProjectList(Map<String, Object> params);
	/**
	 * 换人
	 * @param flow
	 * @return
	 */
	public Result queryProjectList(String flow);
	/**
	 * 向门户发送待办日志
	 * @param taskId
	 * @param url
	 * @param createDate
	 * @param title
	 * @param owner
	 * @param status
	 * @param sender
	 */
	void sendWaitingToPotal(String taskId, String url, String createDate,
			String title, String owner, String status, String sender);
	/**
	 * 分页查所有运行流程
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryProjectListByPage(PageAssistant params);
	/**
	 * 结束流程
	 * @param type
	 * @param businessId
	 * @param reason
	 * @return 
	 */
	public Result endFlow(String type, String businessId,String reason);
	/**
	 * 获取流程ProcessInstanceId
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> getProcessInstanceId(String businessId);
	/**
	 * 查询节点人员
	 * @param flow
	 * @return
	 */
	public Result getTaskPerson(String flow);
}
