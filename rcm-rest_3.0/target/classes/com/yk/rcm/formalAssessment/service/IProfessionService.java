/**
 * 
 */
package com.yk.rcm.formalAssessment.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;

/**
 * 专业评审人员service
 * @author wufucan
 *
 */
public interface IProfessionService {

	/**
	 * 新增组
	 * @param data
	 * @return 
	 */
	public Result addTeam(String json);
	/**
	 * 查询所有的组，不分启用禁用
	 * @return
	 */
	public void queryAllTeams(PageAssistant page);
	 /**
	  * 根据组的id查询组内成员（没有删除的）
	  * @param teamId
	  * @return
	  */
	public List<Map<String, Object>> queryMembersByTeamId(String teamId);
	/**
	 * 更新组信息
	 * @param json
	 * @return
	 */
	public Result updateTeam(String json);
	/**
	 * 查询一个用户的数据
	 * @param teamId
	 * @return
	 */
	public Map<String, Object> getTeamByID(String teamId);
	/**
	 * 修改组的状态为禁用
	 * @param teamId
	 */
	public void updateTeamSatusById(String teamId);
	/**
	 * 查询专业评审负责人
	 * @return
	 */
	public List<Map<String, Object>> queryProfessionReview();
	
}
