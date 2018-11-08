package com.yk.rcm.meeting.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 用户通知
 * @author hubiao
 */
public interface INoticeUserMapper extends BaseMapper {

	public void add(@Param("noticeUsers") List<Map<String, Object>> noticeUsers);

	/**
	 * 根据关联ID，删除信息
	 * @param meetingNoticeId
	 */
	public void deleteByRelationId(@Param("relationId") String relationId);
	
	/**
	 * 根据关联ID，修改审阅状态
	 * @param relationId
	 * @param reviewStatus
	 */
	public void updateReviewStatus(Map<String, Object> param);
}
