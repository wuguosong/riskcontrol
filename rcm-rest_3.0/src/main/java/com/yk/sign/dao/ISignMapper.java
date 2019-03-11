package com.yk.sign.dao;

import com.yk.common.BaseMapper;
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
}
