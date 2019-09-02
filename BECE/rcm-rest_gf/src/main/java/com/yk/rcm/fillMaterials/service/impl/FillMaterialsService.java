package com.yk.rcm.fillMaterials.service.impl;

import com.yk.common.BaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.fillMaterials.dao.IFillMaterialsMapper;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.project.service.IFormalAssesmentService;

import common.PageAssistant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * @author gaohe
 * 
 *         2019年3月26日
 *
 */

@Service
@Transactional
public class FillMaterialsService implements IFillMaterialsService {

	@Resource
	private BaseMongo baseMongo;
	@Resource
	private IFillMaterialsMapper fillMaterialsMapper;
	@Resource
	private IFormalAssesmentService formalAssesmentService;

//	正是评审RFI_IS_SUBMIT_REPORT 1)null:未新建报告;2）0:已新建报告，未提交;3)1:报告已提交

	@Override
	public PageAssistant queryNoSubmitList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("userId", retMap.get("userId"));
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = fillMaterialsMapper.queryNoSubmitList(params);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant querySubmitList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("userId", retMap.get("userId"));
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = fillMaterialsMapper.querySubmitList(params);
		page.setList(list);
		return page;
	}

	@Override
	public void updateProjectStaus(Map<String, Object> params) {
//		String table, String filed, String BUSINESSID, String status
		fillMaterialsMapper.updateProjectStaus(params);
	}

	@Override
	public Map<String, Object> getRFIStatus(String businessid) {
		Map<String, Object> object = fillMaterialsMapper.getRFIStatus(businessid);
		return object;
	}

	@Override
	public Map<String, Object> getRPIStatus(String businessid) {
		Map<String, Object> object = fillMaterialsMapper.getRPIStatus(businessid);
		return object;
	}

	@Override
	public void updateProjectBiddingStaus(Map<String, Object> params) {
		fillMaterialsMapper.updateProjectBiddingStaus(params);
	}

}
