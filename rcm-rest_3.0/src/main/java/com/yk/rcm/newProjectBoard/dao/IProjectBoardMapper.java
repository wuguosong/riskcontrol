package com.yk.rcm.newProjectBoard.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * @author Sunny Qi
 *
 */
public interface IProjectBoardMapper extends BaseMapper {

	/**
	 * 获取所有项目
	 */
	public List<Map<String, Object>> getALLProjectList(Map<String, Object> params);

	/**
	 * 获取角色绑定的项目以及本人参与审批的项目
	 */
	public List<Map<String, Object>> getRoleProjectList(Map<String, Object> params);

	/**
	 * 获取所有项目数量
	 */
	public int getALLProjectListCount(Map<String, Object> params);

	/**
	 * 获取角色绑定的项目以及本人参与审批的项目数量
	 */
	public int getRoleProjectListCount(Map<String, Object> params);
	
	/** 20190610 改版后 **/
	/**
	 * 获取正式评审所有项目
	 * */
	public List<Map<String, Object>> getAllPfrProjectList(Map<String, Object> params);
	
	/**
	 * 获取正式评审所有项目数量
	 * */
	public int getAllPfrProjectListCount(Map<String, Object> params);
	
	/**
	 * 获取投标评审所有项目
	 * */
	public List<Map<String,Object>> getAllPreProjectList(Map<String, Object> params);
	
	/**
	 * 获取正式评审所有项目数量
	 * */
	public int getAllPreProjectListCount(Map<String, Object> params);
	
	/**
	 * 获取其他评审所有项目
	 * */
	public List<Map<String, Object>> getAllBulletinProjectList(Map<String, Object> params);
	
	/**
	 * 获取正式评审所有项目数量
	 * */
	public int getAllBulletinProjectListCount(Map<String, Object> params);

	
	/**
	 * 查询正式项目列表, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public List<Map<String, Object>> getRolePfrProjectList(Map<String, Object> params);
	
	/**
	 * 查询正式项目列表数量, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public int getRolePfrProjectListCount(Map<String, Object> params);
	
	/**
	 * 查询投标项目列表, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public List<Map<String,Object>> getRolePreProjectList(Map<String, Object> params);
	
	/**
	 * 查询投标项目列表数量, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public int getRolePreProjectListCount(Map<String, Object> params);
	
	/**
	 * 查询其他项目列表, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public List<Map<String, Object>> getRoleBulletinProjectList(Map<String, Object> params);
	
	/**
	 * 查询其他项目列表数量, 查询角色绑定的项目以及本人参与审批的项目
	 * */
	public int getRoleBulletinProjectListCount(Map<String, Object> params);
} 
