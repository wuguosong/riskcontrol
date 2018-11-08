package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPersonnelWorkMapper extends BaseMapper{

	public List<Map<String, Object>> queryFormalGoingList(Map<String, Object> params);

	public List<Map<String, Object>> queryFormalDealedList(Map<String, Object> paramMap);

	public List<Map<String, Object>> queryPreGoingList(Map<String, Object> paramMap);

	public List<Map<String, Object>> queryPreDealedList(Map<String, Object> paramMap);
	
}
