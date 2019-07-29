package com.yk.rcm.formalAssessment.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import common.PageAssistant;
import common.Result;

public interface IFormalAssessmentInfoService {
	/**
	 * 查询所有旧表中的正式评审数据
	 * 刷过数据可删
	 * @return
	 */
	public List<Map<String,Object>> getAllOldFormal();
	/**
	 * 通过businessid查询正式评审信息
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getFormalAssessmentByID(String businessid);
	
	/**
	 * 查询oracle 正式评审 
	 * @param businessid
	 * @return
	 */
	public Map<String, Object> getOracleByBusinessId(String businessid);
	
	public void save(Map<String,Object> param);
	
	public void update(Map<String,String> param);
	
	public PageAssistant queryByPage(PageAssistant page);
	/**
	 * 查询导出台账列表
	 * @param page
	 * @return
	 */
	public PageAssistant queryPageForExport(PageAssistant page);
	
	public void create(String json);
	
	/**
	 * 专供投资系统推送数据提供
	 * @param oldDoc
	 * @return
	 */
	public Result saveOrUpdate(Document oldDoc);
	
	/**
	 * 新增或修改正式评审
	 * @param doc
	 * @return
	 */
	public Result saveOrUpdateForTz(Document doc,Result result);
	
	public void updateAfterStartflow(String businessId);
	/**
	 * 修改投资中心/水环境意见到mongo
	 * @param json
	 * @param businessId
	 */
	public void updateServiceTypeOpinionByBusinessId(String json,
			String businessId);
	/**
	 * 保存法律评审信息
	 * @param json
	 * @param businessId
	 */
	public void saveLegalReviewInfo(String json, String businessId);
	/**
	 * 保存专业评审信息
	 * @param json
	 * @param businessId
	 */
	public void saveReviewInfo(String json, String businessId);
	/**
	 * 保存固定小组成员意见到mongo
	 * @param json
	 */
	public void saveFixGroupOption(String json);
	/**
	 * 根据业务id删除新正式评审基本信息
	 * @param businessId
	 */
	void deleteByBusinessId(String businessId);
	/**
	 * 根据relationcode查询旧项目相关信息
	 * @param businessId
	 * @param relationTypeCode
	 * @return
	 */
	Map<String, Object> queryRelationByTypeId(String businessId, String relationTypeCode);
	
	/**
	 * 查询旧项目人员关系
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> getOldProjectRelationByBusinessId(
			String businessId);
	/**
	 * 根据人的id和条件查询待办信息
	 * @param conditions
	 * @return
	 */
	public Map<String, Object> queryWaitingByConditions(
			String userId,String businessId);
	/**
	 * 查询已办
	 * @param reviewLeaderId
	 * @param businessId
	 * @return
	 */
	public Map<String, Object> queryAuditedByConditions(String userId,
			String businessId);
	
	public PageAssistant querySubmitedByPage(PageAssistant page);
	/**
	 * 修改正式评审信息人员记录
	 * @param params
	 */
	public void updatePersonById(Map<String, Object> params);
	
	/**
	 * 查询旧流程固定小组成员意见
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> queryFixGroupOption(String businessId);
	/**
	 * 修改项目完成时间
	 * @param businessId
	 * @param now
	 */
	public void updateCompleteDateById(String businessId, Date now);
	/**
	 * 查询会议信息列表(未处理)
	 * @param page
	 */
	public void queryInformationList(PageAssistant page);
	/**
	 * 查询会议信息列表(已处理)
	 * @param page
	 */
	public void queryInformationListed(PageAssistant page);
	/**
	 * 新增会议信息(保存到mongo)
	 * @param json
	 */
	public void addConferenceInformation(String json);
	/**
	 * 无需上会
	 * @param businessId
	 * @param stage
	 * @param need_meeting 
	 */
	public void updateStageById(String businessId, String stage, String need_meeting, String projectType);
	
	/**
	 * 查询所有评审项目
	 * @param page
	 */
	public PageAssistant queryAllInfoByPage(PageAssistant page);
	
	/**
	 * 统计所有评审项目
	 */
	public int countAll();
	
	/**
	 * 修改固定小组成员ids
	 * @param businessId
	 * @param fixedGroupIds
	 */
	public void updateFixGroupIds(String businessId, String fixedGroupIds);
	
	/** 获取正式评审项目报表
	 */
	public List<Map<String, Object>> getProjectReport0706();
	
