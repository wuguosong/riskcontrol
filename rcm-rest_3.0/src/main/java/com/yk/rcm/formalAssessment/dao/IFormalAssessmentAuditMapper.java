package com.yk.rcm.formalAssessment.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface IFormalAssessmentAuditMapper extends BaseMapper {

	List<Map<String, Object>> queryAuditedLogsById(String businessId);

	List<Map<String, Object>> queryAuditedList(Map<String, Object> params);

	List<Map<String, Object>> queryWaitList(Map<String, Object> params);

	void save(Map<String, Object> log);

	int queryMaxOrderNum(String businessId);

	void updateWaitLog(Map<String, Object> data);

	int queryNextOrderNum(String businessId);

	void updateAuditStatusByBusinessId(Map<String, Object> data);

	int isExistWaitLog(Map<String, Object> params);

	List<Map<String, Object>> queryUsersByRoleId(String roleId);

	List<String> queryFirstLevelLawyersIdsByServiceTypeIds(HashMap<String, Object> params);

	void deleteWaitlogs(Map<String, Object> taskMap);

	void updateAuditStageByBusinessId(Map<String, Object> data);

	void updateWaitLogsFormWaitT2FinishedByBusinessId(Map<String, Object> log);

	void updateWaitingPerson(Map<String, Object> data);
	/**
	 * 查询待办日志
	 * @param data
	 * @return
	 */
	List<Map<String, Object>> queryWaitingLogs(Map<String, Object> data);

	List<Map<String, Object>> queryWaitingLogsById(Map<String, Object> data);
	/**
	 * 修改审核意见
	 * @param data
	 */
	void updateOptionById(Map<String, Object> data);
	/**
	 * 查询序号最大已办
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryMaxAuditLogById(String businessId);

	void deleteHuiQianTaskByExecutionId(Map<String, Object> data);

	
}
