package com.yk.log.service;


import java.util.List;
import java.util.Map;

import com.yk.log.entity.SysLogDto;

/**
 * 系统日志Service接口
 */
public interface ISysLogService{
    void save(SysLogDto sysLogDto);
    
    /**
     * 查询文件替换日志
     * */
    List<Map<String, Object>> getReplaceFile(Map<String, Object> params);
}
