package com.daxt.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import util.PropertiesUtil;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.meeting.service.IPigeonholeService;

@Service("com.daxt.service.impl.ContractJob")
public class ContractJob {
	
	@Resource
	private IPigeonholeService pigeonholeService;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;

	
	public void execute() {
		//扫描截止昨天的合同创建时间(因为主数据推送是全量，防止访问相同的数据!)
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(new Date());  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date contractTime = calendar.getTime();  
        
        //此刻的归档时间
        String property = PropertiesUtil.getProperty("daxt.SRSM001");
        calendar = Calendar.getInstance();  
	    calendar.setTime(new Date());
	    calendar.add(calendar.DATE, Integer.parseInt(property));
	    calendar.set(calendar.HOUR_OF_DAY, 23);
	    calendar.set(calendar.MINUTE, 0);
	    calendar.set(calendar.SECOND, 0);
	    Date pigeonholeTime = calendar.getTime(); 
        
		List<Map<String, Object>> projects = pigeonholeService.queryPfrContract(contractTime);
		for (Map<String, Object> project : projects) {
			formalAssessmentInfoService.startPigeonholeByBusinessId(project.get("BUSINESSID").toString(), pigeonholeTime);
		}
	}
}
