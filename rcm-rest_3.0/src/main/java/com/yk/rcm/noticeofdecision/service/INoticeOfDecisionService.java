/**
 * 
 */
package com.yk.rcm.noticeofdecision.service;

import java.util.Map;

/**
 * @author 80845530
 *
 */
public interface INoticeOfDecisionService {
	
	/**
	 * 根据评审id查询决策通知书mongo数据
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryMongoByFormalId(String businessId);
	/**
	 *  根据评审id查询决策通知书oracle数据
	 * @param formalId
	 * @return
	 */
	public Map<String, Object> queryOracleByFormalId(String formalId);
	/**
	 * 根据业务id查询oracle
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryOracleById(String businessId);
}
