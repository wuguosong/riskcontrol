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
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userID = retMap.get("userId").toString();

		// 通过登录人ID，获取登录人角色来判断查询的项目信息
		params.put("userId", userID);
		params1.put("userId", userID);
		List<Map<String, Object>> roles = userMapper.getRoleByUserId(params);
		int count = 0;
		for (int i = 0; i < roles.size(); i++) {
			if (Constants.ROLE_CODE_RISK_DATA.equals(roles.get(i).get("CODE"))) {
				count = 1;
				break;
			}
		}

		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int totalCount = 0;

		if (count == 1) {
			list = this.projectBoardMapper.getALLProjectList(params);
			totalCount = this.projectBoardMapper.getALLProjectListCount(params1);
		} else {
			list = this.projectBoardMapper.getRoleProjectList(params);
			totalCount = this.projectBoardMapper.getRoleProjectListCount(params1);
		}

		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant getPfrProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userId = retMap.get("userId").toString();

		int count = this.getFlagForList(userId);
		
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int totalCount = 0;
		if (count == 1) {
			list = this.projectBoardMapper.getAllPfrProjectList(params);
			totalCount = this.projectBoardMapper.getAllPfrProjectListCount(params1);
		} else {
			params.put("userId", userId);
			params1.put("userId", userId);
			list = this.projectBoardMapper.getRolePfrProjectList(params);
			totalCount = this.projectBoardMapper.getRolePfrProjectListCount(params1);
		}
		
		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant getPreProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userId = retMap.get("userId").toString();

		int count = this.getFlagForList(userId);
		
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int totalCount = 0;
		if (count == 1) {
			list = this.projectBoardMapper.getAllPreProjectList(params);
			totalCount = this.projectBoardMapper.getAllPreProjectListCount(params1);
		} else {
			params.put("userId", userId);
			params1.put("userId", userId);
			list = this.projectBoardMapper.getRolePreProjectList(params);
			totalCount = this.projectBoardMapper.getRolePreProjectListCount(params1);
		}
		
		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant getBulletinProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userId = retMap.get("userId").toString();

		int count = this.getFlagForList(userId);
		
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int totalCount = 0;
		if (count == 1) {
			list = this.projectBoardMapper.getAllBulletinProjectList(params);
			totalCount = this.projectBoardMapper.getAllBulletinProjectListCount(params1);
		} else {
			params.put("userId", userId);
			params1.put("userId", userId);
			list = this.projectBoardMapper.getRoleBulletinProjectList(params);
			totalCount = this.projectBoardMapper.getRoleBulletinProjectListCount(params1);
		}
		
		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}
	
	
	/**
	 * 获取登录人角色，判断是需要查询全部项目还是个人项目
	 * */
	public int getFlagForList (String userId) {
		int count = 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		List<Map<String, Object>> roles = userMapper.getRoleByUserId(params);
		for (int i = 0; i < roles.size(); i++) {
			if (Constants.ROLE_CODE_RISK_DATA.equals(roles.get(i).get("CODE"))) {
				count = 1;
				break;
			}
		}
		
		return count;
	}
}
