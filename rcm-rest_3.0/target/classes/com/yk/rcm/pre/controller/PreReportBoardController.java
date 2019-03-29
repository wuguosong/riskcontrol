package com.yk.rcm.pre.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreReportBoardService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author yaphet <br/>
 *         项目看板:<br/>
 *         投标评审、投标评审
 *
 */

@Controller
@RequestMapping("/preReportBoard")
public class PreReportBoardController {

	@Resource
	private IPreReportBoardService preReportBoardService;

	@RequestMapping("/queryPreReportBoardListMore")
	@ResponseBody
	public Result queryInformationListMore(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		preReportBoardService.queryPreReportBoardByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/queryPreReportBoardList")
	@ResponseBody
	public Result queryInformationList(HttpServletRequest request) {
		Result result = new Result();
		String stage = request.getParameter("stage");
		String wf_state = request.getParameter("wf_state");
		PageAssistant page = new PageAssistant();
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("stage", stage);
		data.put("wf_state", wf_state);
		page.setParamMap(data);
		//申请阶段  前5条数据
		page.setPageSize(5);
		page.setTotalItems(0);
		page = preReportBoardService.queryPreReportBoardByPage(page);
		result.setResult_data(page.getList());
		return result;
	}
	@RequestMapping("/getCounts")
	@ResponseBody
	public Result getCounts(HttpServletRequest request) {
		Result result = new Result();
		Map<String,Object> data = this.preReportBoardService.getCounts();
		result.setResult_data(data);
		return result;
	}
}
