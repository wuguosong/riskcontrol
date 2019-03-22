package com.yk.log.service.impl;

import com.yk.log.dao.ISysLogMapper;
import com.yk.log.entity.SysLogDto;
import com.yk.log.service.ISysLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by LiPan on 2019/3/22.
 */
@Service
@Transactional
public class SysLogService implements ISysLogService{
    @Resource
    private ISysLogMapper sysLogMapper;
    @Override
    public void save(SysLogDto sysLogDto) {
        sysLogMapper.insert(sysLogDto);
    }
}
