package com.yk.rcm.meeting.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 拟上会通知
 * @author hubiao
 */
public interface IPreliminaryNoticeService {

	/**
	 * 拟上会通知列表
	 */
	public void queryByPage(PageAssistant page);

	/**
	 * 拟上会通知详情
	 */
	public Map<String, Object> queryById(String id);

	
	/**
	 *  查询   前几条 拟上会通知
	 * @return
	 */
	public List<Map<String, Object>> queryTop();

	/**
	 * 拟上会通知审阅列表
	 */
	public void queryReviewByPage(PageAssistant page);

	/**
	 * 拟上会通知审阅详情
	 */
	public Map<String, Object> queryReviewById(String id);

	/**
	 * 更新通知用户审阅状态
	 * @param noticeId 通知ID
	 */
	public void updateUsreReviewStatus(String noticeId);

	/**
	 * 创建
	 * @param map
	 * @return
	 */
	public String save(Map<String, Object> map);

	/**
	 * 更新
	 * @param map
	 * @return
	 */
	public String update(Map<String, Object> map);

	/**
	 * 删除通知信息，关联删除通知用户
	 * @param id
	 */
	public void deleteById(String id);
	
	/**
	 * 更新通知状态
	 * 	ID	   	通知ID
	 * 	STATUS 状态(0:草稿,1:正常,2:过期)
	 * @param map
	 */
	public void updateStatusById(Map<String, Object> map);
}
