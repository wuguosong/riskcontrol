package com.yk.rcm.file.dao;

import org.springframework.stereotype.Repository;

import com.goukuai.dto.FileDto;
@Repository
public interface IFileMapper {
	/**
	 * @description 本地保存文件记录信息
	 * @param fileDto 文件
	 * @param optName 操作人
	 * @param docType 业务类型
	 * @param docCode 业务编码
	 * @return FileDto
	 */
	FileDto saveFile(FileDto fileDto, String optName, String docType, String docCode);

	/**
	 * @description 本地删除文件记录信息
	 * @param fileDto 文件
	 * @param optName 操作人
	 * @param docType 业务类型
	 * @param docCode 业务编码
	 * @return FileDto
	 */
	FileDto deleteFile(FileDto fileDto, String optName, String docType, String docCode);

}
