package com.yk.rcm.decision.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;


/**
 * @author hubiao
 *
 */
public interface IDecisionReviewMapper extends BaseMapper {

	public List<Map<String, Object>> queryList(Map<String, Object> params);

	public int countTodayLater(Map<String, Object> params);

	public List<Map<String, Object>> queryWaitDecisionListByPage(
			Map<String, Object> params);

	public int countHistoryDecision(Map<String, Object> params);

	public List<Map<String, Object>> queryHistoryDecisionReviewListByPage(
			Map<String, Object> params);

	public List<Map<String, Object>> queryXsh(Map<String, Object> params);

	public List<Map<String, Object>> queryWxsh(Map<String, Object> params);
}
