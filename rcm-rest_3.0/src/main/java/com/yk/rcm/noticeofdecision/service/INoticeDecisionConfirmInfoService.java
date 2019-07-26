/**
 * 
 */
package com.yk.rcm.noticeofdecision.service;

import java.util.Map;

import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.rcm.ws.client.tz.RiskService;
import com.yk.rcm.ws.client.tz.RiskServiceSoap;

import common.PageAssistant;

/**
 * 决策通知书确认模块
 * @author wufucan
 *
 */
public interface INoticeDecisionConfirmInfoService {
	/**
	 * 查询待确认数据
	 * @param page
	 */
	public void queryWaitConfirm(PageAssistant page);
	/**
	 * 分页查询已确认数据
	 * @param page
	 */
	public void queryConfirmed(PageAssistant page);
	/**
	 * 根据正式评审项目id,决策通知书确认
	 * @param formalId
	 */
	public void confirm(Map<String, Object> data);
	
	public void getYesPfrDecisionInfo(String CusTomerId, Boolean IsHaveMeeting, String DecisionOpinion, String AuditCompleteDate);
	
}
