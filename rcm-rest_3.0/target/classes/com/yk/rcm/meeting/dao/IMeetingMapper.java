package com.yk.rcm.meeting.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 
 * 会议管理(已安排|待安排)
 * @author hubiao
 */
public interface IMeetingMapper extends BaseMapper {
	
	public List<Map<String, Object>> queryCanArrangeProjectListByPage(Map<String, Object> params);
	
	public List<Map<String, Object>> queryNotSubmitProjectList();

	public int getLastNotSubmitProjectIndex();
	
	public void addNotSubmitProject(@Param("decisions") List<Map<String, Object>> decisions);
	
	public void updateMeetingStatus(@Param("id")String id, @Param("status")String status);

	public void addNotSubmitMeetingIssue(@Param("mi")Map<String, Object> meetingIssue);

	public void updateNotSubmitMeetingIssue(@Param("mi")Map<String, Object> meetingIssue);

	public void updateNotSubmitMeetingIssueStatus(@Param("id")String id, @Param("status")String status);

	public void removeNotSubmitProject(@Param("project")Map<String, Object> project);

	/**
	 * 更新未提交的项目
	 * @param param
	 */
	public void updateNotSubmitProject(Map<String, Object> param);

	/**
	 * 统计会议期次项目
	 * @param meetingIssueId
	 * @return
	 */
	public int countProject(@Param("meetingIssueId")String meetingIssueId);
	
	/**
	 *移除会议中未表决的项目
	 * @return
	 */
	public void removeMeetingProject(@Param("id")String id);

	/**
	 * 查询会议项目
	 * @return
	 */
	public List<Map<String, Object>> queryProjectInfoByMeetingId(@Param("meetingIssueId")
			String meetingIssueId);

	/**
	 *  更新会议信息
	 * @return
	 */
	public void updateSubmitMeetingProjectInfo(@Param("p")Map<String, Object> project);

	/**
	 * 更新会议项目信息
	 * @return
	 */
	public void updateSubmitMeetingInfo(@Param("mi")Map<String, Object> mi);

	/**
	 * 查询包含当前日期的会议信息
	 * @param todayDate
	 * @return
	 */
	public Map<String, Object> queryContainTodayDate(@Param("todayDate")Date todayDate);

	/**
	 * 获取会议下的最大项目序号
	 * @return
	 */
	public int getLastProjIndeByMeetId(@Param("meetId")String meetId);
}
