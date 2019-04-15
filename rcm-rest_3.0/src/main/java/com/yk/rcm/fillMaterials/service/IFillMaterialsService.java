package com.yk.rcm.fillMaterials.service;

import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author gaohe
 * 
 * 2019年3月26日
 *
 */

public interface IFillMaterialsService {
	
	public PageAssistant queryNoSubmitList(PageAssistant page);

	public PageAssistant querySubmitList(PageAssistant page);
	
	void updateProjectStaus(Map<String, Object> params);
	
	public Map<String, Object> getRFIStatus(String businessid);
	
}
