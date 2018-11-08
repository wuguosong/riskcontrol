package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;

/**
 * 
 * @author yaphet
 * 
 * 2017年5月31日
 *
 */
@Component
public class FormalAssessmentAuditLogListener implements ExecutionListener,TaskListener{
	
	
	private static final long serialVersionUID = 1L;
	public Expression status;
	public Expression taskdesc;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	@Resource 
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String statusStr = (String) status.getValue(execution);
		String taskdescStr = (String)taskdesc.getValue(execution);
		Map<String, Object> log = new HashMap<String, Object>();
		
		String businessId = execution.getProcessBusinessKey();
		
		String auditUserId = ThreadLocalUtil.getUserId();
		
		String opinion = (String) execution.getVariable("opinion");
		
		
		
		//获取当前人id
		String userId = ThreadLocalUtil.getUserId();
		log.put("currentUser", userId);
		log.put("businessId", businessId);
		log.put("auditUserId", auditUserId);
		log.put("auditTime", Util.now());
		log.put("opinion", opinion);
		log.put("auditStatus", statusStr);
		int orderBy = this.formalAssessmentAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", orderBy+1);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskdescStr);
		log.put("executionId", execution.getId());
		this.formalAssessmentAuditLogService.save(log);
	}

	
	/**
	 * 节点监听
	 * 针对固定小组成员的多任务节点
	 * 将当前人的待办修改为已办
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		String id = delegateTask.getId();
		String statusStr = (String) status.getValue(execution);
		String taskdescStr = (String)taskdesc.getValue(execution);
		String businessId = execution.getProcessBusinessKey();
		
		Map<String, Object> log = new HashMap<String, Object>();
		//获取当前人id
		String userId = ThreadLocalUtil.getUserId();
		String opinion = (String) execution.getVariable("opinion");
		String sequenceFlow = (String) execution.getVariable("sequenceFlow");
		if("0".equals(sequenceFlow)){
			statusStr = "B";
		}else if("1".equals(sequenceFlow)){
			statusStr = "C";
		}
		log.put("currentUserId", userId);
		log.put("businessId", businessId);
		log.put("auditUserId", userId);
		log.put("auditTime", Util.now());
		log.put("opinion", opinion);
		log.put("auditStatus", statusStr);
		int orderBy = this.formalAssessmentAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", orderBy+1);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskdescStr);
		log.put("taskId", id);
		log.put("auditStatus", statusStr);
		log.put("executionId", execution.getId());
		this.formalAssessmentAuditLogService.save(log);
//		this.formalAssessmentAuditLogService.updateWaitLogsFormWaitT2FinishedByBusinessId(log);
	}

}
