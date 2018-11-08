package com.yk.rcm.meeting.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 拟上会通知
 * @author hubiao
 */
public interface IPreliminaryNoticeMapper extends BaseMapper {

	/**
	 * 拟上会通知列表
	 */
	public List<Map<String, Object>> queryByPage(Map<String, Object> params);

	/**
	 * 拟上会通知详情
	 */
	public Map<String, Object> queryById(@Param("id")String id);

	/**
	 * 拟上会通知用户列表
	 *	noticeId 通知ID
	 *	type 类型
	 */
	public List<Map<String, Object>> queryUserByNoticeIdAndType(@Param("noticeId")String noticeId,@Param("type") String type);
	
	/**
	 *  查询   前几条 拟上会通知
	 * @return
	 */
	public List<Map<String, Object>> queryTop(Map<String, Object> params);

	/**
	 * 拟上会通知审阅列表
	 */
	public List<Map<String, Object>> queryReviewByPage(
			Map<String, Object> params);

	/**
	 * 拟上会通知审阅详情
	 */
	public Map<String, Object> queryReviewById(Map<String, Object> params);

	/**
	 * 更新通知用户审阅状态
	 * @param noticeId 通知ID
	 */
	public void updateUsreReviewStatus(Map<String, Object> param);

	/**
	 * 创建拟上会通知
	 * @param map
	 */
	public void create(Map<String, Object> map);
	
	/**
	 * 通知用户
	 * @param map
	 */
	public void addNoticeUser(@Param("noticeUsers")List<Map<String, Object>> noticeUsers);
	
	/**
	 * 删除拟上会通知
	 * @param map
	 */
	public void deleteById(@Param("id")String id);

	/**
	 * 删除拟上会通知用户
	 * @param map
	 */
	public void deleteUserByNoticeId(@Param("noticeId")String noticeId);

	/**
	 * 更新通知状态
	 * 	ID	   	通知ID
	 * 	STATUS 状态(0:草稿,1:正常,2:过期)
	 * @param map
	 */
	public void updateStatusById(Map<String, Object> map);
}
