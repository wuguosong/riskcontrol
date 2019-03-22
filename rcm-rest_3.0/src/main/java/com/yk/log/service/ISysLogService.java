package com.yk.log.service;


import com.yk.log.entity.SysLogDto;

/**
 * 系统日志Service接口
 */
public interface ISysLogService{
    void save(SysLogDto sysLogDto);
}
