/**
 * 
 */
package com.yk.rcm.bulletin.service;

import java.util.Map;

import common.PageAssistant;

/**
 * 通报事项Service
 * @author wufucan
 *
 */
public interface IBulletinReviewService {
	/**
	 * 查询待审阅
	 * @param page
	 * @return
	 */
	public void queryWaitList(PageAssistant page);
	/**
	 * 查询已审阅
	 * @param page
	 * @return
	 */
	public void queryAuditedList(PageAssistant page);
	/**
	 * 查询列表初始化数据
	 */
	public Map<String, Object> queryListDefaultInfo();
	/**
	 * 审阅详情页面数据初始化
	 * @param businessId
	 */
	public Map<String, Object> queryViewDefaultInfo(String businessId);
}
