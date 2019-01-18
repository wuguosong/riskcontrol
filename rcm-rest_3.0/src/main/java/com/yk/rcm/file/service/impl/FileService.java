package com.yk.rcm.file.service.impl;

import java.util.Date;
import java.util.List;

import com.yk.rcm.file.dao.ILogMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.dto.LogDto;
import com.goukuai.srv.YunkuFileSrv;
import com.yk.exception.BusinessException;
import com.yk.rcm.file.constant.FileOpt;
import com.yk.rcm.file.dao.IFileMapper;
import com.yk.rcm.file.service.IFileService;

import javax.annotation.Resource;

/**
 * @description 文件操作业务类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/17
 */
@Service
@Transactional
public class FileService implements IFileService {
	private YunkuFileSrv yunkuFileSrv = YunkuFileSrv.getInstance();

	@Resource
	private ILogMapper logMapper;
	@Resource
	private IFileMapper fileMapper;

	@Override
	public FileDto fileUpload(String fullPath, String localFile, String docType, String docCode, String optName,
			Integer optId) throws Exception {
		FileDto fileDto = yunkuFileSrv.uploadByBlock(fullPath, localFile, optName, optId);
		if (fileDto != null) {
			// 保存FileDto
			this.saveFile(fileDto, optName, docType, docCode);
			// 保存LogDto
			logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.UPLOAD));
		}
		return fileDto;
	}

	@Override
	public FileDto fileDelete(String fullPath, String docType, String docCode, String optName) throws Exception {
		FileDto fileDto = yunkuFileSrv.deleteFile(fullPath, optName);
		if (fileDto != null) {
			// 删除FileDto
			this.deleteFile(fileDto, optName, docType, docCode);
			// 保存LogDto
			logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.DELETE));
		}
		return fileDto;
	}

	@Override
	public List<FileDto> fileList(String fullPath) throws Exception {
		return yunkuFileSrv.getFileList(fullPath);
	}

	@Override
	public FileDto fileInfo(String fullPath, String docType, String docCode, String optName) throws Exception {
		FileDto fileDto = yunkuFileSrv.getFileInfo(fullPath);
		if (fileDto != null) {
			if (!StringUtils.isAnyBlank(docType, docCode, optName)) {
				logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.SEARCH));
			}
		}
		return fileDto;
	}

	@Override
	public FileDto fileInfo(String fullPath) throws Exception {
		return this.fileInfo(fullPath, null, null, null);
	}

	@Override
	public LinkDto filePreviewLink(String fullPath, String docType, String docCode, String optName) throws Exception {
		LinkDto linkDto = yunkuFileSrv.getPreviewWindow(fullPath);
		if (linkDto != null) {
			if (!StringUtils.isAnyBlank(docType, docCode, optName)) {
				FileDto fileDto = this.fileInfo(fullPath);
				if (fileDto != null) {
					logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.PREVIEW));
				}
			}
		}
		return linkDto;
	}

	@Override
	public LinkDto filePreviewLink(String fullPath) throws Exception {
		return this.fileDownloadLink(fullPath, null, null, null);
	}

	@Override
	public LinkDto fileDownloadLink(String fullPath, String docType, String docCode, String optName) throws Exception {
		LinkDto linkDto = yunkuFileSrv.getDownloadWindow(fullPath);
		if (linkDto != null) {
			if (!StringUtils.isAnyBlank(docType, docCode, optName)) {
				FileDto fileDto = this.fileInfo(fullPath);
				if (fileDto != null) {
					logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.DOWNLOAD));
				}
			}
		}
		return linkDto;
	}

	@Override
	public LinkDto fileDownloadLink(String fullPath) throws Exception {
		return this.fileDownloadLink(fullPath, null, null, null);
	}

	@Override
	public String filePreviewUrl(String fullPath, String memberName, String docType, String docCode, String optName)
			throws Exception {
		String url = yunkuFileSrv.getPreviewUrl(fullPath, memberName);
		if (StringUtils.isNotBlank(url)) {
			if (!StringUtils.isAnyBlank(docType, docCode, optName)) {
				FileDto fileDto = this.fileInfo(fullPath);
				if (fileDto != null) {
					logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.PREVIEW));
				}
			}
		}
		return url;
	}

	@Override
	public String filePreviewUrl(String fullPath, String memberName) throws Exception {
		return this.filePreviewUrl(fullPath, memberName, null, null, null);
	}

	@Override
	public List<String> fileDownloadUrlByFullPath(String fullPath, String docType, String docCode, String optName)
			throws Exception {
		List<String> urls = yunkuFileSrv.getDownloadUrlByFullPath(fullPath);
		if (CollectionUtils.isNotEmpty(urls)) {
			if (!StringUtils.isAnyBlank(docType, docCode, optName)) {
				FileDto fileDto = this.fileInfo(fullPath);
				if (fileDto != null) {
					logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.DOWNLOAD));
				}
			}
		}
		return urls;
	}

	@Override
	public List<String> fileDownloadUrlByFullPath(String fullPath) throws Exception {
		return this.fileDownloadUrlByFullPath(fullPath, null, null, null);
	}

	@Override
	public List<String> fileDownloadUrlByHash(String hash)
			throws Exception {
		return yunkuFileSrv.getDownloadUrlByHash(hash);
	}


	@Override
	public List<String> fileUploadServer() throws Exception {
		return yunkuFileSrv.getUploadServer();
	}

	@Override
	public FileDto saveFile(FileDto fileDto, String optName, String docType, String docCode) {
		if (fileDto == null) {
			throw new BusinessException("FileDto can't be null!");
		}
		fileDto.setDoctype(docType);
		fileDto.setDoccode(docCode);
		fileDto.setCreate_member_name(optName);
		fileDto.setLast_member_name(optName);
		Long time = new Date().getTime();
		fileDto.setCreate_dateline(time);
		fileDto.setLast_dateline(time);
		fileMapper.saveFile(fileDto, optName, docType, docCode);
		return fileDto;
	}

	@Override
	public FileDto deleteFile(FileDto fileDto, String optName, String docType, String docCode) {
		if (fileDto == null) {
			throw new BusinessException("FileDto can't be null!");
		}
		fileMapper.deleteFile(fileDto, optName, docType, docCode);
		return fileDto;
	}

	/**
	 * @description 初始化LogDto信息
	 * @param fileDto FileDto
	 * @param optName 操作人
	 * @param docType 单据类型
	 * @param docCode 单据编码
	 * @param opt     操作
	 * @return LogDto
	 */
	private LogDto initLog(FileDto fileDto, String optName, String docType, String docCode, String opt) {
		LogDto logDto = new LogDto();
		Long time = new Date().getTime();
		logDto.setDoctype(docType);
		logDto.setDoccode(docCode);
		logDto.setCreatedby(optName);
		logDto.setUpdatedby(optName);
		logDto.setCreateddate(time);
		logDto.setUpdateddate(time);
		logDto.setFilehash(fileDto.getFilehash());
		logDto.setFilename(fileDto.getFilename());
		logDto.setFileopt(opt);
		logDto.setFullpath(fileDto.getFullpath());
		logDto.setHash(fileDto.getHash());
		return logDto;
	}
}
