package com.yk.rcm.decision.serevice;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author hubiao
 *
 */
public interface IDecisionReviewService {


	/**
	 * 获取今日及以后的已安排上会的项目
	 * @return
	 */
	public List<Map<String, Object>> queryList();

	/**
	 * 统计今日及以后的已安排上会的项目
	 * @return
	 */
	public int countTodayLater();
	
	/**
	 * 查询所有 待表决的项目
	 * @param page 
	 * @return
	 */
	public void queryWaitDecisionListByPage(PageAssistant page);

	/**
	 * 历史决策会数量
	 * @return
	 */
	public int countHistoryDecision();
	
	/**
	 * 分页查询历史决策会
	 * @return
	 */
	public void queryHistoryDecisionReviewListByPage(PageAssistant page);

	/**
	 * 过会
	 * @param page 
	 * @return
	 */
	public PageAssistant queryXsh(PageAssistant page);
	
	/**
	 * 无需过会
	 * @return
	 */
	public PageAssistant queryWxsh(PageAssistant page);
}
