package com.yk.rcm.noticeofdecision.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface INoticeDecisionConfirmInfoMapper extends BaseMapper{
	
	/**
	 * 查询待确认数据，查询正式评审列表(待确认) stage：6
	 * @param page
	 */
	public List<Map<String, Object>> queryWaitConfirm(Map<String, Object> params);
	/**
	 * 分页查询已确认数据，查询正式评审列表(已确认) stage：7
	 * @param page
	 */
	public List<Map<String, Object>> queryConfirmed(Map<String, Object> params);
	/**
	 * 修改stage='7'和保存決策通知确认时间
	 * @param map
	 */
	public void updateStageConfirmTime(Map<String, Object> map);

}
