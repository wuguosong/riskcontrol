/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 一级法务人员dao
 * @author 80845530
 *
 */
public interface IFirstlevelLawyerMapper extends BaseMapper{
	/**
	 * 新增
	 * @param data
	 */
	public void save(Map<String, Object> data);
	/**
	 * 查询所有
	 * @return
	 */
	public List<Map<String, Object>> queryAll();
	/**
	 * 根据一级业务类型查询
	 * @param query
	 * @return
	 */
	public List<Map<String, Object>> queryByServiceTypes(Map<String, Object> query);
	/**
	 * 根据id更新
	 * @param data
	 */
	public void updateById(Map<String, Object> data);
}
