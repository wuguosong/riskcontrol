package com.yk.sign.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public interface ISignMapper extends BaseMapper {
    /**
     * 查询代办
     * @param params
     * @return
     */
    List<HashMap> queryAgencyList(HashMap<String, Object> params);

    /**
     * 查询已办
     * @param params
     * @return
     */
    List<HashMap> queryAlreadyList(HashMap<String, Object> params);

    /**
     * 查询代办任务详情
     * @param key
     * @param business_id
     * @return
     */
    List<HashMap> getCurrentTaskInfo(@Param("key") String key, @Param("business_id")String business_id);
    ///////////////////////////////新流程图专用////////////////////////////////
    List<Map<String, Object>>selectUniqueTasksForImageStep(@Param("key")String key, @Param("business_id")String business_id);
    ///////////////////////////////新流程图专用////////////////////////////////

    /**
     * 更新代办状态
     * @param updateParams 更新参数
     */
    void updateLogsStatus(Map<String, Object> updateParams);

    /**
     * 更新代办排序
     * @param updateParams 更新参数
     */
    void updateLogsOrder(Map<String, Object> updateParams);

    /**
     * 审批记录列表
     * @param processKey
     * @param businessId
     * @return
     */
    List<Map<String,Object>> listLogs(@Param("processKey") String processKey, @Param("businessId")String businessId);

    /**
     * 删除审批记录
     * @param updateParams
     */
    void deleteLogsNotInRunTask(Map<String, Object> updateParams);
}
