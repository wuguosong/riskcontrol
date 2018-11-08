package com.yk.rcm.meeting.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;
import com.yk.rcm.meeting.service.IMeetingService;
import common.PageAssistant;
import common.Result;

/**
 * 
 * 会议管理(已安排|待安排)
 * @author hubiao
 */
@Controller
@RequestMapping("/meeting")
public class MeetingController {
	@Resource
	private IMeetingIssueService meetingIssueService;
	
	@Resource
	private IMeetingService meetingService;
	
	@Resource
	private IDecisionService decisionService;
	
	/**
	 * 分页查询可以安排上会的项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCanArrangeProjectListByPage")
	@ResponseBody
	public Result queryCanArrangeProjectListByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		meetingService.queryCanArrangeProjectListByPage(page);
		result.setResult_data(page);
		return result;
	}
	

	/**
	 *   查询  所有 暂存未提交的项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNotSubmitProjectList")
	@ResponseBody
	public Result queryNotSubmitProjectList(){
		Result result = new Result();
		List<Map<String,Object>> resulrData = meetingService.queryNotSubmitProjectList();
		result.setResult_data(resulrData);
		return result;
	}
	
	/**
	 *   初始化    ：查询所有暂存未提交的项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/initQueryNotSubmProjList")
	@ResponseBody
	public Result initQueryNotSubmProjList(){
		Result result = new Result();
		//查询包含当前日期的会议信息,并组织会议信息
		Map<String,Object> meetingIssueInfo = meetingService.queryContainTodayDate(new Date());
		int projectIndex = 1;
		if(null != meetingIssueInfo){
			Object meetingLeadersObject = meetingIssueInfo.get("MEETING_LEADERS");
			if(null != meetingLeadersObject){
				List<Map<String, Object>> meetingLeaders = meetingIssueService.loadMeetingLeader(meetingLeadersObject.toString().split(","));
				meetingIssueInfo.put("MEETING_LEADERS",meetingLeaders);
			}
			projectIndex = meetingService.getLastProjIndeByMeetId(meetingIssueInfo.get("ID").toString());
		}else{
			meetingIssueInfo = new HashMap<String, Object>();
		}
		meetingIssueInfo.put("MEETING_ISSUE_FORMAT", meetingIssueService.getMeetingIssueFormat());
		
		//组织所有未提交的数据
		List<Map<String,Object>> formalProjectList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> bulletinProjectList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> preProjectList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> resulrData = meetingService.queryNotSubmitProjectList();
		for (Map<String, Object> data : resulrData) {
			//根据不同项目类型，查询
			String businessid = data.get("BUSINESS_ID").toString();
			String projectType = data.get("PROJECT_TYPE").toString();
			if("pfr".equals(projectType)){
				Map<String, Object> formalProjectInfo = meetingService.queryFormalProjectInfo(businessid);
				formalProjectInfo.put("projectIndex",String.valueOf(projectIndex++));
				formalProjectList.add(formalProjectInfo);
			}
		}
		for (Map<String, Object> data : resulrData) {
			//根据不同项目类型，查询
			String businessid = data.get("BUSINESS_ID").toString();
			String projectType = data.get("PROJECT_TYPE").toString();
			if("pre".equals(projectType)){
				//投标评审的会议在meetingInfo字段中
				Map<String, Object> preProjectInfo = meetingService.queryPreProjectInfo(businessid);
				Map<String, Object> meetingInfo = (Map<String, Object>) preProjectInfo.get("meetingInfo");
				meetingInfo.put("formalId", businessid);
				meetingInfo.put("projectIndex", String.valueOf(projectIndex++));
				preProjectList.add(meetingInfo);
			}
		}
		for (Map<String, Object> data : resulrData) {
			//根据不同项目类型，查询
			String businessid = data.get("BUSINESS_ID").toString();
			String projectType = data.get("PROJECT_TYPE").toString();
			if("bulletin".equals(projectType)){
				Map<String, Object> bulletinProjectInfo = meetingService.queryBulletinProjectInfo(businessid);
				bulletinProjectInfo.put("projectIndex", String.valueOf(projectIndex++));
				bulletinProjectList.add(bulletinProjectInfo);
			}
		}
		//会议的项目
		meetingIssueInfo.put("formalProjectList", formalProjectList);
		meetingIssueInfo.put("bulletinProjectList", bulletinProjectList);
		meetingIssueInfo.put("preProjectList", preProjectList);
		result.setResult_data(meetingIssueInfo);
		return result;
	}
	
	
	/**
	 *   保存未提交上会的项目
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveNotSubmitMeetingIssue")
	@ResponseBody
	public Result saveNotSubmitMeetingIssue(String meetingIssueJson){
		Result result = new Result();
		
		Map<String, Object> meetingIssue = null;
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			meetingIssue = mapper.readValue(meetingIssueJson, HashMap.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		meetingService.saveNotSubmitMeetingIssue(meetingIssue);
		//把会议信息ID传回去
		result.setResult_data(meetingIssue.get("ID"));
		return result;
	}
	
	/**
	 *   提交上会的项目
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/submitMeetingIssue")
	@ResponseBody
	public Result submitMeetingIssue(String meetingIssueJson){
		Result result = new Result();
		
		Map<String, Object> meetingIssue = JsonUtil.fromJson(meetingIssueJson, HashMap.class);
		//如果当前会议期次在今天之前,则不允许提交!
		Object meetingIssueNumberObject = meetingIssue.get("MEETING_ISSUE_NUMBER");
		Date meetingTime = Util.parse(meetingIssue.get("MEETING_TIME").toString(),"yyyy-MM-dd");
		int meetingIssueNumber = Integer.parseInt(meetingIssueNumberObject.toString());
		int maxMeetingIssue = meetingIssueService.getMaxMeetingIssue(meetingTime);
		if(meetingIssueNumber < maxMeetingIssue){
			result.setSuccess(false).setResult_name("会议期次不能在今天之前的期次！");
		}
		
		if(result.isSuccess()){
			//提交上会
			meetingService.submitMeetingIssue(meetingIssue);
		}
		return result;
	}
	
	/**
	 * 添加 未提交上会的项目
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/addNotSubmitProject")
	@ResponseBody
	public Result addNotSubmitProject(String projectJson){
		Result result = new Result();
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<Map<String, Object>> decisions = null;
		int lastNotSubmitProjectIndex = meetingService.getLastNotSubmitProjectIndex();
		try {
			ArrayList<String> arrayList = mapper.readValue(projectJson, ArrayList.class);
			decisions = new ArrayList<Map<String, Object>>(arrayList.size());
			for (String mapJson : arrayList) {
				HashMap<String, Object> map = mapper.readValue(mapJson, HashMap.class);
				String projectType = map.get("PROJECT_TYPE").toString();
				int formalType = 0;
				if("pfr".equals(projectType)){
					formalType = 0;
				}else if("pre".equals(projectType)){
					formalType = 2;
				}else if("bulletin".equals(projectType)){
					formalType = 1;
				}
				Map<String, Object> project = new HashMap<String, Object>();
				project.put("id", Util.getUUID());
				project.put("order_index", lastNotSubmitProjectIndex++);
				project.put("formal_id", map.get("BUSINESS_ID"));
				project.put("formal_type", formalType);
				project.put("arrange_status", 1);
				decisions.add(project);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		meetingService.addNotSubmitProject(decisions);
		return result;
	}
	
	/**
	 * 移除 未提交上会的项目
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/removeNotSubmitProject")
	@ResponseBody
	public Result removeNotSubmitProject(String projectJson){
		Result result = new Result();

		ObjectMapper mapper = new ObjectMapper(); 
		List<Map<String, Object>> projects = null;
		try {
			ArrayList<String> arrayList = mapper.readValue(projectJson, ArrayList.class);
			projects = new ArrayList<Map<String, Object>>(arrayList.size());
			for (String mapJson : arrayList) {
				HashMap<String, Object> decision = mapper.readValue(mapJson, HashMap.class);
				projects.add(decision);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		
		meetingService.removeNotSubmitProject(projects);
		return result;
	}
	
	
	/**
	 * 根据businessID查询参会信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryConferenceInfomationById")
	@ResponseBody
	public Result queryConferenceInfomationById(HttpServletRequest request){
		Result result = new Result();
		String formalId = request.getParameter("formalId");
		Map<String, Object> doc = this.meetingService.queryConferenceInfomationById(formalId);
		result.setResult_data(doc);
		return result;
	}
	
	/**
	 * 取消项目上会
	 * @return
	 */
	@RequestMapping("/removeMeetingProject")
	@ResponseBody
	public Result removeMeetingProject(String projectJson){
		Result result = new Result();
		
		List<Map<String,Object>> projectArray = JsonUtil.fromJson(projectJson, List.class);
		List<Map<String, Object>> decisions = decisionService.queryByIds(projectArray);
		for (Map<String, Object> decision : decisions) {
			//确保撤消的项目一定是未表决状态！
			if(decision.get("VOTE_STATUS").toString().equals("0")){
				meetingService.removeMeetingProject(decision.get("ID").toString());
			}else{
				result.setSuccess(false).setResult_name("只能撤消 未表决的项目！");
			}
		}
		
		return result;
	}
	
