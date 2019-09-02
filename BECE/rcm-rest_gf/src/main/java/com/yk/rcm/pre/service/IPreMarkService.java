package com.yk.rcm.pre.service;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface IPreMarkService {
	/**
	 * 查询评分记录
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryMarks(@Param("businessId") String businessId);
	/**
	 * 存评价记录
	 * @param businessId
	 * @param json
	 */
	void saveOrUpdate(String businessId, String json);
}
