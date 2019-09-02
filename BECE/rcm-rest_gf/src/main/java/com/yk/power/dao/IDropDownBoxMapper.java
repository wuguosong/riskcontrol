package com.yk.power.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IDropDownBoxMapper extends BaseMapper {
	/**
	 * 根据code查字典获取下拉框
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> queryByCode(@Param("code")String code);
	
	
}
