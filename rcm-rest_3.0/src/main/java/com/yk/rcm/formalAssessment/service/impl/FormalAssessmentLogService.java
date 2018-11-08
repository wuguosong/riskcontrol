package com.yk.rcm.formalAssessment.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.rcm.formalAssessment.dao.IFormalAssessmentAuditMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
@Transactional
@Service
public class FormalAssessmentLogService implements
		IFormalAssessmentAuditLogService {

	@Resource
	private IFormalAssessmentAuditMapper formalAssessmentAuditMapper;
	
	@Override
	public void save(Map<String, Object> log) {
		this.formalAssessmentAuditMapper.save(log);
	}

	@Override
	public List<Map<String, Object>> queryAuditLogs(String businessId) {
		return this.formalAssessmentAuditMapper.queryAuditedLogsById(businessId);
	}

	@Override
	public int queryNextOrderNum(String businessId) {
		
		return this.formalAssessmentAuditMapper.queryNextOrderNum(businessId);
	}

	@Override
	public int queryMaxOrderNum(String businessId) {
		return this.formalAssessmentAuditMapper.queryMaxOrderNum(businessId);
	}

	@Override
	public void updateWaitLog(Map<String, Object> data) {
		data.put("now", Util.now());
		this.formalAssessmentAuditMapper.updateWaitLog(data);
	}

	@Override
	public boolean isExistWaitLog(Map<String, Object> params) {
		return this.formalAssessmentAuditMapper.isExistWaitLog(params) > 0;
	}

	@Override
	public List<Map<String, Object>> queryUsersByRoleId(String roleId) {
		return this.formalAssessmentAuditMapper.queryUsersByRoleId(roleId);
	}

	@Override
	public void deleteWaitlogs(Map<String, Object> taskMap) {
		this.formalAssessmentAuditMapper.deleteWaitlogs(taskMap);
	}

	@Override
	public void updateWaitLogsFormWaitT2FinishedByBusinessId(Map<String, Object> log) {
		this.formalAssessmentAuditMapper.updateWaitLogsFormWaitT2FinishedByBusinessId(log);
	}

	@Override
	public void updateWaitingPerson(String id,String newUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("newUserId", newUserId);
		data.put("id", id);
		this.formalAssessmentAuditMapper.updateWaitingPerson(data);
	}
	@Override
	public List<Map<String, Object>> queryWaitingLogs(String auditUserId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("auditUserId", auditUserId);
		return this.formalAssessmentAuditMapper.queryWaitingLogs(data);
	}

	@Override
	public List<Map<String, Object>> queryWaitingLogsById(String businessId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("businessId", businessId);
		return this.formalAssessmentAuditMapper.queryWaitingLogsById(data);
	}

	@Override
	public void updateOptionById(String id, String option) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("option", option);
		this.formalAssessmentAuditMapper.updateOptionById(data);
	}

	@Override
	public Map<String, Object> queryMaxAuditLogById(String businessId) {
		return this.formalAssessmentAuditMapper.queryMaxAuditLogById(businessId);
	}

	@Override
	public void deleteHuiQianTaskByExecutionId(String executionId) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("executionId", executionId);
		this.formalAssessmentAuditMapper.deleteHuiQianTaskByExecutionId(data);
	}
	
	
}
