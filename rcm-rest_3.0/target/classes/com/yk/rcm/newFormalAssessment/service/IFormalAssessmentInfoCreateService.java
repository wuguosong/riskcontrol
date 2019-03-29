package com.yk.rcm.newFormalAssessment.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IFormalAssessmentInfoCreateService {
	
	/**
	 * 新增项目
	 * */
	public void createProject(String json);
	
	/**
	 * 修改项目
	 * */
	public void updateProject(String json);
	
	/**
	 * 删除项目
	 * */
	public void deleteProject(String ids);
	
	/**
	 * 查询新增项目
	 * */
	public PageAssistant getNewProjectList(PageAssistant page);
	
	/**
	 * 查询新增项目详情
	 * */
	public Map<String, Object> getProjectByID(String id);
}
