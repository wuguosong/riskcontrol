package com.yk.rcm.formalAssessment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IFormalAssessmentInfoMapper extends BaseMapper {

	List<Map<String, Object>> getAllOldFormal();

	Map<String, Object> getFormalAssessmentById(@Param("businessId")String businessId);

	void update(Map<String, String> param);

	List<Map<String, Object>> queryByPage(Map<String, Object> params);
	List<Map<String, Object>> queryEnvirByPage(Map<String, Object> params);
	
	List<Map<String, Object>> queryPageForExport(Map<String, Object> params);

	void insert(Map<String, Object> dataForOracle);

	void deleteByBusinessId(@Param("businessId")String businessId);

	void updateAfterStartflow(Map<String, Object> map);
	
	Map<String, Object> queryRelationByTypeId(Map<String, Object> map);
	/**
	 * 查询会议信息列表(未处理)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryInformationList(Map<String, Object> params);
	/**
	 * 查询会议信息列表(已处理)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryInformationListed(Map<String, Object> params);
	/**
	 * 保存之后修改stage状态
	 * @param map
	 */
	public void updateStage(Map<String, Object> map);
	
	List<Map<String, Object>> getOldProjectRelationByBusinessId(Map<String, Object> params);

	Map<String, Object> queryWaitingByConditions(Map<String, Object> conditions);

	Map<String, Object> queryAuditedByConditions(Map<String, Object> conditions);

	List<Map<String, Object>> querySubmitedByPage(Map<String, Object> params);

	void updatePersonById(Map<String, Object> params);

	List<Map<String, Object>> queryFixGroupOption(Map<String, Object> params);

	void updateCompleteDateById(Map<String, Object> params);

	public List<Map<String, Object>> queryAllInfoByPage(
			Map<String, Object> params);

	public int countAll();
	
	public int createForTz(Map<String, Object> params);

	void updateFixGroupIds(Map<String, Object> params);

	public List<Map<String, Object>> getProjectReport0706();
	
	public List<Map<String, Object>> getProjectReport0710();

	public List<Map<String, Object>> getProjectReportDetails0706(Map<String, Object> params);

	public List<Map<String, Object>> getProjectReportDetails0710(Map<String, Object> params);

	List<Map<String,Object>> queryStartByPage(Map<String, Object> params);
	List<Map<String,Object>> queryOverByPage(Map<String, Object> params);
	/**
	 * 根据businessId,修改正式评审项目的阶段
	 * @param businessId
	 * @param stage
	 */
	void updateStageById(Map<String, Object> params);

	/**
	 * 根据userid查询专家评审小组
	 * @param userId
	 * @return
	 */
	Map<String, Object> getProfessionByUserid(String userId);
	
	/**
	 * 获取所有数据(刷大区使用)
	 * @return
	 */
	List<Map<String, Object>> queryPertainAreaIsNull();
	/**
	 * 更新大区ID(刷大区使用)
	 * @return
	 */
	void updatePertainAreaId(Map<String, Object> params);

	/**
	 * 根据业务ID，启动归档
	 * @param businessId
	 * @param date
	 */
	void startPigeonholeByBusinessId(Map<String, Object> params);
	/**
	 * 根据业务ID，取消归档
	 * @param businessId
	 */
	void cancelPigeonholeByBusinessId(Map<String, Object> params);
	/**
	 * 查询正式评审大区
	 * @return
	 */
	List<Map<String, Object>> queryAllFormal();
	/**
	 * 正式评审统计
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryFormalCount(Map<String, Object> map);
	/**
	 * 查询项目
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryByStageAndstate(Map<String, Object> map);
	/**
	 * 查询所有大区人为空
	 * @return
	 */
	List<Map<String, Object>> queryAllLargePersonIsNull();
	/**
	 * 根据项目编号查询决策通知书
	 * @param projectNum
	 * @return
	 */
	List<Map<String, Object>> getDecisionByProjectNum(String projectNum);

	/**
	 * 根据风控业务ID，查询项目的合同信息
	 * @param businessId
	 * @return
	 */
	List<Map<String, Object>> queryContractByBusinessId(@Param("businessId")String businessId);

	/**
	 * 查询需要归档的信息
	 * @param params
	 */
	public List<Map<String, Object>> queryAllByDaxt();

	/**
	 * 根据业务ID，更新归档状态
	 * @param businessId
	 * @param status 0:未归档,1:归档中,2:已归档,3:已归档,有文件缺失
	 */
	void updatePigeStatByBusiId(Map<String, Object> params);

	List<Map<String, Object>> queryEnvirSubmitedByPage(Map<String, Object> params);
	
	/**
	 * 保存之后修改stage状态
	 * @param map
	 */
	public void updateStageByProjectType(Map<String, Object> map);
	
	List<Map<String, Object>> queryAproData(Map<String, Object> params);
}
