/**
 * 
 */
package com.yk.power.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author wufucan
 *
 */
public interface IOrgMapper extends BaseMapper {
	/**
	 * 根据orgpkvalue查询组织机构信息
	 * @param pkvalue
	 * @return
	 */
	public Map<String, Object> queryByPkvalue(@Param("orgpkvalue")String pkvalue);
	/**
	 * 根据pkvalue查询所属战区信息，如果找不到，返回null
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryPertainAreaByPkvalue(Map<String, Object> params);
	/**
	 * 根据单位orpkvalue查询单位负责人信息
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryGroupUserInfo(Map<String, Object> params);
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
	public Map<String, Object> queryPertainArea(Map<String, Object> params);
	/**
	 * 查询所有单位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryAllOrg(Map<String, Object> params);
}
