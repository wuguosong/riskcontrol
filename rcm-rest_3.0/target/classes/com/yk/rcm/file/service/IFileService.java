package com.yk.rcm.file.service;

import java.util.List;

import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import org.apache.ibatis.annotations.Param;

public interface IFileService {
	/**
	 * @description 向远程服务器上传文件
	 * @param fullPath  远程服务器文件全路径
	 * @param localFile 本地文件全路径
	 * @param docType   业务类型
	 * @param docCode   业务编码
	 * @param pageLocation 文件在页面的位置
	 * @param optName   操作人
	 * @param optId     操作人ID
	 * @return FileDto
	 * @throws Exception
	 */
	FileDto fileUpload(String fullPath, String localFile, String docType, String docCode, String pageLocation,String optName,
                       Integer optId) throws Exception;

	/**
	 * @description 从远程服务器删除文件
	 * @param fullPath 远程服务器文件全路径
	 * @param optName  操作人
	 * @return FileDto
	 * @throws Exception
	 */
	FileDto fileDelete(String fullPath, String optName) throws Exception;

	/**
	 * @description 从远程服务器删除文件
	 * @param fileDto 文件
	 * @param optName  操作人
	 * @return FileDto
	 * @throws Exception
	 */
	FileDto fileDelete(FileDto fileDto, String optName) throws Exception;

	/**
	 * @description 从远程服务器获取文件列表
	 * @param fullPath 远程服务器文件全路径
	 * @return List<FileDto>
	 * @throws Exception
	 */
	List<FileDto> fileList(String fullPath) throws Exception;

	/**
	 * @description 从远程服务器获取文件
	 * @param fullPath 远程服务器文件全路径
	 * @param docType  业务类型
	 * @param docCode  业务编码
	 * @param optName  操作人
	 * @return FileDto
	 * @throws Exception
	 */
	FileDto fileInfo(String fullPath, String docType, String docCode, String optName) throws Exception;

	/**
	 * @description 从远程服务器文件
	 * @param fullPath 远程服务器文件全路径
	 * @return FileDto
	 * @throws Exception
	 */
	FileDto fileInfo(String fullPath) throws Exception;

	
	/**
	 * @description 从远程服务器获取文件预览链接
	 * @param fullPath 远程服务器文件全路径
	 * @param docType  业务类型
	 * @param docCode  业务编码
	 * @param optName  操作人
	 * @return LinkDto
	 * @throws Exception
	 */
	LinkDto filePreviewLink(String fullPath, String docType, String docCode, String optName) throws Exception;

	/**
	 * @description 从远程服务器获取文件预览链接-无日志记录
	 * @param fullPath 远程服务器文件全路径
	 * @return LinkDto
	 * @throws Exception
	 */
	LinkDto filePreviewLink(String fullPath) throws Exception;

	/**
	 * @description 从远程服务器获取文件下载链接
	 * @param fullPath 远程服务器文件全路径
	 * @param docType  业务类型
	 * @param docCode  业务编码
	 * @param optName  操作人
	 * @return LinkDto
	 * @throws Exception
	 */
	LinkDto fileDownloadLink(String fullPath, String docType, String docCode, String optName) throws Exception;

	/**
	 * @description 从远程服务器获取文件下载链接-无日志记录
	 * @param fullPath 远程服务器文件全路径
	 * @return LinkDto
	 * @throws Exception
	 */
	LinkDto fileDownloadLink(String fullPath) throws Exception;

	/**
	 * @description 从远程服务器获取文件预览URL
	 * @param fullPath 远程服务器文件全路径
	 * @param docType  业务类型
	 * @param docCode  业务编码
	 * @param optName  操作人
	 * @return String
	 * @throws Exception
	 */
	String filePreviewUrl(String fullPath, String memberName, String docType, String docCode, String optName)
			throws Exception;

	/**
	 * @description 从远程服务器获取文件预览URL-无日志记录
	 * @param fullPath 远程服务器文件全路径
	 * @return String
	 * @throws Exception
	 */
	String filePreviewUrl(String fullPath, String memberName) throws Exception;

	/**
	 * @description 从远程服务器获取文件下载URL
	 * @param fullPath 远程服务器文件全路径
	 * @param docType  业务类型
	 * @param docCode  业务编码
	 * @param optName  操作人
	 * @return String
	 * @throws Exception
	 */
	List<String> fileDownloadUrlByFullPath(String fullPath, String docType, String docCode, String optName)
			throws Exception;

	/**
	 * @description 从远程服务器获取文件下载URL-无日志记录
	 * @param fullPath 远程服务器文件全路径
	 * @return List<String>
	 * @throws Exception
	 */
	List<String> fileDownloadUrlByFullPath(String fullPath) throws Exception;

	/**
	 * @description 从远程服务器获取文件下载URL-无日志记录
	 * @param hash 远程服务器唯一Hash
	 * @return List<String>
	 * @throws Exception
	 */
	List<String> fileDownloadUrlByHash(String hash) throws Exception;

	/**
	 * @description 获取远程服务器上传文件地址
	 * @return List<String>
	 * @throws Exception
	 */
	List<String> fileUploadServer() throws Exception;
	

	/**
	 * @description 本地保存文件记录信息
	 * @param fileDto 文件
	 * @return FileDto
	 */
	 FileDto saveFile(FileDto fileDto);

	/**
	 * @description 本地删除文件记录信息
	 * @param fileDto 文件
	 * @return FileDto
	 */
	 FileDto deleteFile(FileDto fileDto);

	/**
	 * @description 更新本地文件记录信息(主要是关于下载/预览链接和二维码的更新)
	 * @param fileDto 文件
	 */
	void updateFile(FileDto fileDto);

	/**
	 * @description 通过业务类型和业务编号获取文件列表
	 * @param docType 业务类型
	 * @param docCode 业务编号
	 * @return List<FileDto>
	 */
	List<FileDto> listFile(String docType, String docCode, String pageLocation);

	/**
	 * @description 通过业务类型和业务编号获取文件列表
	 * @param fileId 文件ID
	 * @return List<FileDto>
	 */
	FileDto getFile(String fileId);
}
