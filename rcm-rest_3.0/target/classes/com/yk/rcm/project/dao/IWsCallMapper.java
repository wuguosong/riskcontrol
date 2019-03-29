/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author 80845530
 *
 */
public interface IWsCallMapper extends BaseMapper{
	/**
	 * 保存
	 * @param data
	 */
	public void insert(Map<String, Object> data);

	public List<Map<String, Object>> queryByPage(Map<String, Object> params);
	/**
	 * 根据批量id查
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryByIds(Map<String, Object> params);
	/**
	 * 根据id查
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryById(@Param("id")String id);
	/**
	 * 根据id更新给定内容
	 * @param data
	 */
	public void updateById(Map<String, Object> data);
	
	
	public void saveFormalAssessmentReport(Map<String, Object> params);

	public void deleteFormalAssessmentReportByBusinessId(String businessId);
/**
	 * 根据_id查询对应的申报单位的id
	 * @param id
	 * @return 
	 */
	public String getUnitIdById(@Param("id")String id);
	/**
	 * 根据申报单位的value查询对应sys_org表的申报单位名称
	 * @param id
	 * @return 
	 */
	public String getorgpkvalueIdById(String value);
	/**
	 * 根据id查询旧表的决策通知书数据
	 */
	public List<Map<String, Object>> queryNotice ();
	/**
	 * 根据id查询oracle数据
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryOracleById(@Param("businessid")String businessid);
	/**
	 * 根据旧决策通知书的businessid去查所对应的申请人(评审负责人)createby
	 * @param id
	 * @return
	 */
	public String getGreateBy(@Param("businessid")String businessid,@Param("relationVal")String relationVal);

	public void saveWsServer(Map<String, Object> param);
}
