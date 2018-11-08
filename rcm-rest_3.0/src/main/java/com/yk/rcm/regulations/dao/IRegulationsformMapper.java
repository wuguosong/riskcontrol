package com.yk.rcm.regulations.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author lyc
 *        规章制度
 *
 */
public interface IRegulationsformMapper extends BaseMapper {

	/**
	 * 获取规章制度列表
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryRegulationsByPage(Map<String, Object> map);

	/**
	 * 新增规章制度
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void addRegulations(Map<String, Object> map);

	/**
	 * 修改规章制度
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void modifyRegulations(Map<String, Object> map);

	/**
	 * 删除规章制度
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteRegulations(String[] ids);

	/**
	 * 提交规章制度，提交之后不允许修改
	 * 
	 * @param map
	 *            {@link Map}
	 */
	public void submitRegulations(Map<String, Object> map);

	/**
	 * 查询规章制度详情信息
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


	public List<Map<String, Object>> queryRiskGuidelinesByPage(
			Map<String, Object> params);

}
