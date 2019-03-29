/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.Map;

/**
 * @author 80845530
 *
 */
public interface IFormalAssesmentService {
	
	/**
	 * 根据业务id删除，专为投资系统调用提供
	 * @param businessId
	 */
	public void deleteById(String businessId);
	/**
	 * 根据业务id查询
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryMongoById(String businessId);
	/**
	 * 根据业务id查询oracle
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryOracleById(String businessId);
	/**
	 * 根据业务id查询决策通知书
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryReportById(String businessId);
	/**
	 * 根据业务id查询项目经验总结数据
	 * Experience
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryProjectExperienceById(String businessId);
	/**
	 * 根据业务id修改oracle数据
	 * @param businessId
	 * @param params
	 * @return
	 */
	public void updateOracleById(String businessId, Map<String, Object> params);
	/**
	 * 风控系统删除正式评审时调用该方法
	 * 根据业务id删除数据，同时调用投资系统的接口同步删除状态
	 * @param businessId
	 */
	public void deleteByIdSyncTz(String businessId);
}
