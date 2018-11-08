/**
 * 
 */
package com.yk.rcm.project.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.project.service.IUpdateProjectNameService;

import common.PageAssistant;
import common.Result;

/**
 * 修改项目名称controller
 * @author shaosimin
 *
 */
@Controller
@RequestMapping("/updateProjectName")
public class UpdateProjectNameController {
	
	@Resource
	private IUpdateProjectNameService  updateprojectNameService;
	
	/**
	 * 查询所有的项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllProject")
	@ResponseBody
	public Result queryAllProject(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.updateprojectNameService.queryAllProject(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 修改项目名称
	 * @param Request
	 * @return
	 */
	@RequestMapping("/updateProject")
	@ResponseBody
	public Result updateProject(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String type = request.getParameter("type");
		String projectName = request.getParameter("projectName");
		this.updateprojectNameService.updateProject(projectName,businessId,type);
		return result;
	}
}
