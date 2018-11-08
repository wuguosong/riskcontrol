package com.yk.rcm.reviewStatistics.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.reviewStatistics.service.IReviewStatisticsformService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author 评审台账
 *         规章制度
 *
 */

@Controller
@RequestMapping("/reviewStatisticsform")
public class ReviewStatisticsformController {

	@Resource
	private IReviewStatisticsformService reviewStatisticsformService;
	
	@RequestMapping("/queryCompleteFormalReportByPage")
	@ResponseBody
	public Result queryCompleteFormalReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.reviewStatisticsformService.queryCompleteFormalReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}
	
	/**
	 * 导出其他决策事项台账
	 * @param request
	 * @return
	 */
	@RequestMapping("/exportBulletinReportInfo")
	@ResponseBody
	public Result exportBulletinReportInfo(HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = null;
		map = this.reviewStatisticsformService.exportBulletinReportInfo(request);
		result.setResult_data(map);
		return result;
	}
	
	
	
	@RequestMapping("/queryTenderReportByPage")
	@ResponseBody
	public Result queryTenderReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.reviewStatisticsformService.queryTenderReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}
	
	/**
	 * 导出投标评审台账
	 * @param request
	 * @return
	 */
	@RequestMapping("/exportPreInfo")
	@ResponseBody
	public Result exportPreInfo(HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = null;
		map = this.reviewStatisticsformService.exportPreInfo(request);
		result.setResult_data(map);
		return result;
	}
	
	
	/**
	 * 导出未过会投标评审台账
	 * @param request
	 * @return
	 */
	@RequestMapping("/exportPreReportInfo")
	@ResponseBody
	public Result exportPreReportInfo(HttpServletRequest request) {
		Result result = this.reviewStatisticsformService.exportPreReportInfo(request);
		return result;
	}
	
	/**
	 * 导出正式评审台账(跟进中)
	 * @param request
	 * @return
	 */
	@RequestMapping("/exportFormalReportInfo")
	@ResponseBody
	public Result exportFormalReportInfo(HttpServletRequest request) {
		Result result = this.reviewStatisticsformService.exportFormalReportInfo(request);
		return result;
	}
	
	
}
