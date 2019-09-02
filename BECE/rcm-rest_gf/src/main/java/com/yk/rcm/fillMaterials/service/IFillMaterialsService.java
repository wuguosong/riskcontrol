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
	
	public PageAssistant queryNoSubmitList(PageAssistant page, String json);

	public PageAssistant querySubmitList(PageAssistant page, String json);
	
	void updateProjectStaus(Map<String, Object> params);
	
	public Map<String, Object> getRFIStatus(String businessid);
	
	public Map<String, Object> getRPIStatus(String businessid);

	public void updateProjectBiddingStaus(Map<String, Object> statusMap);
	
}
