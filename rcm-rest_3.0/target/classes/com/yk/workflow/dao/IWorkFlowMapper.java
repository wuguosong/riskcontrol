package com.yk.workflow.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IWorkFlowMapper extends BaseMapper{

	List<Map<String,Object>> selectProcessInstanceApproveHistory(Map<String,Object> param);
	List<Map<String,Object>> selectForPortal(Map<String,Object> param);
	
	public int getMyTaskCount(@Param("userId")String userId);
	
	/**
	 * 查询已办  或  已完成的任务
 	 * @param userId		 当前用户
	 * @param isCompleted  1表示查询已完成，反之即是查询  已办
	 * @return
	 */
	public int getOverOrCompletedTaskCount(@Param("userId")String userId,@Param("isCompleted")String isCompleted);

	public List<Map<String,Object>> queryMyTaskByPage(Map<String,Object> param);
	
	/**
	 * 查询已办  或  已完成的任务
 	 * @param params.userId 当前用户
	 * @param params.isCompleted  1表示查询已完成，反之即是查询  已办
	 * @return
	 */
	public List<Map<String, Object>> queryOverOrCompletedTaskByPage(Map<String, Object> params);
	
	/**
	 * 查询我的项目
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryMyProjectInfoByPage(Map<String, Object> params);
}
