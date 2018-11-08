package com.yk.rcm.decision.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IDecisionMapper extends BaseMapper {

	public List<Map<String, Object>> queryList(Map<String, Object> params);

	public void updateVoteStatus(@Param("id") String id, @Param("status") int status);

	public Map<String, Object> getCurrUnderwayProject();

	public int isCurrUnderwayProject();

	public Map<String, Object> queryById(@Param("id") String id);

	public void updateDecisionResult(@Param("id") String id,@Param("decisionDate")Date decisionDate, @Param("decisionResult") int decisionResult);

	public List<Map<String, Object>> queryHistory(@Param("decisionDate") String decisionDate);

	public void cancelDecision();
	
	public void insertBeforeDelete(@Param("decision") Map<String, Object> decision);

	public void insert(@Param("decision") Map<String, Object> decision);

	public Map<String, Object> queryByBusinessId(@Param("businessId") String businessId);

	public List<Map<String, Object>> isDecisionComplete(@Param("meetingTime") String meetingTime);

	public void resetDecision(@Param("id") String id);
	
	public List<Map<String, Object>> queryByIds(@Param("projectArray")List<Map<String,Object>> projectArray);
	
	/**
	 * 查询指定会议日期是否还有	未表决或需要表决  的项目
	 * @param meetingTime
	 * @return
	 */
	public int countNeedDecisionProject(@Param("meetingTime") Date meetingTime);
}
