package com.yk.rcm.pre.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IPreMeetingInfoMapper extends BaseMapper {
	
	/**
	 * 查询参会信息列表(未处理)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryInformationList(Map<String, Object> params);
	/**
	 * 查询参会信息列表(已处理)
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryInformationListed(Map<String, Object> params);
	/**
	 * 新增之后改stage状态
	 * @param map
	 */
	public void updateStage(Map<String, Object> map);
	/**
	 * 无需上会并修改状态
	 * @param map
	 */
	public void updateStageById(Map<String, Object> map);
	/**
	 * 无需上会信息列表
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryNotMeetingList(Map<String, Object> params);
	
	
}