	/**
	 * 统计会议数量
	 * @return
	 */
	@RequestMapping("/countProject")
	@ResponseBody
	public Result countProject(String meetingIssueId){
		Result result = new Result();
		int countProject = meetingService.countProject(meetingIssueId);
		result.setResult_data(countProject);
		return result;
	}
	
	/**
	 * 获取会议信息
	 * @return
	 */
	@RequestMapping("/getMeetingInfoById")
	@ResponseBody
	public Result getMeetingInfoById(String meetingId){
		Result result = new Result();
		
		Map<String, Object> meetingInfo = meetingIssueService.queryById(meetingId);
		Object meetingLeadersObject = meetingInfo.get("MEETING_LEADERS");
		List<Map<String, Object>> meetingLeaders = meetingIssueService.loadMeetingLeader(meetingLeadersObject.toString().split(","));
		meetingInfo.put("MEETING_LEADERS",meetingLeaders);
		
		List<Map<String, Object>> projectList = meetingService.queryProjectInfoByMeetingId(meetingId);
		meetingInfo.put("projectList", projectList);
		
		result.setResult_data(meetingInfo);
		
		return result;
	}
	
	/**
	 * 更新会议信息
	 * @return
	 */
	@RequestMapping("/updateSubmitMeetingInfo")
	@ResponseBody
	public Result updateSubmitMeetingInfo(String meetingIssueJson){
		Result result = new Result();
		
		Map<String, Object> meetingIssue = JsonUtil.fromJson(meetingIssueJson, HashMap.class);
		meetingService.updateSubmitMeetingInfo(meetingIssue);
		
		return result;
	}
	
	
	/**
	 * 获取最大会议期次
	 * @param meetingTime
	 * @return
	 */
	@RequestMapping("/getMaxMeetingIssue")
	@ResponseBody
	public Result getMaxMeetingIssue(String meetingTime){
		Result result = new Result();
		int maxMeetingIssue = meetingIssueService.getMaxMeetingIssue(Util.parse(meetingTime,"yyyy-MM-dd"));
		result.setResult_data(maxMeetingIssue);
		return result;
	}
}
