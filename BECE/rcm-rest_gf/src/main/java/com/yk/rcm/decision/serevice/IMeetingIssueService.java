package com.yk.rcm.decision.serevice;

import java.util.Date;
import java.util.List;
import java.util.Map;

import common.PageAssistant;



/**
 * 
 * 会议期次
 * @author hubiao
 *
 */
public interface IMeetingIssueService {
	
	/**
	 * 获取最大会议期次编号
	 * @return
	 */
	public int getMaxMeetingIssue(Date meetingTime);
	
	/**
	 * 获取最大会议期次编号  格式
	 * @return
	 */
	public String getMeetingIssueFormat();

	/**
	 * 添加会议期次
	 * @param meetingIssue
	 */
	public void add(Map<String, Object> meetingIssue);

	/**
	 * 根据  会议期次ID，获取	决策委员
	 * @param meetingIssueId
	 * @return
	 */
	public List<Map<String, Object>> queryMeetingLeadersByMeetingIssueId(String meetingIssueId);
	
	/**
	 * @param meetingTime
	 * @return 根据日期查询会议期次
	 */
	public List<Map<String, Object>> queryByMeetingTime(String meetingTime);
	
	/**
	 * 根据ID，查询会议期次
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryById(String id);

	/**
	 * 移除会议
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 根据委员ID  补齐  委员信息
	 * 示例数据 
	 * 	0001N61000000000197B,0001N6100000000DP86L
	 * 转
	 * 	[{"NAME":"孙敏","VALUE":"0001N61000000000198L"},{"NAME":"张海林","VALUE":"0001N61000000000197B"},{"NAME":"于立国","VALUE":"0001N61000000000196K"},{"NAME":"闫友晖","VALUE":"0001N61000000000197J"}]
	 * @param meetingLeaderArray
	 * @return
	 */
	public List<Map<String, Object>> loadMeetingLeader(String[] meetingLeaderArray);
	

	/**
	 * 根据会议期次查询  会议信息
	 * @param meetingIssueNumber
	 * @return
	 */
	public Map<String, Object> queryByMeetingIssue(Date meetingTime,int meetingIssueNumber);
	
	/**
	 *  根据条件分页查询已安排的会议
	 */
	public void queryListByPage(PageAssistant page);

	public void queryMeetingProjectListByPage(PageAssistant page);

	public void queryProjectReviewListByPage(PageAssistant page);
	
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
	public boolean isShowPublicSearch();
	
	/**
	 * 修改会议预备表中的标识
	 * */
	public void changeMeetingInfoFlag(String json);
}
