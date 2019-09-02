/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 系统错误日志dao
 * @author 80845530
 *
 */
public interface IJournalMapper extends BaseMapper{
	/**
	 * 新增
	 * @param data
	 */
	public void save(Map<String, Object> data);
	
	public List<Map<String, Object>> queryByPage(Map<String, Object> params);

	public Map<String, Object> queryById(Map<String, Object> params);
}
