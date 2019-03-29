/**
 * 
 */
package com.yk.rcm.bulletin.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import util.Util;
import ws.client.portal.dto.PortalClientModel;

import com.yk.exception.BusinessException;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

/**
 * @author wufucan
 *
 */
@Component
public class BulletinSendScheduleListener implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	public Expression assignType;
	public Expression assignId;
	
	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.delegate.DelegateTask)
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		String assignTypeStr = assignType.getExpressionText();
		String assignIdStr = (String) assignId.getValue(delegateTask);
		DelegateExecution execution = delegateTask.getExecution();
		String taskId = delegateTask.getId();
		String businessId = execution.getProcessBusinessKey();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		//查询待处理人
		String nextUserId = null;
		if("userId".equals(assignTypeStr)){
			nextUserId = assignIdStr;
		}else if("roleId".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.bulletinAuditLogService.queryUsersByRoleId(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}else if("roleConstant".equals(assignTypeStr)){
			List<Map<String, Object>> users = this.bulletinAuditLogService.queryUsersByRoleId(assignIdStr);
			if(users.size() > 0){
				nextUserId = (String) users.get(0).get("UUID");
			}else{
				throw new BusinessException("没有找到相应的审批人，请检查业务审核人是否配置正确！");
			}
		}
		Map<String, Object> bulletin = this.bulletinInfoService.queryByBusinessId(businessId);
		PortalClientModel model = new PortalClientModel();
		model.setUniid(taskId);
		model.setTitle((String)bulletin.get("BULLETINNAME"));
		String url = "BulletinMattersAuditView/419752663ee9445592148abe6d48dadd_0";
		model.setUrl(url);
		model.setOwner(nextUserId);
		model.setCreated(Util.now());
		model.setType("1");
		model.setStatus("1");
		
	}
}
