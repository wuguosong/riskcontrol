package com.yk.rcm.statistic.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.statistic.service.IDecisionedReportService;

import common.Result;

@Controller
@RequestMapping("/decisionedReport")
public class DecisionedReportController {

	@Resource
	private IDecisionedReportService decisionedReportService;

	/**
	 * 获取各大区域的投资金额及投资规模汇总
	 * 
	 * @return Result
	 */
	@RequestMapping("/queryInvestmentAmountAndScale")
	@ResponseBody
	public Result queryInvestmentAmountAndScale() {
		Result result = new Result();
		List<Map<String, Object>> listMap = decisionedReportService.queryInvestmentAmountAndScale();
		result.setResult_data(listMap);
		return result;
	}

}
