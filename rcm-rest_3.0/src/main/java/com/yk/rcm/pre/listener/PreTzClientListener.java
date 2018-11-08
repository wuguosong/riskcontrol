package com.yk.rcm.pre.listener;

import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzAfterPreReviewClient;

import com.yk.rcm.pre.service.IPreAuditLogService;
import com.yk.rcm.pre.service.IPreInfoService;

@Component
public class PreTzClientListener implements ExecutionListener{
	
	public Expression stage;
	
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IPreAuditLogService preAuditLogService;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		String businessId = execution.getProcessBusinessKey();
		String opinion = (String) execution.getVariable("opinion");
		
		Map<String, Object> oracle = preInfoService.getOracleByBusinessId(businessId);
		
		if(Util.isNotEmpty(oracle.get("DECISIONOPINION"))){
//			if(Util.isNotEmpty(oracle.get("DECISIONOPINION"))){
//				String decisionopinion = (String) oracle.get("DECISIONOPINION");
//				//给投资推项目信息数据
//				TzAfterPreReviewClient tzAfterPreReviewClient = new TzAfterPreReviewClient(businessId, decisionopinion ,opinion,"2");
//				Thread t = new Thread(tzAfterPreReviewClient);
//				t.start();
//			}
		}
	}
}
