package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import com.yk.common.IBaseMongo;
import com.yk.power.service.IPertainAreaService;

import common.Constants;

@Component
public class FormalAssessmentQueryBusinessSkipListener implements ExecutionListener{
	
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
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessKey, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> apply = (Map<String, Object>) formalMongo.get("apply");
		//业务类型
		Map<String, Object> serviceType = ((List<Map<String, Object>>) apply.get("serviceType")).get(0);
		String serviceTypeId = (String) serviceType.get("KEY");
		//大区过滤
		Map<String, Object> pertainArea = (Map<String, Object>) apply.get("pertainArea");
		//申报单位
		Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
		List<Map<String, Object>> pList = pertainAreaService.queryPertainAreaByOrgPkValue((String)pertainArea.get("KEY"));
		List<Map<String, Object>> rList = pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
		
		Map<String, Object> variables = new HashMap<String, Object>();
		
		variables.put("isSkipBusinessLeader", "1");
		
		variables.put("isSkipBusinessArea", "1");
		
		for (Map<String, Object> map : pList) {
			
			if("1".equals((String)map.get("TYPE"))){
				if("all".equals(map.get("SERVICETYPE")) || serviceTypeId.equals(map.get("SERVICETYPE"))){
					variables.put("isSkipBusinessLeader", "0");
				}
			}
		}
		
		for (Map<String, Object> rmap : rList) {
			if("2".equals((String)rmap.get("TYPE"))){
				variables.put("isSkipBusinessArea", "0");
			}
		}
		
		execution.setVariables(variables);
	}
}
