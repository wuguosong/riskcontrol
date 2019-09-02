package com.yk.rcm.meeting.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
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
import com.yk.power.service.IUserService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.meeting.dao.IMeetingMapper;
import com.yk.rcm.meeting.service.IMeetingService;
import com.yk.rcm.pre.service.IPreInfoService;
import common.Constants;
import common.PageAssistant;

@Service
@Transactional
public class MeetingService implements IMeetingService {
	
	@Resource
	private IMeetingIssueService meetingIssueService;
	
	@Resource
	private IMeetingMapper meetingMapper;
	
	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IRoleService roleService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	public IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	public IFormalAssessmentAuditService assessmentAuditService;
	
	@Resource
	private IDictService dictService;
	
	@Resource
	private IUserService userService;

	@Override
	public void queryCanArrangeProjectListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = meetingMapper.queryCanArrangeProjectListByPage(params);
		page.setList(list);
	}

	@Override
	public List<Map<String, Object>> queryNotSubmitProjectList() {
		return meetingMapper.queryNotSubmitProjectList();
	}
	
	@Override
	public int getLastNotSubmitProjectIndex() {
		return meetingMapper.getLastNotSubmitProjectIndex();
	}
	
	@Override
	public int getLastProjIndeByMeetId(String meetId) {
		return meetingMapper.getLastProjIndeByMeetId(meetId);
	}
	
	@Override
	public void addNotSubmitProject(List<Map<String, Object>> decisions) {
		meetingMapper.addNotSubmitProject(decisions);
	}
	
	@Override
	public void saveNotSubmitMeetingIssue(Map<String, Object> meetingIssue) {
		saveNotSubmitMeetingIssue(meetingIssue, "1");
	}
		
	/**
	 * 保存未提交的项目信息
	 * @param meetingIssue 会议信息
	 * @param arrangeStatus 安排状态(1:草拟,2:已提交)
	 */
	@SuppressWarnings("unchecked")
	public void saveNotSubmitMeetingIssue(Map<String, Object> meetingIssue,String arrangeStatus) {
		try {
			//组织会议信息添加或更新会议期次信息
			Object meetingTimeObject = meetingIssue.get("MEETING_TIME");
			if(Util.isNotEmpty(meetingTimeObject)){
				meetingIssue.put("MEETING_TIME",Util.parse(meetingTimeObject.toString(),"yyyy-MM-dd"));
				meetingIssue.put("MEETING_TIME_STRING",meetingTimeObject.toString());
			}else{
				//防止出现空字符串导致更新sql 将""赋给date会报错!
				meetingIssue.put("MEETING_TIME",null);
			}
			System.out.println(meetingIssue.get("SELECTMEETINGINFOID"));
			System.out.println(meetingIssue.get("START_TIME"));
			System.out.println(meetingIssue.get("END_TIME"));
			meetingIssue.put("SELECTMEETINGINFOID", String.valueOf(meetingIssue.get("SELECTMEETINGINFOID")));
	
			Object meetingLeadersObject = meetingIssue.get("MEETING_LEADERS");
			if(meetingLeadersObject instanceof List){
				StringBuffer leadersBuffer = new StringBuffer();
				StringBuffer chairmansBuffer = new StringBuffer();
				List<Map<String, Object>> meetLeadList = (List<Map<String, Object>>) meetingLeadersObject;
				int size = meetLeadList.size()-1;
				if(null != meetLeadList && size > 0){
					for (int i = 0; i <= size; i++) {
						Map<String, Object> meetLead = meetLeadList.get(i);
						leadersBuffer.append(meetLead.get("VALUE").toString());
						if(i != size){
							leadersBuffer.append(",");
						}
					}
					
					//按顺序存储会议主席
					List<Map<String, Object>> meetChaiList = roleService.queryMeetingChairman();
					for (int i = 0; i < meetChaiList.size(); i++) {
						Map<String, Object> chairman = meetChaiList.get(i);
						for (Map<String, Object> meetLead : meetLeadList) {
							if(chairman.get("USER_ID").toString().equals(meetLead.get("VALUE").toString())){
								chairmansBuffer.append(chairman.get("USER_ID").toString()).append(",");
								break;
							}
						}
					}
					if(chairmansBuffer.length() > 0){
						chairmansBuffer.delete(chairmansBuffer.length()-1, chairmansBuffer.length());
					}
				}
				meetingIssue.put("MEETING_LEADERS", leadersBuffer.toString());
				meetingIssue.put("MEETING_CHAIRMANS", chairmansBuffer.toString());
			}
			meetingIssue.put("STATUS", "0");
			
			Object meetingIssueNumberObject = meetingIssue.get("MEETING_ISSUE_NUMBER");
			int meetingIssueNumber = -1;
			if(Util.isNotEmpty(meetingIssueNumberObject)){
				meetingIssueNumber = Integer.parseInt(meetingIssueNumberObject.toString());
				meetingIssue.put("MEETING_ISSUE_NUMBER", meetingIssueNumber);
			}
			
			Object meetingIssueObject = meetingIssue.get("ID");
			boolean isMeetingIssueExists = true;
			if("2".equals(arrangeStatus)){
				Date meetingTime = (Date) meetingIssue.get("MEETING_TIME");
				//根据会议期次  查询ID，如果有 则不新增
				Map<String, Object> resultData = meetingIssueService.queryByMeetingIssue(meetingTime,meetingIssueNumber);
				if(Util.isNotEmpty(resultData)){
					//有ID,且ID和旧的ID不同,则删除之前的会议期次
					if(Util.isNotEmpty(meetingIssueObject) && !resultData.get("ID").toString().equals(meetingIssueObject.toString())){
						meetingIssueService.deleteById(meetingIssueObject.toString());
					}
					meetingIssueObject = resultData.get("ID");
					meetingIssue.put("ID", meetingIssueObject);
				}
			}
			if(Util.isEmpty(meetingIssueObject)){
				meetingIssue.put("ID", Util.getUUID());
				isMeetingIssueExists = false;
			}
			String meetingIssueId = meetingIssue.get("ID").toString();
			
			//修改未提交项目的状态
			Object formalProjectListObject = meetingIssue.get("formalProjectList");
			List<Map<String,Object>> formalProjectList = null;
			Date createTime = new Date();
			if(formalProjectListObject instanceof List){
				formalProjectList = (List<Map<String, Object>>) formalProjectListObject;
				if(Util.isNotEmpty(formalProjectList)){
					for (Map<String,Object> formal : formalProjectList) {
						Map<String,Object> param = new HashMap<String,Object>(6);
						param.put("createTime",createTime);
						param.put("businessid", formal.get("formalId").toString());
						param.put("arrangeStatus", arrangeStatus);
						param.put("meetingIssueId", meetingIssueId);
						param.put("openMeetingPerson", ThreadLocalUtil.getUserId());
						Object projectIndexObject = formal.get("projectIndex");
						if(null != projectIndexObject && !"".equals(projectIndexObject.toString())){
							param.put("projectIndex", Integer.parseInt(projectIndexObject.toString()));
						}
						meetingMapper.updateNotSubmitProject(param);
					}
				}
			}
			
			Object preProjectListObject = meetingIssue.get("preProjectList");
			List<Map<String,Object>> preProjectList = null;
			if(preProjectListObject instanceof List){
				preProjectList = (List<Map<String, Object>>) preProjectListObject;
				if(Util.isNotEmpty(preProjectList)){
					for (Map<String,Object> pre : preProjectList) {
						Map<String,Object> param = new HashMap<String,Object>(6);
						param.put("createTime",createTime);
						param.put("businessid", pre.get("formalId").toString());
						param.put("arrangeStatus", arrangeStatus);
						param.put("meetingIssueId", meetingIssueId);
						param.put("openMeetingPerson", ThreadLocalUtil.getUserId());
						Object projectIndexObject = pre.get("projectIndex");
						if(null != projectIndexObject && !"".equals(projectIndexObject.toString())){
							param.put("projectIndex", Integer.parseInt(projectIndexObject.toString()));
						}
						meetingMapper.updateNotSubmitProject(param);
					}
				}
			}
			
			Object bulletinProjectListObject = meetingIssue.get("bulletinProjectList");
			List<Map<String,Object>> bulletinProjectList = null;
			if(bulletinProjectListObject instanceof List){
				bulletinProjectList = (List<Map<String, Object>>) bulletinProjectListObject;
				if(Util.isNotEmpty(bulletinProjectList)){
					for (Map<String, Object> bulletin : bulletinProjectList) {
						Map<String,Object> param = new HashMap<String,Object>(4);
						param.put("createTime",createTime);
						param.put("businessid", bulletin.get("_id").toString());
						param.put("arrangeStatus", arrangeStatus);
						param.put("meetingIssueId", meetingIssueId);
						param.put("openMeetingPerson", ThreadLocalUtil.getUserId());
						Object projectIndexObject = bulletin.get("projectIndex");
						if(null != projectIndexObject && !"".equals(projectIndexObject.toString())){
							param.put("projectIndex", Integer.parseInt(projectIndexObject.toString()));
						}
						meetingMapper.updateNotSubmitProject(param);
					}
				}
			}
			
			if(isMeetingIssueExists){
				meetingMapper.updateNotSubmitMeetingIssue(meetingIssue);
			}else{
				meetingMapper.addNotSubmitMeetingIssue(meetingIssue);
			}
//			正式评审项目 mongDb
			if(Util.isNotEmpty(formalProjectList)){
				saveNotSubmitFormalProject(meetingIssue,formalProjectList);
			}
//			投标评审项目 mongDb
			if(Util.isNotEmpty(preProjectList)){
				saveNotSubmitPreProject(meetingIssue,preProjectList);
			}
//			其他决策事项mongDb
			if(Util.isNotEmpty(bulletinProjectList)){
				saveNotSubmitBulletinProject(meetingIssue,bulletinProjectList);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void saveNotSubmitFormalProject(Map<String, Object> meetingIssue, List<Map<String,Object>> bulletinProjectList) throws ParseException {
		for (Map<String,Object> data : bulletinProjectList) {
			BasicDBObject filter = new BasicDBObject();
			filter.put("formalId", data.get("formalId").toString());
			
			//补充会议信息
			Map<String,Object> updateToMongDb = new HashMap<String, Object>();
			updateToMongDb.put("decisionMakingCommitteeStaffNum", meetingIssue.get("MEETING_TYPE"));
			updateToMongDb.put("decisionMakingCommitteeStaff", null);
			updateToMongDb.put("decisionOpinionList", null);
			updateToMongDb.put("huiYiQiCi", meetingIssue.get("MEETING_ISSUE"));
			updateToMongDb.put("jueCeHuiYiZhuXiId", null);
			
			updateToMongDb.put("projectType1", data.get("projectType1"));
			updateToMongDb.put("projectType2", data.get("projectType2"));
			updateToMongDb.put("projectType3", data.get("projectType3"));
			
			updateToMongDb.put("isUrgent", data.get("isUrgent"));
			
			updateToMongDb.put("projectRating", data.get("projectRating"));
			updateToMongDb.put("ratingReason", data.get("ratingReason"));
			updateToMongDb.put("participantMode", data.get("participantMode"));
			updateToMongDb.put("division", data.get("division"));
			updateToMongDb.put("investment", data.get("investment"));
			updateToMongDb.put("contacts", data.get("contacts"));
			
			updateToMongDb.put("telephone", data.get("telephone"));
			updateToMongDb.put("mark", data.get("mark"));
			
			updateToMongDb.put("agenda", data.get("agenda"));
			
			updateToMongDb.put("startTime", data.get("startTime"));
			updateToMongDb.put("endTime", data.get("endTime"));
			
			//会议安排人
			Map<String, Object> user = ThreadLocalUtil.getUser();
			Map<String, Object> map = new HashMap<String, Object>(1);
			map.put("name", user.get("NAME"));
			map.put("value", user.get("UUID"));
			
			updateToMongDb.put("openMeetingPerson", map);
			baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.FORMAL_MEETING_INFO);
			
			//更新决策通知书，会议期次!
			filter.clear();
			filter.put("projectFormalId", data.get("formalId").toString());
			updateToMongDb.clear();
			updateToMongDb.put("decisionStage", String.valueOf(meetingIssue.get("MEETING_ISSUE_NUMBER")));
			if(Util.isNotEmpty(meetingIssue.get("MEETING_TIME_STRING"))){
				updateToMongDb.put("dateOfMeeting", String.valueOf(meetingIssue.get("MEETING_TIME_STRING")));
			}else{
				updateToMongDb.put("dateOfMeeting", null);
			}
			baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_NOTICEDECISION_INFO);
		}
	}
	
	public void saveNotSubmitPreProject(Map<String, Object> meetingIssue, List<Map<String,Object>> bulletinProjectList) throws ParseException {
		for (Map<String,Object> data : bulletinProjectList) {
			String formalId = data.get("formalId").toString();
			
			Map<String, Object> preInfo = baseMongo.queryById(formalId, Constants.RCM_PRE_INFO);
			//补充会议信息
			Map<String,Object> meeting = (Map<String, Object>) preInfo.get("meetingInfo");
			meeting.put("decisionMakingCommitteeStaffNum", meetingIssue.get("MEETING_TYPE"));
			meeting.put("decisionMakingCommitteeStaff", null);
			meeting.put("decisionOpinionList", null);
			meeting.put("huiYiQiCi", meetingIssue.get("MEETING_ISSUE"));
			meeting.put("jueCeHuiYiZhuXiId", null);
			
			meeting.put("projectType1", data.get("projectType1"));
			meeting.put("projectType2", data.get("projectType2"));
			meeting.put("projectType3", data.get("projectType3"));
			
			meeting.put("isUrgent", data.get("isUrgent"));
			
			meeting.put("projectRating", data.get("projectRating"));
			meeting.put("ratingReason", data.get("ratingReason"));
			meeting.put("participantMode", data.get("participantMode"));
			meeting.put("division", data.get("division"));
			meeting.put("investment", data.get("investment"));
			meeting.put("contacts", data.get("contacts"));
			
			meeting.put("telephone", data.get("telephone"));
			meeting.put("mark", data.get("mark"));
			
			meeting.put("agenda", data.get("agenda"));
			
			meeting.put("startTime", data.get("startTime"));
			meeting.put("endTime", data.get("endTime"));
			
			//会议安排人
			Map<String, Object> user = ThreadLocalUtil.getUser();
			Map<String, Object> map = new HashMap<String, Object>(1);
			map.put("name", user.get("NAME"));
			map.put("value", user.get("UUID"));
			meeting.put("openMeetingPerson", map);
			
			//更新会议信息
			Map<String, Object> updateToMongDb = new HashMap<String, Object>();
			updateToMongDb.put("meetingInfo", meeting);
			baseMongo.updateSetByObjectId(formalId, updateToMongDb, Constants.RCM_PRE_INFO);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveNotSubmitBulletinProject(Map<String, Object> meetingIssue, List<Map<String, Object>> bulletinList) throws ParseException {
		for (Map<String, Object> data : bulletinList) {
			BasicDBObject filter = new BasicDBObject();
			String businessId = (String) data.get("_id");
			filter.put("_id", businessId);
			
			//补充会议信息
			Map<String, Object> meeting = (Map<String, Object>) data.get("meeting");
			meeting.put("meetingLeadersNum", meetingIssue.get("MEETING_TYPE"));
			meeting.put("meetingLeaders", null);
			meeting.put("decisionOpinionList", null);
			meeting.put("huiYiQiCi", meetingIssue.get("MEETING_ISSUE"));
			meeting.put("jueCeHuiYiZhuXiId", null);
			//会议安排人
			Map<String, Object> user = ThreadLocalUtil.getUser();
			Map<String, Object> map = new HashMap<String, Object>(1);
			map.put("name", user.get("NAME"));
			map.put("value", user.get("UUID"));
			meeting.put("openMeetingPerson", map);
			
			Map<String, Object> updateToMongDb = new HashMap<String, Object>();
			updateToMongDb.put("meeting", meeting);
			baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_BULLETIN_INFO);
		}
	}
	
	public void resetNotSubmitFormalProject(String businessId) throws ParseException {
		BasicDBObject filter = new BasicDBObject();
		filter.put("formalId", businessId);
		
		//补充会议信息
		Map<String,Object> updateToMongDb = new HashMap<String, Object>();
		updateToMongDb.put("decisionMakingCommitteeStaffNum",null);
		updateToMongDb.put("decisionMakingCommitteeStaff", null);
		updateToMongDb.put("decisionOpinionList", null);
		updateToMongDb.put("huiYiQiCi", null);
		updateToMongDb.put("jueCeHuiYiZhuXiId", null);
		
//		updateToMongDb.put("projectType1", null);
//		updateToMongDb.put("projectType2", null);
//		updateToMongDb.put("projectType3", null);
		
//		updateToMongDb.put("projectRating", null);
//		updateToMongDb.put("ratingReason", null);
//		updateToMongDb.put("division", null);
//		updateToMongDb.put("investment",null);
//		updateToMongDb.put("contacts", null);
		
//		updateToMongDb.put("telephone",null);
//		updateToMongDb.put("mark", null);
		
//		updateToMongDb.put("agenda", null);
		
		updateToMongDb.put("startTime", null);
		updateToMongDb.put("endTime", null);
		
		updateToMongDb.put("openMeetingPerson", null);
		baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.FORMAL_MEETING_INFO);
		
		//更新决策通知书，会议期次!
		filter.clear();
		filter.put("projectFormalId", businessId);
		updateToMongDb.clear();
		updateToMongDb.put("decisionStage", null);
		updateToMongDb.put("dateOfMeeting", null);
		baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_NOTICEDECISION_INFO);
	}
	
	public void resetNotSubmitPreProject(String businessId) throws ParseException {
		Map<String, Object> preInfo = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		//补充会议信息
		Map<String,Object> meetingInfo = (Map<String, Object>) preInfo.get("meetingInfo");
		meetingInfo.put("decisionMakingCommitteeStaffNum",null);
		meetingInfo.put("decisionMakingCommitteeStaff", null);
		meetingInfo.put("decisionOpinionList", null);
		meetingInfo.put("huiYiQiCi", null);
		meetingInfo.put("jueCeHuiYiZhuXiId", null);
		
//		meetingInfo.put("projectType1", null);
//		meetingInfo.put("projectType2", null);
//		meetingInfo.put("projectType3", null);
//		
//		meetingInfo.put("projectRating", null);
//		meetingInfo.put("ratingReason", null);
//		meetingInfo.put("division", null);
//		meetingInfo.put("investment",null);
//		meetingInfo.put("contacts", null);
//		
//		meetingInfo.put("telephone",null);
//		meetingInfo.put("mark", null);
//		
//		meetingInfo.put("agenda", null);
//		
//		meetingInfo.put("startTime", null);
//		meetingInfo.put("endTime", null);
		
		meetingInfo.put("openMeetingPerson", null);
		
		//更新会议信息
		Map<String, Object> updateToMongDb = new HashMap<String, Object>();
		updateToMongDb.put("meetingInfo", meetingInfo);
		baseMongo.updateSetByObjectId(businessId, updateToMongDb, Constants.RCM_PRE_INFO);
	}
	
	public void resetNotSubmitBulletinProject(String businessId) throws ParseException {
		BasicDBObject filter = new BasicDBObject();
		filter.put("_id", businessId);
		
		//补充会议信息
		Map<String, Object> meeting = new HashMap<String, Object>();
		meeting.put("meetingLeadersNum", null);
		meeting.put("meetingLeaders", null);
		meeting.put("decisionOpinionList", null);
		meeting.put("huiYiQiCi", null);
		meeting.put("jueCeHuiYiZhuXiId", null);
		
		meeting.put("otherPerson", null);
		meeting.put("startTime", null);
		meeting.put("endTime", null);
		meeting.put("openMeetingPerson", null);
		
		Map<String, Object> updateToMongDb = new HashMap<String, Object>();
		updateToMongDb.put("meeting", meeting);
		baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_BULLETIN_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void submitMeetingIssue(Map<String, Object> meetingIssue) {
		try {
			//保存未提交的项目信息
			saveNotSubmitMeetingIssue(meetingIssue, "2");
			
			//修改会议期次的状态
			String meetingIssueId = meetingIssue.get("ID").toString();
			meetingMapper.updateNotSubmitMeetingIssueStatus(meetingIssueId,"1");
			
			//修改 未提交项目的状态
			Object formalProjectListObject = meetingIssue.get("formalProjectList");
			List<Map<String,Object>> formalProjectList = null;
			if(formalProjectListObject instanceof List){
				formalProjectList = (List<Map<String, Object>>) formalProjectListObject;
				if(null != formalProjectList && formalProjectList.size() > 0){
					for (Map<String,Object> pfr : formalProjectList) {
						//更新项目 流程阶段 状态  
						String businessid = pfr.get("formalId").toString();
						assessmentAuditService.updateAuditStageByBusinessId(businessid, "5");
					}
				}
			}
			
			Object bulletinProjectListObject = meetingIssue.get("bulletinProjectList");
			List<Map<String,Object>> bulletinProjectList = null;
			if(bulletinProjectListObject instanceof List){
				bulletinProjectList = (List<Map<String, Object>>) bulletinProjectListObject;
				if(null != bulletinProjectList && bulletinProjectList.size() > 0){
					for (Map<String, Object> bulletin : bulletinProjectList) {
						//更新项目 流程阶段 状态  
						String businessid = bulletin.get("_id").toString();
						bulletinInfoService.updateAuditStageByBusinessId(businessid, "3");
					}
				}
			}
			
			Object preProjectListObject = meetingIssue.get("preProjectList");
			List<Map<String,Object>> preProjectList = null;
			if(preProjectListObject instanceof List){
				preProjectList = (List<Map<String, Object>>) preProjectListObject;
				if(null != preProjectList && preProjectList.size() > 0){
					for (Map<String, Object> pre : preProjectList) {
						//更新项目 流程阶段 状态  
						String businessid = pre.get("formalId").toString();
						preInfoService.updateAuditStageByBusinessId(businessid, "5");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, Object> queryFormalProjectInfo(String businessid) {
		BasicDBObject query = new BasicDBObject();
		query.put("formalId",businessid);
		List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(query,Constants.FORMAL_MEETING_INFO);
		return queryByCondition.size() > 0 ? queryByCondition.get(0):null;
	}

	@Override
	public Map<String, Object> queryBulletinProjectInfo(String businessid) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id",businessid);
		List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(query,Constants.RCM_BULLETIN_INFO);
		return queryByCondition.size() > 0 ? queryByCondition.get(0):null;
	}
	
	@Override
	public Map<String, Object> queryPreProjectInfo(String businessid) {
		Map<String, Object> resultInfo = baseMongo.queryById(businessid,Constants.RCM_PRE_INFO);
		return resultInfo;
	}

	@Override
	public void removeNotSubmitProject(List<Map<String, Object>> projects) {
		try {
			String meetingIssueId = null;
			for (Map<String, Object> project : projects) {
				if(null == meetingIssueId && project.containsKey("MEETING_ISSUE_ID") && null != project.get("MEETING_ISSUE_ID")){
					meetingIssueId = project.get("MEETING_ISSUE_ID").toString();
				}
				String projectType = project.get("PROJECT_TYPE").toString();
				String businessId = project.get("BUSINESS_ID").toString();
				if("pfr".equals(projectType)){
					resetNotSubmitFormalProject(businessId);
				}else if("pre".equals(projectType)){
					resetNotSubmitPreProject(businessId);
				}else if("bulletin".equals(projectType)){
					resetNotSubmitBulletinProject(businessId);
				}
				meetingMapper.removeNotSubmitProject(project);
			}
			if(null != meetingIssueId){
				//如果会议期次没有项目了，则删除该会议期次
				int count = meetingMapper.countProject(meetingIssueId);
				if(count == 0){
					meetingIssueService.deleteById(meetingIssueId);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> queryConferenceInfomationById(String formalId) {
		BasicDBObject query = new BasicDBObject();
		query.put("formalId",formalId);
		List<Map<String, Object>> queryByConditions = baseMongo.queryByCondition(query,Constants.FORMAL_MEETING_INFO);
		Map<String, Object> queryByCondition = new HashMap<String, Object>();
		if (Util.isNotEmpty(queryByConditions)){
			queryByCondition = queryByConditions.get(0);
		}
		return queryByCondition;
	}
	
	
	public void addNotice(Map<String, Object> meetingIssue,String noticeStatus) {
		Map<String,Object> notice = (Map<String, Object>) meetingIssue.get("notice");
		//添加项目的  评审负责人，投资经理，单位负责人
		List<Object> noticeUserList = (List<Object>) meetingIssue.get("NOTICE_USER_LIST");
		if(noticeUserList == null){
			noticeUserList = new ArrayList<Object>(10);
		}
		List<Map<String,Object>> formalProjectList = (List<Map<String, Object>>) meetingIssue.get("formalProjectList");
		for (Map<String, Object> formal : formalProjectList) {
			Map<String, Object> result = formalAssessmentInfoService.getOracleByBusinessId(formal.get("formalId").toString());
			//创建人（投资经理）
			Object createByObject = result.get("CREATEBY");
			if(Util.isNotEmpty(createByObject)){
				noticeUserList.add(createByObject.toString());
			}
			//专业评审负责人ID
			Object reviewpersonIdObject = result.get("REVIEWPERSONID");
			if(Util.isNotEmpty(reviewpersonIdObject)){
				noticeUserList.add(reviewpersonIdObject.toString());
			}
			//大区审批人ID(单位负责人)
			Object largeareapersonidObject = result.get("LARGEAREAPERSONID");
			if(Util.isNotEmpty(largeareapersonidObject)){
				noticeUserList.add(largeareapersonidObject.toString());
			}
		}
		
		List<Map<String,Object>> bulletinProjectList = (List<Map<String, Object>>) meetingIssue.get("bulletinProjectList");
		for (Map<String, Object> bulletin : bulletinProjectList) {
			//创建人（投资经理）
			Object applyUserObject = bulletin.get("applyUser");
			if(Util.isNotEmpty(applyUserObject)){
				Map<String, Object> applyUserMap = (Map<String, Object>) applyUserObject;
				noticeUserList.add(applyUserMap.get("VALUE").toString());
			}
			
			//专业评审负责人ID
			Object riskLeaderObject = bulletin.get("riskLeader");
			if(Util.isNotEmpty(riskLeaderObject)){
				Map<String, Object> riskLeaderMap = (Map<String, Object>) riskLeaderObject;
				noticeUserList.add(riskLeaderMap.get("VALUE").toString());
			}
			Object reviewLeaderObject = bulletin.get("reviewLeader");
			if(Util.isNotEmpty(reviewLeaderObject)){
				Map<String, Object> reviewLeaderMap = (Map<String, Object>) reviewLeaderObject;
				noticeUserList.add(reviewLeaderMap.get("VALUE").toString());
			}
			
			//大区审批人ID(单位负责人)
			Object legalLeaderObject = bulletin.get("legalLeader");
			if(Util.isNotEmpty(legalLeaderObject)){
				Map<String, Object> legalLeaderMap = (Map<String, Object>) legalLeaderObject;
				noticeUserList.add(legalLeaderMap.get("VALUE").toString());
			}
		}
		//会议安排人
		noticeUserList.add(ThreadLocalUtil.getUserId());
		
		String meetingIssueId = meetingIssue.get("ID").toString();
		notice.put("STATUS", noticeStatus);
		notice.put("RELATION_ID",meetingIssueId);
		notice.put("RELEASE_TIME", new Date());
		notice.put("TYPE", "MEETING_NOTICE");
		notice.put("EXPIRATION_TIME",null);
		
		//将页面选择用户传递给添加通知
//		notice.put("noticeUsers", noticeUserList);
		
		//先清空，再添加
//		noticeService.deleteByRelationId(meetingIssueId);
//		noticeService.add(notice);
	}

	@Override
	public void removeMeetingProject(String id) {
		Map<String, Object> result = decisionService.queryById(id);
		meetingMapper.removeMeetingProject(id);
		String businessId = result.get("FORMAL_ID").toString();
		String meetingIssueId = result.get("MEETING_ISSUE_ID").toString();
		int formalType = Integer.parseInt(result.get("FORMAL_TYPE").toString());
		switch (formalType) {
			case 0:
				assessmentAuditService.updateAuditStageByBusinessId(businessId, "4");
				break;
			case 1:
				bulletinInfoService.updateAuditStageByBusinessId(businessId, "2");
				break;
			case 2:
				preInfoService.updateAuditStageByBusinessId(businessId, "4");
				break;
		}
		int countProject = meetingMapper.countProject(meetingIssueId);
		if(countProject == 0){
			meetingIssueService.deleteById(meetingIssueId);
		}
	}

	@Override
	public int countProject(String meetingIssueId) {
		return meetingMapper.countProject(meetingIssueId);
	}

	@Override
	public List<Map<String, Object>> queryProjectInfoByMeetingId(String meetingIssueId) {
		List<Map<String, Object>> oracleProjectList = meetingMapper.queryProjectInfoByMeetingId(meetingIssueId);
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>(oracleProjectList.size());
		for (Map<String, Object> oracleProject: oracleProjectList) {
			String businessid = oracleProject.get("BUSINESS_ID").toString();
			String projectType = oracleProject.get("PROJECT_TYPE").toString();
			
			Map<String, Object> mongdbInfo = null;
			if("pfr".equals(projectType)){
				mongdbInfo = queryFormalProjectInfo(businessid);
			}
			else if("pre".equals(projectType)){
				mongdbInfo = queryPreProjectInfo(businessid);
				Map<String, Object> meetingInfo = (Map<String, Object>) mongdbInfo.get("meetingInfo");
				mongdbInfo = meetingInfo;
			}
			else if("bulletin".equals(projectType)){
				mongdbInfo = queryBulletinProjectInfo(businessid);
			}
			
			Map<String, Object> project = new HashMap<String, Object>(2);
			project.put("oracle", oracleProject);
			project.put("mongdb", mongdbInfo);
			projectList.add(project);
		}
		return projectList;
	}
	
	@Override
	public void updateSubmitMeetingInfo(Map<String, Object> meetingIssue) {
		List<Map<String, Object>> projectList = (List<Map<String, Object>>) meetingIssue.get("projectList");
		for (Map<String, Object> project : projectList) {
			Map<String, Object> oracleProjectInfo = (Map<String, Object>) project.get("oracle");
			meetingMapper.updateSubmitMeetingProjectInfo(oracleProjectInfo);
		}
		
		//减少debug输出
		meetingIssue.remove("projectList");
		
		Object meetingLeadersObject = meetingIssue.get("MEETING_LEADERS");
		if(meetingLeadersObject instanceof List){
			StringBuffer leadersBuffer = new StringBuffer();
			StringBuffer chairmansBuffer = new StringBuffer();
			List<Map<String, Object>> meetLeadList = (List<Map<String, Object>>) meetingLeadersObject;
			int size = meetLeadList.size()-1;
			if(null != meetLeadList && size > 0){
				for (int i = 0; i <= size; i++) {
					Map<String, Object> meetLead = meetLeadList.get(i);
					leadersBuffer.append(meetLead.get("VALUE").toString());
					if(i != size){
						leadersBuffer.append(",");
					}
				}
				
				//按顺序存储会议主席
				List<Map<String, Object>> meetChaiList = roleService.queryMeetingChairman();
				for (int i = 0; i < meetChaiList.size(); i++) {
					Map<String, Object> chairman = meetChaiList.get(i);
					for (Map<String, Object> meetLead : meetLeadList) {
						if(chairman.get("USER_ID").toString().equals(meetLead.get("VALUE").toString())){
							chairmansBuffer.append(chairman.get("USER_ID").toString()).append(",");
							break;
						}
					}
				}
				if(chairmansBuffer.length() > 0){
					chairmansBuffer.delete(chairmansBuffer.length()-1, chairmansBuffer.length());
				}
			}
			meetingIssue.put("MEETING_LEADERS", leadersBuffer.toString());
			meetingIssue.put("MEETING_CHAIRMANS", chairmansBuffer.toString());
		}
		
		Object meetingTimeObject = meetingIssue.get("MEETING_TIME");
		if(Util.isNotEmpty(meetingTimeObject)){
			meetingIssue.put("MEETING_TIME",Util.parse(meetingTimeObject.toString(),"yyyy-MM-dd"));
		}else{
			//防止出现空字符串导致更新sql 将""赋给date会报错!
			meetingIssue.put("MEETING_TIME",null);
		}
		meetingMapper.updateSubmitMeetingInfo(meetingIssue);
	}
	
	@Override
	public Map<String, Object> queryContainTodayDate(Date todayDate) {
		return meetingMapper.queryContainTodayDate(todayDate);
	}
}
