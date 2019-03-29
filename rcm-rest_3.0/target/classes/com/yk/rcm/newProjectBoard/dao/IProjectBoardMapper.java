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
	 * 获取项目
	 */
	public List<Map<String, Object>> getProjectList(Map<String, Object> params);
}
