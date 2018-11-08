package com.yk.rcm.noticeofdecision.service;

import java.util.Map;

public interface INoticeDecisionAuditLogService {

	int queryMaxOrderNum(String businessId);

	void save(Map<String, Object> log);

	boolean isExistWaitLog(Map<String, Object> map);

	void updateWaitLog(Map<String, Object> data);

	int queryNextOrderNum(String businessId);
	
}
