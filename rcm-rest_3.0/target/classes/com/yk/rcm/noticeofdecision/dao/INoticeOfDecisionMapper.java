/**
 * 
 */
package com.yk.rcm.noticeofdecision.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author 80845530
 *
 */
public interface INoticeOfDecisionMapper extends BaseMapper{
	/**
	 *  根据评审id查询决策通知书oracle数据
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryOracleByFormalId(@Param("formalId")String formalId);
	/**
	 * 根据业务id查询oracle
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryOracleById(@Param("businessId")String businessId);
}
