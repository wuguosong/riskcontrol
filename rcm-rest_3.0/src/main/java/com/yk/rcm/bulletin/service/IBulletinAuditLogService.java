/**
 * 
 */
package com.yk.rcm.bulletin.service;

import java.util.List;
import java.util.Map;

/**
 * 通报事项Service
 * @author wufucan
 *
 */
public interface IBulletinAuditLogService {
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
	 * 删除待办日志
	 * @param businessId
	 * @param userId
	 * @param executionId
	 */
	/**
	 * 删除待办日志
	 * @param businessId
	 * @param userId
	 * @param executionId
	 * @param taskId 
	 */
	public void deleteWaitlog(String businessId, String userId,
			String executionId, String taskId);
	/**
	 * 修改待办日志人员信息
	 * @param id
	 * @param newUserId
	 */
	public void updateWaitingPerson(String id,String newUserId);
	/**
	 * 
	 * @param businessId
	 * @param newUserId
	 */
	void updateOldPerson(String id, String newUserId);
	/**
	 * 查询待办日志
	 * @param oldUserId
	 * @return
	 */
	public List<Map<String, Object>> queryWaitingLogs(String auditUserId);
	
	/**
	 * 查询待办日志
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> queryWaitingLogsById(String businessId);
	
	/**
	 * 修改审核人
	 * @param auditUserId
	 * @param id
	 */
	void updateAuditUserIdById(String auditUserId, String id);
	/**
	 * 修改审核意见
	 * @param id
	 * @param option
	 */
	public void updateOptionById(String id, String option);
	/**
	 * 查询流程审批最后一次操作信息
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryLastLogsByDaxt(String businessId);
}
