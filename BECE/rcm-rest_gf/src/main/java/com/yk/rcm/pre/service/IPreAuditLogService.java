package com.yk.rcm.pre.service;

import java.util.List;
import java.util.Map;

public interface IPreAuditLogService {
	/**
	 * 查询审核日志
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> queryAuditLogs(String businessId);
	/**
	 * 删除待办日志
	 * @param businessId
	 * @param userId
	 * @param executionId
	 */
	public void deleteWaitlog(String businessId, String userId, String executionId);
	/**
	 * 保存审核日志
	 * @param data
	 * @return
	 */
	public void save(Map<String, Object> data);
	/**
	 * 查询最大排序号，第一个是1
	 * @return
	 */
	public int queryMaxOrderNum(String businessId);
	/**
	 * 根据角色id查询用户
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> queryUsersByRoleId(String roleId);
	
	public void updateWaitingPerson(String id ,String newUserId);
	
	public List<Map<String, Object>> queryWaitingLogs(String auditUserId);
	
	public List<Map<String, Object>> queryWaitingLogsById(String businessId);
	/**
	 * 查询序号最大已办
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryMaxAuditLogById(String businessId);
	/**
	 * 修改日志意见
	 * @param string
	 * @param reason
	 */
	public void updateOptionById(String id, String option);
	
	/**
	 * 根据executionid查询父id删除所有待办
	 * @param executionId
	 */
	public void deleteHuiQianTaskByExecutionId(String executionId);
}
