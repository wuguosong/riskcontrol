package com.yk.rcm.noticeofdecision.listener;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import com.yk.exception.BusinessException;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionAuditLogService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;


/**
 * 决策通知书
 * 流程结束监听
 * @author wufucan
 */
@Component
public class NoticeDecisionAuditEndListener implements ExecutionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private INoticeDecisionAuditLogService noticeDecisionAuditLogService;
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//流程结束时需要做的事
		//1、修改决策通知书的审核状态
		String businessId = execution.getProcessBusinessKey();
		String isAgree = (String) execution.getVariable("isAgree");
		String taskdesc = "";
		if("1".equals(isAgree)){
			this.noticeDecisionInfoService.updateAuditStatusByBusinessId(businessId, "2");
			taskdesc = "通过结束";
		}else if("0".equals(isAgree)){
			this.noticeDecisionInfoService.updateAuditStatusByBusinessId(businessId, "3");
			taskdesc = "不通过结束";
		}else{
			throw new BusinessException("流程结束时，审核状态不明确！");
		}
		//2、修改待审核日志信息
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("orderBy", this.noticeDecisionAuditLogService.queryNextOrderNum(businessId));
		data.put("nextUserId", "");
		data.put("taskdesc", taskdesc);
		
		this.noticeDecisionAuditLogService.updateWaitLog(data);
	}

}
