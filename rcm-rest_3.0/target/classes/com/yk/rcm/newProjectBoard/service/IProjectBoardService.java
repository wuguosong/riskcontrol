package com.yk.rcm.newProjectBoard.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IProjectBoardService {
	
	/**
	 * 查询项目
	 * */
	public PageAssistant getProjectList(PageAssistant page, String json);
}
