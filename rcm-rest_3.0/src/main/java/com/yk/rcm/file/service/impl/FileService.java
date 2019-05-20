package com.yk.rcm.file.service.impl;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.dto.LogDto;
import com.goukuai.srv.YunkuFileSrv;
import com.yk.exception.BusinessException;
import com.yk.log.constant.LogConstant;
import com.yk.log.entity.SysLogDto;
import com.yk.log.service.ISysLogService;
import com.yk.power.dao.IUserMapper;
import com.yk.rcm.file.constant.FileOpt;
import com.yk.rcm.file.dao.IFileMapper;
import com.yk.rcm.file.dao.ILogMapper;
import com.yk.rcm.file.service.IFileService;
import fnd.UserDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.DateUtil;
import util.UserUtil;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiPan[wjsxhclj@sina.com]
 * @description 文件操作业务类
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
    @Resource
    private ISysLogService sysLogService;
    @Resource
    IUserMapper userMapper;


    @Override
    public FileDto fileUpload(String fullPath, String localFile, String docType, String docCode, String pageLocation, String optName,
                              Integer optId) throws Exception {
        FileDto fileDto = yunkuFileSrv.uploadByBlock(fullPath, localFile, optName, optId);
        if (fileDto != null) {
            // 保存FileDto
            fileDto.setDoctype(docType);
            fileDto.setDoccode(docCode);
            fileDto.setPagelocation(pageLocation);
            fileDto.setCreate_member_name(optName);
            fileDto.setLast_member_name(optName);
            fileDto.setLogicopt(FileOpt.UPLOAD);
            this.saveFile(fileDto);
            // 保存LogDto
            logMapper.saveLog(this.initLog(fileDto, optName, docType, docCode, FileOpt.UPLOAD));
        }
        return fileDto;
    }

    @Override
    public FileDto fileDelete(String fullPath, String optName) throws Exception {
        return yunkuFileSrv.deleteFile(fullPath, optName);
    }

    @Override
    public FileDto fileDelete(FileDto fileDto, String optName) throws Exception {
        this.fileDelete(fileDto.getFullpath().replaceFirst(YunkuConf.UPLOAD_ROOT, ""), optName);
        if (fileDto != null) {
            // 执行逻辑删除
            fileDto.setLogicopt(FileOpt.DELETE);
            this.updateFile(fileDto);
            // 保存LogDto
            logMapper.saveLog(this.initLog(fileDto, optName, fileDto.getDoctype(), fileDto.getDoccode(), FileOpt.DELETE));
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
    public FileDto saveFile(FileDto fileDto) {
        if (fileDto == null) {
            throw new BusinessException("FileDto can't be null!");
        }
        Long time = new Date().getTime();
        fileDto.setCreate_dateline(time);
        fileDto.setLast_dateline(time);
        fileMapper.saveFile(fileDto);
        return fileDto;
    }

    @Override
    public FileDto deleteFile(FileDto fileDto) {
        if (fileDto == null) {
            throw new BusinessException("FileDto can't be null!");
        }
        fileMapper.deleteFile(fileDto);
        return fileDto;
    }

    @Override
    public void updateFile(FileDto fileDto) {
        if (fileDto == null) {
            throw new BusinessException("FileDto can't be null!");
        }
        fileMapper.updateFile(fileDto);
    }

    @Override
    public FileDto getFile(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            throw new BusinessException("fileId can't be null!");
        }
        return fileMapper.getFile(fileId);
    }

    @Override
    public List<FileDto> listFile(String docType, String docCode, String pageLocation) {
        return fileMapper.listFile(docType, docCode, pageLocation);
    }

    /**
     * @param fileDto FileDto
     * @param optName 操作人
     * @param docType 单据类型
     * @param docCode 单据编码
     * @param opt     操作
     * @return LogDto
     * @description 初始化LogDto信息
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

    public void saveSysLog(String businessId, String reason, String success, String ip, String oldFileId) {
        SysLogDto sysLogDto = new SysLogDto();
        sysLogDto.setUser(UserUtil.getCurrentUserUuid());
        sysLogDto.setUserName(UserUtil.getCurrentUser().getName());
        sysLogDto.setModule(LogConstant.MODULE_ATTACHMENT);
        sysLogDto.setOperation(LogConstant.REPLACE);
        sysLogDto.setCode(businessId);
        sysLogDto.setDescription(reason);
        sysLogDto.setParams(oldFileId);
        sysLogDto.setCreateDate(DateUtil.getCurrentDate());
        sysLogDto.setMethod("com.yk.rcm.file.controller.YunkuController.replace()");
        if (success.equals("S")) {
            sysLogDto.setSuccess("Y");
        } else {
            sysLogDto.setSuccess("N");
        }

        sysLogDto.setIp(ip);
        //保存系统日志
        sysLogService.save(sysLogDto);
    }

    /**
     * 替换文件时发钉钉消息给指定用户
     *
     * @param message
     * @param shareUsers
     * @param type
     * @return String url
     */
    @Override
    public MessageBack remindPerson(String message, String shareUsers, String type) {
        if (StringUtils.isBlank(type)) {
            type = MessageClient._DT;
        }
        if (message == null) {
            throw new BusinessException("消息推送失败，消息内容为空！");
        }
        if (StringUtils.isBlank(shareUsers)) {
            throw new BusinessException("消息推送失败，为空！");
        }
        String accounts = new String();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("UUID", shareUsers);
        Map<String, Object> user = userMapper.selectAUser(params);
        if (user != null) {
            accounts = String.valueOf(user.get("ACCOUNT"));
        }
        MessageClient client = new MessageClient();
        MessageBack messageBack = null;
        UserDto userDto = UserUtil.getCurrentUser();
        // 钉钉
        if (MessageClient._DT.equalsIgnoreCase(type)) {
            messageBack = client.sendDtText(null, accounts, message);
        }

        return messageBack;
    }

    @Override
    public List<Map<String, Object>> getAttachHistoryList(String id, String businessType, String pageLocation) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("module", LogConstant.MODULE_ATTACHMENT);
        params.put("operation", LogConstant.REPLACE);
        params.put("businessType", businessType);
        params.put("pageLocation", pageLocation);
        List<Map<String, Object>> list = sysLogService.getReplaceFile(params);

        for (Map<String, Object> fileInfo : list) {
            String fullPath = fileInfo.get("FULLPATH").toString().replaceFirst(YunkuConf.UPLOAD_ROOT, "");
            LinkDto download = this.fileDownloadLink(fullPath);
            if (download != null) {
                fileInfo.put("DOWNLOAD3D", download.getLink());
                fileInfo.put("DOWNLOADQR3D", download.getQr_url());
            }
            LinkDto preview = this.filePreviewLink(fullPath);
            if (preview != null) {
                fileInfo.put("DOWNLOAD3D", download.getLink());
                fileInfo.put("DOWNLOADQR3D", download.getQr_url());
            }
        }

        return list;
    }

    @Override
    public List<FileDto> createFileList(String docType, String docCode, String pageLocation) throws Exception {
        List<FileDto> list = this.listFile(docType, docCode, pageLocation);
        for (FileDto fileDto : list) {
            String fullPath = fileDto.getFullpath().replaceFirst(YunkuConf.UPLOAD_ROOT, "");
            LinkDto download = this.fileDownloadLink(fullPath);
            if (download != null) {
                fileDto.setDownload3d(download.getLink());
                fileDto.setDownloadqr3d(download.getQr_url());
            }
            LinkDto preview = this.filePreviewLink(fullPath);
            if (preview != null) {
                fileDto.setPreview3d(preview.getLink());
                fileDto.setPrevieqr3d(preview.getQr_url());
            }
        }
        return list;
    }
}
