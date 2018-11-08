package com.yk.rcm.decision.serevice.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IDictService;
import com.yk.power.service.IRoleService;
import com.yk.rcm.decision.dao.IDecisionReviewMapper;
import com.yk.rcm.decision.serevice.IDecisionReviewService;
import com.yk.rcm.decision.serevice.IDecisionService;

import common.Constants;
import common.PageAssistant;

@Service
@Transactional
public class DecisionReviewServiceImpl implements IDecisionReviewService {

	@Resource
	private IDecisionService decisionService;

	@Resource
	private IDecisionReviewMapper decisionReviewMapper;

	@Resource
	public IRoleService roleService;

	@Resource
	public IBaseMongo baseMongo;

	@Resource
	private IDictService dictService;

	@Override
	public List<Map<String, Object>> queryList() {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("decisionDate", Util.format(new Date(), "yyyy-MM-dd"));
		List<Map<String, Object>> projects = decisionReviewMapper
				.queryList(params);
		if (null == projects || projects.size() == 0)
			return projects;
		// 获取当前用户
		String userId = ThreadLocalUtil.getUserId();

		// 获取此数据
		List<Map<String, Object>> decisionOpinionList = null;

		BasicDBObject queryAndWhere = new BasicDBObject();
		for (Map<String, Object> project : projects) {
			String businessId = project.get("BUSINESS_ID").toString();
			String projectType = project.get("PROJECT_TYPE").toString();

			Map<String, Object> queryData = null;
			if ("pfr".equals(projectType)) {
				queryAndWhere.put("formalId", businessId);
				queryData = baseMongo.queryByCondition(queryAndWhere,
						Constants.FORMAL_MEETING_INFO).get(0);
				decisionOpinionList = (List<Map<String, Object>>) queryData
						.get("decisionOpinionList");
			} else if ("bulletin".equals(projectType)) {
				queryAndWhere.put("_id", businessId);
				queryData = baseMongo.queryByCondition(queryAndWhere,
						Constants.RCM_BULLETIN_INFO).get(0);
				// 当前用户表决意见
				Map meetingData = (Map) queryData.get("meeting");
				decisionOpinionList = (List<Map<String, Object>>) meetingData
						.get("decisionOpinionList");
			} else if ("pre".equals(projectType)) {
				queryData = baseMongo.queryById(businessId,
						Constants.RCM_PRE_INFO);
				// 当前用户表决意见
				Map meetingData = (Map) queryData.get("meetingInfo");
				decisionOpinionList = (List<Map<String, Object>>) meetingData
						.get("decisionOpinionList");
			}
			if (null != decisionOpinionList) {
				for (Map<String, Object> map : decisionOpinionList) {
					if (userId.equals(map.get("userId").toString())) {
						project.put("aagreeOrDisagree",
								map.get("aagreeOrDisagree"));
						break;
					}
				}
			}
			queryAndWhere.clear();
		}
		return projects;
	}

	@Override
	public int countTodayLater() {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("decisionDate", Util.format(new Date(), "yyyy-MM-dd"));
		int countTodayLater = decisionReviewMapper.countTodayLater(params);
		return countTodayLater;
	}

	@Override
	public void queryWaitDecisionListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("todayDate", Util.format(new Date(), "yyyy-MM-dd"));
		// 如果不是委员 并且不是 管理员 而是评审负责人，则做条件过滤
		boolean ifDecisionLeaders = roleService
				.ifRoleContainUser(Constants.ROLE_CODE_DECISION_LEADERS);
		boolean isAdmin = ThreadLocalUtil.getIsAdmin();
		boolean ifCodeReviewfzr = roleService
				.ifRoleContainUser(Constants.ROLE_CODE_REVIEWFZR);
		if (!ifDecisionLeaders && !isAdmin && ifCodeReviewfzr) {
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		List<Map<String, Object>> list = decisionReviewMapper
				.queryWaitDecisionListByPage(params);
		page.setList(list);
	}

	@Override
	public int countHistoryDecision() {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("todayDate", Util.format(new Date(), "yyyy-MM-dd"));
		int countTodayLater = decisionReviewMapper.countHistoryDecision(params);
		return countTodayLater;
	}

	@Override
	public void queryHistoryDecisionReviewListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("todayDate", Util.format(new Date(), "yyyy-MM-dd"));
		List<Map<String, Object>> list = decisionReviewMapper
				.queryHistoryDecisionReviewListByPage(params);
		page.setList(list);
	}

	@Override
	public PageAssistant queryXsh(PageAssistant page) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> decisions = decisionReviewMapper
				.queryXsh(params);

		page.setList(decisions);
		return page;

	}

	@Override
	public PageAssistant queryWxsh(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> decisions = decisionReviewMapper
				.queryWxsh(params);
		page.setList(decisions);
		return page;
	}
}
