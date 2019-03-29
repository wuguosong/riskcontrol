package com.yk.rcm.regulations.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author lyc
 *         规章制度
 *
 */
public interface IRegulationsformService {

	/**
	 * 获取规章制度列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryRegulationsByPage(PageAssistant page);
	
	/**
	 * 获取已提交规章制度列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryRegulationsForSubmit(PageAssistant page);

	/**
	 * 新增规章制度
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String addRegulations(String json);

	/**
	 * 修改规章制度
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String modifyRegulations(String json);

	/**
	 * 提交规章制度，提交之后不允许修改
	 * 
	 * @param id
	 *             ID
	 */
	public void submitRegulations(String id);

	/**
	 * 删除规章制度
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteRegulations(String[] ids);

	/**
	 * 查询规章制度信息(为风险修改页面提供数据)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRegulationsInfo(String id);

	/**
	 * 查询规章制度详情信息(查看详情页面)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRegulationsInfoForView(String id);


}
