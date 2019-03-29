package com.yk.rcm.pre.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.rcm.pre.dao.IPreReportBoardMapper;
import com.yk.rcm.pre.service.IPreReportBoardService;

import common.PageAssistant;

@Service
@Transactional
public class PreReportBoardServiceImpl implements IPreReportBoardService {
	
	@Resource
	private IPreReportBoardMapper preReportBoardMapper;
	
	@Override
	public PageAssistant queryPreReportBoardByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		
		String wf_state = (String) params.get("wf_state");
		String stage = (String) params.get("stage");
		if(Util.isNotEmpty(stage)){
			params.put("stage", stage.split(","));
		}else{
			params.put("stage", null);
		}
		if(Util.isNotEmpty(wf_state)){
			params.put("wf_state", wf_state.split(","));
		}else{
			params.put("wf_state", null);
		}
		
		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.preReportBoardMapper.queryPreReportBoardByPage(params);
		page.setList(list);
		
		return page;
	}

	@Override
	public Map<String, Object> getCounts() {
		Map<String, Object> data = new HashMap<String, Object>();
		
		int applyCount = this.preReportBoardMapper.getCountsByStages("1","0,1");
		int endCount = this.preReportBoardMapper.getCountsByStages("1","3");
		int taskCount = this.preReportBoardMapper.getCountsByStages("2",null);
		int meetingCount = this.preReportBoardMapper.getCountsByStages("4,5",null);
		int overMeetingCount = this.preReportBoardMapper.getCountsByStages("6",null);
		int riskCount = this.preReportBoardMapper.getCountsByStages("3,3.5",null);
		int decisionCount = this.preReportBoardMapper.getCountsByStages("7",null);
		
		data.put("endCount", endCount);
		data.put("applyCount", applyCount);
		data.put("taskCount", taskCount);
		data.put("meetingCount", meetingCount);
		data.put("overMeetingCount", overMeetingCount);
		data.put("riskCount", riskCount);
		data.put("decisionCount", decisionCount);
		return data;
	}

}
