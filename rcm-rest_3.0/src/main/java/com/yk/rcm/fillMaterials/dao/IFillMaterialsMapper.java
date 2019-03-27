package com.yk.rcm.fillMaterials.dao;

import com.yk.common.BaseMapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author gaohe
 * 
 * 2019年3月26日
 *
 */

@Repository
public interface IFillMaterialsMapper extends BaseMapper {

	public List<Map<String, Object>> queryNoSubmitList(Map<String, Object> params);

	public List<Map<String, Object>> querySubmitList(Map<String, Object> params);

	
}
