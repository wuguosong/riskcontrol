package com.yk.log.dao;

import com.yk.common.BaseMapper;
import com.yk.log.entity.SysLogDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 系统日志Dao接口
 * Created by LiPan on 2019/3/22.
 */
@Repository
public interface ISysLogMapper extends BaseMapper {
    void insert(@Param("sysLog")SysLogDto sysLogDto);
}
