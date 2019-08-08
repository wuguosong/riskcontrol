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
	
	/**
	 * 查询正式评审项目
	 */
	public PageAssistant getPfrProjectList(PageAssistant page, String json);
	
	/**
	 * 查询投标评审项目
	 */
	public PageAssistant getPreProjectList(PageAssistant page, String json);
	
	/**
	 * 查询其他评审项目
	 */
	public PageAssistant getBulletinProjectList(PageAssistant page, String json);

	/**
	 * 大区负责人首页查询项目方法
	 * */
	public PageAssistant getProjectListForCompanyHead(PageAssistant page, String json);
}
