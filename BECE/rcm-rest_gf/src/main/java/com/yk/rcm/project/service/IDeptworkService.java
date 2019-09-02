/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 部门工作情况-正式评审service
 * @author 80845530
 *
 */
public interface IDeptworkService {
	
	public Map<String, Object> queryFormalAllViewById(String businessId);
	
	public void queryFormalGoingList(PageAssistant page);

	public void queryFormalDealedList(PageAssistant page);

	public void queryPreGoingList(PageAssistant page);

	public void queryPreDealedList(PageAssistant page);

	public Map<String, Object> queryProjectAllViewById(String businessId);
	
	/**
	 * 查询所有投标评审数量
	 * @return
	 */
	public int countYPSAll();
	
	/**
	 * 查询所有的评审角色用户
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> queryPsfzrUsers();

	List<Map<String, Object>> queryFlfzrUsers();

	List<Map<String, Object>> countByStatus1();
	/**
	 * 根据状态统计个数
	 * @param status
	 * @return
	 */
	List<Map<String, Object>> countByStatus2();

	/**
	 * 查询投标评审未过会项目  并根据大区统计个数
	 * @return
	 */
	public List<Map<String, Integer>> getPreWghGroupAreaReports1401();
	
	/**
	 * 查询投标评审未过会项目  并根据大区统计个数
	 * @return
	 */
	public List<Map<String, Integer>> getPreWghGroupAreaReports1402();
	
	/**
	 * 查询正式评审未过会项目  并根据大区统计个数
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports1401();
	
	/**
	 * 查询正式评审未过会项目  并根据大区统计个数
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports1402();

	/**
	 * 查询业务评审小组报表
	 * @return
	 */
	public List<Map<String, Object>> getProjectReport0922ByYw();

	/**
	 * 查询法律评审小组报表
	 * @return
	 */
	public List<Map<String, Object>> getProjectReport0922ByFL();
	
	/**
	 * 根据组id查询人员
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> getFzrByTeamId(String teamID);
	
	/**
	 * 根据法律组id查询人员
	 * @param businessId
	 * @return
	 */
	public List<Map<String, Object>> getFlFzrByTeamId(String teamID);

	
	public Map<String, Object> queryPreAllViewById(String businessId);

	/**
	 * 查询所有的正式评审，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaAllGroupAreaReports();

	public List<Map<String, Integer>> getFormalaWghGroupAreaReports();
	public List<Map<String, Integer>> getPreaWghGroupAreaReports();

	/**
	 * 查询未过会的正式评审，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghReportsByServiceType();
	/**
	 * 查询未过会的投标评审，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaWghReportsByServiceType();

	/**
	 * 获取未过会项目规模，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghReportsByAreaWithGm();
	/**
	 * 获取未过会投标评审项目规模，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaWghReportsByAreaWithGm();

	/**
	 * 获取已过会项目数量，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaYghGroupAreaReports();
	/**
	 * 获取投标评审已过会项目数量，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaYghGroupAreaReports();

	/**
	 * 获取已过会项目规模，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaYghReportsByAreaWithGm();
	/**
	 * 获取投标评审已过会项目规模，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaYghReportsByAreaWithGm();

	/**
	 * 查询未过会的正式评审项目规模，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghReportsByServiceWithGm();
	/**
	 * 查询未过会的投标评审项目规模，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaWghReportsByServiceWithGm();

	/**
	 * 获取已过会项目数量，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getFromYghGroupServiceWithNum();
	/**
	 * 获取投标评审已过会项目数量，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreFromYghGroupServiceWithNum();

	/**
	 * 获取已过会项目规模，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getFromYghGroupServiceWithGm();
	/**
	 * 获取投标评审已过会项目规模，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreFromYghGroupServiceWithGm();

	/**
	 * 查询跟进中的数量 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getFromWghByServietypeAndAreaWithNum();
	/**
	 * 查询投标评审跟进中的数量 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getPreFromWghByServietypeAndAreaWithNum();
	
	/**
	 * 查询已过会的数量 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getFromYghByServietypeAndAreaWithNum();
	/**
	 * 查询投标评审已过会的数量 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getPreFromYghByServietypeAndAreaWithNum();

	
	/**
	 * 查询跟进中的规模 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getFromWghByServietypeAndAreaWithGm();
	/**
	 * 查询投标评审跟进中的规模 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getPreFromWghByServietypeAndAreaWithGm();
	
	/**
	 * 查询已过会的规模 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getFromYghByServietypeAndAreaWithGm();
	/**
	 * 查询投标评审已过会的规模 根据业务类型 和 大区分组
	 * @return
	 */
	public List<Map<String, Object>> getPreFromYghByServietypeAndAreaWithGm();
	
	/**
	 * 查询正式评审，投标评审，其他评审的跟进中，已决策项目
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryProjects(PageAssistant page);

	public int countTzjcAll();

	public int countTzjcWxsh();

	public int countTzjcXsh();
	/**
	 * 其他评审报表按评审类型划分
	 * @param year 
	 * @return
	 */
	public List<Map<String, Object>> getBulletinTypeReport(String year);

	public List<Map<String, Object>> getBulletinFromWghByServietypeAndAreaWithNum();

	public List<Map<String, Integer>> getBulletinWghGroupAreaReports();

	public List<Map<String, Object>> getBulletinFromYghByServietypeAndAreaWithNum();

	public List<Map<String, Integer>> getBulletinYghGroupAreaReports();
	/**
	 * 获取同意的评审数量按大区、项目类型分组
	 * @return
	 */
	Map<String, Object> getNoticeTYCount();
	/**
	 * 获取不同意的评审数量按大区、项目类型分组
	 * @return
	 */
	Map<String, Object> getNoticeBTYCount();
	/**
	 * 获取同意的评审数量按大区分组
	 * @return
	 */
	List<Map<String, Integer>> getNoticedTYGroupAreaReports();
	/**
	 * 获取不同意的评审数量按大区分组
	 * @return
	 */
	List<Map<String, Integer>> getNoticedBTYGroupAreaReports();
	/**
	 * 查询已决策项目的大区
	 * @return
	 */
	public List<Map<String, Object>> queryNoticedPertainArea();
	/**
	 * 查询已决策项目类型
	 * @return
	 */
	public List<Map<String, Object>> queryNoticedType();
	/**
	 * 查询已决策项目统计
	 * @param result
	 * @param pertainAreaId
	 * @param serviceTypeId
	 * @param year 
	 * @return
	 */
	public List<Map<String, Object>> queryNoticedCount(String result,String pertainAreaId, String serviceTypeId, String year);
}
