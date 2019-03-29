package com.yk.workflow.controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;
import util.Util;

import com.google.gson.JsonArray;
import com.yk.bpmn.entity.TaskInfo;
import com.yk.exception.BusinessException;
import com.yk.workflow.service.IWorkflowService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author yaphet
 * 
 * 2017年5月22日
 *
 */
@RequestMapping("/workFlow")
@Controller
public class WorkFlowController {
	
	@Resource
	private IWorkflowService workflowService;
	
	
	@RequestMapping("startProcess")
	@ResponseBody
	public Result startProcess(HttpServletRequest request){
		String json = request.getParameter("param");
		this.workflowService.startProcess(json);
		return new Result();
	}
	
	@RequestMapping("approve")
	@ResponseBody
	public Result approve(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = this.workflowService.approve(json);
		return result;
	}
	
	@RequestMapping("getProcessDefinitionId")
	@ResponseBody
	public Result getProcessDefinitionId(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		Document processDefinitionId = this.workflowService.getProcessDefinitionId(json);
		result.setResult_data(processDefinitionId);
		return result;
	}
	
	@RequestMapping("getBpmImg")
	public void getBpmImg(HttpServletRequest request,HttpServletResponse response){
		String json = request.getParameter("param");
		InputStream bpmImg = this.workflowService.getBpmImg(json);
		
		InputStream in = (InputStream) bpmImg;
		try {
			OutputStream out = response.getOutputStream();
			// Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			if(in != null) in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new BusinessException("下载流程图时出错", e);
		}
	}
	
	@RequestMapping("getActiveActivityIds")
	@ResponseBody
	public Result getActiveActivityIds(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		JsonArray activeActivityIds = this.workflowService.getActiveActivityIds(json);
		result.setResult_data(activeActivityIds);
		return result;
	}
	
	/**
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryOverTaskByPage")
	@ResponseBody
	public Result queryOverTaskByPage(String page){
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		Map<String, Object> paramMap = pageAssistant.getParamMap();
		if(null == paramMap){
			pageAssistant.setParamMap(new HashMap<String, Object>());
		}
		pageAssistant.getParamMap().put("userId", ThreadLocalUtil.getUserId());
		workflowService.queryOverTaskByPage(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}
	
	/**
	 * 获取已办任务获取已完成任务
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCompletedTaskByPage")
	@ResponseBody
	public Result queryCompletedTaskByPage(String page){
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		Map<String, Object> paramMap = pageAssistant.getParamMap();
		if(null == paramMap){
			pageAssistant.setParamMap(new HashMap<String, Object>());
		}
		pageAssistant.getParamMap().put("userId", ThreadLocalUtil.getUserId());
		workflowService.queryCompletedTaskByPage(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}
	
	/**
	 * 获取待办任务
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryMyTaskByPage")
	@ResponseBody
	public Result queryMyTaskByPage(String page){
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		Map<String, Object> paramMap = pageAssistant.getParamMap();
		if(null == paramMap){
			pageAssistant.setParamMap(new HashMap<String, Object>());
		}
		pageAssistant.getParamMap().put("userId", ThreadLocalUtil.getUserId());
		workflowService.queryMyTaskByPage(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}
	
	@RequestMapping("getProcessInstanceApproveHistory")
	@ResponseBody
	public Result getProcessInstanceApproveHistory(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		 JsonArray processInstanceApproveHistory = this.workflowService.getProcessInstanceApproveHistory(json);
		result.setResult_data(processInstanceApproveHistory);
		return result;
	}
	@RequestMapping("completeProcess")
	@ResponseBody
	public Result completeProcess(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		this.workflowService.completeProcess(json);
		return result;
	}
	@RequestMapping("getProcessInstance")
	@ResponseBody
	public Result getProcessInstance(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		ProcessInstance processInstance = this.workflowService.getProcessInstance(json);
		result.setResult_data(processInstance);
		return result;
	}
	@RequestMapping("getProcessActivityImpl")
	@ResponseBody
	public Result getProcessActivityImpl(HttpServletRequest request){
		String json = request.getParameter("param");
		Result result = new Result();
		List<ActivityImpl> processActivityImpl = this.workflowService.getProcessActivityImpl(json);
		result.setResult_data(processActivityImpl);
		return result;
	}
	/**
	 * 根据流程key和业务id查询第一个任务节点信息
	 * @param processKey
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/getTaskInfo")
	@ResponseBody
	public TaskInfo getTaskInfo(String processKey, String businessId){
		return this.workflowService.getTaskInfo(processKey, businessId);
	}
	
	/**
	 * 初始化  当前用户的任务管理信息 
	 * @param request
	 * @return
	 */
	@RequestMapping("/initializeCurrUserTaskInfo")
	@ResponseBody
	public Result initializeCurrUserTaskInfo(){
		Result result = new Result();
		Map<String,Object> data = new HashMap<String,Object>();

		//获取当前登录用户
		String userId = ThreadLocalUtil.getUserId();
		
		//获取待办(该处理|审核)总数量
		int myTaskCount = workflowService.getMyTaskCount(userId);
		data.put("myTaskCount", myTaskCount);
		
		//获取已办(流程没结束)总数量
		int overTaskCount = workflowService.getOverTaskCount(userId);
		data.put("overTaskCount", overTaskCount);
//		
//		//获取已完成(流程结束[完成或终止])总数量
//		int completedTaskCount = workflowService.getCompletedTaskCount(userId);
//		data.put("completedTaskCount", completedTaskCount);
		
		//获取待办  前5条数据
		PageAssistant page = new PageAssistant();
		page.getParamMap().put("userId", userId);
		page.setPageSize(5);
		page.setTotalItems(0);
		page = workflowService.queryMyTaskByPage(page);
		data.put("myTaskList", page.getList());
		
		//获取已办(流程没结束)  前5条数据
		page.setList(null);
		page.setTotalItems(0);
		page = workflowService.queryOverTaskByPage(page);
		data.put("overTaskList", page.getList());
		//当前时间
		data.put("now", Util.now());
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		data.put("week", dateFm.format(Util.now()));
		
		
		//获取已完成(流程结束[完成或终止])  前5条数据
//		page.setList(null);
//		page.setTotalItems(0);
//		page = workflowService.queryCompletedTaskByPage(page);
//		data.put("completedTaskList", page.getList());
		
		result.setResult_data(data);
		return result;
	}
	public static void main(String[] args) {
		  Date date=new Date();
	}
	
	@RequestMapping("queryMyProjectInfoTop")
	@ResponseBody
	public Result queryMyProjectInfoTop(){
		PageAssistant page = new PageAssistant();
		page.setPageSize(5);
		page.setTotalItems(0);
		
		Result result = new Result();
		page = workflowService.queryMyProjectInfoByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("queryMyProjectInfoByPage")
	@ResponseBody
	public Result queryMyProjectInfoByPage(String page){
		PageAssistant pageAssistant = new PageAssistant(page);
		
		Result result = new Result();
		pageAssistant = workflowService.queryMyProjectInfoByPage(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}
}
