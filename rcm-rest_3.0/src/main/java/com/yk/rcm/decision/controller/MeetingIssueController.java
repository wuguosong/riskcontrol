package com.yk.rcm.decision.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.flow.util.JsonUtil;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;

import common.PageAssistant;
import common.Result;

/**
 * 会议其次
 * @author hubiao
 */
@Controller
@RequestMapping("/meetingIssue")
public class MeetingIssueController {
	
	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IMeetingIssueService meetingIssueService;

	/**
	 *  验证候选委员中是否含有会议主席
	 * @param meetingLeaders 
	 * 数据示例  [{"NAME":"孙敏","VALUE":"0001N61000000000198L"},{"NAME":"于立国","VALUE":"0001N61000000000196K"},{"NAME":"闫友晖","VALUE":"0001N61000000000197J"}]
	 * @return
	 */
	@RequestMapping("/isIncludeChairman")
	@ResponseBody
	public Result isIncludeChairman(String meetingLeaders){
		Result result = new Result();
		List<Map<String, Object>> meetingLeadersList = JsonUtil.fromJson(meetingLeaders, List.class);
		String chairmanId = decisionService.getChairman(meetingLeadersList);
		if(StringUtils.isEmpty(chairmanId)){
			result.setSuccess(false).setResult_name("决策委员中没有会议主席，不能开始表决！");
		}
		return result;
	}
	
	/**
	 *  根据  会议期次ID，获取	决策委员
	 * @param meetingIssueId   会议期次ID
	 * @return
	 */
	@RequestMapping("/queryMeetingLeadersByMeetingIssueId")
	@ResponseBody
	public Result queryMeetingLeadersByMeetingIssueId(String meetingIssueId){
		Result result = new Result();
		if(StringUtils.isEmpty(meetingIssueId)){
//			return result.setSuccess(false).setResult_name("请确认所选择的项目！");
			return result;
		}
		List<Map<String, Object>> leaders = meetingIssueService.queryMeetingLeadersByMeetingIssueId(meetingIssueId);
		result.setResult_data(leaders);
		return result;
	}
	
	/**
	 *  根据条件分页查询已安排的会议
	 */
	@RequestMapping("/queryListByPage")
	@ResponseBody
	public Result queryListByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		meetingIssueService.queryListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 *  根据条件分页查询已安排的会议
	 */
	@RequestMapping("/queryMeetingProjectListByPage")
	@ResponseBody
	public Result queryMeetingProjectListByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		meetingIssueService.queryMeetingProjectListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 *  根据条件分页查询所有项目
	 */
	@RequestMapping("/queryProjectReviewListByPage")
	@ResponseBody
	public Result queryProjectReviewListByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		meetingIssueService.queryProjectReviewListByPage(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 是否显示全局搜索框
	 * @param userId
	 * @return
	 */
	@RequestMapping("/isShowPublicSearch")
	@ResponseBody
	public Result isShowPublicSearch(HttpServletRequest request){
		Result result = new Result();
		boolean fk = meetingIssueService.isShowPublicSearch();
		result.setSuccess(fk);
		return result;
	}
	
	/**
	 * 修改会议标识
	 * @param meetingInfo
	 * @return
	 */
	@RequestMapping("/changeMeetingInfoFlag")
	@ResponseBody
	public Result changeMeetingInfoFlag(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("meetingInfo");
		try {
			meetingIssueService.changeMeetingInfoFlag(json);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_name(e.getMessage());
		}
		
		return result;
	}
}
