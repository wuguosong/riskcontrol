package com.yk.rcm.formalAssessment.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IFormalMarkMapper extends BaseMapper {
	/**
	 * 查询评价
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryMarks(@Param("businessId")String businessId);
	/**
	 * 新增记录
	 * @param data
	 */
	void save(Map<String, Object> data);
	/**
	 * 修改记录
	 * @param data
	 */
	void update(Map<String, Object> data);
}
