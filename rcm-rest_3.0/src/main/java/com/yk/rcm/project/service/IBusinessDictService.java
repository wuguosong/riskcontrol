/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.List;
import java.util.Map;

/**
 * 决策项目类型service
 * @author 80845530
 *
 */
public interface IBusinessDictService {
	
	/**
	 * 查询决策项目类型
	 * @param page
	 */
	public List<Map<String, Object>> queryBusinessType();
	
	
}
