/**
 * 
 */
package com.yk.rcm.noticeofdecision.service;
import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * @author yaphet
 *
 */
public interface INoticeDecisionDraftInfoService {
	
	/**
	 * 初始化决策通知书列表--状态未提交
	 * @param PageAssistant
	 */
	PageAssistant queryStartByPage(PageAssistant assistant);
	/**
	 * 分页查询未上会的数据
	 * @param page
	 */
	PageAssistant notMeetingList(PageAssistant assistant);
	/**
	 * 初始化决策通知书列表--状态已上会
	 * @param PageAssistant
	 */
	PageAssistant queryOverByPage(PageAssistant assistant);
	
	/**
	 * 修改决策通知书信息
	 * @param json
	 * @return
	 */
	String update(String json);
	/**
	 * 新增决策通知书
	 * @param json
	 * @return
	 */
	String create(String json);
	/**
	 * 根据businessId删除决策通知书
	 * @param projectId
	 * @return
	 */
	void deleteByFormalIds(String formalIds);
	/**
	 * 根据正式评审的ID查询对应的决策通知书
	 * @param id
	 * @return
	 */
	Map<String, Object> queryNoticeDecstionByFormalId(String formalId);
	/**
	 * 根据决策通知书的businessId查询对应的决策通知书
	 * @param id
	 * @return
	 */
	Map<String, Object> queryNoticeDecstionById(String id);
	/**
	 * 查询可以起草决策通知书的项目
	 * @return
	 */
	List<Map<String, Object>> queryFormalForCreate();
	/**
	 * 决策通知书生成word文档
	 * @param businessId
	 */
	Map<String, String> getNoticeOfDecisionWord(String businessId);
	/**
	 * 根据ID查询
	 * 查询新增页面初始数据
	 * @param userId
	 * @return
	 */
	Map<String, Object> querySaveDefaultInfo(String userId);
	/**
	 * 根据businessId修改stage状态='3.9'
	 * @param businessId
	 * @param stage
	 */
	void updateStageByBusinessId(String businessId, String stage);
	
	/**
	 * 保存决策通知书
	 * @param json
	 */
	void saveDecisionFile(String json);
}
