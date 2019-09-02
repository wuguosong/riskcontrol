package com.yk.rcm.formalAssessment.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.formalAssessment.dao.IFormalReportBoardMapper;
import com.yk.rcm.formalAssessment.service.IFormalReportBoardService;

import common.PageAssistant;
import util.ThreadLocalUtil;
import util.Util;


@Service
@Transactional
public class FormalReportBoardServiceImpl implements IFormalReportBoardService {
	
	@Resource
	private IFormalReportBoardMapper formalReportBoardMapper;

	@Override
	public PageAssistant queryFormalReportBoardByPage(PageAssistant page) {
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
		List<Map<String,Object>> list = this.formalReportBoardMapper.queryFormalReportBoardByPage(params);
		page.setList(list);
		
		return page;
	}

	@Override
	public Map<String, Object> getCounts() {
		Map<String, Object> data = new HashMap<String, Object>();
		
		int endCount = this.formalReportBoardMapper.getCountsByStages("1","3");
		int applyCount = this.formalReportBoardMapper.getCountsByStages("1","0,1");
		int taskCount = this.formalReportBoardMapper.getCountsByStages("2",null);
		int meetingCount = this.formalReportBoardMapper.getCountsByStages("4,5",null);
		int overMeetingCount = this.formalReportBoardMapper.getCountsByStages("6",null);
		int riskCount = this.formalReportBoardMapper.getCountsByStages("3,3.5",null);
		int decisionCount = this.formalReportBoardMapper.getCountsByStages("7",null);
		
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
