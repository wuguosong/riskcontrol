package com.yk.rcm.formalAssessment.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IFormalReportBoardService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author dsl <br/>
 *         项目看板:<br/>
 *         投标评审、正式评审
 *
 */

@Controller
@RequestMapping("/formalReportBoard")
public class FormalReportBoardController {

	@Resource
	private IFormalReportBoardService formalReportBoardService;

	@RequestMapping("/queryFormalReportBoardListMore")
	@ResponseBody
	public Result queryInformationListMore(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		formalReportBoardService.queryFormalReportBoardByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/queryFormalReportBoardList")
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
		page = formalReportBoardService.queryFormalReportBoardByPage(page);
		result.setResult_data(page.getList());
		return result;
	}
	@RequestMapping("/getCounts")
	@ResponseBody
	public Result getCounts(HttpServletRequest request) {
		Result result = new Result();
		Map<String,Object> data = this.formalReportBoardService.getCounts();
		result.setResult_data(data);
		return result;
	}
}
