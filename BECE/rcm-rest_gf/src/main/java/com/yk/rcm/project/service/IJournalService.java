/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.Map;

import common.PageAssistant;

/**
 * 系统错误日志service
 * @author 80845530
 *
 */
public interface IJournalService {
	/**
	 * 保存
	 * @param data
	 */
	public void save(Map<String, Object> data);
	
	public void queryByPage(PageAssistant page);

	public Map<String, Object> queryById(String id);
	
}
