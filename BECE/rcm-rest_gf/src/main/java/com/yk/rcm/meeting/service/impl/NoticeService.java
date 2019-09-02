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

import com.yk.rcm.meeting.dao.INoticeMapper;
import com.yk.rcm.meeting.service.INoticeService;
import com.yk.rcm.meeting.service.INoticeUserService;

import common.PageAssistant;


@Service
@Transactional
public class NoticeService implements INoticeService {

	@Resource
	private INoticeMapper noticeMapper;
	
	@Resource
	private INoticeUserService noticeUserService;
	
	@Override
	public void deleteByRelationId(String relationId) {
		List<Map<String,Object>> notices = noticeMapper.queryByRelationId(relationId);
		if(Util.isNotEmpty(notices)){
			for (Map<String, Object> notice : notices) {
				String noticeId = notice.get("ID").toString();
				noticeMapper.delete(noticeId);
				noticeUserService.deleteByRelationId(noticeId);
			}
		}
	}

	@Override
	public Map<String, Object> getByRelationId(String relationId) {
		List<Map<String,Object>> notices = noticeMapper.queryByRelationId(relationId);
		if(Util.isEmpty(notices))
			return null;
		return notices.get(0);
	}
	
	@Override
	public List<Map<String,Object>> queryByRelationId(String relationId) {
		return noticeMapper.queryByRelationId(relationId);
	}

	@Override
	public void add(Map<String, Object> notice) {
		if(Util.isEmpty(notice.get("ID"))){
			 notice.put("ID",Util.getUUID());
		}
		Object noticeId = notice.get("ID");
		notice.put("CREATE_TIME",new Date());
		notice.put("CREATE_BY",ThreadLocalUtil.getUserId());
		noticeMapper.add(notice);
		Object noticeUserObject = notice.get("noticeUsers");
		if(noticeUserObject instanceof List && Util.isNotEmpty(noticeUserObject)){
			//传过来的数据
			List<Object> noticeUserList = (List<Object>) noticeUserObject;
			List<Map<String, Object>> noticeUsers = new ArrayList<Map<String, Object>>(noticeUserList.size());
			for (Object object : noticeUserList) { 	 
				String userId = "";
				if(object instanceof Map){
					Map<String, Object> map = (Map<String, Object>) object;
					userId = Util.isEmpty(map.get("VALUE")) ? map.get("value").toString() : map.get("VALUE").toString();
				}else if(object instanceof String){
					userId = object.toString();
				}
				//防止出现重复
				boolean isMatch = false;
				for (Map<String, Object> map : noticeUsers) {
					if(userId.equals(map.get("USER_ID"))){
						isMatch = true;break;
					}
				}
				if(isMatch){
					continue;
				}
				Map<String, Object> noticeUser = new HashMap<String, Object>();
				noticeUser.put("ID", Util.getUUID());
				noticeUser.put("RELATION_ID", noticeId);
				noticeUser.put("TYPE", notice.get("TYPE"));
				noticeUser.put("USER_ID", userId);
				noticeUser.put("REVIEW_STATUS", "1");
				noticeUsers.add(noticeUser);
			}
			noticeUserService.add(noticeUsers);
		}
	}

	@Override
	public void delete(String noticeId) {
		noticeMapper.delete(noticeId);
	}

	@Override
	public void queryListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		params.put("userId", ThreadLocalUtil.getUserId());
		List<Map<String, Object>> list = noticeMapper.queryListByPage(params);
		page.setList(list);
	}

	@Override
	public List<Map<String, Object>> queryListTopLimit(Map<String, Object> notice) {
		notice.put("userId", ThreadLocalUtil.getUserId());
		return noticeMapper.queryListTopLimit(notice);
	}

	@Override
	public Map<String, Object> queryInfo(Map<String, Object> notice) {
		notice.put("userId", ThreadLocalUtil.getUserId());
		return noticeMapper.queryInfo(notice);
	}
}
