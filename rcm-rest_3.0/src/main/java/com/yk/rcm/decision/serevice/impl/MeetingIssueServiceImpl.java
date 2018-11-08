package com.yk.rcm.decision.serevice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IDictService;
import com.yk.power.service.IRoleService;
import com.yk.power.service.IUserService;
import com.yk.rcm.decision.dao.IMeetingIssueMapper;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;

import common.Constants;
import common.PageAssistant;

@Service
@Transactional
public class MeetingIssueServiceImpl implements IMeetingIssueService {

	@Resource
	private IDecisionService decisionService;

	@Resource
	private IMeetingIssueMapper meetingIssueMapper;

	@Resource
	public IBaseMongo baseMongo;

	@Resource
	private IDictService dictService;
	
	@Resource
	private IUserService userService;
	
	@Resource
	private IRoleService roleService;

	@Override
	public int getMaxMeetingIssue(Date meetingTime) {
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("meetingTime", meetingTime);
		param.put("todayDate", new Date());
		return meetingIssueMapper.getMaxMeetingIssue(param);
	}

	@Override
	public void add(Map<String, Object> meetingIssue) {
		meetingIssueMapper.add(meetingIssue);
	}
	
	@Override
	public List<Map<String, Object>> queryMeetingLeadersByMeetingIssueId(
			String meetingIssueId) {
		List<Map<String, Object>> meetingLeaders = meetingIssueMapper
				.queryMeetingLeadersByMeetingIssueId(meetingIssueId,Constants.ROLE_CODE_DECISION_LEADERS);
		return meetingLeaders;
	}
	
	@Override
	public List<Map<String, Object>> loadMeetingLeader(
			String[] meetingLeaderArray) {
		List<Map<String, Object>> meetingLeaders = new ArrayList<Map<String, Object>>(
				meetingLeaderArray.length);
		for (String  meetingLeader : meetingLeaderArray) {
			Map<String, Object> queryById = userService.queryById(meetingLeader);
			Map<String, Object> data = new HashMap<String,Object>(2);
			data.put("VALUE", queryById.get("UUID"));
			data.put("NAME", queryById.get("NAME"));
			meetingLeaders.add(data);
		}
		return meetingLeaders;
	}

	@Override
	public List<Map<String, Object>> queryByMeetingTime(
			String meetingTime) {
		return meetingIssueMapper.queryByMeetingTime(meetingTime);
	}

	@Override
	public Map<String, Object> queryById(String id) {
		return meetingIssueMapper.queryById(id);
	}

	@Override
	public String getMeetingIssueFormat() {
		return "年第%s次投资评审会";
	}

	@Override
	public void deleteById(String id) {
		meetingIssueMapper.deleteById(id);
	}

	@Override
	public Map<String, Object> queryByMeetingIssue(Date meetingTime,int meetingIssue) {
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("meetingTime", meetingTime);
		param.put("meetingIssue", meetingIssue);
		return meetingIssueMapper.queryByMeetingIssue(param);
	}

