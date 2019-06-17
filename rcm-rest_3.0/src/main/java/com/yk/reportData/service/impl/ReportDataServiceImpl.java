package com.yk.reportData.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.reportData.dao.IReportDataMapper;
import com.yk.reportData.service.IReportDataService;
import common.Constants;
import common.PageAssistant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.Util;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReportDataServiceImpl implements IReportDataService {

	@Resource
	private IReportDataMapper reportDataMapper;
	@Resource
	private IBaseMongo baseMongo;

	private Logger logger = LoggerFactory.getLogger(ReportDataServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public void saveOrUpdateReportData(String Json) throws Exception {
		HashMap<String, Object> jsonMap = JSON.parseObject(Json, HashMap.class);
		String projectType = (String) jsonMap.get("projectType");
		if (Util.isNotEmpty(projectType)) {
			if ("pfr".equals(projectType)) {
				this.saveOrUpdatePfrData(jsonMap);
			} else if ("pre".equals(projectType)) {
				this.saveOrUpdatePreData(jsonMap);
			} else {
				this.saveOrUpdateBulletinData(jsonMap);
			}
		}
	}

	// 保存正式评审报表信息
	public void saveOrUpdatePfrData(Map<String, Object> jsonMap) throws Exception {
		List<Map<String, Object>> list = reportDataMapper.getPfrProjectList(jsonMap);
		for (int i = 0; i < list.size(); i++) {
			String businessId = list.get(i).get("BUSINESSID").toString();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("BUSINESSID", businessId);
			System.out.println("BUSINESSID===========================================" + businessId);
			Map<String, Object> report = reportDataMapper.getPfrProjectByBusinessID(params);
			Map<String, Object> dataForOracle = this.dataForPfr(businessId);
			// 判断该数据是否已存在
			if (Util.isEmpty(report)) {
				reportDataMapper.insertPfr(dataForOracle);
				logger.info("新增报表数据成功：[正式评审," + businessId + "]");
			} else {
				dataForOracle.put("ID", report.get("ID"));
				reportDataMapper.updatePfr(dataForOracle);
				logger.info("修改报表数据成功：[正式评审," + businessId + "]");
			}
		}
	}

	// 整理正式评审报表数据
	public Map<String, Object> dataForPfr(String businessId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("BUSINESSID", businessId);

		/** 整理INFO表数据开始 **/
		Map<String, Object> pfrInfo = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		Document apply = (Document) pfrInfo.get("apply");
		data.put("PROJECT_ADDRESS", apply.get("projectAddress"));
		data.put("SINGLE_ITEMS_NUM", apply.get("projectNum"));
		Document supplement = (Document) apply.get("supplement");
		if (Util.isNotEmpty(supplement)) {
			data.put("CHANGE_OPTION", supplement.get("changeOption"));
		} else {
			data.put("CHANGE_OPTION", "");
		}
		/** 整理INFO表数据结束 **/

		/** 整理决策会表数据开始 **/
		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> pfrDecisions = baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_NOTICEDECISION_INFO);
		if (Util.isNotEmpty(pfrDecisions)) {
			Map<String, Object> pfrDecision = pfrDecisions.get(0);

			data.put("CONTRACT_SCALE", pfrDecision.get("contractScale"));
			data.put("EVALUATION_SCALE", pfrDecision.get("evaluationScale"));
			data.put("REVIEW_OF_TOTAL_INVESTMENT", pfrDecision.get("reviewOfTotalInvestment"));
			data.put("CONSENT_TO_INVESTMENT", pfrDecision.get("consentToInvestment"));
			data.put("IMPLEMENTATION_MATTERS", pfrDecision.get("implementationMatters"));
			Document subjectOfImplementation = (Document) pfrDecision.get("subjectOfImplementation");
			if (Util.isNotEmpty(subjectOfImplementation)) {
				data.put("SUBJECT_OF_IMPLEMENTATION", subjectOfImplementation.get("value"));
			}
			data.put("REGISTER", pfrDecision.get("register"));
			Document reportingUnit = (Document) pfrDecision.get("reportingUnit");
			if (Util.isNotEmpty(reportingUnit)) {
				data.put("RESPONSIBILITYUNIT_VALUE", reportingUnit.get("value"));
			}
			data.put("IMPLEMENTATION_REQUIREMENTS", pfrDecision.get("implementationRequirements"));
		}
		/** 整理决策会表数据结束 **/

		/** 整理会议详情表数据开始 **/
		BasicDBObject queryAndWhere1 = new BasicDBObject();
		queryAndWhere1.put("formalId", businessId);
		List<Map<String, Object>> pfrMeetings = baseMongo.queryByCondition(queryAndWhere1,
				Constants.FORMAL_MEETING_INFO);
		if (Util.isNotEmpty(pfrMeetings)) {
			Map<String, Object> pfrMeeting = pfrMeetings.get(0);
			Document projectRating = (Document) pfrMeeting.get("projectRating");
			if (Util.isNotEmpty(projectRating)) {
				data.put("PROJECT_RATE", projectRating.get("value"));
			}
			data.put("RATE_REASON", pfrMeeting.get("ratingReason"));
			Document openMeetingPerson = (Document) pfrMeeting.get("openMeetingPerson");
			if (Util.isNotEmpty(openMeetingPerson)) {
				data.put("OPEN_MEETING_PERSON", openMeetingPerson.get("value"));
			}
			List<Document> decisionOpinionList = (List<Document>) pfrMeeting.get("decisionOpinionList");
			if (Util.isNotEmpty(decisionOpinionList)) {
				String meetingLeaders = "";
				String votingResults = "";
				for (int i = 0; i < decisionOpinionList.size(); i++) {
					Document decisionOption = decisionOpinionList.get(i);
					Object result = decisionOption.get("aagreeOrDisagree");
					if (Util.isEmpty(result)) {
						result = "0";
					}
					if (i < decisionOpinionList.size() - 1) {
						meetingLeaders = meetingLeaders + decisionOption.get("userId") + ",";
						votingResults = votingResults + result + ",";
					} else {
						meetingLeaders = meetingLeaders + decisionOption.get("userId");
						votingResults = votingResults + result;
					}
				}
				data.put("MEETING_LEADERS", meetingLeaders);
				data.put("VOTING_RESULTS", votingResults);
			}
		}
		/** 整理会议详情表数据结束 **/

		/** 整理报告详情表开始 **/
		List<Map<String, Object>> pfrReports = baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_FORMALREPORT_INFO);
		if (Util.isNotEmpty(pfrReports)) {
			Map<String, Object> pfrReport = pfrReports.get(0);
			data.put("FK_PS_RESULT", pfrReport.get("fkPsResult"));
			data.put("FK_RISK_TIP", pfrReport.get("fkRiskTip"));
			Document policyDecision = (Document) pfrReport.get("policyDecision");
			if (Util.isNotEmpty(policyDecision)) {
				data.put("RATEOFRETURN", policyDecision.get("rateOfReturn"));
				data.put("SPECIAL_NOTICE", policyDecision.get("specialNotice"));
			}
		}
		/** 整理报告详情表结束 **/

		/** 整理提交决策会材料模板表开始 **/
		Map<String, Object> summary = this.baseMongo.queryById(businessId, Constants.RCM_FORMAL_SUMMARY);
		if (Util.isNotEmpty(summary)) {
			Document investmentType = (Document) summary.get("investmentType");
			if (Util.isNotEmpty(investmentType)) {
				data.put("INVESTMENT_TYPE", investmentType.get("ITEM_CODE"));
			}
			Document project = (Document) summary.get("project");
			if (Util.isNotEmpty(project)) {
				data.put("INVESTMENT_GLOBAL", project.get("investmentGlobale"));
				data.put("INVESTMENT_INTERNAL", project.get("investmentInternal"));
				data.put("INVESTMENT_STATIC", project.get("investmentStatic"));
				data.put("FINANCIAL_ROE", project.get("financialROE"));
				data.put("FINANCIAL_ROI", project.get("financialROI"));
				data.put("INDICATOR_CASH", project.get("indicatorCash"));
			}
		}
		/** 整理提交决策会材料模板表结束 **/

		return data;
	}

	// 保存投标评审报表信息
	public void saveOrUpdatePreData(Map<String, Object> jsonMap) throws Exception {
		List<Map<String, Object>> list = reportDataMapper.getPreProjectList(jsonMap);
		for (int i = 0; i < list.size(); i++) {
			String businessId = list.get(i).get("BUSINESSID").toString();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("BUSINESSID", businessId);
			Map<String, Object> report = reportDataMapper.getPreProjectByBusinessID(params);
			Map<String, Object> dataForOracle = this.dataForPre(businessId);
			// 判断该数据是否已存在
			if (Util.isEmpty(report)) {
				reportDataMapper.insertPre(dataForOracle);
				logger.info("新增报表数据成功：[投标评审," + businessId + "]");
			} else {
				dataForOracle.put("ID", report.get("ID"));
				reportDataMapper.updatePre(dataForOracle);
				logger.info("修改报表数据成功：[投标评审," + businessId + "]");
			}
		}
	}

	// 整理投标评审报表数据
	public Map<String, Object> dataForPre(String businessId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("BUSINESSID", businessId);

		/** 整理INFO表数据开始 **/
		Map<String, Object> preInfo = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		Document apply = (Document) preInfo.get("apply");
		data.put("PROJECT_ADDRESS", apply.get("projectAddress"));
		data.put("SINGLE_ITEMS_NUM", apply.get("projectNum"));
		Document meetingInfo = (Document) preInfo.get("meetingInfo");
		if (Util.isNotEmpty(meetingInfo)) {
			Document openMeetingPerson = (Document) meetingInfo.get("openMeetingPerson");
			if (Util.isNotEmpty(openMeetingPerson)) {
				data.put("OPEN_MEETING_PERSON", openMeetingPerson.get("value"));
			}
			Document projectRating = (Document) meetingInfo.get("projectRating");
			if (Util.isNotEmpty(projectRating)) {
				data.put("PROJECT_RATE", projectRating.get("value"));
			}
			data.put("RATE_REASON", meetingInfo.get("ratingReason"));
			Document investmentType = (Document) meetingInfo.get("investmentType");
			if (Util.isNotEmpty(investmentType)) {
				data.put("INVESTMENT_TYPE", investmentType.get("ITEM_CODE"));
			}
			List<Document> decisionOpinionList = (List<Document>) meetingInfo.get("decisionOpinionList");
			if (Util.isNotEmpty(decisionOpinionList)) {
				String meetingLeaders = "";
				String votingResults = "";
				for (int i = 0; i < decisionOpinionList.size(); i++) {
					Document decisionOption = decisionOpinionList.get(i);
					Object result = decisionOption.get("aagreeOrDisagree");
					if (Util.isEmpty(result)) {
						result = "0";
					}
					if (i < decisionOpinionList.size() - 1) {
						meetingLeaders = meetingLeaders + decisionOption.get("userId") + ",";
						votingResults = votingResults + result + ",";
					} else {
						meetingLeaders = meetingLeaders + decisionOption.get("userId");
						votingResults = votingResults + result;
					}
				}
				data.put("MEETING_LEADERS", meetingLeaders);
				data.put("VOTING_RESULTS", votingResults);
			}
		}

		data.put("FK_PS_RESULT", preInfo.get("fkPsResult"));
		data.put("FK_RISK_TIP", preInfo.get("fkRiskTip"));
		Document policyDecision = (Document) preInfo.get("policyDecision");
		if (Util.isNotEmpty(policyDecision)) {
			data.put("SPECIAL_NOTICE", policyDecision.get("specialNotice"));
		}
		/** 整理INFO表数据结束 **/

		return data;
	}

	// 保存其他评审报表信息
	public void saveOrUpdateBulletinData(Map<String, Object> jsonMap) throws Exception {
		List<Map<String, Object>> list = reportDataMapper.getBulletinProjectList(jsonMap);
		for (int i = 0; i < list.size(); i++) {
			String businessId = list.get(i).get("BUSINESSID").toString();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("BUSINESSID", businessId);
			Map<String, Object> report = reportDataMapper.getBulletinProjectByBusinessID(params);
			Map<String, Object> dataForOracle = this.dataForBulletin(businessId);
			// 判断该数据是否已存在
			if (Util.isEmpty(report)) {
				reportDataMapper.insertBulletin(dataForOracle);
				logger.info("新增报表数据成功：[其他评审," + businessId + "]");
			} else {
				dataForOracle.put("ID", report.get("ID"));
				reportDataMapper.updateBulletin(dataForOracle);
				logger.info("修改报表数据成功：[其他评审," + businessId + "]");
			}
		}
	}

	// 整理其他评审报表数据
	public Map<String, Object> dataForBulletin(String businessId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("BUSINESSID", businessId);

		/** 整理INFO表数据开始 **/
		Map<String, Object> bulletinInfo = baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		if (Util.isNotEmpty(bulletinInfo)) {
			Document taskallocation = (Document) bulletinInfo.get("taskallocation");
			if (Util.isNotEmpty(taskallocation)) {
				Document reviewLeader = (Document) taskallocation.get("reviewLeader");
				if (Util.isNotEmpty(reviewLeader)) {
					data.put("REVIEWPERSONID", reviewLeader.get("VALUE"));
				}
				Document legalLeader = (Document) taskallocation.get("legalLeader");
				if (Util.isNotEmpty(legalLeader)) {
					data.put("LEGALREVIEWPERSONID", legalLeader.get("VALUE"));
				}
			}

			Document meeting = (Document) bulletinInfo.get("meeting");
			if (Util.isNotEmpty(meeting)) {
				Document openMeetingPerson = (Document) meeting.get("openMeetingPerson");
				// 此处OPEN_MEETING_PERSON存值有大写有小写，需要作区分
				if (Util.isNotEmpty(openMeetingPerson)) {
					if (Util.isNotEmpty(openMeetingPerson.get("VALUE"))) {
						data.put("OPEN_MEETING_PERSON", openMeetingPerson.get("VALUE"));
					} else {
						data.put("OPEN_MEETING_PERSON", openMeetingPerson.get("value"));
					}
				}
				List<Document> decisionOpinionList = (List<Document>) meeting.get("decisionOpinionList");
				if (Util.isNotEmpty(decisionOpinionList)) {
					String meetingLeaders = "";
					String votingResults = "";
					for (int i = 0; i < decisionOpinionList.size(); i++) {
						Document decisionOption = decisionOpinionList.get(i);
						Object result = decisionOption.get("aagreeOrDisagree");
						if (Util.isEmpty(result)) {
							result = "0";
						}
						if (i < decisionOpinionList.size() - 1) {
							meetingLeaders = meetingLeaders + decisionOption.get("userId") + ",";
							votingResults = votingResults + result + ",";
						} else {
							meetingLeaders = meetingLeaders + decisionOption.get("userId");
							votingResults = votingResults + result;
						}
					}
					data.put("MEETING_LEADERS", meetingLeaders);
					data.put("VOTING_RESULTS", votingResults);
				}
			}
		}
		/** 整理INFO表数据结束 **/

		return data;
	}

	@Override
	public PageAssistant getProjectList(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap();
		Map<String, Object> params1 = new HashMap();
		JSONObject js = JSONObject.parseObject(json, JSONObject.class);
		String userId = null;
		if(js != null){
			if(js.get("loginUser") != null){
				userId = this.getMeetingLeaderByCurrentUser(js.getString("loginUser"));
			}
		}
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
			params1.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = null;
		if(StringUtils.isNotBlank(userId)){
			params.put("meetingLeader", userId);
			params1.put("meetingLeader", userId);
		}
		list = this.reportDataMapper.getProjectList(params);
		// 设置表决结果
		this.setVotingResult(list, userId);
		int totalCount = this.reportDataMapper.getProjectListCount(params1);
		page.setTotalItems(totalCount);
		page.setList(list);
		return page;
	}

	@Override
	public String getMeetingLeaderByCurrentUser(String loginUser) {
		List<Map<String, Object>> list = reportDataMapper.getLeaderSecretaryMappingList();
		String userId = null;
		if(CollectionUtils.isEmpty(list)){
			return userId;
		}
		// 先直接判断领导是否是登录用户
		for(Map<String,Object> map : list){
			String leader = String.valueOf(map.get("leader"));
			if(loginUser.equals(leader)){
				return loginUser;
			}
		}
		// 再判断当前登录用户是否是秘书，取其映射的领导
		for(Map<String,Object> map : list){
			String secretary = String.valueOf(map.get("secretary"));
			if(loginUser.equals(secretary)){
				String leader = String.valueOf(map.get("leader"));
				return leader;
			}
		}
		return userId;
	}

	/**
	 * 设置表决结果
	 * @param list
	 */
	private void setVotingResult(List<Map<String, Object>> list,String userId){
		if(StringUtils.isNotBlank(userId)){
			if(CollectionUtils.isNotEmpty(list)){
				for(Map<String, Object> map : list){
					String leaders = String.valueOf(map.get("MEETING_LEADERS"));
					if(StringUtils.isNotBlank(leaders)){
						String[] leadersArray = null;
						if(leaders.contains(",")){
							leadersArray = leaders.split(",");
						}else{
							leadersArray = new String[]{leaders};
						}
						int idx = -1;
						// 获取评审索引
						for(int i = 0; i < leadersArray.length; i++){
							if(userId.equals(leadersArray[i])){
								idx = i;
								break;
							}
						}
						// 获取评审结果
						if(idx != -1){
							String votes = String.valueOf(map.get("VOTING_RESULTS"));
							if(StringUtils.isNotBlank(votes)){
								String[] votesArray = null;
								if(votes.contains(",")){
									votesArray = votes.split(",");
								}else{
									votesArray = new String[]{votes};
								}
								for(int j = 0; j < votesArray.length; j++){
									if(j == idx){
										map.put("VOTING_RESULT", votesArray[j]);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
