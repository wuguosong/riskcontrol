package com.yk.rcm.templateFile.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.riskGuideLines.service.IRiskGuidelinesformService;
import com.yk.rcm.templateFile.dao.ITemplateFileformMapper;
import com.yk.rcm.templateFile.service.ITemplateFileformService;

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
public class TemplateFileformService implements ITemplateFileformService {

	@Resource
	private ITemplateFileformMapper templateFileformMapper;

	@Override
	public PageAssistant queryTemplateFilesByPage(PageAssistant page) {
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
		List<Map<String, Object>> list = this.templateFileformMapper.queryTemplateFilesByPage(params);
		page.setList(list);

		return page;
	}

	@Override
	public String addTemplateFile(String json) {

		String id = Util.getUUID();
		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("id", id);
		map.put("create_by", ThreadLocalUtil.getUserId());
		map.put("create_date", Util.now());
		map.put("status", "1");
		this.templateFileformMapper.addTemplateFile(map);
		return id;
	}

	@Override
	public String modifyTemplateFile(String json) {

		Map<String, Object> map = Util.parseJson2Map(json);
		this.templateFileformMapper.modifyTemplateFile(map);

		return (String) map.get("ID");
	}

	@Override
	public void deleteTemplateFile(String[] ids) {

		this.templateFileformMapper.deleteTemplateFile(ids);

	}

	@Override
	public Map<String, Object> queryTemalateFileInfo(String id) {

		return this.templateFileformMapper.queryTemalateFileInfo(id);
	}
	
	@Override
	public Map<String, Object> queryRideGuidelineInfoForView(String id) {
		
		return this.templateFileformMapper.queryRideGuidelineInfoForView(id);
	}

	@Override
	public void submitTemalateFile(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ID", id);
		map.put("STATUS", "2");
		this.templateFileformMapper.submitTemalateFile(map);

	}
	

	@Override
	public PageAssistant queryRiskGuidelinesForSubmit(PageAssistant page) {
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
		List<Map<String, Object>> list = this.templateFileformMapper.queryTemplateFilesByPage(params);
		page.setList(list);
		return page;
	}


}
