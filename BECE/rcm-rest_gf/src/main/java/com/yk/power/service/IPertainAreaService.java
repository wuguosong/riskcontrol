/**
 * 
 */
package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;

/**
 * 大区人员关系模块
 * @author yaphet
 */
public interface IPertainAreaService {
	/**
	 * 通过orgPkvalue查询大区对应列表
	 * @param orgPkvalue
	 * @return
	 */
	List<Map<String, Object>> queryPertainAreaByOrgPkValue(String orgPkvalue);
	/**
	 * 修改人员信息
	 * @param pid
	 * @param newUserId
	 * @param type
	 */
	void updatePersonByPertainAreaId(String pid, String newUserId, String type);
	/**
	 * 根据id修改人
	 * @param id
	 * @param newUserId
	 */
	void updateById(String id, String newUserId);
	
	/**
	 * 根据id修改人
	 * @param json
	 */
	Result updateByUserId(String json);
	
	/**
	 * 查询列表
	 * @param page 
	 * @return
	 */
	List<Map<String, Object>> queryList(PageAssistant page);
	
	/**
	 * 根据id查询数据
	 * @param id
	 * @return
	 */
	Map<String, Object> getByUserId(String userId);
	
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	Result deleteByUserId(String userId);
	
	/**
	 * save
	 * @param json
	 * @return
	 */
	Result save(String json);

}
