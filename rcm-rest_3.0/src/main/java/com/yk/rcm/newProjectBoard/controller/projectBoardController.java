package com.yk.rcm.newProjectBoard.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.newProjectBoard.service.IProjectBoardService;

import common.PageAssistant;
import common.Result;

/**
 * 项目看板
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/projectBoard")
public class projectBoardController {

	@Resource
	private IProjectBoardService projectBoardService;
	

	@RequestMapping("/getProjectList")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.projectBoardService.getProjectList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getPfrProjectList")
	@ResponseBody
	public Result getPfrProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.projectBoardService.getPfrProjectList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getPreProjectList")
	@ResponseBody
	public Result getPreProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.projectBoardService.getPreProjectList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getBulletinProjectList")
	@ResponseBody
	public Result getBulletinProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.projectBoardService.getBulletinProjectList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getProjectListForCompanyHead")
	@ResponseBody
	public Result getProjectListForCompanyHead(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.projectBoardService.getProjectListForCompanyHead(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getProjectCodeSame")
	@ResponseBody
	public Result getProjectCodeSame(HttpServletRequest request){
		Result result = new Result();
		String PROJECTCODE = request.getParameter("PROJECTCODE");
		String BUSINESSID = request.getParameter("BUSINESSID");
		List<Map<String, Object>> projectCodeSame = this.projectBoardService.getProjectCodeSame(PROJECTCODE, BUSINESSID);
		result.setResult_data(projectCodeSame);
		return result;
	}
}
