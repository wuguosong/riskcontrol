/**
 * 
 */
package com.yk.rcm.bulletin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class BulletinAuditLogService implements IBulletinAuditLogService {
	@Resource
	private IBulletinAuditMapper bulletinAuditMapper;
	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinAuditLogService#save(java.util.Map)
	 */
	@Override
	public void save(Map<String, Object> data) {
		this.bulletinAuditMapper.save(data);
	}

	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinAuditLogService#queryAuditLogs(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> queryAuditLogs(String businessId) {
		return this.bulletinAuditMapper.queryAuditLogs(businessId);
	}

	@Override
	public int queryNextOrderNum(String businessId) {
		return this.bulletinAuditMapper.queryNextOrderNum(businessId);
	}
	
	@Override
	public int queryMaxOrderNum(String businessId) {
		return this.bulletinAuditMapper.queryMaxOrderNum(businessId);
	}

	@Override
	public void updateWaitLog(Map<String, Object> data) {
		data.put("now", Util.now());
		this.bulletinAuditMapper.updateWaitLog(data);
	}

	@Override
	public boolean isExistWaitLog(Map<String, Object> params) {
		return this.bulletinAuditMapper.isExistWaitLog(params) > 0;
	}

	@Override
	public List<Map<String, Object>> queryUsersByRoleId(String roleId) {
		return this.bulletinAuditMapper.queryUsersByRoleId(roleId);
	}

	@Override
	public void deleteWaitlog(String businessId, String userId,
			String executionId,String taskId) {
		Map<String,Object> taskMap = new HashMap<String,Object>();
		taskMap.put("businessId", businessId);
		taskMap.put("isWaiting", "1");
		taskMap.put("currentUserId", userId);
		taskMap.put("executionId", executionId);
		taskMap.put("taskId", taskId);
		this.bulletinAuditMapper.deleteWaitlog(taskMap);
	}

	@Override
	public void updateWaitingPerson(String id,String newUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("newUserId", newUserId);
		this.bulletinAuditMapper.updateWaitingPerson(data);
	}
	@Override
	public void updateOldPerson(String id,String newUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("newUserId", newUserId);
		this.bulletinAuditMapper.updateOldPerson(data);
	}

	@Override
	public List<Map<String, Object>> queryWaitingLogs(String auditUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("auditUserId", auditUserId);
		return bulletinAuditMapper.queryWaitingLogs(data);
	}
	@Override
	public void updateAuditUserIdById(String auditUserId,String id) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("auditUserId", auditUserId);
		data.put("id", id);
		bulletinAuditMapper.updateAuditUserIdById(data);
	}

	@Override
	public List<Map<String, Object>> queryWaitingLogsById(String businessId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("businessId", businessId);
		return bulletinAuditMapper.queryWaitingLogsById(data);
	}

	@Override
	public void updateOptionById(String id, String option) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("option", option);
		bulletinAuditMapper.updateOptionById(data);
	}
	
	@Override
	public Map<String, Object> queryLastLogsByDaxt(String businessId) {
		return bulletinAuditMapper.queryLastLogsByDaxt(businessId);
	}
}
