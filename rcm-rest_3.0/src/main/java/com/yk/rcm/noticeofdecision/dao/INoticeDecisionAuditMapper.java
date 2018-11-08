package com.yk.rcm.noticeofdecision.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface INoticeDecisionAuditMapper extends BaseMapper  {

	void save(Map<String, Object> log);

	int queryMaxOrderNum(String businessId);

	int isExistWaitLog(Map<String, Object> map);

	void updateWaitLog(Map<String, Object> data);

	int queryNextOrderNum(String businessId);

	List<Map<String, Object>> queryWaitList(Map<String, Object> params);

	List<Map<String, Object>> queryAuditedList(Map<String, Object> params);

	List<Map<String, Object>> queryAuditedLogsById(String businessId);

}
