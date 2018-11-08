package com.yk.rcm.formalAssessment.service;

import java.util.List;
import java.util.Map;
/**
 * 
 * @author yaphet
 * 
 * 2017年6月1日
 *
 */
public interface IFormalAssessmentAuditLogService {
	
	/**
	 * 保存审核日志
	 * @param data
	 * @return
	 */
	public void save(Map<String, Object> data);
	/**
	 * 查询审核日志
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> queryAuditLogs(String businessId);
	/**
	 * 查询下一个排序号，第一个是2
	 * @return
	 */
	public int queryNextOrderNum(String businessId);
	/**
	 * 查询最大排序号，第一个是1
	 * @return
	 */
	public int queryMaxOrderNum(String businessId);
	/**
	 * 更新待处理的那条日志信息
	 * @param data
	 */
	public void updateWaitLog(Map<String, Object> data);
	/**
	 * 是否存在待处理日志
	 * @param params
	 * @return
	 */
	public boolean isExistWaitLog(Map<String, Object> params);
	/**
	 * 根据角色id查询用户
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> queryUsersByRoleId(String roleId);
	/**
	 * 根据businessid和审核人id、executionid删除待办日志
	 * 仅供流程监听使用
	 * @param taskMap
	 */
	public void deleteWaitlogs(Map<String, Object> taskMap);
	/**
	 * 根据businessid和审核人id修改待办日志
	 * 将固定小组成员的待办修改为已办
	 * 仅供流程监听使用
	 * @param taskMap
	 */
	public void updateWaitLogsFormWaitT2FinishedByBusinessId(Map<String, Object> log);
	public void updateWaitingPerson(String taskId , String newUserId);
	/**
	 * 查询待办日志
	 * @param auditUserId
	 * @return
	 */
	List<Map<String, Object>> queryWaitingLogs(String auditUserId);
	/**
	 * 查询待办日志
	 * @param businessId
	 * @return
	 */
	List<Map<String, Object>> queryWaitingLogsById(String businessId);
	/**
	 * 修改审核意见
	 * @param id
	 * @param option
	 */
	public void updateOptionById(String id, String option);
	/**
	 * 查询序号最大已办
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryMaxAuditLogById(String businessId);
	
	public void deleteHuiQianTaskByExecutionId(String executionId);
}
