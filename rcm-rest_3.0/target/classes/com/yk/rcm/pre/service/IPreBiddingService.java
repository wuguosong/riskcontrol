package com.yk.rcm.pre.service;

import java.util.Map;

import org.bson.Document;

import common.PageAssistant;

public interface IPreBiddingService {

	/**
	 * 获取待提交评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            createBy(投资经理)&pertainareaname(申报单位)
	 */
	public void queryUncommittedByPage(PageAssistant page, String json);

	/**
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            createBy(投资经理)&pertainareaname(申报单位)
	 */
	public void querySubmittedByPage(PageAssistant page, String json);

	/**
	 * 查询决策会材料的详情页面
	 */
	public Map<String, Object> getByBusinessId(String projectFormalId);

	/**
	 * 修改、提交决策委员会材料
	 * 
	 * @param document
	 *            document
	 * @param method
	 *            method
	 * @return boolean
	 * 
	 */
	public boolean addPolicyDecision(Document document, String method);

	/**
	 * 在提交决策会材料的时候判断参会信息是否已填写
	 * 
	 * @param formalId
	 * 
	 * @return boolean
	 */
	public boolean isHaveMeetingInfo(String formalId);
}
