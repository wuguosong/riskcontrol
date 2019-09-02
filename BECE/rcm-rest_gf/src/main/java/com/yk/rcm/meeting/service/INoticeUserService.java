package com.yk.rcm.meeting.service;

import java.util.List;
import java.util.Map;


/**
 * 通知用户
 * @author hubiao
 */
public interface INoticeUserService {

	/**
	 * 添加决策通知用户
	 * @param noticeUsers
	 */
	public void add(List<Map<String, Object>> noticeUsers);

	/**
	 * 根据关联ID，删除信息
	 * @param meetingNoticeId
	 */
	public void deleteByRelationId(String relationId);

	/**
	 * 根据关联ID，修改审阅状态
	 * @param relationId
	 * @param reviewStatus
	 */
	public void updateReviewStatus(String relationId, String reviewStatus);
}
