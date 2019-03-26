package com.yk.rcm.newFormalAssessment.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;

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
	
	@RequestMapping("/addAttachmengInfoToMongo")
	@ResponseBody
	public Result addAttachmengInfoToMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoCreateService.addNewAttachment(json);
		
		return result;
	}
	@RequestMapping("/deleteAttachmengInfoInMongo")
	@ResponseBody
	public Result deleteAttachmengInfoInMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoCreateService.deleteAttachment(json);
		
		return result;
	}
}
