package com.yk.rcm.meeting.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;

import com.yk.rcm.meeting.dao.INoticeUserMapper;
import com.yk.rcm.meeting.service.INoticeUserService;


@Service
@Transactional
public class NoticeUserService implements INoticeUserService {

	@Resource
	private INoticeUserMapper noticeUserMapper;

	@Override
	public void add(List<Map<String, Object>> noticeUsers) {
		noticeUserMapper.add(noticeUsers);
	}

	@Override
	public void deleteByRelationId(String relationId) {
		noticeUserMapper.deleteByRelationId(relationId);		
	}

	@Override
	public void updateReviewStatus(String relationId, String reviewStatus) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("relationId", relationId);
		param.put("reviewStatus", reviewStatus);
		param.put("userId", ThreadLocalUtil.getUserId());
		noticeUserMapper.updateReviewStatus(param);		
	}
}
