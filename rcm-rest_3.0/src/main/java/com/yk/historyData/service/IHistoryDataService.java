package com.yk.historyData.service;

import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IHistoryDataService {
	
	/**
	 * 将备份表数据整理后存储到历史表中
	 * @param projectList
	 */
	public void saveOrUpdateHistoryData(String Json) throws Exception;
	
	/**
	 * 获取历史数据列表
	 * @return PageAssistant
	 */
	public PageAssistant getHistoryList(PageAssistant page);
	
	/**
	 * 获取历史数据
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getHistoryById(String id);
	
	/**
	 * 新增附件
	 * @param json
	 */
	public void addNewAttachment(String json);
	
	/**
	 * 删除附件
	 * @param json
	 */
	public void deleteAttachment(String json);
}
