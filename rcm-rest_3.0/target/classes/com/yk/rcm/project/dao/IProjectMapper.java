/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author 80845530
 *
 */
public interface IProjectMapper extends BaseMapper{
	
	public void insert(Map<String, Object> params);

	/**
	 * 根据业务id删除
	 * @param businessId
	 */
	public void deleteByBusinessId(@Param("businessId")String businessId);
	/**
	 * 根据id查询
	 * @return
	 */
	public Map<String, Object> queryById(@Param("businessId")String businessId);
	
	/**
	 * 根据id修改
	 * @param params
	 * @return
	 */
	public void updateOracleById(Map<String, Object> params);
}
