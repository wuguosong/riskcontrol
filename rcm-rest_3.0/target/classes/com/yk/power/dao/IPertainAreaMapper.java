/**
 * 
 */
package com.yk.power.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * @author yaphet
 */
public interface IPertainAreaMapper extends BaseMapper {

	List<Map<String, Object>> queryPertainAreaByOrgPkValue(Map<String, Object> map);

	void updatePersonByPertainAreaId(Map<String, Object> map);
	
	void deleteByUserId(Map<String, Object> map);
	
	void deleteByOrgPkValue(Map<String, Object> map);
	
	void save(Map<String, Object> map);

	void updateById(Map<String, Object> map);
	
	void updatePersonById(Map<String, Object> map);

	List<Map<String, Object>> queryList(Map<String, Object> params);

	Map<String, Object> getByUserId(Map<String, Object> map);

	List<Map<String, Object>> queryAll();
	
}
