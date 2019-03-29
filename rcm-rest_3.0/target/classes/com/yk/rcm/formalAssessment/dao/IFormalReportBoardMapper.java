package com.yk.rcm.formalAssessment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

public interface IFormalReportBoardMapper extends BaseMapper {

	/**
	 * 获取正式评审的项目
	 * 
	 * @param params
	 *            Map&lt;String,Object&gt;
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryFormalReportBoardByPage(Map<String, Object> params);
	/**
	 * 获取统计数据
	 * @param string
	 * @return
	 */
	public int getCountsByStages(@Param("stage")String stage,@Param("wf_state")String wf_state);

}
