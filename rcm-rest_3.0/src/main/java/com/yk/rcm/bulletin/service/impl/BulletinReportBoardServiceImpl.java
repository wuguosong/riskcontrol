package com.yk.rcm.bulletin.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.rcm.bulletin.dao.IBulletinReportBoardMapper;
import com.yk.rcm.bulletin.service.IBulletinReportBoardService;
import common.PageAssistant;

@Service
@Transactional
public class BulletinReportBoardServiceImpl implements IBulletinReportBoardService {
	
	@Resource
	private IBulletinReportBoardMapper bulletinReportBoardMapper;
	
	@Override
	public PageAssistant queryBulletinReportBoardByPage(PageAssistant page) {
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
			orderBy = " CREATETIME desc ";
		}
		params.put("orderBy", orderBy);
		
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
		
		
		List<Map<String,Object>> list = this.bulletinReportBoardMapper.queryBulletinReportBoardByPage(params);
		page.setList(list);
		
		return page;
	}

	@Override
	public Map<String, Object> getCounts() {
		Map<String, Object> data = new HashMap<String, Object>();
		
		int applyCount = this.bulletinReportBoardMapper.getCountsByStages("1","0,1");
		int taskCount = this.bulletinReportBoardMapper.getCountsByStages("1.5,2",null);
		int meetingCount = this.bulletinReportBoardMapper.getCountsByStages("3",null);
		int overMeetingCount = this.bulletinReportBoardMapper.getCountsByStages("4",null);
		int summaryedCount = this.bulletinReportBoardMapper.getCountsByStages("5",null);
		int endCount = this.bulletinReportBoardMapper.getCountsByStages("1","3");

		
		data.put("applyCount", applyCount);
		data.put("taskCount", taskCount);
		data.put("meetingCount", meetingCount);
		data.put("overMeetingCount", overMeetingCount);
		data.put("summaryedCount", summaryedCount);
		data.put("endCount", endCount);
		return data;
	}

}
