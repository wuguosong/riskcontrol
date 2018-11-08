package com.yk.rcm.formalAssessment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yk.bpmn.entity.TaskInfo;

import common.PageAssistant;
import common.Result;

public interface IFormalAssessmentAuditService {

	Result startSingleFlow(String businessId);

	Result auditSingle(String businessId, String processOption,	Map<String, Object> variables ,String documentation);

	Result querySingleProcessOptions(String businessId, String taskMark);

	void queryAuditedList(PageAssistant page);

	void queryWaitList(PageAssistant page);

	void updateAuditStatusByBusinessId(String businessId, String status);

	void saveTaskToMongo(String json);

	List<String> queryFirstLevelLawyersIdsByServiceTypeIds(HashMap<String, Object> params);

	void updateAuditStageByBusinessId(String businessId,String stage);

	TaskInfo queryTaskInfoByBusinessId(String businessId,String processKey, String taskMark);
	
	/**
	 * 新增附件
	 * @param json
	 */
	public void addNewAttachment(String json);
	
	/**
	 * 删除附件
	 * @param json
	 */
	public void deleteAttachment(String json);
	
	/**
	 * 修改附件
	 * @param json
	 */
	public void updateAttachment(String json);
}
