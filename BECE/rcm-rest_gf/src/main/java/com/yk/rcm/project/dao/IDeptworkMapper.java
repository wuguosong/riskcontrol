/**
 * 
 */
package com.yk.rcm.project.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * 
 * @author hubiao
 *
 */
public interface IDeptworkMapper extends BaseMapper{
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

	public List<Map<String, Object>> queryFlfzrUsers();

	/**
	 * 根据状态统计个数 未过会
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> countByStatus1();
	
	/**
	 * 根据状态统计个数 已过会
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> countByStatus2();

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
	public List<Map<String, Object>> getProjectReport0922ByFl();
	
	/**
	 * 根据组id查询人员
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getFzrByTeamId(Map<String, Object> params);
	
	/**
	 * 根据法律组id查询人员
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getFlFzrByTeamId(Map<String, Object> params);

	/**
	 * 查询所有的正式评审，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaAllGroupAreaReports();

	/**
	 * 查询未上会的正式评审，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports();
	/**
	 * 查询未上会的投标评审，根据大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getPreaWghGroupAreaReports();
	/**
	 * 查询未上会的正式评审，根据业务类型分组
	 * @return
	 */
	public List<Map<String, Integer>> getFormalaWghReportsByServiceType();
	/**
	 * 查询未上会的投标评审，根据业务类型分组
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
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryProjects(Map<String, Object> params);
	/**
	 * @param data
	 * @return
	 */
	public Map<String, Object> queryFLFZR(Map<String, Object> data);
	/**
	 * @param data
	 * @return
	 */
	public Map<String, Object> queryYWFZR(Map<String, Object> data);

	public int countTzjcAll();

	public int countTzjcWxsh();

	public int countTzjcXsh();
	/**
	 * 其他评审饼图数据按类划分
	 * @param map 
	 * @return
	 */
	public List<Map<String, Object>> getBulletinTypeReport(Map<String, Object> map);

	/**
	 * 其他决策事项的 未过会
	 * @return
	 */
	public List<Map<String, Object>> getBulletinFromWghByServietypeAndAreaWithNum();

	/**
	 *  其他决策事项的 未过会  柱状图
	 * @return
	 */
	public List<Map<String, Integer>> getBulletinWghGroupAreaReports();

	/**
	 * 其他决策事项的 已过会
	 * @return
	 */
	public List<Map<String, Object>> getBulletinFromYghByServietypeAndAreaWithNum();
	
	/**
	 * 其他决策事项的 已过会  柱状图
	 * @return
	 */
	public List<Map<String, Integer>> getBulletinYghGroupAreaReports();
	/**
	 * 获取同意的评审数量按大区,项目类型分组
	 * @return
	 */
	public List<Map<String, Object>> getNoticeTYCount();
	
	/**
	 * 获取同意的评审数量按大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getNoticedTYGroupAreaReports();
	/**
	 * 获取不同意的评审数量按大区,项目类型分组
	 * @return
	 */
	public List<Map<String, Object>> getNoticeBTYCount();
	
	/**
	 * 获取不同意的评审数量按大区分组
	 * @return
	 */
	public List<Map<String, Integer>> getNoticedBTYGroupAreaReports();
	
	/**
	 * 查询所有已决策项目
	 * @return
	 */
	public List<Map<String, Object>> queryAllNoticed();
	/**
	 * 查询已决策项目类型
	 * @return
	 */
	public List<Map<String, Object>> queryNoticedType();
	/**
	 * 查询已决策项目统计
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> queryNoticedCount(Map<String, Object> map);
}
