package com.daxt.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IDaxtMapper extends BaseMapper {

	public int addEssFk(Map<String, Object> essFk);

	public void addDssFkDzwj(@Param("essFkDzwjs") List<Map<String, Object>> essFkDzwjs);

	public void deleteESSFK(@Param("fkId") String fkId);
	
	public void deleteESSFKDZWJ(@Param("fkId") String fkId);
}
