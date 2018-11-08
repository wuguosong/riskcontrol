package com.yk.rcm.pre.service;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;

import common.PageAssistant;

public interface IPreAuditReportService {

	/**
	 * 获取未新建投标评审报告的项目
	 * 
	 * @return List&lt;Map&ltString,Object&gt;&gt;
	 * 
	 */
	public List<Map<String, Object>> queryNotNewlyPreAuditProject();

	/**
	 * 获取待提交评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 * 
	 */
	public void queryUncommittedReportByPage(PageAssistant page, String json);

	/**
	 * 获取已提交评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 */
	public void querySubmittedReportByPage(PageAssistant page, String json);

	/**
	 * 批量删除待提交报告的项目中的报告
	 * 
	 * @param businessids
	 *            businessid
	 */
	public void batchDeletePreReport(String[] businessids);

	/**
	 * 获取指定的列
	 * 
	 * @param query
	 *            查询条件 {@link BasicDBObject}
	 * @param fields
	 *            需要查询的列 {@link BasicDBObject}
	 * @param collectionName
	 *            集合名称 String
	 * @return {@link FindIterable}&lt;{@link Document}&gt;
	 */
	public FindIterable<Document> querySpecifiedColumn(BasicDBObject query, BasicDBObject fields,
			String collectionName);

	/**
	 * 获取新建评审报告项目信息
	 * 
	 * @param id
	 *            _id
	 * @return {@link Document}
	 */
	public Document getPreProjectFormalReviewByID(String id);

	/**
	 * 根据ID查询更新评审报告
	 * 
	 * @param json
	 *            json
	 * @return String
	 */
	public String saveReviewReportById(String json);

	/**
	 * 更新报告
	 * 
	 * @param json
	 *            String
	 * @return String
	 */
	public String updateReviewReport(String json);

	/**
	 * 提交报告
	 * 
	 * @param businessid
	 *            String
	 */
	public void submitAndupdate(String businessid);

	/**
	 * 获取项目信息
	 * 
	 * @param id
	 *            String
	 * @return {@link Document}
	 */
	public Document getById(String id);

	/**
	 * 导出报表
	 * 
	 * @param id
	 *            String
	 * @return {@link Map}
	 */
	public Map<String, String> getPreWordReport(String id);
	
	/**
	 * 查询项目状态和审核阶段
	 * 
	 * @param businessid
	 *            businessid
	 * @return boolean
	 */
	public boolean isPossible2Submit(String businessid);

	/**
	 * 更新项目规模、投资金额、收益率、业务类型
	 * @param params
	 */
	public void updateReportByBusinessId(Map<String, Object> params);
	/**
	 * 根据业务ID 查询oracle 基本信息 (投标评审报告表)
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getOracleByBusinessId(String businessid);
	
}
