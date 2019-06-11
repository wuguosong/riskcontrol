package com.yk.rcm.newProjectBoard.controller;

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
}
