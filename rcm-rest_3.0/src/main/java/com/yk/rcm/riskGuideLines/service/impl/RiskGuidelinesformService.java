package com.yk.rcm.riskGuideLines.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.riskGuideLines.dao.IRiskGuidelinesformMapper;
import com.yk.rcm.riskGuideLines.service.IRiskGuidelinesformService;

import common.PageAssistant;
import util.ThreadLocalUtil;
import util.Util;

/**
 * 
 * @author lyc
 *         平台公告
 *
 */

@Service
@Transactional
public class RiskGuidelinesformService implements IRiskGuidelinesformService {

	@Resource
	private IRiskGuidelinesformMapper riskGuidelinesformMapper;

	@Override
	public PageAssistant queryRiskGuidelinesByPage(PageAssistant page) {
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
		List<Map<String, Object>> list = this.riskGuidelinesformMapper.queryRiskGuidelinesByPage(params);
		page.setList(list);

		return page;
	}

	@Override
	public String addRiskGuideline(String json) {

		String id = Util.getUUID();
		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("id", id);
		map.put("create_by", ThreadLocalUtil.getUserId());
		map.put("create_date", Util.now());
		map.put("status", "1");
		this.riskGuidelinesformMapper.addRiskGuideline(map);
		return id;
	}

	@Override
	public String modifyRiskGuideline(String json) {

		Map<String, Object> map = Util.parseJson2Map(json);
		this.riskGuidelinesformMapper.modifyRiskGuideline(map);

		return (String) map.get("ID");
	}

	@Override
	public void deleteRiskGuideline(String[] ids) {

		this.riskGuidelinesformMapper.deleteRiskGuideline(ids);

	}

	@Override
	public Map<String, Object> queryRideGuidelineInfo(String id) {

		return this.riskGuidelinesformMapper.queryRideGuidelineInfo(id);
	}
	
	@Override
	public Map<String, Object> queryRideGuidelineInfoForView(String id) {
		
		return this.riskGuidelinesformMapper.queryRideGuidelineInfoForView(id);
	}

	@Override
	public void submitRideGuideline(String id) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ID", id);
		map.put("STATUS", "2");

		this.riskGuidelinesformMapper.submitRideGuideline(map);

	}
	
	@Override
	public PageAssistant queryIndividualNotificationList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);

		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " STATUS ASC, PUBDATE DESC ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.riskGuidelinesformMapper.queryIndividualNotificationList(params);
		page.setList(list);

		return page;
	}

	@Override
	public List<Map<String, Object>> queryForIndividualTable() {

		return this.riskGuidelinesformMapper.queryForIndividualTable();
	}

	@Override
	public PageAssistant queryRiskGuidelinesByPageForSubmit(PageAssistant page) {
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
		List<Map<String, Object>> list = this.riskGuidelinesformMapper.queryRiskGuidelinesByPage(params);
		page.setList(list);
		return page;
	}

}
