package com.yk.sign.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

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
}
