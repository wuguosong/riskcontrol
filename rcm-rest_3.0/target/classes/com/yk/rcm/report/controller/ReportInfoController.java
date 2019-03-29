package com.yk.rcm.report.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.flow.util.JsonUtil;
import com.yk.rcm.report.service.IReportInfoService;

import common.PageAssistant;
import common.Result;

@Controller
@RequestMapping("/reportInfo")
public class ReportInfoController {
	@Resource
	private IReportInfoService reportInfoService;

	@RequestMapping("/queryPertainAreaAchievement")
	@ResponseBody
	public Result queryPertainAreaAchievement(HttpServletRequest request){
		Result result = new Result();
		List<Map<String, Object>> pertainAreaAchievement = this.reportInfoService.queryPertainAreaAchievement();
		result.setResult_data(pertainAreaAchievement);
		return result;
	}
	
	@RequestMapping("/queryProjectsByPertainareaid")
	@ResponseBody
	public Result queryProjectsByPertainareaid(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.reportInfoService.queryProjectsByPertainareaid(page);
		JsonUtil.toJson(page);
		result.setResult_data(page);
		return result;
	}
	
	
}
