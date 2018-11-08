/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 
 * @author 80845530
 *
 */
public interface IBusinessDictMapper extends BaseMapper{
	
	public List<Map<String, Object>> queryByParentCode(@Param("code")String code);
}
