package com.yk.rcm.statistic.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author DSL
 *
 */
public interface IDecisionedReportMapper extends BaseMapper {

	/**
	 * 获取各大区域投资金额及投资规模汇总
	 */
	List<Map<String,Object>> queryInvestmentAmountAndScale();

}
