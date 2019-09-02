package com.yk.rcm.newProject.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IProjectReportReviewService {
	
	/**
	 * 获取已过会和无需上会的项目
	 * */
	public void queryProjectReportListByPage(PageAssistant page);
}
