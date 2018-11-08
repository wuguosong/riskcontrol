package com.yk.rcm.statistic.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yk.rcm.statistic.dao.IDecisionedReportMapper;
import com.yk.rcm.statistic.service.IDecisionedReportService;

@Service
public class DecisionedReportService implements IDecisionedReportService {

	@Resource
	private IDecisionedReportMapper decisionedReportMapper;

	@Override
	public List<Map<String, Object>> queryInvestmentAmountAndScale() {
		List<Map<String, Object>> list = decisionedReportMapper.queryInvestmentAmountAndScale();
		return list ;
	}

}
