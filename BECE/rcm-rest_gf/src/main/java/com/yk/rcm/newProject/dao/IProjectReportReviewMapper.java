package com.yk.rcm.newProject.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;


/**
 * @author Sunny Qi
 *
 */
public interface IProjectReportReviewMapper extends BaseMapper {
	
	public List<Map<String, Object>> queryProjectReportListByPage(
			Map<String, Object> params);
}
