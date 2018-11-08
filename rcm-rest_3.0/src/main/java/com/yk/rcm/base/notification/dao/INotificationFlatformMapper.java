package com.yk.rcm.base.notification.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author Archer<br/>
 *         平台公告
 *
 */
public interface INotificationFlatformMapper extends BaseMapper {

	/**
	 * 获取平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotificationByPage(Map<String, Object> map);

	/**
	 * 新增平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void addNotification(Map<String, Object> map);

	/**
	 * 修改平台公告
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void modifyNotification(Map<String, Object> map);

	/**
	 * 删除平台公告
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteNotification(String[] ids);

	/**
	 * 提交公告，提交之后公告不允许修改
	 * 
	 * @param map
	 *            {@link Map}
	 */
	public void submitNotification(Map<String, Object> map);

	/**
	 * 查询公告详情信息
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
	public List<Map<String, Object>> queryNotifTop(Map<String, Object> map);

	/**
	 * 获取个人待办-->更多--公告列表
	 * 
	 * @param map
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotifByPage(Map<String, Object> map);

}
