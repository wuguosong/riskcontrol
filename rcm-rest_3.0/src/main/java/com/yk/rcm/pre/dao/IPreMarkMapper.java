package com.yk.rcm.pre.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface IPreMarkMapper extends BaseMapper {
    /**
     * 查询评价
     *
     * @param businessId
     * @return
     */
    Map<String, Object> queryMarks(@Param("businessId") String businessId);

    /**
     * 新增记录
     *
     * @param data
     */
    void save(Map<String, Object> data);

    /**
     * 修改记录
     *
     * @param data
     */
    void update(Map<String, Object> data);
}
