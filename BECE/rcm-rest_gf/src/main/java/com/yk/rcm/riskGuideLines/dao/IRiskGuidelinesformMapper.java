package com.yk.rcm.riskGuideLines.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author lyc
 *         风险指引
 *
 */
public interface IRiskGuidelinesformMapper extends BaseMapper {

	/**
	 * 获取风险指引列表
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryRiskGuidelinesByPage(Map<String, Object> map);

	/**
	 * 新增风险指引
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void addRiskGuideline(Map<String, Object> map);

	/**
	 * 修改风险
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void modifyRiskGuideline(Map<String, Object> map);

	/**
	 * 删除风险案例
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteRiskGuideline(String[] ids);

	/**
	 * 提交风险，提交之后不允许修改
	 * 
	 * @param map
	 *            {@link Map}
	 */
	public void submitRideGuideline(Map<String, Object> map);

	/**
	 * 查询风险详情信息
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRideGuidelineInfo(String id);

	/**
	 * 查询风险详情信息(查看详情页面)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRideGuidelineInfoForView(String id);

	/**
	 * 为个人待办--平台公告板块提供信息
	 * 
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryForIndividualTable();

	/**
	 * 获取个人待办-->更多--公告列表
	 * 
	 * @param map
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryIndividualNotificationList(Map<String, Object> map);

}
