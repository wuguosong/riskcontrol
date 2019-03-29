package com.yk.rcm.decision.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.decision.serevice.IDecisionReviewService;

import common.PageAssistant;
import common.Result;

/**
 * 决策会审阅
 * 
 * @author hubiao
 */
@Controller
@RequestMapping("/decisionReview")
public class DecisionReviewController {

	@Resource
	private IDecisionReviewService decisionReviewService;

	/**
	 * 根据条件查询所有决策会议
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryList")
	@ResponseBody
	public Result queryList(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> decisions = decisionReviewService.queryList();
		result.setResult_data(decisions);
		return result;
	}
	
	/**
	 * 根据查询过会项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryXsh")
	@ResponseBody
	public Result queryXsh(HttpServletRequest request) {
		
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		decisionReviewService.queryXsh(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 根据查询无需过会项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWxsh")
	@ResponseBody
	public Result queryWxsh(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		decisionReviewService.queryWxsh(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 根据条件查询所有决策会议
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/countTodayLater")
	@ResponseBody
	public Result countTodayLater(HttpServletRequest request) {
		Result result = new Result();
		int countTodayLater =  decisionReviewService.countTodayLater();
		result.setResult_data(countTodayLater);
		return result;
	}
	
	/**
	 * 查询所有  待决策会项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWaitDecisionListByPage")
	@ResponseBody
	public Result queryWaitDecisionListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		decisionReviewService.queryWaitDecisionListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 历史决策会数量
	 * @param request
	 * @return
	 */
	@RequestMapping("/countHistoryDecision")
	@ResponseBody
	public Result countHistoryDecision() {
		Result result = new Result();
		int historyDecisionNumber = decisionReviewService.countHistoryDecision();
		result.setResult_data(historyDecisionNumber);
		return result;
	}
	
	/**
	 * 历史决策会数量
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryHistoryDecisionReviewListByPage")
	@ResponseBody
	public Result queryHistoryDecisionReviewListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		decisionReviewService.queryHistoryDecisionReviewListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
}
