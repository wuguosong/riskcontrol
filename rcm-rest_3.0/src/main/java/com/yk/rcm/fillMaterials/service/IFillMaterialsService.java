package com.yk.rcm.fillMaterials.service;

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
	
}