/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;

import com.daxt.service.IDaxtService;
import com.yk.exception.BusinessException;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

/**
 * 流程结束监听
 * @author wufucan
 */
@Component
public class BulletinAuditEndListener implements ExecutionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	@Resource
	private IDaxtService daxtService;
	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.ExecutionListener#notify(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//流程结束时需要做的事
		//1、修改通报事项的审核状态
		String businessId = execution.getProcessBusinessKey();
		String isAgree = (String) execution.getVariable("isAgree");
		String taskdesc = "";
		String auditUserId = "";
		if("1".equals(isAgree)){
			this.bulletinInfoService.updateAuditStatusByBusinessId(businessId, "2");
			this.bulletinInfoService.updateAuditStageByBusinessId(businessId, "2");
			taskdesc = "通过结束";
		}else if("0".equals(isAgree)){
			this.bulletinInfoService.updateAuditStatusByBusinessId(businessId, "3");
			taskdesc = "不通过结束";
			this.daxtService.bulletinStart(businessId);
		}else if(Util.isEmpty(isAgree)){
			taskdesc = "管理员终止";
			auditUserId = ThreadLocalUtil.getUserId();
			this.bulletinInfoService.updateAuditStatusByBusinessId(businessId, "3");
			this.bulletinInfoService.updateAuditStageByBusinessId(businessId, "1");
			this.daxtService.bulletinStart(businessId);
		}else{
			throw new BusinessException("流程结束时，审核状态不明确！");
		}
		this.bulletinAuditLogService.deleteWaitlog(businessId, null, null, null);
		//2、修改待审核日志信息
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("orderBy", this.bulletinAuditLogService.queryMaxOrderNum(businessId));
		data.put("nextUserId", "");
		data.put("auditUserId", auditUserId);
		data.put("taskdesc", taskdesc);
		data.put("auditTime", Util.now());
		data.put("isWaiting", "1");
		data.put("auditStatus", "9");
		this.bulletinAuditLogService.save(data);
	}

}
