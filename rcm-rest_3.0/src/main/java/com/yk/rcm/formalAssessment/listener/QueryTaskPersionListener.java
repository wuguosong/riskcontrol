package com.yk.rcm.formalAssessment.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.bson.Document;
import org.springframework.stereotype.Component;

import util.Util;
import ws.client.HpgClient;

import com.yk.common.IBaseMongo;
import com.yk.power.service.IRoleService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;

import common.Constants;
@Component
public class QueryTaskPersionListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;
	@Resource 
	private IBaseMongo baseMongo;
	@Resource 
	private IRoleService roleService;
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	private  static final String RCM_FORMALASSESSMENT_INFO = Constants.RCM_FORMALASSESSMENT_INFO; 

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		String businessKey = execution.getProcessBusinessKey();
	
		Map<String, Object> fromalAssessmentMongo = baseMongo.queryById(businessKey, RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> taskallocation = (Map<String, Object>) fromalAssessmentMongo.get("taskallocation");
		
		List<Document> fixedGroup = (List<Document>) taskallocation.get("fixedGroup");
		Document legalReviewLeader = (Document) taskallocation.get("legalReviewLeader");
		Document reviewLeader = (Document) taskallocation.get("reviewLeader");
		
		Map<String, Object> variables = new HashMap<String,Object>();
		
		if(Util.isNotEmpty(legalReviewLeader)){
			String legalReviewLeaderId = legalReviewLeader.getString("VALUE");
			variables.put("legalReviewLeader", legalReviewLeaderId);
		}
		
		if(Util.isNotEmpty(reviewLeader)){
			String reviewLeaderId = reviewLeader.getString("VALUE");
			variables.put("reviewLeader", reviewLeaderId);
		}
		
		if(Util.isNotEmpty(fixedGroup)){
			List<String> groupMembersList = new ArrayList<String>();
			for (Document ll : fixedGroup) {
				String id = ll.getString("VALUE");
				groupMembersList.add(id);
			}
			variables.put("groupMembers", groupMembersList);
		}
		execution.setVariables(variables);
		
		HpgClient hpgClient = new HpgClient(businessKey,reviewLeader.getString("VALUE"));
		Thread thread = new Thread(hpgClient);
		thread.start();
	}

}
