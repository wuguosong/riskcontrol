package com.yk.rcm.decision.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;


/**
 * 会议期次
 * @author hubiao
 *
 */
public interface IMeetingIssueMapper extends BaseMapper {

	public int getMaxMeetingIssue(Map<String, Object> param);

	public void add(@Param("mi")Map<String, Object> mi);

	public List<Map<String, Object>> queryMeetingLeadersByMeetingIssueId(
			@Param("meetingIssueId")String meetingIssueId, @Param("role_code")String role_code);
	
	public List<Map<String, Object>> queryByMeetingTime(
			@Param("meetingTime")String meetingTime);

	public Map<String, Object> queryById(@Param("id")String id);

	public void deleteById(@Param("id")String id);

	public Map<String, Object> queryByMeetingIssue(Map<String, Object> param);

	public Map<String, Object> queryOverByMeetingIssue(Map<String, Object> param);
	
	public List<Map<String, Object>> queryListByPage(Map<String, Object> params);

	public List<Map<String, Object>> queryMeetingProjectListByPage(
			Map<String, Object> params);
	
	public int countProjectReviewListByPage(
			Map<String, Object> params);
	
	public List<Map<String, Object>> queryProjectReviewListByPage(
			Map<String, Object> params);
	
	/**
	 * 判断  指定 Team  是否  包含当前登录用户
	 * @param type
	 * @param userId
	 * @return 
	 *   >0  表示包含
	 *   <1表示不包含
	 */
	public int ifTeamContainUser(@Param("type")String type, @Param("userId")String userId);
	
	/**
	 * 判断  指定 Team  是否  包含当前登录用户
	 * @param type
	 * @param userId
	 * @return 
	 *   >0  表示包含
	 *   <1表示不包含
	 */
	public int ifTeamsContainUser(@Param("types")String[] types, @Param("userId")String userId);

	/**
	 * 获取所有会议期次
	 * @return
	 */
	public List<Map<String, Object>> queryAll();

	/**
	 * 更新会议期次的会议主席
	 * @param meetIssu
	 */
	public void updateMeetChai(Map<String, Object> meetIssu);

	/**
	 * 是否显示搜索框
	 * @param userId
	 * @return
	 */
	public int isShowPublicSearch(@Param("userId")String userId);
	
	/**
	 * 更新会议预备表标识
	 */
	public void changeMeetingInfoFlag(Map<String, Object> json);
}
