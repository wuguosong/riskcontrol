/**
 * 
 */
package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 组织机构模块
 * @author wufucan
 *
 */
public interface IOrgService {
	/**
	 * 根据orgPkvalue查询org信息
	 * @param pkvalue
	 * @return
	 */
	public Map<String, Object> queryByPkvalue(String pkvalue);
	/**
	 * 根据pkvalue查询所属战区信息，如果找不到，返回null
	 * @param orgpkvalue
	 * @return
	 */
	public Map<String, Object> queryPertainAreaByPkvalue(String orgpkvalue);
	/**
	 * 根据用户id查询单位负责人信息
	 * @param userId
	 * @return
	 */
	public Map<String, Object> queryGroupUserInfo(String userId);
	/**
	 * 查询单位和单位负责人表信息，不分页
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryReportOrg(Map<String, Object> params);
	/**
	 * 根据parentId为空则查询顶级org,反之查询父节点为parentId的org
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryCommonOrg(Map<String, Object> params);
	
	/**
	 * 根据申报单位查询  所属	大区(战区)ID
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryPertainArea(String orgpkvalue);
	public List<Map<String, Object>> queryAllOrg(PageAssistant params);
}
