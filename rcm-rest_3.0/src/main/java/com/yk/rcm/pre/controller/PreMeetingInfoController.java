/**
 * 
 */
package com.yk.rcm.pre.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreMeetingInfoService;

import common.PageAssistant;
import common.Result;
import util.Util;

/**
 * 投标评审参会信息
 * @author shaosimin
 *
 */
@Controller
@RequestMapping("/preMeetingInfo")
public class PreMeetingInfoController {
	
	@Resource
	private IPreMeetingInfoService  preMeetingInfoService;
	
	/**
	 * 查询参会信息列表(未处理)
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryInformationList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询未处理参会信息列表")
	public Result queryInformationList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preMeetingInfoService.queryInformationList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询参会信息列表(已处理)
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryInformationListed")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询已处理参会信息列表")
	public Result queryInformationListed(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preMeetingInfoService.queryInformationListed(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 新增参会信息(保存到mongo)
	 * @param requedt
	 * @return
	 */
	@RequestMapping("/addMeetingInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.CREATE, description = "新增参会信息")
	public Result addMeetingInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("information");
		this.preMeetingInfoService.addMeetingInfo(json);
		return result;
	}
	/**
	 * 根据businessID查询参会信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryMeetingInfoById")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询参会信息")
	public Result queryMeetingInfoById(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("formalId");
		Map<String, Object> doc = this.preMeetingInfoService.queryMeetingInfoById(id);
		Map<String, Object> meetingInfo = new HashMap<String, Object>();
		if (Util.isNotEmpty(doc.get("meetingInfo"))){
			meetingInfo = (Map<String, Object>) doc.get("meetingInfo");
		}
		result.setResult_data(meetingInfo);
		return result;
	}
	/**
	 * 无需上会
	 * @param request
	 * @return
	 */
	@RequestMapping("/noNeedMeeting")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.UPDATE, description = "无需上会")
	public Result noNeedMeeting(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String stage = request.getParameter("stage");
		String need_meeting = request.getParameter("need_meeting");
		this.preMeetingInfoService.updateStageById(businessId,stage,need_meeting);
		return result;
	}
	/**
	 * 
	 * 无需上会信息列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNotMeetingList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "无需上会信息列表")
	public Result queryNotMeetingList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preMeetingInfoService.queryNotMeetingList(page);
		result.setResult_data(page);
		return result;
	}
	
}
