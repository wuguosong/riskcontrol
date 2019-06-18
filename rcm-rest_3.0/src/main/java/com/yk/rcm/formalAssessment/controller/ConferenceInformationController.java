/**
 * 
 */
package com.yk.rcm.formalAssessment.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import common.PageAssistant;
import common.Result;

/**
 * @author shaosimin
 * 正式评审会议信息controller
 */
@Controller
@RequestMapping("/information")
public class ConferenceInformationController {
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;

	/**
	 *  查询会议信息列表(未处理)
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryInformationList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询未处理会议信息列表")
	public Result queryInformationList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoService.queryInformationList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询会议信息列表(已处理)
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryInformationListed")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询已处理会议信息列表")
	public Result queryInformationListed(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoService.queryInformationListed(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 新增会议信息(保存到mongo)
	 * @param requedt
	 * @return
	 */
	@RequestMapping("/addConferenceInformation")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "新增会议信息")
	public Result addConferenceInformation(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("information");
		this.formalAssessmentInfoService.addConferenceInformation(json);
		return result;
	}
	/**
	 * 无需上会
	 * @param request
	 * @return
	 */
	@RequestMapping("/noNeedMeeting")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "无需上会")
	public Result noNeedMeeting(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String stage = request.getParameter("stage");
		String need_meeting = request.getParameter("need_meeting");
		String projectType = request.getParameter("projectType");
		this.formalAssessmentInfoService.updateStageById(businessId,stage,need_meeting,projectType);
		return result;
	}

}
