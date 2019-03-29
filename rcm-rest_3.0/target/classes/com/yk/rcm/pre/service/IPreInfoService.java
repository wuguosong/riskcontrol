package com.yk.rcm.pre.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import common.PageAssistant;
import common.Result;

public interface IPreInfoService {
	
	/**
	 * 修改审核状态
	 * @param businessId
	 * @param auditStatus
	 */
	void updateAuditStatusByBusinessId(String businessId, String wf_status);
	/**
	 * 修改通报事项	流程阶段 状态  
	 * @param businessId
	 * @param stage
	 */
	void updateAuditStageByBusinessId(String businessId, String stage);
	/**
	 * 查询基本信息
	 * @param businessId
	 * @return
	 */
	Map<String, Object> getPreInfoByID(String businessId);
	/**
	 * 查询起草列表
	 * @param page
	 */
	void queryPreListByPage(PageAssistant page);
	/**
	 * 查询已提交列表
	 * @param page
	 */
	void queryPreSubmitedList(PageAssistant page);
	/**
	 * 修改申请时间
	 * @param data
	 */
	void updateApplyDate(String businessId);
	/**
	 * 保存投资中心/水环境的相关意见
	 * @param json
	 * @param businessId
	 */
	void saveServiceTypeOpinionByBusinessId(String json, String businessId);
	/**
	 * 保存任务分配信息
	 * @param businessId
	 * @param json
	 */
	void saveTaskPerson(String json);
	/**
	 * 保存专业评审信息
	 * @param json
	 * @param businessId
	 */
	void saveReviewInfo(String json, String businessId);
	/**
	 * 获取所有的预评审醒目
	 * @return
	 */
	List<Map<String, Object>> getAllOldPre();
	/**
	 * 查询projectRelation
	 * @param businessId
	 * @return
	 */
	List<Map<String, Object>> getOldProjectRelationByBusinessId(String businessId);
	/**
	 * 删除预评审基本信息
	 * @param businessId
	 */
	void deleteByBusinessId(String businessId);
	/**
	 * 新增信息
	 * @param param
	 */
	void save(Map<String, Object> param);
	/**
	 * 查询人员待办
	 * 只为就刷数据提供
	 * @param userId
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryWaitingByConditions(String userId, String businessId);
	/**
	 * 查询人员已办
	 * 只为就刷数据提供
	 * @param reviewLeaderId
	 * @param businessId
	 * @return
	 */
	Map<String, Object> queryAuditedByConditions(String reviewLeaderId, String businessId);
	/**
	 * 查询relation表
	 * @param businessId
	 * @param string
	 * @return
	 */
	Map<String, Object> queryRelationByTypeId(String businessId, String string);
	/**
	 * 查询固定小组成员意见
	 * @param businessId
	 * @return
	 */
	List<Map<String, Object>> queryFixGroupOption(String businessId);
	/**
	 * 修改完成时间
	 * @param businessId
	 * @param now
	 */
	void updateCompleteDate(String businessId, Date now);
	/**
	 * 查询基本信息
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> getOracleByBusinessId(String businessId);
	/**
	 * 专供投资系统推送数据提供
	 * @param oldDoc
	 * @return
	 */
	public void saveOrUpdateForTz(Document doc, Result result);
	/**
	 * 删除附件
	 * @param json
	 */
	void deleteAttachment(String json);
	/**
	 * 新增附件
	 * @param json
	 */
	void addNewAttachment(String json);
	/**
	 * 替换附件
	 * @param json
	 */
	void updateAttachment(String json);
	/**
	 * 根据业务ID，删除  mongdb和oracle
	 * @param businessid
	 */
	void deleteOracleAndMongdbByBusinessId(String businessid);
	/**
	 * 保存专业评审信息
	 * @param json
	 * @param json2 
	 */
	void saveMajorMemberInfo(String businessId, String json);
	/**
	 * 统计项目总数
	 * @return
	 */
	int countAll();
	/** 
	 * 获取正式评审项目报表详情
	 * @param page
	 */
	void getProjectReportDetails0706(PageAssistant page);
	/**
	 * 查询投标评审项目
	 * @param page
	 */
	void queryAllInfoByPage(PageAssistant page);
	
	/**
	 * 获取所有数据(刷大区使用)
	 * @return
	 */
	List<Map<String, Object>> queryPertainAreaIsNull();
	/**
	 * 更新大区ID(刷大区使用)
	 * @return
	 */
	void updatePertainAreaId(String string, String pertainAreaId);
	/**
	 * 按阶段和状态查询项目
	 * @param string
	 * @param string2
	 * @return
	 */
	List<Map<String, Object>> queryByStageAndstate(String stage, String state);
	/**
	 * 查询项目统计
	 * @param wf_state
	 * @param stage
	 * @param pertainAreaId
	 * @param serviceTypeId
	 * @param year 
	 * @return
	 */
	public List<Map<String, Object>> queryPreCount(String wf_state,String stage, String pertainAreaId, String serviceTypeId, String year);

	/**
	 * 根据业务ID，启动归档
	 * @param businessId
	 * @param date
	 */
	public void startPigeonholeByBusinessId(String businessId, Date date);
	/**
	 * 根据业务ID，取消归档
	 * @param businessId
	 */
	public void cancelPigeonholeByBusinessId(String businessId);
	/**
	 * 根据业务ID，更新归档状态
	 * @param businessId
	 * @param status 0:未归档,1:归档中,2:已归档,3:已归档,有文件缺失
	 */
	public void updatePigeStatByBusiId(String businessId,String status);
	/**
	 * 查询投标评审大区
	 * @return
	 */
	public List<Map<String, Object>> queryPrePertainArea();
	/**
	 * 查询评审台账列表
	 * @param page
	 */
	void queryPageForExport(PageAssistant page);
	/**
	 * 保存是否需要上会与是否需要提交报告
	 * @param businessId
	 * @param pre
	 */
	void saveNeedMeetingAndNeedReport(String businessId, String pre);
	void updatePersonById(Map<String, Object> data);
	/**
	 * 查询历史项目需要档案信息
	 * @return
	 */
	List<Map<String, Object>> queryAllByDaxt();
}
