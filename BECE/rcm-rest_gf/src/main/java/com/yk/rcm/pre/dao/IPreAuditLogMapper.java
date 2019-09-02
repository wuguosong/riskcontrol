package com.yk.rcm.pre.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface IPreAuditLogMapper extends BaseMapper{

	List<Map<String, Object>> queryAuditedLogsById(String businessId);

	void save(Map<String, Object> data);

	void deleteWaitlogs(HashMap<String, Object> taskMap);

	int queryMaxOrderNum(String businessId);

	List<Map<String, Object>> queryUsersByRoleId(String roleId);

	void updateWaitingPerson(Map<String, Object> data);
	/**
	 * 查询待办日志
	 * @param data
	 * @return
	 */
	List<Map<String, Object>> queryWaitingLogs(Map<String, Object> data);
	
	List<Map<String, Object>> queryWaitingLogsById(Map<String, Object> data);
	/**
	 * 查询序号最大已办
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryMaxAuditLogById(String businessId);
	
	/**
	 * 修改审核意见
	 * @param data
	 */
	void updateOptionById(Map<String, Object> data);
	/**
	 * 根据executionid查询父id删除所有待办
	 * @param data
	 */
	void deleteHuiQianTaskByExecutionId(Map<String, Object> data);
	
}
