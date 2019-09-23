package com.yk.historyData.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

import common.PageAssistant;

import org.springframework.stereotype.Repository;


/**
 * @author Sunny Qi
 *
 */
@Repository
public interface IHistoryDataMapper extends BaseMapper {
	
	/**
	 * 新增历史数据
	 * */
	public void insert(Map<String, Object> dataForOracle);
	
	/**
	 * 修改历史数据
	 * */
	public void update(Map<String, Object> dataForOracle);
	
	/**
	 * 根据条件获取历史备份表中的数据
	 * */
	public List<Map<String, Object>> getHistoryAlterList(Map<String, Object> params);
	
	/**
	 * 根据条件获取历史数据列表
	 * */
	public List<Map<String, Object>> getHistoryListByPage(Map<String, Object> params);
	
	/**
	 * 根据条件获取单个历史数据
	 * */
	public Map<String, Object> getHistoryById(String id);
	
	/**
	 * 根据条件获取单个历史数据
	 * */
	public Map<String, Object> getHistoryByBusinessId(String id);

	public List<Map<String, Object>> getNewData();
}
