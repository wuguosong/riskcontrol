package com.yk.rcm.bulletin.service;
import java.util.Map;
import common.PageAssistant;

public interface IBulletinReportBoardService {

	/**
	 * 获取投标评审的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @return {@link PageAssistant}
	 * 
	 */
	public PageAssistant queryBulletinReportBoardByPage(PageAssistant page);
	/**
	 * 获取统计数量
	 * @return
	 */
	public Map<String, Object> getCounts();

}
