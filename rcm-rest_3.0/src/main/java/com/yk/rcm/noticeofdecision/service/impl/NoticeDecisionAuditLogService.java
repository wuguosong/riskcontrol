package com.yk.rcm.noticeofdecision.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.rcm.bulletin.dao.IBulletinAuditMapper;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionAuditMapper;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionAuditLogService;


/**
 * @author yaphet
 *
 */
@Service
@Transactional
public class NoticeDecisionAuditLogService implements INoticeDecisionAuditLogService{

	@Resource
	private INoticeDecisionAuditMapper noticeDecisionAuditMapper;
	
	@Override
	public int queryMaxOrderNum(String businessId) {
		return this.noticeDecisionAuditMapper.queryMaxOrderNum(businessId);
	}

	@Override
	public void save(Map<String, Object> log) {
		this.noticeDecisionAuditMapper.save(log);
	}

	@Override
	public boolean isExistWaitLog(Map<String, Object> map) {
		return this.noticeDecisionAuditMapper.isExistWaitLog(map) > 0;
	}

	@Override
	public void updateWaitLog(Map<String, Object> data) {
		data.put("now", Util.now());
		this.noticeDecisionAuditMapper.updateWaitLog(data);
	}

	@Override
	public int queryNextOrderNum(String businessId) {
		return this.noticeDecisionAuditMapper.queryNextOrderNum(businessId);
	}

}
