package com.yk.rcm.pre.service;
import java.util.Map;
import common.PageAssistant;

public interface IPreReportBoardService {

	/**
	 * 获取投标评审的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryPreReportBoardByPage(PageAssistant page);
	/**
	 * 获取统计数量
	 * @return
	 */
	public Map<String, Object> getCounts();

}
