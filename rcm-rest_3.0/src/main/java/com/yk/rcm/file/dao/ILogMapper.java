package com.yk.rcm.file.dao;

import com.goukuai.dto.LogDto;
import org.springframework.stereotype.Repository;

@Repository
public interface ILogMapper {
	/**
	 * @description 保存日志信息
	 * @param logDto 日志信息
	 */
	void saveLog(LogDto logDto);
}
