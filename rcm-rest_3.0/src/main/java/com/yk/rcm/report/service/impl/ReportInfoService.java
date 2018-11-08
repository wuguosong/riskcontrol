package com.yk.rcm.report.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yk.power.service.IOrgService;
import com.yk.rcm.report.dao.IReportInfoMapper;
import com.yk.rcm.report.service.IReportInfoService;

import common.PageAssistant;

@Service
public class ReportInfoService implements IReportInfoService {
	
	@Resource
	private IReportInfoMapper reportInfoMapper;
	@Resource
	private IOrgService orgService;

	@Override
	public void saveReport(Map<String, Object> params) {
		this.reportInfoMapper.saveReport(params);
	}

	@Override
	public void updateReportByBusinessId(Map<String, Object> params) {
		this.reportInfoMapper.updateReportByBusinessId(params);
	}
	
	@Override
	public List<Map<String, Object>> queryPertainAreaAchievement() {
		return this.reportInfoMapper.queryPertainAreaAchievement();
	}

	@Override
	public List<Map<String, Object>> queryProjectsByPertainareaid(PageAssistant page) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("page", page);
		if(page.getParamMap() != null){
			if(page.getParamMap().get("state").equals("all")){
				page.getParamMap().remove("state");
			}
			paramMap.putAll(page.getParamMap());
		}
		List<Map<String, Object>> queryProjectsByPertainareaid = this.reportInfoMapper.queryProjectsByPertainareaid(paramMap);
		page.setList(queryProjectsByPertainareaid);
		Map<String, Object> queryByPkvalue = orgService.queryByPkvalue((String)page.getParamMap().get("pertainareaId"));
		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("name", (String)queryByPkvalue.get("NAME"));
		page.setParamMap(data);
		return queryProjectsByPertainareaid;
	}
	
	
	
	
}
