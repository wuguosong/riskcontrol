package com.yk.rcm.formalAssessment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IFormalReportMapper extends BaseMapper {

	/**
	 * 获取待提交评审报告的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryUncommittedReportByPage(Map<String, Object> params);

	/**
	 * 获取已提交评审报告的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> querySubmittedReportByPage(Map<String, Object> params);

	/**
	 * 批量删除待提交报告的项目中的报告
	 * 
	 * @param businessids
	 *            businessid
	 * @return int
	 */
	public int batchDeleteFormalReport(String[] businessids);

	/**
	 * 列出未新增评审报告的项目
	 * 
	 * @param userId
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotNewlyBuiltProject(String userId);

	/**
	 * 报告是否已存在
	 * 
	 * @param businessid
	 * @return
	 */
	public int isReportExist(String businessid);

	/**
	 * 查询项目状态和审核阶段
	 * 
	 * @param businessid
	 *            businessid
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> isPossible2Submit(String businessid);

	/**
	 * 更改项目的审核阶段状态
	 * 
	 * @param map(businessid,decision_commit_time,stage)
	 */
	public void changeState(Map<String, Object> map);

	/**
	 * 提交决策委员会材料
	 * 
	 * @param businessId
	 *            businessId
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> selectPrjReviewView(String businessId);

	/**
	 * 获取待提交决策会材料的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryUncommittedDecisionMaterialByPage(Map<String, Object> params);

	/**
	 * 获取已提交决策会材料的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> querySubmittedDecisionMaterialByPage(Map<String, Object> params);

	/**
	 * 获取提交申请时间
	 * 
	 * @param businessId
	 *            businessId
	 * @return String
	 */
	public String queryApplDate(String businessId);

	/**
	 * 根据业务ID 查询oracle 基本信息 
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getByBusinessId(@Param("businessId")String businessId);
	/**
	 * 查询可替换的决策会信息
	 * @param paramMap 
	 * @return
	 */
	public List<Map<String, Object>> queryPfrNoticeFileList(Map<String, Object> paramMap);

}
