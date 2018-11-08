/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * @author shaosimin
 *
 */
public interface IUpdateProjectNameMapper extends BaseMapper{

	/**
	 * 查询所有的项目
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryAllProject(Map<String, Object> params);
	/**
	 * 修改项目名称(其他需决策事项)
	 * @param map
	 */
	void updateBulletin(Map<String, Object> map);
	/**
	 * 修改项目名称(正式评审)
	 * @param map
	 */
	void updateFormal(Map<String, Object> formal);
	/**
	 * 修改项目名称(投标评审)
	 * @param map
	 */
	void updatePre(Map<String, Object> pre);
	
	
}
