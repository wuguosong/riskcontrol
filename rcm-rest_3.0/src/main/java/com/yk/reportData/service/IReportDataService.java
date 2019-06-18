package com.yk.reportData.service;

import common.PageAssistant;

import java.util.List;
import java.util.Map;

/**
 * @author Sunny Qi
 */
public interface IReportDataService {

    /**
     * 将Mongo数据存储到Oracle报表中
     *
     * @param Json
     */
    public void saveOrUpdateReportData(String Json) throws Exception;

    /**
     * 查询项目列表
     *
     * @param page
     * @param json
     * @return
     */
    PageAssistant getProjectList(PageAssistant page, String json);

    /**
     * 通过当前登录用户获取MeetingLeader
     *
     * @param loginUser
     * @return
     */
    String getMeetingLeaderByCurrentUser(String loginUser);

    /**
     * 过滤查询到的角色用户列表
     *
     * @param mapList
     * @return
     */
    List<Map<String, Object>> filterRoleUser(List<Map<String, Object>> mapList);
}
