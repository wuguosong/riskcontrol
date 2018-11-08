package com.yk.rcm.formalAssessment.service;


import java.util.Map;

import common.PageAssistant;

public interface IFormalReportBoardService {

	/**
	 * 获取正式评审的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryFormalReportBoardByPage(PageAssistant page);
	/**
	 * 获取统计数量
	 * @return
	 */
	public Map<String, Object> getCounts();

}
