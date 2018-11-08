package com.yk.rcm.pre.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreAuditReportService;

import common.PageAssistant;
import common.Result;

/**
 * 投资评审报告
 * 
 * @author dsl
 *
 */
@Controller
@RequestMapping("/preAuditReport")
public class PreAuditReportController {

	@Resource
	private IPreAuditReportService preAuditReportService;

	/**
	 * 获取还未新建投标评审报告的项目
	 * 
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotNewlyPreAuditProject")
	@ResponseBody
	public Result queryNotNewlyPreAuditProject(HttpServletRequest request) {
		Result result = new Result();

		List<Map<String, Object>> listMap = this.preAuditReportService.queryNotNewlyPreAuditProject();
		result.setResult_data(listMap);
		return result;
	}

	/**
	 * 查询已新建投资评审报告但未提交报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/queryUncommittedReportByPage")
	@ResponseBody
	public Result queryUncommittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.preAuditReportService.queryUncommittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);
		return result;
	}

	/**
	 * 查询已提交投资评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/querySubmittedReportByPage")
	@ResponseBody
	public Result querySubmittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.preAuditReportService.querySubmittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);
		return result;
	}

	/**
	 * 删除已新建投资评审报告但未提交报告的项目
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/batchDeletePreReport")
	@ResponseBody
	public Result batchDeletePreReport(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] businessids = ids.split(",");
		this.preAuditReportService.batchDeletePreReport(businessids);

		return result;
	}

	/**
	 * 获取新建评审报告项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getPreProjectFormalReviewByID")
	@ResponseBody
	public Result getPreProjectFormalReviewByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document doc = this.preAuditReportService.getPreProjectFormalReviewByID(id);
		result.setResult_data(doc);

		return result;
	}

	/**
	 * 保存评审报告项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/saveReviewReportById")
	@ResponseBody
	public Result saveReviewReportById(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.preAuditReportService.saveReviewReportById(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改报告
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/updateReviewReport")
	@ResponseBody
	public Result updateReviewReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.preAuditReportService.updateReviewReport(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交报告
	 * 
	 * @param businessid
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/submitAndupdate")
	@ResponseBody
	public Result submitAndupdate(String businessid, HttpServletRequest request) {
		Result result = new Result();
		this.preAuditReportService.submitAndupdate(businessid);

		return result;
	}

	/**
	 * 查询项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public Result getById(String id, HttpServletRequest request) {
		Result result = new Result();
		Document document = this.preAuditReportService.getById(id);
		result.setResult_data(document);

		return result;
	}

	/**
	 * 生成word文档
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getPreWordReport")
	@ResponseBody
	public Result getPreWordReport(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = this.preAuditReportService.getPreWordReport(id);
		result.setResult_data(map);

		return result;
	}
	
	/**
	 * 查看报告是否可提交(如果流程未结束，则不可提交报告)
	 * 
	 * @param businessid
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/isPossible2Submit")
	@ResponseBody
	public Result isPossible2Submit(String businessid, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.preAuditReportService.isPossible2Submit(businessid);
		result.setResult_data(flag);

		return result;
	}
	@RequestMapping("/getByBusinessId")
	@ResponseBody
	public Result getByBusinessId(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.preAuditReportService.getOracleByBusinessId(businessId);
		result.setResult_data(map);
		return result;
	}
}