	/** 获取投标评审项目报表
	 */
	public List<Map<String, Object>> getProjectReport0710();
	/** 获取正式评审项目报表详情
	 */
	public PageAssistant getProjectReportDetails0706(PageAssistant page);
	/** 获取投标评审项目报表详情
	 */
	public PageAssistant getProjectReportDetails0710(PageAssistant page);
	/**
	 * 根据业务ID，删除  正式评审的oracle和mongdb
	 * @param businessid 业务ID
	 */
	public void deleteOracleAndMongdbByBusinessId(String businessid);
	/**
	 * 查询正式评审列表stage：3.5
	 * @param page
	 */
	public void queryStartByPage(PageAssistant page);
	/**
	 * 查询正式评审列表stage：3.9
	 * @param page
	 */
	public void queryOverByPage(PageAssistant page);
	/**
	 * 根据businessId,修改正式评审项目的阶段
	 * @param businessId
	 * @param stage
	 */
	public void updateStageById(String businessId, String stage);
	
	/**
	 * 保存专家评审意见
	 * @param json
	 * @param businessId
	 * @param user 
	 */
	public void saveMajMemberInfo(String json, String businessId, Map<String, Object> user);
	/**
	 * 保存选择的专家到mongo
	 * @param json
	 * @param businessId 
	 */
	public void saveMajorMemberOption(String json, String businessId);

	/**
	 * 获取所有评审项目(刷大区使用)
	 * @return
	 */
	public List<Map<String, Object>> queryPertainAreaIsNull();
	
	/**
	 * 更新大区ID(刷大区使用)
	 * @param id
	 * @param pertainAreaId
	 */
	public void updatePertainAreaId(String id, String pertainAreaId);
	
	/**
	 * 根据业务ID，启动归档
	 * @param businessId
	 * @param date
	 */
	public void startPigeonholeByBusinessId(String businessId, Date date);
	/**
	 * 根据业务ID，更新归档状态
	 * @param businessId
	 * @param status 0:未归档,1:归档中,2:已归档,3:已归档,有文件缺失
	 */
	public void updatePigeStatByBusiId(String businessId,String status);
	/**
	 * 根据业务ID，取消归档
	 * @param businessId
	 */
	public void cancelPigeonholeByBusinessId(String businessId);
	/**
	 * 查询正式评审大区
	 * @return
	 */
	public List<Map<String, Object>> queryFormalPertainArea();
	/**
	 * 查询项目统计
	 * @param wf_state
	 * @param stage
	 * @param pertainAreaId
	 * @param serviceTypeId
	 * @param year 
	 * @return
	 */
	public List<Map<String, Object>> queryFormalCount(String wf_state,String stage, String result,String pertainAreaId, String serviceTypeId, String year);
	/**
	 * 按条件查询
	 * @param stage
	 * @param state
	 * @return
	 */
	public List<Map<String, Object>> queryByStageAndstate(String stage,String state);
	/**
	 * 查询所有大区人为空
	 * @return
	 */
	public List<Map<String, Object>> queryAllLargePersonIsNull();
	
	/**
	 * 根据风控业务ID，查询项目的合同信息
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> queryContractByBusinessId(String businessId);
	/**
	 * 保存基础文件
	 */
	public void updateAttachment(String json);
	
	/**
	 * 保存风控文件
	 */
	public Result updateRiskAttachment(String json);
	/**
	 * 删除基础附件
	 * @param json
	 */
	public Result deleteAttachment(String json);
	/**
	 * 删除风控附件
	 * @param json
	 */
	public Result deleteRiskAttachment(String json);
	
	/**
	 * 删除标记位
	 * @param json
	 */
	public void deleteSign(String json);

	/**
	 * 替换基础附件
	 * @param json
	 */
	public void changeAttachment(String json);
	
	/**
	 * 保存上会附件
	 * @param json
	 */
	public void saveMeetingFiles(String json);
	/**
	 * 批量移除未读标记
	 * @param json
	 */
	public void signRead(String json);
	
	public List<Map<String, Object>> queryAllByDaxt();
	public PageAssistant queryEnvirByPage(PageAssistant page);
	public PageAssistant queryEnvirSubmitedByPage(PageAssistant page);
	Result saveOrUpdateForTz_V01(Document doc, Result result);
	
	// 调用投资接口修改数据
	public void updateRiskAuditInfo(String businessId, String wf_state, String mark);
	
}
