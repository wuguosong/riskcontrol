package com.yk.initfile.dao;


import com.alibaba.fastjson.JSONObject;
import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Mapper
 */
@Repository
public interface IInitFileMapper extends BaseMapper{
    List<JSONObject> queryFileListByPage(Map<String, Object> queryParams);
}
