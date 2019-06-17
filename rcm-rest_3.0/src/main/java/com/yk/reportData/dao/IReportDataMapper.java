package com.yk.reportData.dao;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author Sunny Qi
 */
@Repository
public interface IReportDataMapper extends BaseMapper {

    /**
     * 新增正式评审报告数据
     */
    public void insertPfr(Map<String, Object> dataForOracle);

    /**
     * 修改正式评审报告数据
     */
    public void updatePfr(Map<String, Object> dataForOracle);

    /**
     * 新增投标评审报告数据
     */
    public void insertPre(Map<String, Object> dataForOracle);

    /**
     * 修改投标评审报告数据
     */
    public void updatePre(Map<String, Object> dataForOracle);

    /**
     * 新增其他评审报告数据
     */
    public void insertBulletin(Map<String, Object> dataForOracle);

    /**
     * 修改其他评审报告数据
     */
    public void updateBulletin(Map<String, Object> dataForOracle);

    /**
     * 根据业务ID获取正式评审报表数据
     */
    public Map<String, Object> getPfrProjectByBusinessID(Map<String, Object> params);

    /**
     * 根据业务ID获取投标评审报表数据
     */
    public Map<String, Object> getPreProjectByBusinessID(Map<String, Object> params);

    /**
     * 根据业务ID获取其他评审报表数据
     */
    public Map<String, Object> getBulletinProjectByBusinessID(Map<String, Object> params);

    /**
     * 根据条件获取正式评审项目列表
     */
    public List<Map<String, Object>> getPfrProjectList(Map<String, Object> params);

    /**
     * 根据条件获取投标评审项目列表
     */
    public List<Map<String, Object>> getPreProjectList(Map<String, Object> params);

    /**
     * 根据条件获取正式评审项目列表
     */
    public List<Map<String, Object>> getBulletinProjectList(Map<String, Object> params);

    /**
     * 查询项目列表
     *
     * @param params 查询参数
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getProjectList(Map<String, Object> params);

    /**
     * 查询项目数量
     *
     * @param params 查询参数
     * @return List<Map<String, Object>>
     */
    int getProjectListCount(Map<String, Object> params);

    /**
     * 获取领导和秘书的映射关系
     * @return
     */
    List<Map<String, Object>> getLeaderSecretaryMappingList();
}
