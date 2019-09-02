package com.yk.rcm.pre.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.bpmn.entity.TaskInfo;
import com.yk.rcm.pre.service.IPreAuditLogService;
import com.yk.rcm.pre.service.IPreAuditService;

import common.PageAssistant;
import common.Result;

/**
 * 预评审审核
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/preAudit")
public class PreAuditController {
	@Resource
	private IPreAuditService preAuditService;
	@Resource
	private IPreAuditLogService preAuditLogService;
	/**
	 * 根据业务id查询待办节点信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryTaskInfoByBusinessId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "查询待办节点信息")
	public Result queryTaskInfoByBusinessId(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String processKey = request.getParameter("processKey");
		TaskInfo taskInfo = this.preAuditService.queryTaskInfoByBusinessId(businessId,processKey);
		result.setResult_data(taskInfo);
		return result;
	}
	/**
	 * 查询已处理
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedLogsById")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "查询已处理")
	public Result queryAuditedLogsById(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		List<Map<String,Object>> logs = this.preAuditLogService.queryAuditLogs(businessId);
		result.setResult_data(logs);
		return result;
	}
	/**
	 * 查询流程选项
	 * @param request
	 * @return
	 */
	@RequestMapping("/querySingleProcessOptions")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "查询流程选项")
	public Result querySingleProcessOptions(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		Result querySingleProcessOptions = this.preAuditService.querySingleProcessOptions(businessId);
		return querySingleProcessOptions;
	}
	/**
	 * 提交一个工单
	 * @param businessId
	 * @return
	 */
	@RequestMapping("startSingleFlow.do")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "提交一个工单")
	public Result startSingleFlow(String businessId){
		Result result = this.preAuditService.startSingleFlow(businessId);
		return result;
	}
	
	@RequestMapping("auditSingle.do")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "提交一个工单")
	public Result auditSingle(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String processOption = request.getParameter("processOption");
		String opinion = request.getParameter("opinion");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("opinion", opinion);
		return this.preAuditService.auditSingle(businessId, processOption, variables);
	}
	/**
	 * 查询已处理
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "查询已处理")
	public Result queryAuditedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preAuditService.queryAuditedList(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 查询待处理（草稿，还有未通过终止的，可以重复提交）
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWaitList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.WORKFLOW, description = "查询待处理")
	public Result queryWaitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preAuditService.queryWaitList(page);
		result.setResult_data(page);
		return result;
	}
}
