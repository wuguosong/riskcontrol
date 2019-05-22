package com.yk.rcm.newProjectBoard.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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
}
