package com.yk.rcm.newProjectBoard.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.power.dao.IUserMapper;
import com.yk.rcm.newProjectBoard.dao.IProjectBoardMapper;
import com.yk.rcm.newProjectBoard.service.IProjectBoardService;

import common.Constants;
import common.PageAssistant;


@Service
@Transactional
public class projectBoardServiceImpl implements IProjectBoardService {

	
	@Resource
	private IProjectBoardMapper projectBoardMapper;
	@Resource
	private IUserMapper userMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Override
	public PageAssistant getProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userID = retMap.get("userId").toString();
		
		
		// 通过登录人ID，获取登录人角色来判断查询的项目信息
		params.put("userId", userID);
		List<Map<String, Object>> roles = userMapper.getRoleByUserId(params);
		int count = 0;
		for (int i = 0; i < roles.size(); i++){
			if (Constants.ROLE_CODE_RISK_DATA.equals(roles.get(i).get("CODE"))){
				count = 1;
				break;
			}
		}
		
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		if (count == 1){
			list = this.projectBoardMapper.getALLProjectList(params);
		} else {
			list = this.projectBoardMapper.getRoleProjectList(params);
		}
		
		page.setList(list);
		return page;
	}

}
