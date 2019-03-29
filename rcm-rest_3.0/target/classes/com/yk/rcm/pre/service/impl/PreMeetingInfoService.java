package com.yk.rcm.pre.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.pre.dao.IPreMeetingInfoMapper;
import com.yk.rcm.pre.service.IPreMeetingInfoService;

import common.Constants;
import common.PageAssistant;
/**
 * 投标评审参会信息service
 * @author shaosimin
 */
@Service
@Transactional
public class PreMeetingInfoService implements IPreMeetingInfoService {					
	
	@Resource
	private IPreMeetingInfoMapper PreMeetingInfoMapper;
	@Resource
	private IBaseMongo baseMongo;

	@Override
	public void queryInformationList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.PreMeetingInfoMapper.queryInformationList(params);
		page.setList(list);
	}

	@Override
	public void queryInformationListed(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.PreMeetingInfoMapper.queryInformationListed(params);
		page.setList(list);
	}

	@Override
	public void addMeetingInfo(String json) {
		Document pfr = Document.parse(json);
		Document meeting = new Document();
		Map<String, Object> meetingInfo = new HashMap<String, Object>();
		//修改oracle的stage状态
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stage", "3.5");
		map.put("need_meeting", "1");
		map.put("metting_commit_time", Util.getTime());
		map.put("businessId", pfr.getString("_id"));
		this.PreMeetingInfoMapper.updateStage(map);
		//参会信息保存到mongo投标评审apply下
		Document apply = (Document) pfr.get("apply");
		String projectName = apply.getString("projectName");
		meeting.put("projectName", projectName);
		meeting.put("user_id", ThreadLocalUtil.getUserId());
		meeting.put("serviceType", apply.get("serviceType"));
		meeting.put("isUrgent", pfr.get("isUrgent"));
		meeting.put("projectRating", pfr.get("projectRating"));
		meeting.put("projectType1", pfr.get("projectType1"));
		meeting.put("projectType2", pfr.get("projectType2"));
		meeting.put("projectType3", pfr.get("projectType3"));
		meeting.put("ratingReason", pfr.get("ratingReason"));
		meeting.put("participantMode", pfr.get("participantMode"));
		meeting.put("division", pfr.get("division"));
		meeting.put("investment", pfr.get("investment"));
		meeting.put("agenda", pfr.get("agenda"));
		meeting.put("contacts", pfr.get("contacts"));
		meeting.put("telephone", pfr.get("telephone"));
		meeting.put("create_date", Util.getTime());
		meetingInfo.put("meetingInfo", meeting);
		String id = pfr.getString("_id");
		meetingInfo.put("_id",new ObjectId(id));
		this.baseMongo.updateSetByObjectId(id, meetingInfo, Constants.RCM_PRE_INFO);
	}

	@Override
	public Map<String, Object> queryMeetingInfoById(String id) {
		Map<String, Object> queryByCondition = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
		return queryByCondition;
	}

	@Override
	public void updateStageById(String businessId, String stage,String need_meeting) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] businessIdArr = businessId.split(",");
		for (String id : businessIdArr) {
			map.put("businessId", id);
			map.put("stage", "9");
			map.put("need_meeting", "0");
			map.put("metting_commit_time", Util.getTime());
			this.PreMeetingInfoMapper.updateStage(map);
		}
	}

	@Override
	public PageAssistant queryNotMeetingList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " notice_create_time desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.PreMeetingInfoMapper.queryNotMeetingList(params);
		page.setList(list);
		return page;
	}
	
	
}
