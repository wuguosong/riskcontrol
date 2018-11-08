package com.yk.rcm.pre.service;

import java.util.Map;

import org.bson.Document;

import common.PageAssistant;


public interface IPreMeetingInfoService {

	/**
	 * 查询参会信息列表(未处理)
	 * @param page
	 */
	public void queryInformationList(PageAssistant page);
	/**
	 * 查询参会信息列表(已处理)
	 * @param page
	 */
	public void queryInformationListed(PageAssistant page);
	/**
	 * 新增参会信息
	 * @param json
	 */
	public void addMeetingInfo(String json);
	/**
	 * 根据businessID查询参会信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryMeetingInfoById(String id);
	/**
	 * 无需上会并修改状态
	 * @param businessId
	 * @param stage
	 * @param need_meeting
	 */
	public void updateStageById(String businessId, String stage,String need_meeting);
	/**
	 * 无需上会信息列表
	 * @param page
	 * @return 
	 */
	public PageAssistant queryNotMeetingList(PageAssistant page);
	
}
