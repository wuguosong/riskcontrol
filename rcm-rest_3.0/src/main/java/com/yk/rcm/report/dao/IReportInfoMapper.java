package com.yk.rcm.report.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author yaphet
 * 
 * 2017年6月19日
 *
 */
public interface IReportInfoMapper extends BaseMapper{

	List<Map<String, Object>> queryPertainAreaAchievement();
	
	void saveReport(Map<String, Object> params);

	void updateReportByBusinessId(Map<String, Object> params);

	List<Map<String, Object>> queryProjectsByPertainareaid(Map<String, Object> paramMap);

}
