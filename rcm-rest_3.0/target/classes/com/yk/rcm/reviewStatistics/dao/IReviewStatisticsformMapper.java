package com.yk.rcm.reviewStatistics.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author lyc
 *        评审台账
 *
 */
public interface IReviewStatisticsformMapper extends BaseMapper {
	/**
	 * 查出所有已完成的正式评审信息
	 * @param object
	 * @return
	 */
	public List queryReviewStatistics(Object object);

	/**
	 * 查询已过会和不需过会的正式评审  状态为6 或者 9
	 * @param pageAssistant
	 * @param json
	 */
	public List<Map<String, Object>> queryCompleteFormalReportByPage(
			Map<String, Object> params);

	/**
	 * 查询预期收益率
	 * @param id
	 */
	public Map<String, Object> queryFormalassessmentReport(String id);

	/**
	 * 查询表决结果
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryDecisionResolution(String id);

	/**
	 * 查出所有已完成的投标评审信息
	 * @param object
	 * @return
	 */
	public List<Map<String, Object>> queryTenderReportByPage(
			Map<String, Object> params);

	/**
	 * 查询已过会和不需过会的投标评审  状态为6 或者 9
	 * @param pageAssistant
	 * @param json
	 */
	public List<Map<String, Object>> queryTenderStatistics(Map<String, Object> params);

	
	/**
	 * 查询未过会的投标评审  状态'1','2','3','3.5','3.7','4','5'
	 * @param pageAssistant
	 * @param json
	 */
	public List<Map<String, Object>> queryTenderStatisticsWgh(Map<String, Object> params);

}
