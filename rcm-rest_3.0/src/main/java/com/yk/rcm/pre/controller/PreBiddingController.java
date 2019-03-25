package com.yk.rcm.pre.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreBiddingService;

import common.PageAssistant;
import common.Result;

@Controller
@RequestMapping("/preBidding")
public class PreBiddingController {

	@Resource
	private IPreBiddingService preBiddingService;

	@RequestMapping("/queryUncommittedByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "页面未提交的查询")
	public Result queryUncommittedByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		preBiddingService.queryUncommittedByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/querySubmittedByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "页面已提交的查询")
	public Result querySubmittedByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		preBiddingService.querySubmittedByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);
		return result;
	}
	
	@RequestMapping("/getByBusinessId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询决策会材料的详情页面")
	public Result getByBusinessId(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = preBiddingService.getByBusinessId(businessId);
		result.setResult_data(map);
		return result;
	}
	
	@RequestMapping("/addPolicyDecision")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.CREATE, description = "修改、提交决策委员会材料")
	public Result addPolicyDecision(String json, String method, HttpServletRequest request) {
		// 提交的判断项目参会信息是否已填写，如没填写禁止提交，并提示先填写参会信息才能够提交
		Result result = new Result();
		Document document = Document.parse(json);
		boolean flag = preBiddingService.addPolicyDecision(document, method);
		result.setResult_data(flag);
		return result;
	}
}
