package com.yk.rcm.file.dao;

import com.goukuai.dto.LogDto;
import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface ILogMapper extends BaseMapper {
	/**
	 * @description 保存日志信息
	 * @param logDto 日志信息
	 */
	void saveLog(@Param("logDto")LogDto logDto);
}
