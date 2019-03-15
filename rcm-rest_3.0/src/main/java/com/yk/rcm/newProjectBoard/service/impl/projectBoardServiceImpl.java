package com.yk.rcm.newProjectBoard.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.newProjectBoard.dao.IProjectBoardMapper;
import com.yk.rcm.newProjectBoard.service.IProjectBoardService;

import common.PageAssistant;


@Service
@Transactional
public class projectBoardServiceImpl implements IProjectBoardService {

	
	@Resource
	private IProjectBoardMapper projectBoardMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Override
	public PageAssistant getProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("userId", retMap.get("userId"));
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
		List<Map<String,Object>> list = this.projectBoardMapper.getProjectList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		/*for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}*/
		page.setList(list);
		return page;
	}

}
