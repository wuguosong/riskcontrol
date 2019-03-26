package com.yk.rcm.newPre.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IPreInfoCreateService {
	
	/**
	 * 新增项目
	 * */
	public String createProject(String json);
	
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
	/**
	 * 新增附件
	 * @param json
	 */
	void addNewAttachment(String json);
	/**
	 * 删除附件
	 * @param json
	 */
	void deleteAttachment(String json);
	/**
	 * 替换附件
	 * @param json
	 */
	/*void updateAttachment(String json);*/
}
