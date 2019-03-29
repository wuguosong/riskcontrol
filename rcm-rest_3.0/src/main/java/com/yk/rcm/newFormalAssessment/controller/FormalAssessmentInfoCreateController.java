package com.yk.rcm.newFormalAssessment.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;

import common.PageAssistant;
import common.Result;

/**
 * 正式评审新增
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/formalAssessmentInfoCreate")
public class FormalAssessmentInfoCreateController {

	@Resource
	private IFormalAssessmentInfoCreateService formalAssessmentInfoCreateService;
	

	@RequestMapping("/createProject")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		String businessId = this.formalAssessmentInfoCreateService.createProject(json);
		result.setResult_data(businessId);
		return result;
	}
	
	@RequestMapping("/updateProject")
	@ResponseBody
	public Result update(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		this.formalAssessmentInfoCreateService.updateProject(json);
		return result;
	}
	
	@RequestMapping("/deleteProject")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("ids");
		this.formalAssessmentInfoCreateService.deleteProject(json);
		return result;
	}
	
	@RequestMapping("/getNewProjectList")
	@ResponseBody
	public Result getNewProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoCreateService.getNewProjectList(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	@RequestMapping("/getProjectByID")
	@ResponseBody
	public Result getFormalAssessmentByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> projectInfo = this.formalAssessmentInfoCreateService.getProjectByID(id);
		String userId = ThreadLocalUtil.getUserId();
		projectInfo.put("currentUserId", userId);
		result.setResult_data(projectInfo);
		return result;
	}
	
	/**
	 * 新增附件信息到Mongo
	 * @param request
	 * @return
	 */
	@RequestMapping("/addAttachmengInfoToMongo")
	@ResponseBody
	public Result addAttachmengInfoToMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoCreateService.addNewAttachment(json);
		
		return result;
	}
	
	/**
	 * 删除Mongo附件信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/deleteAttachmengInfoInMongo")
	@ResponseBody
	public Result deleteAttachmengInfoInMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoCreateService.deleteAttachment(json);
		
		return result;
	}
	
	/**
	 * 新增会议信息(保存到mongo)
	 * @param request
	 * @return
	 */
	@RequestMapping("/addConferenceInformation")
	@ResponseBody
	public Result addConferenceInformation(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("information");
		String method = request.getParameter("method");
		this.formalAssessmentInfoCreateService.addConferenceInformation(json, method);
		return result;
	}
}
