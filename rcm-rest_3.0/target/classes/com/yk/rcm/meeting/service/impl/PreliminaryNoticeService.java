package com.yk.rcm.meeting.service.impl;

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

import com.yk.rcm.meeting.dao.IPreliminaryNoticeMapper;
import com.yk.rcm.meeting.service.IPreliminaryNoticeService;

import common.PageAssistant;


@Service
@Transactional
public class PreliminaryNoticeService implements IPreliminaryNoticeService {

	@Resource
	private IPreliminaryNoticeMapper preliminaryNoticeMapper;

	@Override
	public void queryByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = preliminaryNoticeMapper.queryByPage(params);
		page.setList(list);
	}

	@Override
	public Map<String, Object> queryById(String id) {
		Map<String, Object> queryInfo = preliminaryNoticeMapper.queryById(id);
		List<Map<String, Object>> noticeWyUser = preliminaryNoticeMapper.queryUserByNoticeIdAndType(id,"WY");
		queryInfo.put("noticeWyUser", noticeWyUser);
		
		List<Map<String, Object>> noticeFkUser = preliminaryNoticeMapper.queryUserByNoticeIdAndType(id,"FK");
		queryInfo.put("noticeFkUser", noticeFkUser);
		
		List<Map<String, Object>> noticeQtUser = preliminaryNoticeMapper.queryUserByNoticeIdAndType(id,"QT");
		queryInfo.put("noticeQtUser", noticeQtUser);
		return queryInfo;
	}

	@Override
	public List<Map<String, Object>> queryTop() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", ThreadLocalUtil.getUserId());
		List<Map<String, Object>> list = preliminaryNoticeMapper.queryTop(params);
		return list;
	}

	@Override
	public void queryReviewByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		params.put("userId", ThreadLocalUtil.getUserId());
		List<Map<String, Object>> list = preliminaryNoticeMapper.queryReviewByPage(params);
		page.setList(list);
	}

	@Override
	public Map<String, Object> queryReviewById(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("userId", ThreadLocalUtil.getUserId());
		Map<String, Object> queryInfo = preliminaryNoticeMapper.queryReviewById(params);
		return queryInfo;
	}
	
	@Override
	public void updateUsreReviewStatus(String noticeId) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("noticeId", noticeId);
		param.put("userId", ThreadLocalUtil.getUserId());
		param.put("reviewStatus", "2");
		preliminaryNoticeMapper.updateUsreReviewStatus(param);		
	}

	@Override
	public String save(Map<String, Object> map) {
		map.put("CREATE_TIME", new Date());
		map.put("CREATE_BY", ThreadLocalUtil.getUserId());
		//状态(0:草稿,1:正常,2:过期)
		map.put("STATUS", "0");
		Object meetingTimeObject = map.get("RELEASE_TIME");
		if(Util.isNotEmpty(meetingTimeObject)){
			map.put("RELEASE_TIME",Util.parse(meetingTimeObject.toString(),"yyyy-MM-dd"));
		}else{
			map.put("RELEASE_TIME",null);
		}
		String noticeId = map.get("ID").toString();
		
		List<Map<String, Object>> noticeUsers = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> noticeWyUser = (List<Map<String, Object>>) map.get("noticeWyUser");
		List<Map<String, Object>> noticeFkUser = (List<Map<String, Object>>) map.get("noticeFkUser");
		List<Map<String, Object>> noticeQtUser = (List<Map<String, Object>>) map.get("noticeQtUser");
		if(null != noticeWyUser){
			for (Map<String, Object> map2 : noticeWyUser) {
				String userId = map2.get("VALUE").toString();
				Map<String, Object> noticeUser = new HashMap<String, Object>();
				noticeUser.put("ID", Util.getUUID());
				noticeUser.put("RELATION_ID", noticeId);
				noticeUser.put("TYPE", "WY");
				noticeUser.put("USER_ID", userId);
				noticeUser.put("REVIEW_STATUS", "1");
				noticeUsers.add(noticeUser);	
			}
		}
		if(null != noticeFkUser){
			for (Map<String, Object> map2 : noticeFkUser) {
				String userId = map2.get("ITEM_CODE").toString();
				Map<String, Object> noticeUser = new HashMap<String, Object>();
				noticeUser.put("ID", Util.getUUID());
				noticeUser.put("RELATION_ID", noticeId);
				noticeUser.put("TYPE", "FK");
				noticeUser.put("USER_ID", userId);
				noticeUser.put("REVIEW_STATUS", "1");
				noticeUsers.add(noticeUser);	
			}
		}
		if(null != noticeQtUser){
			for (Map<String, Object> map2 : noticeQtUser) {
				String userId = map2.get("VALUE").toString();
				Map<String, Object> noticeUser = new HashMap<String, Object>();
				noticeUser.put("ID", Util.getUUID());
				noticeUser.put("RELATION_ID", noticeId);
				noticeUser.put("TYPE", "QT");
				noticeUser.put("USER_ID", userId);
				noticeUser.put("REVIEW_STATUS", "1");
				noticeUsers.add(noticeUser);	
			}
		}
		preliminaryNoticeMapper.create(map);
		if(noticeUsers.size() > 0){
			preliminaryNoticeMapper.addNoticeUser(noticeUsers);
		}
		return noticeId;
	}

	@Override
	public String update(Map<String, Object> map) {
		deleteById(map.get("ID").toString());
		return save(map);
	}

	@Override
	public void deleteById(String id) {
		preliminaryNoticeMapper.deleteById(id);
		preliminaryNoticeMapper.deleteUserByNoticeId(id);
	}

	@Override
	public void updateStatusById(Map<String, Object> map) {
		preliminaryNoticeMapper.updateStatusById(map);
	}
}
