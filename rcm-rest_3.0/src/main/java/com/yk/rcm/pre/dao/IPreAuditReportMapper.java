package com.yk.rcm.pre.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPreAuditReportMapper extends BaseMapper {

	/**
	 * 获取未新建投标评审报告的项目
	 * 
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryNotNewlyPreAuditProject(Map<String, Object> map);

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
	 */
	public void batchDeletePreReport(String[] businessids);

	/**
	 * 保存投标评审报告信息
	 * 
	 * @param map
	 *            {@link Map}
	 */
	public void savePreReport(Map<String, Object> map);


	/**
	 * 更新报告
	 * 
	 * @param map
	 *            Map
	 */
	public void updatePreReport(Map<String, Object> map);

	/**
	 * 提交报告时更新项目状态
	 * 
	 * @param map
	 */
	public void changeState(Map<String, Object> map);
	
	/**
	 * 查询项目状态和审核阶段
	 * 
	 * @param businessid
	 *            businessid
	 * @return boolean
	 */
	public Map<String, Object> isPossible2Submit(String businessid);

	public void delete();

	public void updateReportByBusinessId(Map<String, Object> params);
	/**
	 * 根据业务ID 查询oracle 基本信息 (投标评审报告表)
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getByBusinessId(String businessid);
}
