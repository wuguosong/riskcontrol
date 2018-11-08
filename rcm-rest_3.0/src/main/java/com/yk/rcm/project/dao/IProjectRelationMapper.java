/**
 * 
 */
package com.yk.rcm.project.dao;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author 80845530
 *
 */
public interface IProjectRelationMapper extends BaseMapper{
	/**
	 * 根据业务id删除
	 * @param businessId
	 */
	public void deleteByBusinessId(@Param("businessId")String businessId);
}
