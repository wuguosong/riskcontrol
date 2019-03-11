/**
 * 
 */
package com.yk.rcm.bulletin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author wufucan
 *
 */
@Repository
public interface IBulletinAuditMapper extends BaseMapper {
	/**
	 * 保存
	 * @param data
	 */
	public void save(Map<String, Object> data);
	/**
	 * 查询
	 * @return
	 */
	public List<Map<String, Object>> queryAuditLogs(@Param("businessId")String businessId);
	/**
	 * 查询业务审核角色
	 * @param businessId
	 * @return
	 */
	public String queryBusinessRole(@Param("businessId")String businessId);
	/**
	 * 查询下一个排序号，第一个是1
	 * @return
	 */
	public int queryNextOrderNum(@Param("businessId")String businessId);
	/**
	 * 查询最大排序号，第一个是1
	 * @return
	 */
	public int queryMaxOrderNum(@Param("businessId")String businessId);
	/**
	 * 更新待处理的那条日志信息
	 * @param data
	 */
	public void updateWaitLog(Map<String, Object> data);
	/**
	 * 是否存在待处理日志,返回1：存在，0：不存在
	 * @param params
	 * @return
	 */
	public int isExistWaitLog(Map<String, Object> params);
	/**
	 * 根据角色id查询用户
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> queryUsersByRoleId(@Param("roleId")String roleId);
	/**
	 * 查询待处理
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryWaitList(Map<String, Object> params);
	/**
	 * 查询已处理
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryAuditedList(Map<String, Object> params);
	/**
	 * 删除待办日志
	 * @param taskMap
	 */
	public void deleteWaitlog(Map<String, Object> taskMap);
	/**
	 * 修改待办日志人员信息
	 * @param data
	 */
	public void updateWaitingPerson(Map<String, Object> data);
	/**
	 * 修改转办日志人员信息
	 * @param data
	 */
	public void updateOldPerson(Map<String, Object> data);
	/**
	 * 查询待办日志
	 * @param data
	 * @return
	 */
	public List<Map<String, Object>> queryWaitingLogs(Map<String, Object> data);
	
	public void updateAuditUserIdById(Map<String, Object> data);
	
	public List<Map<String, Object>> queryWaitingLogsById(Map<String, Object> data);
	/**
	 * 修改日志审核意见
	 * @param data
	 */
	public void updateOptionById(Map<String, Object> data);
	
	/**
	 * 查询流程审批最后一次操作信息
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryLastLogsByDaxt(@Param("businessId")String businessId);
}
