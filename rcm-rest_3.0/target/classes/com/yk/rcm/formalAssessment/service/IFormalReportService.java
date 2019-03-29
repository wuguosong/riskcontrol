package com.yk.rcm.formalAssessment.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import common.PageAssistant;
import common.Result;

public interface IFormalReportService {

	/**
	 * 获取待提交评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            createBy(投资经理)&pertainareaname(申报单位)
	 */
	public void queryUncommittedReportByPage(PageAssistant page, String json);

	/**
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            createBy(投资经理)&pertainareaname(申报单位)
	 */
	public void querySubmittedReportByPage(PageAssistant page, String json);

	/**
	 * 报告是否已存在
	 * 
	 * @param businessid
	 * @return
	 */
	public int isReportExist(String businessid);

	/**
	 * 批量删除待提交报告的项目中的报告
	 * 
	 * @param businessids
	 *            businessid
	 * @return Map&lt;String,Object&gt;
	 */
	public void batchDeleteFormalReport(String[] businessids);

	/**
	 * 导出待新增及待提交评审报告的项目
	 * 
	 * @return Map&lt;String,String&gt;
	 */
	public Map<String, String> exportReportInfo() throws IOException;

	/**
	 * 列出未新增评审报告的项目
	 * 
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotNewlyBuiltProject();

	/**
	 * 
	 * @param id
	 *            _id
	 * @return {@link Document}
	 */
	public Document getProjectFormalReviewByID(String id);

	/**
	 * 新建评审报告
	 * 
	 * @param json
	 *            json
	 * @return String
	 */
	public String createNewReport(String json);

	/**
	 * 如果评审报告已经新建过，则再次点击保存按钮就执行该方法,更新评审报告信息
	 * 
	 * @param json
	 *            json
	 * @return String
	 */
	public String updateReport(String json);

	/**
	 * 提交报告
	 * 
	 * @param id
	 *            _id
	 */
	public void submitAndupdate(String id, String projectFormalId);

	/**
	 * createWord
	 * 
	 * @param id
	 *            id
	 * @return Map&lt;String,String&gt;
	 */
	public Map<String, String> getPfrAssessmentWord(String id);

	/**
	 * getByID
	 * 
	 * @param id
	 *            id
	 * @return {@link Document}
	 */
	public Document getByID(String id);

	/**
	 * 查询项目状态和审核阶段
	 * 
	 * @param projectFormalId
	 *            businessid
	 * @return boolean
	 */
	public boolean isPossible2Submit(String projectFormalId);

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
	 */
	public void queryUncommittedDecisionMaterialByPage(PageAssistant page, String json);

	/**
	 * 获取已提交决策会材料的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 */
	public void querySubmittedDecisionMaterialByPage(PageAssistant page, String json);

	/**
	 * 根据projectFormalId查询更新评审负责人,初始化FormalBiddingInfo页面信息
	 * 
	 * @param projectFormalId
	 * @return Map&lt;String,Object&gt;
	 */
	public Map<String, Object> findFormalAndReport(String projectFormalId);

	/**
	 * 修改、提交决策委员会材料
	 * 
	 * @param json
	 *            json
	 * @param method
	 *            method
	 * @return boolean
	 * 
	 */
	public Result addPolicyDecision(String json, String method);

	/**
	 * 在提交决策会材料的时候判断参会信息是否已填写
	 * 
	 * @param formalId
	 * 
	 * @return boolean
	 */
	public boolean isHaveMeetingInfo(String formalId);

	/**
	 * 根据业务ID 查询oracle 基本信息 
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getOracleByBusinessId(String businessid);
	/**
	 * 查询可替换的决策会信息
	 * @param queryObj 
	 * @return
	 */
	public List<Map<String, Object>> queryPfrNoticeFileList(PageAssistant page);

	public boolean updatePolicyDecision(String json, String method);

	/**
	 * 保运报告文件
	 * @param json
	 */
	public void saveReportFile(String json);
	
	/**
	 * 风控评审意见汇总暂存功能
	 * 
	 * @param json
	 *            json
	 * @param method
	 *            method
	 * @return boolean
	 * 
	 */
	public boolean saveOrUpdateFormalProjectSummary(Document summaryDoc);

	/**
	 * 风控评审意见汇总暂存功能
	 * 
	 * @param json
	 *            json
	 * @param method
	 *            method
	 * @return boolean
	 * 
	 */
	public Map<String, Object> findFormalProjectSummary();

}
