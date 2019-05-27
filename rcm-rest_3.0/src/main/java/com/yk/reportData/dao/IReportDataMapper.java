package com.yk.reportData.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * @author Sunny Qi
 *
 */
@Repository
public interface IReportDataMapper extends BaseMapper {
	
	/**
	 * 新增正式评审报告数据
	 * */
	public void insertPfr(Map<String, Object> dataForOracle);
	
	/**
	 * 修改正式评审报告数据
	 * */
	public void updatePfr(Map<String, Object> dataForOracle);
	
	/**
	 * 新增投标评审报告数据
	 * */
	public void insertPre(Map<String, Object> dataForOracle);
	
	/**
	 * 修改投标评审报告数据
	 * */
	public void updatePre(Map<String, Object> dataForOracle);
	
	/**
	 * 新增其他评审报告数据
	 * */
	public void insertBulletin(Map<String, Object> dataForOracle);
	
	/**
	 * 修改其他评审报告数据
	 * */
	public void updateBulletin(Map<String, Object> dataForOracle);
	
	/**
	 * 根据业务ID获取正式评审报表数据
	 * */
	public Map<String, Object> getPfrProjectByBusinessID(Map<String, Object> params);
	
	/**
	 * 根据业务ID获取投标评审报表数据
	 * */
	public Map<String, Object> getPreProjectByBusinessID(Map<String, Object> params);
	
	/**
	 * 根据业务ID获取其他评审报表数据
	 * */
	public Map<String, Object> getBulletinProjectByBusinessID(Map<String, Object> params);
	
	/**
	 * 根据条件获取正式评审项目列表
	 * */
	public List<Map<String, Object>> getPfrProjectList(Map<String, Object> params);
	
	/**
	 * 根据条件获取投标评审项目列表
	 * */
	public List<Map<String, Object>> getPreProjectList(Map<String, Object> params);
	
	/**
	 * 根据条件获取正式评审项目列表
	 * */
	public List<Map<String, Object>> getBulletinProjectList(Map<String, Object> params);
}
