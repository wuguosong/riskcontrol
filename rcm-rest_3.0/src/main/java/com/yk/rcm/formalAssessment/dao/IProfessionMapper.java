package com.yk.rcm.formalAssessment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 专业评审人员mapper
 * @author wufucan
 *
 */
public interface IProfessionMapper extends BaseMapper {

	/**
	 * 查询所有的组，不分启用禁用
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryAllTeams(Map<String, Object> params);
	/**
	 * 查询team表验证部门名称是否存在
	 * @return
	 */
	int quaryReviewtype();
	/**
	 * 新增PROFESSION_TEAM表的数据
	 * @param  
	 */
	void insertProfessionTeam(Map<String, Object> paramMap);
	/**
	 * 新增所有的数据
	 * @param paramMap2 
	 */
	void insertAll(Map<String, Object> paramMap2);
	/**
	 * 根据组的id查询组内成员
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> queryMembersByTeamId(Map<String, Object> paramMap);
	/**
	 * 查询一个用户
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> selectOneTeam(Map<String, String> paramMap);
	List<Map<String, Object>> selectTeamItem(Map<String, String> paramMap);
	/**
	 * 更新组信息
	 * @param paramMap
	 */
	void updateTeam(Map<String, Object> paramMap);
	void deleteItem(Map<String, Object> paramMap);
	void deleteTeam(Map<String, Object> paramMap);
	/**
	 * 修改组的状态为禁用
	 * @param params
	 */
	void updateTeamSatusById(Map<String, Object> params);
	/**
	 * 查询专业评审负责人
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryProfessionReview(Map<String, Object> params);
}
