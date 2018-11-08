package com.yk.rcm.noticeofdecision.dao;

import java.util.Map;

import com.yk.common.BaseMapper;

public interface INoticeDecisionRoleMapper extends BaseMapper {
	String hasRole(Map<String, Object> paramMap);
}
