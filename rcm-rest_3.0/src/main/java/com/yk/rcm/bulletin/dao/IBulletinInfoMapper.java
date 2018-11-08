/**
 * 
 */
package com.yk.rcm.bulletin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author wufucan
 *
 */
public interface IBulletinInfoMapper extends BaseMapper {
	/**
	 * 保存
	 * @param data
	 */
	public void save(Map<String, Object> data);
	/**
	 * 根据业务id删除
	 * @param businessId
	 */
	public void deleteByBusinessId(@Param("businessId")String businessId);
	/**
	 * 根据业务id修改
	 * @param data
	 */
	public void updateByBusinessId(Map<String, Object> data);
	/**
	 * 查询起草中
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryApplyByPage(Map<String, Object> params);
	/**
	 * 查询已提交
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryApplyedByPage(Map<String, Object> params);
	/**
	 * 查询通报事项对应的业务负责人
	 * @return
	 */
	public List<Map<String, Object>> queryTbsxUserRelations();
	/**
	 * 批量删除(假删除，改状态)
	 * @param params
	 */
	public void deleteByIds(Map<String, Object> params);
	/**
	 * 根据业务id查询
	 * @param bulletin
	 */
	public Map<String, Object> queryByBusinessId(@Param("businessId")String businessId);
	/**
	 * 修改审核状态
	 * @param businessId
	 * @param auditStatus
	 */
	public void updateAuditStatusByBusinessId(Map<String, Object> params);
	/**
	 * 流程启动成功后，修改提交时间和审核状态
	 * @param businessId
	 * @param auditStatus
	 */
	public void updateAfterStartflow(Map<String, Object> params);
	
	/**
	 * 修改通报事项	流程阶段 状态  
	 * @param map
	 */
	public void updateAuditStageByBusinessId(Map<String, Object> map);
	/**
	 * 查询未出会议纪要（流程状态为stage：4）
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> quaryMettingSummary(Map<String, Object> params);
	/**
	 * 查询已出会议纪要（流程状态为stage：5）
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryMettingSummaryed(Map<String, Object> params);
	/**
	 * 保存之后修改stage状态为"5"
	 * @param map
	 */
	public void updateStage(Map<String, Object> params);
	/**
	 * 查询rcm_bulletin_info数据信息
	 * @return
	 */
	public List<Map<String, Object>> queryBulletin();
	
	/**
	 * 根据ID修改事项类型
	 * @param id
	 */
	public void updateByBusinessIdWithBulletinypeId(HashMap<String, Object> params);
	/**
	 * 统计所有数量
	 * @return
	 */
	public int countAll();
	/**
	 * 修改人员信息
	 * @param data
	 */
	public void updatePerson(HashMap<String, Object> data);
	/**
	 * 按阶段与审核状态查询项目
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> queryByStageAndstate(Map<String, Object> map);
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
	 * 更新大区ID
	 * @param string
	 * @param pertainAreaId
	 */
	public void updatePertainAreaId(Map<String, Object> params);
	
	/**
	 * 查询项目统计
	 * @return
	 */
	public List<Map<String, Object>> queryBulletinCount(Map<String, Object> map);
	
	/**
	 * 差所欲投标评审
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryAllInfoByPage(Map<String, Object> params);
	/**
	 * 分页列表
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryListByPage(Map<String, Object> params);
	
	/**
	 * 查询历史项目需要档案信息
	 * @return
	 */
	public List<Map<String, Object>> queryAllByDaxt();
}
