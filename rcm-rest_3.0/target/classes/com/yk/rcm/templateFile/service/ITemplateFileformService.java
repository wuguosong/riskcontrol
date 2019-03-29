package com.yk.rcm.templateFile.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author lyc
 *         模板文件
 *
 */
public interface ITemplateFileformService {

	/**
	 * 获取模板文件列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryTemplateFilesByPage(PageAssistant page);
	
	/**
	 * 获取已提交模板文件列表
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryRiskGuidelinesForSubmit(PageAssistant page);

	/**
	 * 新增模板文件
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String addTemplateFile(String json);

	/**
	 * 修改模板文件
	 * 
	 * @param map
	 *            Map&lt;String,Object&gt;
	 * @return id
	 */
	public String modifyTemplateFile(String json);

	/**
	 * 提交模板文件，提交之后不允许修改
	 * 
	 * @param id
	 *             ID
	 */
	public void submitTemalateFile(String id);

	/**
	 * 删除模板文件
	 * 
	 * @param ids
	 *            id
	 */
	public void deleteTemplateFile(String[] ids);

	/**
	 * 查询模板文件信息(为风险修改页面提供数据)
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


}
