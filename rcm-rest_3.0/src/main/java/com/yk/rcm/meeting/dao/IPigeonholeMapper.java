package com.yk.rcm.meeting.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPigeonholeMapper extends BaseMapper {

	public List<Map<String, Object>> queryProject(Map<String, Object> param);
	
	public List<Map<String, Object>> queryPfrContract(Map<String, Object> param);
}
