package com.yk.rcm.fillMaterials.service.impl;

import com.yk.common.BaseMongo;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.power.service.IRoleService;
import com.yk.rcm.fillMaterials.dao.IFillMaterialsMapper;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
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
	

	@Override
	public PageAssistant queryNoSubmitList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = fillMaterialsMapper.queryNoSubmitList(params);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant querySubmitList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(null != page.getParamMap() && page.getParamMap().size() > 0){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = fillMaterialsMapper.querySubmitList(params);
		page.setList(list);
		return page;
	}

}
