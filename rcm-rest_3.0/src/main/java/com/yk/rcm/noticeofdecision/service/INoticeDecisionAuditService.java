package com.yk.rcm.noticeofdecision.service;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import common.PageAssistant;
import common.Result;

public interface INoticeDecisionAuditService {

	Result startSingleFlow(String businessId);

	Result auditSingle(String businessId, String processOption, Map<String, Object> variables);

	Result querySingleProcessOptions(String businessId);

	void queryWaitList(PageAssistant page);

	void queryAuditedList(PageAssistant page);

	List<Map<String, Object>> queryAuditedLogsById(String businessId);

	void saveAttachmentById(String businessId, Document attachment);

}
