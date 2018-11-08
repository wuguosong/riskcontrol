package com.yk.workflow.service;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.bson.Document;

import com.google.gson.JsonArray;
import com.yk.bpmn.entity.TaskInfo;

import common.PageAssistant;
import common.Result;

public interface IWorkflowService {
	
	public void startProcess(String json);
	
	public Result approve(String json);
	
	public Document getProcessDefinitionId(String json);
	
	public InputStream getBpmImg(String json);
	
	public JsonArray getActiveActivityIds(String json);
	
	public JsonArray getProcessInstanceApproveHistory(String json);
	
	public void completeProcess(String json);
	
	public ProcessInstance getProcessInstance(String json);
	
	public List<ActivityImpl> getProcessActivityImpl(String json);
	/**
	 * 根据流程key和业务id查询第一个任务节点信息
	 * @param processKey
	 * @param businessId
	 * @return
	 */
	public TaskInfo getTaskInfo(String processKey, String businessId);

	/**
	 * 获取待办(该处理|审核)总数量
	 * @param userId
	 * @return
	 */
	public int getMyTaskCount(String userId);

	/**
	 * 获取已办(流程没结束)总数量
	 * @param userId
	 * @return
	 */
	public int getCompletedTaskCount(String userId);

	/**
	 * 获取已完成(流程结束[完成或终止])总数量
	 * @param userId
	 * @return
	 */
	public int getOverTaskCount(String userId);

	/**
	 * 分页查询代办任务
	 * @param page
	 * @return
	 */
	public PageAssistant queryMyTaskByPage(PageAssistant page);
	
	/**
	 * 分页查询已办
	 * @param page
	 * @return
	 */
	public PageAssistant queryOverTaskByPage(PageAssistant page);
	
	/**
	 * 分页查询已完成任务
	 * @param page
	 * @return
	 */
	public PageAssistant queryCompletedTaskByPage(PageAssistant page);
	
	/**
	 * 查询我的项目
	 * @param page
	 * @return
	 */
	public PageAssistant queryMyProjectInfoByPage(PageAssistant page);
}
