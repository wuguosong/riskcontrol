package com.yk.rcm.pre.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPreBiddingMapper extends BaseMapper {

	/**
	 * 获取待提交决策会材料的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryUncommittedByPage(Map<String, Object> params);

	/**
	 * 获取已提交决策会材料的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> querySubmittedByPage(Map<String, Object> params);


	/**
	 * 获取提交申请时间
	 * 
	 * @param businessId
	 *            businessId
	 * @return String
	 */
	public String queryApplDate(String businessId);
	
	/**
	 * 更改项目的审核阶段状态
	 * 
	 * @param map(businessid,decision_commit_time,stage)
	 */
	public void changeState(Map<String, Object> map);
}
