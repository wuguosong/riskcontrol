/**
 * 
 */
package com.yk.rcm.formalAssessment.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	public Result noNeedMeeting(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String stage = request.getParameter("stage");
		String need_meeting = request.getParameter("need_meeting");
		this.formalAssessmentInfoService.updateStageById(businessId,stage,need_meeting);
		return result;
	}

}
