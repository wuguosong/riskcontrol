package com.yk.rcm.formalAssessment.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface IFormalMarkService {
	/**
	 * 查询评分记录
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryMarks(@Param("businessId")String businessId);
	/**
	 * 存评价记录
	 * @param businessId
	 * @param json
	 */
	public void saveOrUpdate(String businessId, String json);
}
