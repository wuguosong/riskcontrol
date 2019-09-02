/**
 * 
 */
package com.yk.rcm.noticeofdecision.service;
import java.util.Map;

import org.bson.Document;

import common.PageAssistant;

/**
 * @author yaphet
 *
 */
public interface INoticeDecisionInfoService {
	
	/**
	 * 初始化决策通知书列表--状态未提交
	 * @param PageAssistant
	 */
	PageAssistant queryStartByPage(PageAssistant assistant);
	/**
	 * 初始化决策通知书列表--状态已提交
	 * @param PageAssistant
	 */
	PageAssistant queryOverByPage(PageAssistant assistant);
	
	
	/**
	 * 初始化决策通知书新增界面数据
	 * @return 
	 */
	Document querySaveDefaultInfo(String userId);
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	String update(String json);
	/**
	 * 
	 * @param json
	 * @return
	 */
	String create(String json);
	/**
	 * 从mongo中查询通知书基本信息
	 * @param projectId
	 * @return
	 */
	Document queryUpdateInitInfo(String projectId);
	
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	String delete(String projectId);
	
	/**
	 * 
	 * @param businessId
	 * @param string
	 */
	void updateAuditStatusByBusinessId(String businessId, String string);
	/**
	 * 
	 * @param id
	 * @return
	 */
	Map<String, Object> getNoticeDecstionByID(String id);
	
	void updateAfterStartflow(String businessId);
	void insert(Map<String, Object> params);
	void updateOracleById(Map<String, Object> params);
	void deleteOracle(String projectId);
	
	/**
	 * get rcm_noticeDecision_info mongoDB  by BusinessId
	 * @param businessId 业务ID
	 * @return
	 */
	Map<String, Object> getNoticeDecstionByBusinessId(String businessId);
	
	public void updateDecisionByBusinessId(String businessId,int decisionResult);
}
