package com.yk.rcm.bulletin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.bulletin.service.IBulletinReportBoardService;
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
@RequestMapping("/bulletinReportBoard")
public class BulletinReportBoardController {

	@Resource
	private IBulletinReportBoardService bulletinReportBoardService;

	@RequestMapping("/queryInformationListMore")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询信息列表更多")
	public Result queryInformationListMore(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		bulletinReportBoardService.queryBulletinReportBoardByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/queryPreReportBoardList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询信息列表更多")
	public Result queryPreReportBoardList(HttpServletRequest request) {
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
		page = bulletinReportBoardService.queryBulletinReportBoardByPage(page);
		result.setResult_data(page.getList());
		return result;
	}
	
	
	@RequestMapping("/getCounts")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询数量")
	public Result getCounts(HttpServletRequest request) {
		Result result = new Result();
		Map<String,Object> data = this.bulletinReportBoardService.getCounts();
		result.setResult_data(data);
		return result;
	}
}
