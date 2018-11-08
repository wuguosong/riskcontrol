/**
 * 
 */
package com.yk.power.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.power.dao.IOrgMapper;
import com.yk.power.service.IOrgService;

import common.Constants;
import common.PageAssistant;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class OrgService implements IOrgService {
	@Resource
	private IOrgMapper orgMapper;
	
	/* (non-Javadoc)
	 * @see com.yk.power.service.IOrgService#queryByPkvalue(java.lang.String)
	 */
	@Override
	public Map<String, Object> queryByPkvalue(String pkvalue) {
		return this.orgMapper.queryByPkvalue(pkvalue);
	}

	@Override
	public Map<String, Object> queryPertainAreaByPkvalue(String orgpkvalue) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportUnitId", orgpkvalue);
		List<String> areaCodes = new ArrayList<String>();
		areaCodes.add(Constants.SYS_ORG_CODE_EAST);
		areaCodes.add(Constants.SYS_ORG_CODE_WEST);
		areaCodes.add(Constants.SYS_ORG_CODE_SOUTH);
		areaCodes.add(Constants.SYS_ORG_CODE_NORTH);
		areaCodes.add(Constants.SYS_ORG_CODE_CENTER);
		params.put("areaCodes", areaCodes);
		return this.orgMapper.queryPertainAreaByPkvalue(params);
	}

	@Override
	public Map<String, Object> queryGroupUserInfo(String userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("rootCode", Constants.SYS_ORG_CODE_ROOT);
		return this.orgMapper.queryGroupUserInfo(params);
	}

	@Override
	public List<Map<String, Object>> queryReportOrg(Map<String, Object> params) {
		return this.orgMapper.queryReportOrg(params);
	}
	
	@Override
	public List<Map<String, Object>> queryCommonOrg(Map<String, Object> params) {
		return orgMapper.queryCommonOrg(params);
	}
	
	@Override
	public Map<String, Object> queryPertainArea(String orgpkvalue) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportUnitId", orgpkvalue);
		params.put("code", Constants.SYS_ORG_CODE_ROOT);
		return orgMapper.queryPertainArea(params);
	}

	@Override
	public List<Map<String, Object>> queryAllOrg(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		page.setList(this.orgMapper.queryAllOrg(params));
		return null;
	}
}
