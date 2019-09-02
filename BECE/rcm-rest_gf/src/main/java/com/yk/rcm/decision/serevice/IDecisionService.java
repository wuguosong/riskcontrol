package com.yk.rcm.decision.serevice;

import java.util.List;
import java.util.Map;

/**
 * @author hubiao
 *
 */
public interface IDecisionService {
	
	/**
	 * 根据条件  分页查询所有决策会议
	 */
	public List<Map<String, Object>> queryList();
	
	/**
	 * 更新项目  决策状态
	 * @param id
	 * @param status
	 */
	public void updateVoteStatus(String id, int status);

	/**
	 * 获取当前决策项目
	 * @return
	 */
	public Map<String, Object> getCurrUnderwayProject();
	
	/**
	 * 添加 当前用户 决策 意见
	 * @param formalId
	 * @param formalType
	 * @param aagreeOrDisagree
	 */
	public void addDecisionOpinion(String formalId, String formalType,
			String aagreeOrDisagree);

	/**
	 *  是否有项目正在决策中
	 * @return
	 */
	public boolean isCurrUnderwayProject();
	
	/**
	 * 获取正在决策信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> getCurrDecisionOpinion(String id);

	/**
	 * 获取决策结果信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> getDecisionResult(String id);
	
	/**
	 * 是否使用否决权
	 * @param resultData
	 */
	public void isShiYongFouJueQuan(Map<String, Object> resultData);

	public List<Map<String, Object>> queryHistory();

	/**
	 * 根据决策id，更新其对应的决策会委员
	 * @param id
	 * @param meetingLeaders
	 * @param chairmanId 
	 */
	public void updateMeetingLeadersById(String id, List<Map<String, Object>> meetingLeaders, String chairmanId);

	/**
	 * 是否为	今天上会(表决)
	 * @return 
	 */
	public boolean isTodayDecision();
	
	/**
	 * 是否为	有当前用户需要表决的项目
	 * @return
	 */
	public boolean isUserDecision();

	/**
	 * 撤消项目表决
	 */
	public void cancelDecision();

	/**
	 * 根据ID，获取表决项目
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryById(String id);

	/**
	 * 根据  业务ID，获取表决项目
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryByBusinessId(String businessId);
	
	/**
	 * 获取 会议主席
	 * @param meetingLeadersList
	 * @return
	 */
	public String getChairman(List<Map<String, Object>> meetingLeadersList);
	
	/**
	 * 根据项目决策ID，查询相应决策信息
	 * @param decisionIdList
	 * @return
	 */
	public List<Map<String, Object>> queryByIds(List<Map<String,Object>> projectArray);

	/**
	 * 添加会议之前，按业务ID删除项目
	 * @param decision
	 */
	public void insertBeforeDelete(Map<String, Object> decision);
	
	/**
	 * 迁移数据
	 * @param decision
	 */
	public void insert(Map<String, Object> decision);
	
	
	/**
	 * 重置(还原)表决
	 */
	public void resetDecision(String id);

	public void addDecisionOpinionNew(String formalId, String formalType, String aagreeOrDisagree, String zhuxiStatus);

	public Map<String, Object> getDecisionResultNew(String id);
	

	/**
	 * 获取投票结果详情
	 * 
	 * @param id
	 * @return Map
	 */
	public Map<String, Object> getDecisionResultInfo(String id);
	
}
