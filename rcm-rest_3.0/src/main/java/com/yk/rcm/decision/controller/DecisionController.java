package com.yk.rcm.decision.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.flow.util.JsonUtil;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;

import common.Result;

/**
 * @author hubiao
 *
 */
@Controller
@RequestMapping("/decision")
public class DecisionController {
	@Resource
	private IDecisionService decisionService;

	@Resource
	private IMeetingIssueService meetingIssueService;

	/**
	 * 根据条件查询所有决策会议
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryList")
	@ResponseBody
	public Result queryList() {
		Result result = new Result();
		List<Map<String, Object>> queryList = decisionService.queryList();
		result.setResult_data(queryList);
		return result;
	}

	/**
	 * 开始项目决策
	 * 
	 * @param id
	 * @param meetingLeaders
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/entryDecision")
	@ResponseBody
	public Result entryDecision(String id, String meetingLeaders) {
		Result result = ifIsEntryDecision(id);
		if (result.isSuccess()) {
			result = isCurrUnderwayProject();
			if (result.isSuccess()) {
				// 1:验证候选委员中是否含有会议主席
				List<Map<String, Object>> meetingLeadersList = JsonUtil.fromJson(meetingLeaders, List.class);
				String chairmanId = decisionService.getChairman(meetingLeadersList);
				if (StringUtils.isNotEmpty(chairmanId)) {
					// 2、更新项目的决策会委员和主席
					this.decisionService.updateMeetingLeadersById(id, meetingLeadersList, chairmanId);
					// 3、更新项目状态为表决中
					decisionService.updateVoteStatus(id, 1);
				} else {
					result.setSuccess(false).setResult_name("决策委员中没有会议主席，不能开始表决！");
				}
			}
		}
		return result;
	}

	/**
	 * 是否有项目正在决策中
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/isCurrUnderwayProject")
	@ResponseBody
	public Result isCurrUnderwayProject() {
		Result result = new Result();
		boolean isCurrUnderwayProject = decisionService.isCurrUnderwayProject();
		if (isCurrUnderwayProject) {
			result.setSuccess(false).setResult_name("有项目正在决策中,不能开启多个！");
		}
		return result;
	}

	/**
	 * 开始表决前验证 1:不能重复表决 2:不能开启多个表决项目
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/verificationEntryDecision")
	@ResponseBody
	public Result verificationEntryDecision(String id) {
		Result result = ifIsEntryDecision(id);
		if (result.isSuccess()) {
			result = isCurrUnderwayProject();
		}
		return result;
	}

	/**
	 * 判断项目是否已表决
	 * 
	 * @return
	 */
	@RequestMapping("/ifIsEntryDecision")
	@ResponseBody
	public Result ifIsEntryDecision(String id) {
		Result result = new Result();
		Map<String, Object> decision = decisionService.queryById(id);
		if (!"0".equals(decision.get("VOTE_STATUS").toString())) {
			result = new Result();
			result.setSuccess(false).setResult_name("不能重复开始表决!");
		}
		return result;
	}

	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(String id) {
		Result result = new Result();
		Map<String, Object> decision = decisionService.queryById(id);
		result.setResult_data(decision);
		return result;
	}

