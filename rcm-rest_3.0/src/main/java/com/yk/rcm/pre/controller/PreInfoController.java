package com.yk.rcm.pre.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreInfoService;

import common.PageAssistant;
import common.Result;
/**
 * 预评审基本信息
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/preInfo")
public class PreInfoController {
	@Resource
	private IPreInfoService preInfoService; 
	
	/**
	 * 查询预评审起草列表
	 */
	@RequestMapping("/getPreByID")
	@ResponseBody
	public Result getPreByID(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String,Object> list = this.preInfoService.getPreInfoByID(businessId);
		result.setResult_data(list);
		return result;
	}
	/**
	 * 查询预评审起草列表
	 */
	@RequestMapping("/queryPreList")
	@ResponseBody
	public Result queryPreList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preInfoService.queryPreListByPage(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询评审台账列表
	 */
	@RequestMapping("/queryPageForExport")
	@ResponseBody
	public Result queryPageForExport(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preInfoService.queryPageForExport(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询预评审提交列表
	 */
	@RequestMapping("/queryPreSubmitedList")
	@ResponseBody
	public Result queryPreSubmitedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preInfoService.queryPreSubmitedList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 根据id修改mongo中的测算意见、投资协议意见
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveServiceTypeOpinion")
	@ResponseBody
	public Result updateServiceTypeOpinion(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("serviceTypeOpinion");
		String businessId = request.getParameter("businessId");
		this.preInfoService.saveServiceTypeOpinionByBusinessId(json,businessId);
		return result;
	}
	/**
	 * 保存任务分配信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveTaskPerson")
	@ResponseBody
	public Result saveTaskPerson(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("task");
		this.preInfoService.saveTaskPerson(json);
		return result;
	}
	/**
	 * 保存评审信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveReviewInfo")
	@ResponseBody
	public Result saveReviewInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		this.preInfoService.saveReviewInfo(json,businessId);
		return result;
	}

	/**
	 * 新增附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/addNewAttachment")
	@ResponseBody
	public Result addNewAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.preInfoService.addNewAttachment(json);
		
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
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.preInfoService.updateAttachment(json);
		
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
	public Result deleteAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.preInfoService.deleteAttachment(json);
		return result;
	}
	
	/**
	 * 保存专业评审信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveMajorMemberInfo")
	@ResponseBody
	public Result saveMajorMemberInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		this.preInfoService.saveMajorMemberInfo(businessId,json);
		return result;
	}
	/**
	 * 保存是否需要上会与是否需要提交报告
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveNeedMeetingAndNeedReport")
	@ResponseBody
	public Result saveNeedMeetingAndNeedReport(HttpServletRequest request){
		Result result = new Result();
		String pre = request.getParameter("pre");
		String businessId = request.getParameter("businessId");
		this.preInfoService.saveNeedMeetingAndNeedReport(businessId,pre);
		return result;
	}
}
