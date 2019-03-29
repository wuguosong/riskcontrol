/**
 * 
 */
package com.yk.rcm.noticeofdecision.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.noticeofdecision.service.INoticeDecisionAuditService;

import common.PageAssistant;
import common.Result;

/**
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/noticeDecisionAudit")
public class NoticeDecisionAuditContoller {
	
	@Resource
	private INoticeDecisionAuditService noticeDecisionAuditService;
	
	/**
	 * 保存附件信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateAttachment")
	@ResponseBody
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String attachmentStr = request.getParameter("attachment");
		Document attachment = Document.parse(attachmentStr);
		this.noticeDecisionAuditService.saveAttachmentById(businessId,attachment);
		return result;
	}
	
	/**
	 * 查询已处理
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedLogsById")
	@ResponseBody
	public Result queryAuditedLogs(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		List<Map<String,Object>> logs = this.noticeDecisionAuditService.queryAuditedLogsById(businessId);
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
	public Result queryAuditedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionAuditService.queryAuditedList(page);
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
	public Result queryWaitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionAuditService.queryWaitList(page);
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
		return this.noticeDecisionAuditService.querySingleProcessOptions(businessId);
	}
	
	/**
	 * 提交一个工单
	 * @param businessId
	 * @return
	 */
	@RequestMapping("startSingleFlow.do")
	@ResponseBody
	public Result startSingleFlow(String businessId){
		Result result = this.noticeDecisionAuditService.startSingleFlow(businessId);
		return result;
	}
	
	@RequestMapping("auditSingle.do")
	@ResponseBody
	public Result auditSingle(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		String processOption = request.getParameter("processOption");
		String opinion = request.getParameter("opinion");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("opinion", opinion);
		return this.noticeDecisionAuditService.auditSingle(businessId, processOption, variables);
	}
	
	
	
	
	
}
