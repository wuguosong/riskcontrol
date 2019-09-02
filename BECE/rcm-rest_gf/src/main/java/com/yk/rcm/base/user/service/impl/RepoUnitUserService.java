package com.yk.rcm.base.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.base.user.dao.IRepoUnitUserMapper;
import com.yk.rcm.base.user.service.IRepoUnitUserService;

import common.PageAssistant;

@Service
@Transactional
public class RepoUnitUserService implements IRepoUnitUserService {

	@Resource
	private IRepoUnitUserMapper repoUnitUserMapper;
	
	@Override
	public PageAssistant queryByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = repoUnitUserMapper.queryByPage(params);
		page.setList(list);
		return page;
	}

	@Override
	public void create(Map<String, Object> map) {
		repoUnitUserMapper.create(map);
	}

	@Override
	public void update(Map<String, Object> map) {
		repoUnitUserMapper.update(map);
	}

	@Override
	public void deleteById(String id) {
		repoUnitUserMapper.deleteById(id);
	}

	@Override
	public Map<String, Object> queryById(String id) {
		return repoUnitUserMapper.queryById(id);
	}

	@Override
	public Map<String, Object> queryByRepoUnitId(String id) {
		return repoUnitUserMapper.queryByRepoUnitId(id);
	}

}
