/**
 * 
 */
package com.yk.power.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author wufucan
 *
 */
@Repository
public interface IUserMapper extends BaseMapper {

	/**
	 * 根据用户的uuid查询
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> queryById(Map<String, Object> paramMap);
	/**
	 * 根据条件分页查询
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryByCondition(Map<String, Object> params);
	
	public List<Map<String, Object>> selectUser(Map<String, Object> params);
	
	public List<Map<String, Object>> directiveUser(Map<String, Object> params);
	
	public Map selectOneUser(Map<String, Object> paramMap);
	
	public List<Map> selectUserPosition(Map<String, Object> paramMap);
	
	public int selectIsAdmin(Map<String, Object> paramMap);
	
	public Map<String, Object> selectAUser(Map<String, Object> params);
	
	public List<Map<String, Object>> queryRoleByUser(Map<String, Object> params);
	/**
	 * 查询所有角色
	 * @return
	 */
	public List<Map<String, Object>> getAllRole();
	/**
	 * 根据userid查询关联角色
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getRoleByUserId(Map<String, Object> map);
	
	public void deleteUserRoleByUserId(String userId);
	
	public void saveUserRole(HashMap<String, Object> map);
	
	
	/**
	 * 按角色分页查询用户信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> directiveRoleUser(Map<String, Object> params);
	
	/**
	 * 按条件查询用户信息总数量
	 * @param params
	 * @return
	 */
	public int directiveRoleUserCount(Map<String, Object> params);
	
}
