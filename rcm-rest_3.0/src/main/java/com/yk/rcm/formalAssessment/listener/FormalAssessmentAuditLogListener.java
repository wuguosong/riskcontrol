package com.yk.rcm.formalAssessment.listener;

import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import common.Constants;
import org.activiti.engine.delegate.*;
import org.springframework.stereotype.Component;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
	@Resource
	private INotifyService notifyService;
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
		// 同步待阅
		notifyService.sendToPortal(Constants.PROCESS_KEY_FormalAssessment, businessId, true, false, Notify.TYPE_NOTIFY);
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
