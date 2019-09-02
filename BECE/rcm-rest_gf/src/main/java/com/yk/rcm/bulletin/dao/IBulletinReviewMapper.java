/**
 * 
 */
package com.yk.rcm.bulletin.dao;

import java.util.List;
import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * @author wufucan
 *
 */
public interface IBulletinReviewMapper extends BaseMapper {
	/**
	 * 查询待审阅
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryWaitByPage(Map<String, Object> params);
	/**
	 * 查询已审阅
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryApplyedByPage(Map<String, Object> params);
}
