/**
 * 
 */
package com.yk.rcm.meeting.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import common.PageAssistant;

/**
 * 
 * 会议管理(已安排|待安排)
 * @author hubiao
 */
public interface IMeetingService {
	/**
	 *	分页查询	可以 安排上会的项目
	 * @param page
	 */
	public void queryCanArrangeProjectListByPage(PageAssistant page);
	
	/**
	 * 查询  所有 暂存未提交的项目
	 * @return
	 */
	public List<Map<String, Object>> queryNotSubmitProjectList();
	
	/**
	 * 获取未提交的项目最后索引
	 * @return
	 */
	public int getLastNotSubmitProjectIndex();
	
	/**
	 * 添加 未提交上会的项目
	 * @param decisions
	 */
	public void addNotSubmitProject(List<Map<String, Object>> decisions);
	
	/**
	 * 保存未提交上会的项目
	 * @param meetingIssue
	 */
	public void saveNotSubmitMeetingIssue(Map<String, Object> meetingIssue);
	
	/**
	 * 查询  正式评审项目
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> queryFormalProjectInfo(String businessid);
	
	/**
	 * 查询  其他决策事项 
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> queryBulletinProjectInfo(String businessid);
	
	/**
	 * 提交会议信息
	 * @param meetingIssue
	 */
	public void submitMeetingIssue(Map<String, Object> meetingIssue);
	
	/**
	 * 移除未提交上会的项目
	 * @param decisions
	 */
	public void removeNotSubmitProject(List<Map<String, Object>> decisions);
	
	/**
	 * 根据businessID查询参会信息
	 * @param formalId
	 * @return
	 */
	public Map<String, Object> queryConferenceInfomationById(String formalId);
	
	/**
	 * 会议通知
	 * @param meetingIssue
	 * @param noticeStatus 状态(0:草稿,1:正常,2:过期) 
	 */
	public void addNotice(Map<String, Object> meetingIssue, String noticeStatus);
	
	
	/**
	 * 查询投标评审基本信息
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> queryPreProjectInfo(String businessid);
	
	/**
	 * 取消项目上会
	 * @param id
	 */
	public void removeMeetingProject(String id);
	
	/**
	 * 统计会议期次项目
	 * @param meetingIssueId
	 * @return
	 */
	public int countProject(@Param("meetingIssueId")String meetingIssueId);
	
	/**
	 * 根据会议ID，查询会议信息
	 * @param meetingIssueId
	 * @return
	 */
	public List<Map<String, Object>> queryProjectInfoByMeetingId(String meetingIssueId);

	/**
	 * 更新已提交会议信息
	 * @param meetingIssueId
	 * @return
	 */
	public void updateSubmitMeetingInfo(Map<String, Object> meetingIssue);

	/**
	 * 查询包含当前日期的会议信息
	 * @param todayDate
	 * @return
	 */
	public Map<String, Object> queryContainTodayDate(Date todayDate);
	
	/**
	 * 获取会议下的最大项目序号
	 * @return
	 */
	public int getLastProjIndeByMeetId(String meetId);	
}
