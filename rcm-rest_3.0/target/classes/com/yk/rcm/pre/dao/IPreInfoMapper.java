package com.yk.rcm.pre.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPreInfoMapper extends BaseMapper {
	/**
	 * 修改审核状态
	 * @param data
	 */
	void updateAuditStatusByBusinessId(Map<String, Object> data);
	/**
	 * 修改阶段
	 * @param data
	 */
	void updateAuditStageByBusinessId(Map<String, Object> data);
	/**
	 * 查询基本信息
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryPreInfoById(String businessId);
	/**
	 * 修改申请时间
	 * @param data
	 */
	void updateApplyDate(Map<String, Object> data);
	/**
	 * 查询起草列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryPreListByPage(Map<String, Object> params);
	/**
	 * 查询已提交列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryPreSubmitedList(Map<String, Object> params);
	/**
	 * 修改最近修改时间
	 * @param data
	 */
	void updateLastUpdateDate(Map<String, Object> data);
	/**
	 * 查询所有预评审项目
	 * @return
	 */
	List<Map<String, Object>> getAllOldPre();
	/**
	 * 查询projectRelation
	 * @param businessId
	 * @return
	 */
	List<Map<String, Object>> getOldProjectRelationByBusinessId(Map<String, Object> map);
	/**
	 * 删除预评审基本信息
	 * @param map
	 */
	void deleteByBusinessId(Map<String, Object> map);
	/**
	 * 保存
	 * @param param
	 */
	void save(Map<String, Object> param);
	/**
	 * 查询待办
	 * 提供给刷数据
	 * @param conditions
	 * @return
	 */
	Map<String, Object> queryWaitingByConditions(Map<String, Object> conditions);
	/**
	 * 查询已办
	 * 提供给刷数据
	 * @param conditions
	 * @return
	 */
	Map<String, Object> queryAuditedByConditions(Map<String, Object> conditions);
	/**
	 * 查询relation表
	 * @param data
	 * @return
	 */
	Map<String, Object> queryRelationByTypeId(Map<String, Object> data);
	/**
	 * 查询固定小组成员意见
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryFixGroupOption(Map<String, Object> params);
	/**
	 * 保存固定小组成员
	 * @param params
	 */
	void updateFixGroupIds(Map<String, Object> params);
	/**
	 * 保存人员信息
	 * @param params
	 */
	void updatePersonById(Map<String, Object> params);
	/**
	 * 修改完成时间
	 * @param params
	 */
	void updateCompleteDate(Map<String, Object> params);
	/**
	 * 接口创建数据
	 * @param dataForOracle
	 */
	void createForTz(Map<String, Object> dataForOracle);
	/**
	 * 统计项目总数
	 * @return
	 */
	int countAll();
	/** 
	 * 获取正式评审项目报表详情
	 */
	List<Map<String, Object>> getProjectReportDetails0706(Map<String, Object> params);
	/**
	 * 差所欲投标评审
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryAllInfoByPage(Map<String, Object> params);
	
	/**
	 * 获取所有数据(刷大区使用)
	 * @return
	 */
	List<Map<String, Object>> queryPertainAreaIsNull();
	/**
	 * 更新大区ID(刷大区使用)
	 * @return
	 */
	int updatePertainAreaId(Map<String, Object> params);
	/**
	 * 按阶段与项目审核状态查询信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryByStageAndstate(Map<String, Object> map);
	/**
	 * 正式评审统计
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryPreCount(Map<String, Object> map);

	/**
	 * 根据业务ID，启动归档
	 * @param businessId
	 * @param date
	 */
	void startPigeonholeByBusinessId(Map<String, Object> params);

	/**
	 * 根据业务ID，更新归档状态
	 * @param businessId
	 * @param status 0:未归档,1:归档中,2:已归档,3:已归档,有文件缺失
	 */
	void updatePigeStatByBusiId(Map<String, Object> params);

	/**
	 * 根据业务ID，取消归档
	 * @param businessId
	 */
	void cancelPigeonholeByBusinessId(Map<String, Object> params);
	/**
	 * 查询所有投标评审
	 * @return
	 */
	List<Map<String, Object>> queryAllPre();
	/**
	 * 按条件查询导出台账列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryPreListForExport(Map<String, Object> params);
	/**
	 * 保存是否需要上会与是否需要提交报告
	 * @param params
	 */
	void saveNeedMeetingAndNeedReport(Map<String, Object> params);
	/**
	 * 修改会议提交时间
	 * @param hashMap
	 */
	void updateMeetingCommitTime(Map<String, Object> hashMap);
	
	/**
	 * 查询历史项目需要档案信息
	 * @return
	 */
	List<Map<String, Object>> queryAllByDaxt();
}
