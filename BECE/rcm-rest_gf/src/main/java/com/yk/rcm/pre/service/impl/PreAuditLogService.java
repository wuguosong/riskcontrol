package com.yk.rcm.pre.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import com.yk.rcm.pre.service.IPreAuditLogService;

@Service
public class PreAuditLogService implements IPreAuditLogService{
	
	@Resource
	private IPreAuditLogMapper preAuditLogMapper;
	
	@Override
	public List<Map<String, Object>> queryAuditLogs(String businessId) {
		return this.preAuditLogMapper.queryAuditedLogsById(businessId);
	}

	@Override
	public void deleteWaitlog(String businessId, String userId, String executionId) {
		HashMap<String, Object> taskMap = new HashMap<String,Object>();
		taskMap.put("businessId", businessId);
		taskMap.put("currentUserId", userId);
		taskMap.put("executionId", executionId);
		taskMap.put("isWaiting", "1");
		this.preAuditLogMapper.deleteWaitlogs(taskMap);
	}

	@Override
	public void save(Map<String, Object> data) {
		this.preAuditLogMapper.save(data);
	}

	@Override
	public int queryMaxOrderNum(String businessId) {
		return this.preAuditLogMapper.queryMaxOrderNum(businessId);
	}

	@Override
	public List<Map<String, Object>> queryUsersByRoleId(String roleId) {
		return this.preAuditLogMapper.queryUsersByRoleId(roleId);
	}

	@Override
	public void updateWaitingPerson(String id,String newUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("newUserId", newUserId);
		this.preAuditLogMapper.updateWaitingPerson(data);
	}

	@Override
	public List<Map<String, Object>> queryWaitingLogs(String auditUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("auditUserId", auditUserId);
		return this.preAuditLogMapper.queryWaitingLogs(data);
	}

	@Override
	public List<Map<String, Object>> queryWaitingLogsById(String businessId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("businessId", businessId);
		return this.preAuditLogMapper.queryWaitingLogsById(data);
	}

	@Override
	public Map<String, Object> queryMaxAuditLogById(String businessId) {
		return this.preAuditLogMapper.queryMaxAuditLogById(businessId);
	}

	@Override
	public void updateOptionById(String id, String option) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("option", option);
		this.preAuditLogMapper.updateOptionById(data);
	}

	@Override
	public void deleteHuiQianTaskByExecutionId(String executionId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("executionId", executionId);
		this.preAuditLogMapper.deleteHuiQianTaskByExecutionId(data);
	}
	
}
