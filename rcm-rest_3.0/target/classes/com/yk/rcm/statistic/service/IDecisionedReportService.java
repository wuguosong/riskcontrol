package com.yk.rcm.statistic.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Archer
 *
 */
public interface IDecisionedReportService {

	/**
	 * 获取各大区域投资金额及投资规模汇总
	 */
	List<Map<String, Object>> queryInvestmentAmountAndScale();

}
