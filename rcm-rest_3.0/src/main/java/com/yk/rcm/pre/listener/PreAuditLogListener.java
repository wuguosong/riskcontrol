package com.yk.rcm.pre.listener;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import com.yk.rcm.pre.service.IPreAuditLogService;
import common.Constants;
import org.activiti.engine.delegate.*;
import org.springframework.stereotype.Component;
import util.ThreadLocalUtil;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 审核日志监听器
 * @author yaphet
 */
@Component
public class PreAuditLogListener implements ExecutionListener,TaskListener {

	private static final long serialVersionUID = 1L;
	@Resource
	private IPreAuditLogService preAuditLogService;
	public Expression status;
	public Expression taskdesc;
	@Resource
	private INotifyService notifyService;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String statusStr = (String) status.getValue(execution);
		String taskdescStr = (String)taskdesc.getValue(execution);
		
		Map<String, Object> log = new HashMap<String, Object>();
		
		String businessId = execution.getProcessBusinessKey();
		String opinion = (String) execution.getVariable("opinion");
		String auditUserId = ThreadLocalUtil.getUserId();
		
		log.put("businessId", businessId);
		log.put("auditUserId", auditUserId);
		log.put("auditTime", Util.now());
		log.put("opinion", opinion);
		log.put("auditStatus", statusStr);
		int orderBy = this.preAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", ++orderBy);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskdescStr);
		log.put("executionId", execution.getId());
		this.preAuditLogService.save(log);
		// 同步待阅
		notifyService.sendToPortal(Constants.PROCESS_KEY_PREREVIEW, businessId, true, false, Notify.TYPE_NOTIFY);
	}

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
		
		log.put("currentUserId", userId);
		log.put("businessId", businessId);
		log.put("auditUserId", userId);
		log.put("auditTime", Util.now());
		log.put("opinion", opinion);
		String sequenceFlow = (String) execution.getVariable("sequenceFlow");
		if("0".equals(sequenceFlow)){
			statusStr = "B";
		}else if("1".equals(sequenceFlow)){
			statusStr = "C";
		}
		log.put("auditStatus", statusStr);
		int orderBy = this.preAuditLogService.queryMaxOrderNum(businessId);
		log.put("orderBy", orderBy+1);
		log.put("isWaiting", "0");
		log.put("taskdesc", taskdescStr);
		log.put("taskId", id);
		log.put("auditStatus", statusStr);
		log.put("executionId", execution.getId());
		this.preAuditLogService.save(log);
	}

}
