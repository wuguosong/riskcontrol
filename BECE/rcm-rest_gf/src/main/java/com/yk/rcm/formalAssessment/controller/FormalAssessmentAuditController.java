package com.yk.rcm.formalAssessment.controller;

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
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;

import common.PageAssistant;
import common.Result;

@Controller
@RequestMapping("/formalAssessmentAudit")
public class FormalAssessmentAuditController {
	@Resource 
	IFormalAssessmentAuditService formalAssessmentAuditService;
	@Resource 
	IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	/**
	 * 保存分配任务人员信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveTaskToMongo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存分配任务人员信息")
	public Result saveTaskToMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("task");
		this.formalAssessmentAuditService.saveTaskToMongo(json);
		return result;
	}
	/**
	 * 根据业务id查询待办节点信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryTaskInfoByBusinessId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "查询代办节点")
	public Result queryTaskInfoByBusinessId(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String processKey = request.getParameter("processKey");
		String taskMark = request.getParameter("taskMark");
		TaskInfo taskInfo = this.formalAssessmentAuditService.queryTaskInfoByBusinessId(businessId,processKey,taskMark);
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "查询已办")
	public Result queryAuditedLogs(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		List<Map<String,Object>> logs = this.formalAssessmentAuditLogService.queryAuditLogs(businessId);
		result.setResult_data(logs);
		return result;
	}
	
	/**
	 * 查询已处理
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "查询已办")
	public Result queryAuditedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentAuditService.queryAuditedList(page);
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "查询代办")
	public Result queryWaitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentAuditService.queryWaitList(page);
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "查询流程选项")
	public Result querySingleProcessOptions(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String taskMark = request.getParameter("taskMark");
		Result querySingleProcessOptions = this.formalAssessmentAuditService.querySingleProcessOptions(businessId,taskMark);
		return querySingleProcessOptions;
	}
	
	/**
	 * 提交一个工单
	 * @param businessId
	 * @return
	 */
	@RequestMapping("startSingleFlow.do")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "流程提交(单个)")
	public Result startSingleFlow(String businessId){
		Result result = this.formalAssessmentAuditService.startSingleFlow(businessId);
		return result;
	}
	
	@RequestMapping("auditSingle.do")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.WORKFLOW, description = "流程提交(单个)")
	public Result auditSingle(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String processOption = request.getParameter("processOption");
		String opinion = request.getParameter("opinion");
		String documentation = request.getParameter("documentation");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("opinion", opinion);
		return this.formalAssessmentAuditService.auditSingle(businessId, processOption, variables ,documentation);
	}
	
	
	/**
	 * 新增附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/addNewAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "新增附件")
	public Result addNewAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.formalAssessmentAuditService.addNewAttachment(json);
		
		return result;
	}
	/**
	 * 替换附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/updateAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "替换附件")
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.formalAssessmentAuditService.updateAttachment(json);
		
		return result;
	}
	/**
	 * 删除附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/deleteAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "删除附件")
	public Result deleteAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentAuditService.deleteAttachment(json);
		return result;
	}
}
