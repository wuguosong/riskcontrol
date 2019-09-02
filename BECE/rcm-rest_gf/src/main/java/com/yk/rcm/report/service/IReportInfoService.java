package com.yk.rcm.report.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 
 * @author yaphet
 * 
 * 2017年6月19日
 *
 */
public interface IReportInfoService {

	void saveReport(Map<String, Object> params);

	void updateReportByBusinessId(Map<String, Object> params);

	List<Map<String, Object>> queryPertainAreaAchievement();

	List<Map<String, Object>> queryProjectsByPertainareaid(PageAssistant page);

}
