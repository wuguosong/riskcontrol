package com.yk.rcm.file.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.goukuai.dto.FileDto;

import java.util.List;

@Repository
public interface IFileMapper extends BaseMapper {
	/**
	 * @description 本地保存文件记录信息
	 * @param fileDto 文件
	 * @return FileDto
	 */
	void saveFile(@Param("fileDto") FileDto fileDto);

	/**
	 * @description 本地删除文件记录信息
	 * @param fileDto 文件
	 * @return FileDto
	 */
	void deleteFile(@Param("fileDto")FileDto fileDto);

	/**
	 * @description 更新本地文件记录信息(主要是关于下载/预览链接和二维码的更新)
	 * @param fileDto 文件
	 */
	void updateFile(@Param("fileDto")FileDto fileDto);

	/**
	 * @description 通过业务类型和业务编号获取文件列表
	 * @param docType 业务类型
	 * @param docCode 业务编号
	 * @return List<FileDto>
	 */
	List<FileDto> listFile(@Param("docType")String docType, @Param("docCode")String docCode, @Param("pageLocation")String pageLocation);

	/**
	 * @description 通过业务类型和业务编号获取文件列表
	 * @param fileId 文件ID
	 * @return List<FileDto>
	 */
	FileDto getFile(@Param("fileId")String fileId);
}
