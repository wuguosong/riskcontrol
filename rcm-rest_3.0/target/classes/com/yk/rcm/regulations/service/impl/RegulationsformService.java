package com.yk.rcm.regulations.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.regulations.dao.IRegulationsformMapper;
import com.yk.rcm.regulations.service.IRegulationsformService;

import common.PageAssistant;
import util.ThreadLocalUtil;
import util.Util;

/**
 * 
 * @author lyc
 *         模板文件
 *
 */

@Service
@Transactional
public class RegulationsformService implements IRegulationsformService {

	@Resource
	private IRegulationsformMapper regulationsformMapper;

	@Override
	public PageAssistant queryRegulationsByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);

		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " STATUS ASC, CREATE_DATE DESC ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.regulationsformMapper.queryRegulationsByPage(params);
		page.setList(list);

		return page;
	}

	@Override
	public String addRegulations(String json) {

		String id = Util.getUUID();
		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("id", id);
		map.put("create_by", ThreadLocalUtil.getUserId());
		map.put("create_date", Util.now());
		map.put("status", "1");
		this.regulationsformMapper.addRegulations(map);
		return id;
	}

	@Override
	public String modifyRegulations(String json) {

		Map<String, Object> map = Util.parseJson2Map(json);
		this.regulationsformMapper.modifyRegulations(map);

		return (String) map.get("ID");
	}

	@Override
	public void deleteRegulations(String[] ids) {
		this.regulationsformMapper.deleteRegulations(ids);
	}

	@Override
	public Map<String, Object> queryRegulationsInfo(String id) {
		return this.regulationsformMapper.queryRegulationsInfo(id);
	}
	
	@Override
	public Map<String, Object> queryRegulationsInfoForView(String id) {
		return this.regulationsformMapper.queryRegulationsInfoForView(id);
	}

	@Override
	public void submitRegulations(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ID", id);
		map.put("STATUS", "2");
		this.regulationsformMapper.submitRegulations(map);
	}
	

	@Override
	public PageAssistant queryRegulationsForSubmit(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = "CREATE_DATE DESC ";
		}
		params.put("status", "2");
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.regulationsformMapper.queryRegulationsByPage(params);
		page.setList(list);
		return page;
	}


}
