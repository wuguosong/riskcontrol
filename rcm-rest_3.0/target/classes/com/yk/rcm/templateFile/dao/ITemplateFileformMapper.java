package com.yk.rcm.templateFile.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author lyc
 *        模板文件
 *
 */
public interface ITemplateFileformMapper extends BaseMapper {

	/**
	 * 获取模板文件列表
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryTemplateFilesByPage(Map<String, Object> map);

	/**
	 * 新增模板文件
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void addTemplateFile(Map<String, Object> map);

	/**
	 * 修改模板文件
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 */
	public void modifyTemplateFile(Map<String, Object> map);

	/**
	 * 删除模板文件
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteTemplateFile(String[] ids);

	/**
	 * 提交模板文件，提交之后不允许修改
	 * 
	 * @param map
	 *            {@link Map}
	 */
	public void submitTemalateFile(Map<String, Object> map);

	/**
	 * 查询模板文件详情信息
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryTemalateFileInfo(String id);

	/**
	 * 查询模板文件详情信息(查看详情页面)
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> queryRideGuidelineInfoForView(String id);


	public List<Map<String, Object>> queryRiskGuidelinesByPage(
			Map<String, Object> params);

}
