package com.daxt.service;

import java.util.Date;


public interface IDaxtService{
	/**
	 * 投标评审 归档
	 * @param businessId
	 */
	public void preStart(String businessId);
	
	/**
	 * 取消(暂停)归档
	 * @param businessId
	 */
	public void preStop(String businessId);
	
	/**
	 * 正式评审 归档
	 * @param businessId
	 */
	public void prfStart(String businessId);
	
	/**
	 * 取消(暂停)归档
	 * @param businessId
	 */
	public void prfStop(String businessId);
	
	/**
	 * 其他决策事项
	 * @param businessId
	 */
	public void bulletinStart(String businessId);
	
	/**
	 * 取消(暂停)归档
	 * @param businessId
	 */
	public void bulletinStop(String businessId);

	/**
	 * 正式评审流程终止一年推送
	 */
	public void SRSM006(Date today, String businessId);

	/**
	 * 正式评审小项目未过会的，办结后三个月推送
	 * @param businessId
	 */
	public void SRSM002(Date today, String businessId);

	/**
	 * 正式评审无合同签订的，决策通知书下达后一年推送
	 * @param businessId
	 */
	public void SRSM003(Date today, String businessId);

	/**
	 * 正式评审有合同签订的，在合同签订后（合同上传扫描件）推送
	 * @param businessId
	 */
	public void SRSM001(Date today, String businessId);

	/**
	 * 投标评审流程终止一年推送
	 */
	public void SRSM007(Date today, String businessId);

	/**
	 * 投标评审留最终版及评审意见，办结后一年推送
	 */
	public void SRSM005(Date today, String businessId);

	/**
	 * 其他评审流程终止一年推送
	 */
	public void SRSM008(Date today, String businessId);

	/**
	 * 其他决策事项办结后一个月推送
	 * @param businessId
	 */
	public void SRSM004(Date today, String businessId);
}
