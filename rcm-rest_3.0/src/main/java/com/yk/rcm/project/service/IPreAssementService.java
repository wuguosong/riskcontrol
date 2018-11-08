/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.Map;

import org.bson.Document;

import common.Result;


/**
 * @author 80845530
 *
 */
public interface IPreAssementService {
	
	/**
	 * 根据业务id删除
	 * @param businessId
	 */
	public void deleteById(String businessId);
	/**
	 * 保存
	 * @param data
	 * @return
	 */
	public Result saveOrUpdate(Document data);
	
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
	 * 保存
	 * @param data
	 * @return
	 */
	public Result save(Document data);
	/**
	 * 修改
	 * @param data
	 * @return
	 */
	public Result update(Document data);
	/**
	 * 风控系统删除预评审时调用该方法
	 * 根据业务id删除数据，同时调用投资系统的接口同步删除状态
	 * @param businessId
	 */
	void deleteByIdSyncTz(String businessId);
	
}
