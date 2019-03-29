package com.yk.rcm.riskGuideLines.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author lyc
 *         风险指引
 *
 */
public interface IRiskGuidelinesformService {

	/**
	 * 获取风险指引列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryRiskGuidelinesByPage(PageAssistant page);
	
	/**
	 * 获取已提交风险指引列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryRiskGuidelinesByPageForSubmit(PageAssistant page);

	/**
	 * 新增风险指引
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String addRiskGuideline(String json);

	/**
	 * 修改平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String modifyRiskGuideline(String json);

	/**
	 * 提交风险，提交之后不允许修改
	 * 
	 * @param id
	 *            风险 ID
	 */
	public void submitRideGuideline(String id);

	/**
	 * 删除风险案例
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteRiskGuideline(String[] ids);

	/**
	 * 查询风险详情信息(为风险修改页面提供数据)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRideGuidelineInfo(String id);

	/**
	 * 查询公告详情信息(查看详情页面)
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
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 */
	public PageAssistant queryIndividualNotificationList(PageAssistant page);

}
