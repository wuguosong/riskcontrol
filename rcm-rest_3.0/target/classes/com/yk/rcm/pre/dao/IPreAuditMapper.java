package com.yk.rcm.pre.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface IPreAuditMapper extends BaseMapper {

	List<Map<String, Object>> queryWaitList(Map<String, Object> params);

	List<Map<String, Object>> queryAuditedList(Map<String, Object> params);

}
