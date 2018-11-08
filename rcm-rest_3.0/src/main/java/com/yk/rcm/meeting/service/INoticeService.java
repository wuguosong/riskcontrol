package com.yk.rcm.meeting.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 会议通知
 * @author hubiao
 */
public interface INoticeService {

	/**
	 * 添加通知
	 * 	1:其中key为noticeUsers 表示 通知的用户字段,数据格式noticeUsers[{value=用户ID},{value=用户2ID}]
	 *  2:如果会有重复添加,请先调用  deleteByRelationId(relationId)或 delete(noticeId) 进行清空
	 * @param notice
	 */
	public void add(Map<String, Object> notice);

	public void delete(String noticeId);
	
	public void deleteByRelationId(String relationId);
	
	/**
	 * 根据关联ID查询第一个通知详情信息
	 * @param relationId
	 * @return
	 */
	public Map<String, Object> getByRelationId(String relationId);
	
	/**
	 * 根据关联ID查询所有通知详情信息
	 * @param relationId
	 * @return
	 */
	public List<Map<String,Object>> queryByRelationId(String relationId);

	/**
	 * 分页查询数据
	 * @param notice
	 * @param limit
	 */
	public void queryListByPage(PageAssistant page);
	
	/**
	 * 查询前几条数据
	 * @param notice
	 * @param limit
	 */
	public List<Map<String, Object>> queryListTopLimit(Map<String, Object> notice);

	/**
	 * 根据条件查询详情
	 * @param notice
	 * 	notice.id 通知ID(必填)
	 * @return
	 */
	public Map<String, Object> queryInfo(Map<String, Object> notice);
}