	@Override
	public void queryListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		params.put("todayDate", new Date());
		List<Map<String, Object>> meetingIssues = meetingIssueMapper.queryListByPage(params);
		for (Map<String, Object> meetingIssue : meetingIssues) {
			String[] meetingLeaderArray = meetingIssue.get("MEETING_LEADERS").toString().split(",");
			meetingIssue.put("MEETINGLEADER_COUNT", meetingLeaderArray.length);

			Object MEETING_CHAIRMANS_Object = meetingIssue.get("MEETING_CHAIRMANS");
			if(MEETING_CHAIRMANS_Object != null){
				String[] meetChaiArray = MEETING_CHAIRMANS_Object.toString().split(",");
				List<Map<String, Object>> meetChais = getMeetingChairmans(meetChaiArray);
				meetingIssue.put("MEETING_CHAIRMANS", meetChais);
			}
		}
		page.setList(meetingIssues);
	}

	public List<Map<String, Object>> getMeetingChairmans(String[] meetChaiArray) {
		List<Map<String, Object>> meetChais = new ArrayList<Map<String,Object>>(meetChaiArray.length);
		for (String chairman : meetChaiArray) {
			Map<String, Object> queryById = userService.queryById(chairman);
			Map<String, Object> data = new HashMap<String,Object>(2);
			data.put("VALUE", queryById.get("UUID"));
			data.put("NAME", queryById.get("NAME"));
			meetChais.add(data);
			break;
		}
		return meetChais;
	}
	
	@Override
	public void queryMeetingProjectListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> meetProject = meetingIssueMapper.queryMeetingProjectListByPage(params);
		for (Map<String, Object> project : meetProject) {
			String businessId = project.get("BUSINESS_ID").toString();
			String projectType = project.get("PROJECT_TYPE").toString();
			if("pfr".equals(projectType)){
				Map<String, Object> pfrMeetInfo = baseMongo.queryByCondition(new BasicDBObject("formalId", businessId),Constants.FORMAL_MEETING_INFO).get(0);
				Object jueCeHuiYiZhuXiObject = pfrMeetInfo.get("jueCeHuiYiZhuXiId");
				if(null != jueCeHuiYiZhuXiObject){
					Map<String, Object> userInfo = userService.queryById(jueCeHuiYiZhuXiObject.toString());
					project.put("CHAIRMAN_NAME", userInfo.get("NAME"));
				}
			}else if("bulletin".equals(projectType)){
				Map<String, Object> bulletinMeetInfo = baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
				Map<String, Object> meeting = (Map<String, Object>) bulletinMeetInfo.get("meeting");
				Object jueCeHuiYiZhuXiObject = meeting.get("jueCeHuiYiZhuXiId");
				if(null != jueCeHuiYiZhuXiObject){
					Map<String, Object> userInfo = userService.queryById(jueCeHuiYiZhuXiObject.toString());
					project.put("CHAIRMAN_NAME", userInfo.get("NAME"));
				}
			}else if("pre".equals(projectType)){
				Map<String, Object> bulletinMeetInfo = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
				Map<String, Object> meetingInfo = (Map<String, Object>) bulletinMeetInfo.get("meetingInfo");
				Object jueCeHuiYiZhuXiObject = meetingInfo.get("jueCeHuiYiZhuXiId");
				if(null != jueCeHuiYiZhuXiObject){
					Map<String, Object> userInfo = userService.queryById(jueCeHuiYiZhuXiObject.toString());
					project.put("CHAIRMAN_NAME", userInfo.get("NAME"));
				}
			}
		}
		page.setList(meetProject);
	}
	
	public boolean ifTeamContainUser(String type) {
		String userId = ThreadLocalUtil.getUserId();
		int count = meetingIssueMapper.ifTeamContainUser(type,userId);
		return count > 0;
	}
	
	public boolean ifTeamsContainUser(String... types) {
		String userId = ThreadLocalUtil.getUserId();
		int count = meetingIssueMapper.ifTeamsContainUser(types,userId);
		return count > 0;
	}
	
	@Override
	public boolean isShowPublicSearch() {
//		boolean isAdmin = ThreadLocalUtil.getIsAdmin();
//		boolean ifRolesContainUser = isAdmin ? isAdmin : roleService.ifRolesContainUser(Constants.ROLE_CODE_INVESTMENT_MANAGER,Constants.ROLE_CODE_LEGALFZR,Constants.ROLE_CODE_REVIEWFZR,Constants.ROLE_SYSTEM_ADMIN,Constants.ROLE_MEETING_ADMIN);
//		boolean ifTeamsContainUser = ifRolesContainUser ? ifRolesContainUser : ifTeamsContainUser(Constants.TEAM_CODE_REVIEWFZR,Constants.TEAM_TYPE_LEGALFZR);
//		return  isAdmin || ifRolesContainUser || ifTeamsContainUser;
		String userId = ThreadLocalUtil.getUserId();
		int count = meetingIssueMapper.isShowPublicSearch(userId);
		return count > 0;
	}
	
	

	@Override
	public void queryProjectReviewListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
		//投资经理
		if(!ThreadLocalUtil.getIsAdmin() && roleService.ifRoleContainUser(Constants.ROLE_CODE_INVESTMENT_MANAGER))			
		{
			params.put("createby", ThreadLocalUtil.getUserId());
		}
		
		int totalItems = meetingIssueMapper.countProjectReviewListByPage(params);
		page.setTotalItems(totalItems);
		
		params.put("offsetRow", page.getOffsetRow());
		params.put("pageSize", page.getPageSize());
		List<Map<String, Object>> meetingIssues = meetingIssueMapper.queryProjectReviewListByPage(params);
		page.setList(meetingIssues);
	}
	
	@Override
	public List<Map<String, Object>> queryAll() {
		return meetingIssueMapper.queryAll();
	}
	
	@Override
	public void updateMeetChai(Map<String, Object> meetIssu) {
		meetingIssueMapper.updateMeetChai(meetIssu);		
	}
}
