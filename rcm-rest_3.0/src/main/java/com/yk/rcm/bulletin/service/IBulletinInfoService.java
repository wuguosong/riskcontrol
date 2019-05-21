/**
 * 
 */
package com.yk.rcm.bulletin.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;

/**
 * 通报事项Service
 * @author wufucan
 *
 */
public interface IBulletinInfoService {
	/**
	 * 保存
	 * @param bulletin 
	 */
	public String save(Map<String, Object> bulletin);
	/**
	 * 根据业务id删除
	 * @param businessId
	 */
	public void deleteByBusinessId(String businessId);
	/**
	 * 根据业务id更新
	 * @param bulletin
	 */
	public void updateByBusinessId(Map<String, Object> bulletin);
	/**
	 * 查询起草中
	 * @param page
	 */
	public void queryApplyByPage(PageAssistant page);
	/**
	 * 查询已提交
	 * @param page
	 */
	public void queryApplyedByPage(PageAssistant page);
	/**
	 * 查询创建初始信息
	 * @param userId
	 * @return
	 */
	public Result queryCreateDefaultInfo(String userId);
	/**
	 * 查询修改初始信息
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryUpdateDefaultInfo(String businessId);
	/**
	 * 批量删除
	 * @param businessIds
	 */
	public void deleteByIds(String[] businessIds);
	/**
	 * 查询列表初始化数据
	 */
	public Map<String, Object> queryListDefaultInfo();
	/**
	 * 详情查看页面数据初始化
	 * @param businessId
	 */
	public Map<String, Object> queryViewDefaultInfo(String businessId);
	
	/**
	 * 新增附件
	 * @param json
	 */
	public void addNewAttachment(String json);
	/**
	 * 删除附件
	 * @param json
	 */
	public void deleteAttachment(String json);
	/**
	 * 替换附件
	 * @param json
	 */
	/*public void updateAttachment(String json);*/
	
	/**
	 * 获取历史附件
	 * @param Json
	 */
	List<Map<String, Object>> getHistoryList(String Json) throws Exception;
	
	/**
	 * 根据业务id查询
	 * @param businessId
	 */
	public Map<String, Object> queryByBusinessId(String businessId);
	/**
	 * 修改审核状态
	 * @param businessId
	 * @param auditStatus
	 */
	public void updateAuditStatusByBusinessId(String businessId, String auditStatus);
	/**
	 * 流程启动成功后，修改提交时间和审核状态
	 * @param businessId
	 */
	public void updateAfterStartflow(String businessId);
	/**
	 * 修改通报事项	流程阶段 状态  
	 * @param businessId
	 * @param stage
	 */
	public void updateAuditStageByBusinessId(String businessId, String stage);
	/**
	 * 查询未出会议纪要（流程状态为stage：4）
	 * @param page
	 */
	public void quaryMettingSummary(PageAssistant page);
	/**
	 * 查询已出会议纪要（流程状态为stage：5）
	 * @param page
	 */
	public void queryMettingSummaryed(PageAssistant page);
	/**
	 * 保存会议纪要信息(并修改stage状态为"5")
	 * @param businessId
	 * @param attachment
	 */
	public void saveMettingSummary(String businessId, String mettingSummaryInfo);
	/**
	 * 查询rcm_bulletin_info数据信息
	 */
	public List<Map<String, Object>> queryBulletin();
	/**
	 * 保存法律评审提交信息
	 * @param businessId
	 * @param opinion 
	 * @param legalLeaderAttachment
	 */
	public void saveLegalLeaderAttachment(String businessId, String legalLeaderAttachment, String opinion);
	/**
	 * 保存业务评审提交信息
	 * @param businessId
	 * @param opinion 
	 * @param legalLeaderAttachment
	 */
	public void saveReviewLeaderAttachment(String businessId, String reviewLeaderAttachment, String opinion);
	/**
	 * 保存任务分配信息
	 * @param request
	 */
	public void saveTaskPerson(String businessId, String json);
	/**
	 * 保存风控评审提交信息
	 * @param businessId
	 * @param opinion 
	 * @param riskLeaderAttachment
	 */
	public void saveRiskLeaderAttachment(String businessId, String riskLeaderAttachment, String opinion);
	/**
	 * 根据ID修改事项类型
	 * @param businessId
	 * 
	 */
	public void updateByBusinessIdWithBulletinypeId(String businessId, String BulletinypeId);

	/**
	 * 统计所有评审项目
	 */
	public int countAll();
	/**
	 * 修改人员信息
	 * @param data
	 */
	public void updatePerson(HashMap<String, Object> data);
	
	/**
	 * 按阶段与状态查询项目
	 * @param stage
	 * @param state
	 * @return
	 */
	public List<Map<String, Object>> queryByStageAndstate(String stage,String state);
	
	/**
	 * 根据业务ID，启动归档
	 * @param businessId
	 * @param date
	 */
	public void startPigeonholeByBusinessId(String businessId, Date date);
	
	/**
	 * 根据业务ID，完成归档
	 * @param businessId
	 */
	public void updatePigeStatByBusiId(String businessId, String status);
	
	/**
	 * 根据业务ID，取消归档
	 * @param businessId
	 */
	public void cancelPigeonholeByBusinessId(String businessId);

	/**
	 * 更新大区ID
	 * @param string
	 * @param pertainAreaId
	 */
	public void updatePertainAreaId(String businessId, String pertainAreaId);
	
	/**
	 * 查询项目统计
	 * @param wf_state
	 * @param stage
	 * @param pertainAreaId
	 * @param serviceTypeId
	 * @param year 
	 * @return
	 */
	public List<Map<String, Object>> queryBulletinCount(String wf_state, String stage,
			String pertainAreaId, String serviceTypeId, String year);
	
	public List<Map<String, Object>> queryBulletinPertainArea();

	public void queryAllInfoByPage(PageAssistant page);
	/**
	 * 分页查询列表
	 * @param page
	 */
	public void queryListByPage(PageAssistant page);
	/**
	 * 修改基础附件
	 * @param businessId
	 * @param attachment
	 */
	public void updateBaseFile(String businessId, String attachment);
	
	/**
	 * 查询历史项目需要档案信息
	 * @return
	 */
	public List<Map<String, Object>> queryAllByDaxt();
	public Result queryRBIMettingSummarys(String businessId);
}
