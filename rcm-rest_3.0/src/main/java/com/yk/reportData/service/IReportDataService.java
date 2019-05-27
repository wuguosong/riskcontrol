package com.yk.reportData.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IReportDataService {
	
	/**
	 * 将Mongo数据存储到Oracle报表中
	 * @param projectList
	 */
	public void saveOrUpdateReportData(String Json) throws Exception;
}
