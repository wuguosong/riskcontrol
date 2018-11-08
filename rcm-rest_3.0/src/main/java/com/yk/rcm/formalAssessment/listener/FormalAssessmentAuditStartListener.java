package com.yk.rcm.formalAssessment.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import common.Constants;
import util.PropertiesUtil;
import util.Util;
import ws.client.TzClient;

@Component
public class FormalAssessmentAuditStartListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessId = execution.getProcessBusinessKey();
		String serverPath = PropertiesUtil.getProperty("domain.allow");
		StringBuffer url = new StringBuffer(serverPath+"/html/index.html#/");
		url.append("FormalAssessmentAuditDetailView/");
		url.append(businessId);
		String oldurl = "#/";
		String ntUrl = Util.encodeUrl(oldurl);
		url.append("/"+ntUrl);
		//调用投资接口，通知他们更新项目状态
		TzClient client = new TzClient();
		client.setBusinessId(businessId);
		client.setStatus("1");
		client.setLocation(url.toString());
		Thread t = new Thread(client);
		t.start();
	}
	
}
