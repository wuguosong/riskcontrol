package com.yk.rcm.base.notification.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author Archer<br/>
 *         平台公告
 *
 */
public interface INotificationFlatformService {

	/**
	 * 获取平台公告
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryNotificationByPage(PageAssistant page);

	/**
	 * 新增平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String addNotification(String json);

	/**
	 * 修改平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String modifyNotification(String json);

	/**
	 * 提交公告，提交之后公告不允许修改
	 * 
	 * @param id
	 *            公告 ID
	 */
	public void submitNotification(String id);

	/**
	 * 删除平台公告
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteNotification(String[] ids);

	/**
	 * 查询公告详情信息(为公告修改页面提供数据)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryNotificationInfo(String id);

	/**
	 * 查询公告详情信息(查看详情页面)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryNotificationInfoForView(String id);

	/**
	 * 为个人待办--平台公告板块提供信息
	 * 
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotifTop();

	/**
	 * 获取个人待办-->更多--公告列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 */
	public PageAssistant queryNotifByPage(PageAssistant page);

}
