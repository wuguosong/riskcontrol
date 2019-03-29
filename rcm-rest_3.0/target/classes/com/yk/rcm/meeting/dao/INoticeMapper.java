package com.yk.rcm.meeting.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 通知信息
 * @author hubiao
 */
public interface INoticeMapper extends BaseMapper {

	/**
	 * 添加通知
	 */
	public void add(Map<String, Object> notice);

	/**
	 *  根据关联ID，查询所有通知信息
	 * @param meetingIssueId
	 * @return
	 */
	public List<Map<String,Object>> queryByRelationId(@Param("relationId")String relationId);
	
	/**
	 * 根据ID，删除通知信息
	 * @param id
	 */
	public void delete(@Param("id")String id);

	/**
	 * 分页查询数据
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryListByPage(Map<String, Object> params);

	/**
	 * 根据条件查询   前几条数据
	 * @param notice
	 * 		notice.type  通知类型(必填)
	 * 		notice.limit 表示前几条,默认为10条(可填)
	 * @return
	 */
	public List<Map<String, Object>> queryListTopLimit(
			Map<String, Object> notice);

	/**
	 * 根据条件查询详情
	 * @param notice
	 * 	notice.id 通知ID(必填)
	 * @return
	 */
	public Map<String, Object> queryInfo(Map<String, Object> notice);
}