	/**
	 * 初始化 当前用户的决策信息
	 * 
	 * @return
	 */
	@RequestMapping("/initialize")
	@ResponseBody
	public Result initialize() {
		Result result = new Result();

		Map<String, Object> resultData = new HashMap<String, Object>(2);
		// 获取当前要上会的项目
		Map<String, Object> decisionsData = decisionService.getCurrUnderwayProject();
		if (null != decisionsData && decisionsData.size() > 0) {
			resultData.put("decisionsData", decisionsData);
		} else {
			// 没有要上会的项目，则查询当前用户今日 的已表决项目
			List<Map<String, Object>> historyDecisions = decisionService.queryHistory();
			if (null != historyDecisions && historyDecisions.size() > 0) {
				resultData.put("historyDecisions", historyDecisions);
			}
		}
		if (0 == resultData.size()) {
			resultData = null;
		}
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 添加 当前用户 决策 意见
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/addDecisionOpinion")
	@ResponseBody
	public Result addDecisionOpinion(String id, String formalId, String formalType, String aagreeOrDisagree) {
		Result result = new Result();
		// 如果项目在决策中，则才可以保存(防止出现与撤消项目时并发)
		Map<String, Object> decision = decisionService.queryById(id);
		if ("1".equals(decision.get("VOTE_STATUS").toString())) {
			decisionService.addDecisionOpinion(formalId, formalType, aagreeOrDisagree);
		} else {
			result.setResult_name("0");
		}
		return result;
	}

	/**
	 * 添加 当前用户 决策 意见
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/addDecisionOpinionNew")
	@ResponseBody
	public Result addDecisionOpinionNew(String id, String formalId, String formalType, String aagreeOrDisagree,
			String zhuxiStatus) {
		Result result = new Result();
		// 如果项目在决策中，则才可以保存(防止出现与撤消项目时并发)
		Map<String, Object> decision = decisionService.queryById(id);
		if ("1".equals(decision.get("VOTE_STATUS").toString())) {
			decisionService.addDecisionOpinionNew(formalId, formalType, aagreeOrDisagree, zhuxiStatus);
		} else {
			result.setResult_name("0");
		}
		return result;
	}

	/**
	 * 获取正在决策信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getCurrDecisionOpinion")
	@ResponseBody
	public Result getCurrDecisionOpinion(String id) {
		Result result = new Result();
		Map<String, Object> resultData = decisionService.getCurrDecisionOpinion(id);
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 获取决策结果信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDecisionResult")
	@ResponseBody
	public Result getDecisionResult(String id) {
		Result result = new Result();
		Map<String, Object> resultData = decisionService.getDecisionResult(id);
		decisionService.isShiYongFouJueQuan(resultData);
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 获取决策结果信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDecisionResultNew")
	@ResponseBody
	public Result getDecisionResultNew(String id) {
		Result result = new Result();
		Map<String, Object> resultData = decisionService.getDecisionResultNew(id);
		decisionService.isShiYongFouJueQuan(resultData);
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 是否为 今天上会(表决),并 当前用户 是决策委员角色
	 * 
	 * @return
	 */
	@RequestMapping("/isTodayDecision")
	@ResponseBody
	public Result isTodayDecision() {
		Result result = new Result();
		boolean isTodayDecision = decisionService.isTodayDecision();
		result.setSuccess(isTodayDecision);
		return result;
	}

	/**
	 * 是否为 有当前用户需要表决的项目
	 * 
	 * @return
	 */
	@RequestMapping("/isUserDecision")
	@ResponseBody
	public Result isUserDecision() {
		Result result = new Result();

		boolean isTodayDecision = decisionService.isTodayDecision();
		boolean isUserDecision = decisionService.isUserDecision();
		Map<String, Object> resultData = new HashMap<String, Object>(2);
		resultData.put("isTodayDecision", isTodayDecision);
		resultData.put("isUserDecision", isUserDecision);

		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 取消 当前表决项目
	 * 
	 * @return
	 */
	@RequestMapping("/cancelDecision")
	@ResponseBody
	public Result cancelDecision() {
		Result result = new Result();
		decisionService.cancelDecision();
		return result;
	}

	/**
	 * 重新开始 表决
	 * 
	 * @return
	 */
	@RequestMapping("/resetDecision")
	@ResponseBody
	public Result resetDecision(String id) {
		Result result = new Result();
		decisionService.resetDecision(id);
		return result;
	}

	@RequestMapping("/queryByIds")
	@ResponseBody
	public Result queryByIds(String projectJson) {
		Result result = new Result();
		List<Map<String, Object>> projectArray = JsonUtil.fromJson(projectJson, List.class);
		List<Map<String, Object>> decisions = decisionService.queryByIds(projectArray);
		result.setResult_data(decisions);
		return result;
	}
	
	/**
	 * 获取投票详情
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDecisionResultInfo")
	@ResponseBody
	public Result getDecisionResultInfo(String id) {
		Result result = new Result();
		Map<String, Object> resultList = decisionService.getDecisionResultInfo(id);
		result.setResult_data(resultList);
		return result;
	}
	
}
