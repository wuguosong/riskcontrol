package com.yk.bpmn.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IBpmnMapper extends BaseMapper {
	/**
	 * 查询所有评审列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryProjectList(Map<String, Object> params);

	Map<String, Object> getProcInstIdByBusinessId(@Param("businessId")String businessId);

	void updateActTask(@Param("executionId")String executionId, @Param("newUser")String newUser);

	void updateActVariable(@Param("procInstId")String procInstId, @Param("newUser")String newUser, @Param("variableKey")String variableKey);

	List<Map<String, Object>> queryVariableByProcInstId(@Param("procInstId")String procInstId);
	
	Map<String, Object> queryActRuTaskByTaskId(@Param("taskId")String taskId);
	/**
	 * 查询task
	 * @param procInstId
	 * @return
	 */
	List<Map<String, Object>> queryActTaskByProcInstId(@Param("procInstId")String procInstId);
	/**
	 * 查询历史ProcInstId
	 * @param businessId
	 * @return
	 */
	Map<String, Object> getHiProcInstIdByBusinessId(String businessId);
}
