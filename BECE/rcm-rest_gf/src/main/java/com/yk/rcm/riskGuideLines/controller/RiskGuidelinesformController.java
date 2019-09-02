package com.yk.rcm.riskGuideLines.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.riskGuideLines.service.IRiskGuidelinesformService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author lyc
 *         风险指引
 *
 */

@Controller
@RequestMapping("/riskGuidelinesform")
public class RiskGuidelinesformController {

	@Resource
	private IRiskGuidelinesformService riskGuidelinesformService;

	/**
	 * 获取风险指引列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRiskGuidelines")
	@ResponseBody
	public Result queryRiskGuidelines(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.riskGuidelinesformService.queryRiskGuidelinesByPage(page);
		result.setResult_data(page);

		return result;
	}
	
	/**
	 * 获取已提交风险指引列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRiskGuidelinesForSubmit")
	@ResponseBody
	public Result queryRiskGuidelinesForSubmit(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.riskGuidelinesformService.queryRiskGuidelinesByPageForSubmit(page);
		result.setResult_data(page);

		return result;
	}
	/**
	 * 新增风险指引
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/addRiskGuideline")
	@ResponseBody
	public Result addRiskGuideline(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.riskGuidelinesformService.addRiskGuideline(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改风险指引
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/modifyRiskGuideline")
	@ResponseBody
	public Result modifyNotification(String json, HttpServletRequest request) {
		Result result = new Result();

		String id = this.riskGuidelinesformService.modifyRiskGuideline(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交风险案例，提交之后不允许修改
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/submitRideGuideline")
	@ResponseBody
	public Result submitRideGuideline(String id, HttpServletRequest request) {
		Result result = new Result();
		this.riskGuidelinesformService.submitRideGuideline(id);
		return result;
	}

	/**
	 * 删除风险案例
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/deleteRiskGuideline")
	@ResponseBody
	public Result deleteRiskGuideline(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] idsArr = ids.split(",");
		this.riskGuidelinesformService.deleteRiskGuideline(idsArr);

		return result;
	}

	/**
	 * 查询风险信息(为风险修改页面提供数据)
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRideGuidelineInfo")
	@ResponseBody
	public Result queryRideGuidelineInfo(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.riskGuidelinesformService.queryRideGuidelineInfo(id);
		result.setResult_data(map);
		return result;
	}

	/**
	 * 查询公告详情信息(查看详情页面)
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRideGuidelineInfoForView")
	@ResponseBody
	public Result queryRideGuidelineInfoForView(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.riskGuidelinesformService.queryRideGuidelineInfoForView(id);
		result.setResult_data(map);

		return result;
	}

	/**
	 * 获取个人待办-->更多--公告列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryIndividualNotificationList")
	@ResponseBody
	public Result queryIndividualNotificationList(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.riskGuidelinesformService.queryIndividualNotificationList(page);
		result.setResult_data(page);

		return result;
	}

	/**
	 * 为个人待办--平台公告板块提供信息
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryForIndividualTable")
	@ResponseBody
	public Result queryForIndividualTable(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> map = this.riskGuidelinesformService.queryForIndividualTable();
		result.setResult_data(map);

		return result;
	}
}
