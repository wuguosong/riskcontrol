/**
 * 
 */
package com.yk.rcm.bulletin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.bpmn.entity.TaskInfo;
import com.yk.rcm.bulletin.service.IBulletinAuditService;

import common.PageAssistant;
import common.Result;

/**
 * @author wufucan
 * 通报模块controller
 */
@Controller
@RequestMapping("/bulletinAudit")
public class BulletinAuditController {
	@Resource
	private IBulletinAuditService bulletinAuditService;

	
	/**
	 * 根据业务id查询待办节点信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryTaskInfoByBusinessId")
	@ResponseBody
	public Result queryTaskInfoByBusinessId(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String processKey = request.getParameter("processKey");
		TaskInfo taskInfo = this.bulletinAuditService.queryTaskInfoByBusinessId(businessId,processKey);
		result.setResult_data(taskInfo);
		return result;
	}
	
	/**
	 * 提交一个工单
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/startSingleFlow")
	@ResponseBody
	public Result startSingleFlow(String businessId){
		Result result = this.bulletinAuditService.startSingleFlow(businessId);
		return result;
	}
	/**
	 * 提交一批工单
	 * @param ids
	 * @return
	 */
	@RequestMapping("/startBatchFlow")
	@ResponseBody
	public Result startBatchFlow(String ids){
		String[] idsArr = ids.split(",");
		Result result = this.bulletinAuditService.startBatchFlow(idsArr);
		return result;
	}
	
	
	/**
	 * 查询待处理（草稿，还有未通过终止的，可以重复提交）
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWaitList")
	@ResponseBody
	public Result queryWaitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinAuditService.queryWaitList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询已处理
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedList")
	@ResponseBody
	public Result queryAuditedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinAuditService.queryAuditedList(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 查询流程选项
	 * @param request
	 * @return
	 */
	@RequestMapping("/querySingleProcessOptions")
	@ResponseBody
	public Result querySingleProcessOptions(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		return this.bulletinAuditService.querySingleProcessOptions(businessId);
	}
	
	/**
	 * 处理单个流程
	 * @param request
	 * @return
	 */
	@RequestMapping("/auditSingle")
	@ResponseBody
	public Result auditSingle(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String processOption = request.getParameter("processOption");
		String opinion = request.getParameter("opinion");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("opinion", opinion);
		return this.bulletinAuditService.auditSingle(businessId, processOption, variables);
	}
	/**
	 * 转办工作
	 * @param request
	 * @return
	 */
	@RequestMapping("/changeWork")
	@ResponseBody
	public Result changeWork(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String user = request.getParameter("user");
		String taskId = request.getParameter("taskId");
		String option = request.getParameter("option");
		return this.bulletinAuditService.changeWork(businessId, user,taskId,option);
	}
	/**
	 * 工作办结
	 * @param request
	 * @return
	 */
	@RequestMapping("/workOver")
	@ResponseBody
	public Result workOver(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String oldUserId = request.getParameter("oldUser");
		String taskId = request.getParameter("taskId");
		String option = request.getParameter("option");
		return this.bulletinAuditService.workOver(businessId, oldUserId,taskId,option);
	}
}
