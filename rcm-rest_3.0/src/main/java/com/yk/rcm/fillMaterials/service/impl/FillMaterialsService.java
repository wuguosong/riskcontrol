package com.yk.rcm.fillMaterials.service.impl;

import com.alibaba.fastjson.JSON;
import com.yk.common.BaseMongo;
import com.yk.rcm.fillMaterials.dao.IFillMaterialsMapper;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.project.service.IFormalAssesmentService;

import common.PageAssistant;
import util.Util;

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
	public PageAssistant queryNoSubmitList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (null != page.getParamMap() && page.getParamMap().size() > 0) {
			params.putAll(page.getParamMap());
			System.out.println(JSON.toJSON(page));
		}
		List<Map<String, Object>> list = fillMaterialsMapper.queryNoSubmitList(params);
		for (Map<String, Object> map : list) {
			if (map.get("PROJECT_TYPE").equals("pfr")) {
				Map<String, Object> summary = this.formalAssesmentService
						.querySummaryById(map.get("BUSINESSID").toString());
				// 判断在途数据
				if (map.get("STAGE").equals("3.9")) {
					map.put("RPI_IS_SUBMIT_REPORT", "1");
					map.put("RPI_IS_SUBMIT_BIDDING", "1");
					if (Util.isNotEmpty(summary)) {
						map.put("RPI_IS_SUBMIT_REPORT", "0");
					}
				} else if (map.get("STAGE").equals("3.5")) {
					map.put("RPI_IS_SUBMIT_REPORT", "1");
					if (Util.isNotEmpty(map.get("PFR_NOTICE_CREATE_DATE"))) {
						map.put("RPI_IS_SUBMIT_BIDDING", "0");
					}
				} else if (map.get("STAGE").equals("3")) {
					if (Util.isNotEmpty(map.get("PFR_REPORT_CREATE_DATE"))) {
						map.put("RPI_IS_SUBMIT_REPORT", "0");
					}
				}

			}
		}

		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant querySubmitList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (null != page.getParamMap() && page.getParamMap().size() > 0) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = fillMaterialsMapper.querySubmitList(params);
		page.setList(list);
		return page;
	}

}
