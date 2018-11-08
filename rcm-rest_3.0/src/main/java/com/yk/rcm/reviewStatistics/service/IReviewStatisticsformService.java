package com.yk.rcm.reviewStatistics.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author lyc
 *         评审台账
 *
 */
public interface IReviewStatisticsformService {


	/**
	 * 查询已过会和不需过会的正式评审  状态为6 或者 9
	 * @param pageAssistant
	 * @param json
	 */
	public void queryCompleteFormalReportByPage(PageAssistant pageAssistant,
			String json);

	
	/**
	 * 导出正式评审台账
	 * @return
	 */
	public Result exportFormalReportInfo(HttpServletRequest request);


	/**
	 * 查询已过会和不需过会的投标评审  状态为6 或者 9
	 * @param pageAssistant
	 * @param json
	 */
	public void queryTenderReportByPage(PageAssistant pageAssistant, String json);


	/**
	 * 导出已过会投标评审
	 * @param request
	 * @return
	 */
	public Map<String, String> exportPreInfo(HttpServletRequest request);

	/**
	 * 导出投标评审
	 * @param request
	 * @return
	 */
	public Result exportPreReportInfo(HttpServletRequest request);

	
	/**
	 * 导出其他决策事项台账
	 * @param request
	 * @return
	 */
	public Map<String, String> exportBulletinReportInfo(
			HttpServletRequest request);


}
