/**
 * 
 */
package com.yk.rcm.project.service;

import common.PageAssistant;

/**
 * @author shaosimin
 *
 */
public interface IUpdateProjectNameService {
	/**
	 * 查询所有的项目
	 * @param page
	 */
	void queryAllProject(PageAssistant page);
	/**
	 * 修改项目名称
	 * @param businessId 
	 * @param businessId
	 * @param type 
	 */
	void updateProject(String projectName, String businessId, String type);

}
