package com.yk.rcm.formalAssessment.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

@Component
public class FirstLevelLawyerSkipListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;
	@Resource 
	private IBaseMongo baseMongo;
	@Resource 
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		String businessid = execution.getProcessBusinessKey();
		
		Map<String, Object> formalAssessment = formalAssessmentInfoService.getFormalAssessmentByID(businessid);
		
		Map<String, Object> formalAssessmentOracle = (Map<String, Object>) formalAssessment.get("formalAssessmentOracle");
		
		String servicetype_ids = (String) formalAssessmentOracle.get("SERVICETYPE_ID");
		
		HashMap<String, Object> params = new HashMap<String,Object>();
		params.put("servicetype_ids", servicetype_ids);
		List<String> firstLevelLawyersIds = formalAssessmentAuditService.queryFirstLevelLawyersIdsByServiceTypeIds(params);
		
		Map<String, Object> variables = new HashMap<String,Object>();
		
		String isSkipFirstLawyer="0";
		
		//判断是否跳过一级法务人员,为空则跳过此节点
		if(Util.isEmpty(firstLevelLawyersIds)){
			
			isSkipFirstLawyer = "1";
			
		}else{
			
			variables.put("firstLevelLawyer", firstLevelLawyersIds.get(0));
			
		}
		
		variables.put("isSkipFirstLawyer", isSkipFirstLawyer);
//		Integer.parseInt("aa");
		execution.setVariables(variables);
	}

}
