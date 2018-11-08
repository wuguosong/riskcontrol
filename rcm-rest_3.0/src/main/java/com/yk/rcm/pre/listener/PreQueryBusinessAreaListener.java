package com.yk.rcm.pre.listener;

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
public class PreQueryBusinessAreaListener implements ExecutionListener{
	private static final long serialVersionUID = 1L;
	
	public Expression variablesKey;

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
		
		Map<String, Object> preMongo = this.baseMongo.queryById(businessKey, Constants.RCM_PRE_INFO);
		
		Map<String, Object> apply = (Map<String, Object>) preMongo.get("apply");
		
		//申报单位
		Map<String, Object> reportingUnit = (Map<String, Object>) apply.get("reportingUnit");
		
		List<Map<String, Object>> rList = pertainAreaService.queryPertainAreaByOrgPkValue((String)reportingUnit.get("KEY"));
		
		for (Map<String, Object> map : rList) {
			if("2".equals((String)map.get("TYPE"))){
				execution.setVariable(variablesKeyStr, (String)map.get("LEADERID"));
			}
		}
	}
}
