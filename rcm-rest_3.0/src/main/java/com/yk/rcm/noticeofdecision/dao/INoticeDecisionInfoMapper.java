package com.yk.rcm.noticeofdecision.dao;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;
public interface INoticeDecisionInfoMapper extends BaseMapper{

	List<Map<String,Object>> queryStartByPage(Map<String, Object> params);
	
	List<Map<String,Object>> queryOverByPage(Map<String, Object> params);
	
	void save (Map<String, Object> params);

	void update(Map<String, Object> paramsForOracle);

	void delete(Map<String, Object> oracleParams);

	void updateAuditStatusByBusinessId(Map<String, Object> map);

	void updateAfterStartflow(Map<String, Object> map);

	Map<String, Object> queryById(@Param("businessId")String businessId);

	void updateFormalAssessmentStage(Map<String, Object> map);

	void updateDecisionByProjectformalId(@Param("projectformalId")String projectformalId, @Param("decisionResult")int decisionResult);
	
	Map<String, Object> queryByProjectformalId(@Param("projectformalId")String projectformalId);
}
