package com.yk.rcm.noticeofdecision.dao;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;
public interface INoticeDecisionDraftInfoMapper extends BaseMapper{
	
	/**
	 * 初始化决策通知书列表--状态未提交
	 * @param PageAssistant
	 */
	  List<Map<String,Object>> queryStartByPage(Map<String, Object> params);
	
	/**
	 * 初始化决策通知书列表--状态已提交
	 * @param PageAssistant
	 */
	 List<Map<String,Object>> queryOverByPage(Map<String, Object> params);
	 
	 /**
		 * 新增决策通知书
		 * @param json
		 * @return
		 */
	void save (Map<String, Object> params);
	
	/**
	 * 修改决策通知书信息
	 * @param json
	 * @return
	 */
	void update(Map<String, Object> paramsForOracle);
	/**
	 * 根据正式评审项目id删除（起草状态的）决策通知书
	 * @param oracleParams
	 */
	void deleteByFormalIds(Map<String, Object> oracleParams);

	Map<String, Object> queryById(@Param("businessId")String businessId);

	void updateStage(Map<String, Object> map);
	/**
	 * 查询可以起草决策通知书的项目
	 * @return
	 */
	List<Map<String, Object>> queryFormalForCreate(Map<String, Object> params);
	/**
	 * 根据businessId修改stage='3.9'
	 * @param map
	 */
	void updateStageByBusinessId(Map<String, Object> map);
	/**
	 * 查询未上会的数据
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> notMeetingList(Map<String, Object> params);

}
