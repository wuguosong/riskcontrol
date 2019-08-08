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
			list = this.projectBoardMapper.getALLProjectList(params);
			totalCount = this.projectBoardMapper.getALLProjectListCount(params1);
		} else {
			params.put("userId", userId);
			params1.put("userId", userId);
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
	
	/**大区负责人首页查询项目方法
	 * */
	@Override
	public PageAssistant getProjectListForCompanyHead(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> params1 = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		String userId = retMap.get("userId").toString();
		Map<String, Object> params3 = new HashMap<String, Object>();
		params3.put("id", userId);
		List<Map<String, Object>> companyList = this.projectBoardMapper.queryByCompanyHeaderId(params3);
		String[] pertainareaNameList = null;
		if(companyList!=null&&companyList.size()>0){
			pertainareaNameList = new String[companyList.size()];
			for(int i=0;i<companyList.size();i++){
				pertainareaNameList[i] = (String)companyList.get(i).get("REPORTINGUNIT_NAME");
			}
		}
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}
		params.put("pertainareaNameList", pertainareaNameList);
		params1.put("pertainareaNameList", pertainareaNameList);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int totalCount = 0;
		list = this.projectBoardMapper.getALLProjectList(params);
		totalCount = this.projectBoardMapper.getALLProjectListCount(params1);
		

		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}

}
