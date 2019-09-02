package com.yk.rcm.formalAssessment.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.power.service.IPertainAreaService;
import common.Constants;

@Component
public class FormalAssessmentQueryBusinessLeaderListener implements ExecutionListener{
	public Expression variablesKey;
	
	private static final long serialVersionUID = 1L;

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IPertainAreaService pertainAreaService;

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String businessKey = execution.getProcessBusinessKey();
		//key
		String variablesKeyStr = (String)variablesKey.getValue(execution);
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessKey, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> apply = (Map<String, Object>) formalMongo.get("apply");
		
		//业务类型
		Map<String, Object> serviceType = ((List<Map<String, Object>>) apply.get("serviceType")).get(0);
		String serviceTypeId = (String) serviceType.get("KEY");
		//大区过滤
		Map<String, Object> pertainArea = (Map<String, Object>) apply.get("pertainArea");
		
		List<Map<String, Object>> pList = pertainAreaService.queryPertainAreaByOrgPkValue((String)pertainArea.get("KEY"));
		
		List<String> leaderlist = new ArrayList<String>();
		for (Map<String, Object> map : pList) {
			if("1".equals((String)map.get("TYPE"))){
				if("all".equals(map.get("SERVICETYPE")) || serviceTypeId.equals(map.get("SERVICETYPE"))){
					leaderlist.add((String)map.get("LEADERID"));
					execution.setVariable("isSkipBusinessLeader", "0");
				}
			}
		}
		
		execution.setVariable(variablesKeyStr, leaderlist);
		if(Util.isEmpty(leaderlist)){
			execution.setVariable("isSkipBusinessLeader", "1");
		}
	}
}
