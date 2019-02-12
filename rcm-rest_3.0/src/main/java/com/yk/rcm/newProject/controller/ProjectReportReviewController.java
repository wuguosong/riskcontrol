package com.yk.rcm.newProject.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.newProject.service.IProjectReportReviewService;

import common.PageAssistant;
import common.Result;

/**
 * 项目报告
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/projectReport")
public class ProjectReportReviewController {

	@Resource
	private IProjectReportReviewService projectReportReviewService;
	
	/**
	 * 项目报告数量
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryProjectReportListByPage")
	@ResponseBody
	public Result queryHistoryDecisionReviewListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		projectReportReviewService.queryProjectReportListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
}
