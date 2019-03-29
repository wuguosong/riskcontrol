package com.yk.rcm.newPre.dao;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author Sunny Qi
 *
 */
@Repository
public interface IPreInfoCreateMapper extends BaseMapper {
	
	/**
	 * 新增项目到Oracle
	 * */
	public void insert(Map<String, Object> dataForOracle);
	
	/**
	 * 修改Oracle中的项目
	 * */
	public void update(Map<String, Object> dataForOracle);
	
	/**
	 * 删除Oracle中的项目
	 * */
	public void delete(String id);
	
	/**
	 * 获取待提交决策会材料的项目
	 */
	public List<Map<String, Object>> getNewProjectList(Map<String, Object> params);
	
	/**
	 * 通过业务ID获取项目数据
	 * */
	public Map<String, Object> getProjectByID(String businessId);
}
