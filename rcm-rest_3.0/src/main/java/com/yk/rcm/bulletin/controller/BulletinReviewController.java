/**
 * 
 */
package com.yk.rcm.bulletin.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.bulletin.service.IBulletinReviewService;
import common.PageAssistant;
import common.Result;

/**
 * @author wufucan
 * 通报模块controller
 */
@Controller
@RequestMapping("/bulletinReview")
public class BulletinReviewController {
		
	@Resource
	private IBulletinReviewService bulletinReviewService;

	/**
	 * 查询待审阅
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWaitList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.WORKFLOW, description = "查询代办")
	public Result queryWaitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinReviewService.queryWaitList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询已审阅
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuditedList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.WORKFLOW, description = "查询已办")
	public Result queryAuditedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinReviewService.queryAuditedList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 列表页面数据初始化
	 * @return
	 */
	@RequestMapping("/queryListDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "列表页面数据初始化")
	public Result queryListDefaultInfo(){
		Result result = new Result();
		Map<String, Object> map = this.bulletinReviewService.queryListDefaultInfo();
		result.setResult_data(map);
		return result;
	}
	/**
	 * 审阅详情页面数据初始化
	 * @return
	 */
	@RequestMapping("/queryViewDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "审阅详情页面数据初始化")
	public Result queryViewDefaultInfo(String businessId){
		Result result = new Result();
		Map<String, Object> map = this.bulletinReviewService.queryViewDefaultInfo(businessId);
		result.setResult_data(map);
		return result;
	}
}
