package com.yk.rcm.newProjectBoard.service;

import common.PageAssistant;

/**
 * @author Sunny Qi
 *
 */
public interface IProjectBoardService {

	/**
	 * 查询项目
	 */
	public PageAssistant getProjectList(PageAssistant page, String json);
}
