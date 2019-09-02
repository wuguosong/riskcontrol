package com.yk.rcm.file.controller;


import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.kit.PathKit;
import com.yk.log.utils.IPUtils;
import com.yk.rcm.file.constant.FileOpt;
import com.yk.rcm.file.service.IFileService;
import common.Constants;
import common.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import util.UserUtil;
import ws.msg.client.MessageBack;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/cloud")
public class YunkuController {
    private Logger logger = LoggerFactory.getLogger(YunkuController.class);
    @Resource
    private IFileService fileService;

    /**
     * @param request      HttpServletRequest
     * @param docType      业务类型
     * @param docCode      业务编码
     * @param pageLocation 文件在页面的位置
     * @return Result
     * @description 文件上传
     */
    @RequestMapping("/upload")
    @ResponseBody
    public Result upload(HttpServletRequest request, String docType, String docCode, String pageLocation) {
        Result result = new Result();
        String optName = UserUtil.getCurrentUserName();
        Integer optId = new Integer(UserUtil.getCurrentUserId());
        try {
            // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                    request.getSession().getServletContext());
            // 检查form中是否有enctype="multipart/form-data"
            if (multipartResolver.isMultipart(request)) {
                // 将request变成多部分request
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                // 获取multiRequest 中所有的文件名
                Iterator<?> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    // 一次遍历所有文件
                    MultipartFile multipartFile = multiRequest.getFile(iter.next().toString());
                    if (multipartFile != null) {
                        String originalFilename = multipartFile.getOriginalFilename();
                        String extName = "";
                        if(originalFilename.contains(".")){
                            extName  = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String rootDisk = PathKit.getWebRootDisk();
                        System.out.println(rootDisk);
                        String fullPath = YunkuConf.UPLOAD_ROOT + "/" + docType + "/" + docCode + "/";
                        String path = rootDisk + "/" + fullPath;
                        String nowFileName = System.currentTimeMillis() + extName;
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        // 上传
                        File fileNow = new File(path + nowFileName);
                        multipartFile.transferTo(fileNow);
                        FileDto fileDto = fileService.fileUpload(fullPath.replaceFirst(YunkuConf.UPLOAD_ROOT, "") + originalFilename, fileNow.getAbsolutePath(), docType, docCode, pageLocation, optName, optId);
                        List<FileDto> newFiles = fileService.createFileList(docType, docCode, pageLocation);
                        result.setSuccess(true);
                        result.setResult_code(Constants.S);
                        if(CollectionUtils.isEmpty(newFiles)){
                            result.setResult_data(fileDto);
                        }else{
                            result.setResult_data(newFiles.get(0));
                        }
                        result.setResult_name("上传文件成功!");
                    }
                }
            }
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("上传文件失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param docType      业务类型
     * @param docCode      业务编码
     * @param pageLocation 页面位置[应用场景:当一个同一个业务(业务类型+业务编码)出现多个上传组件时,根据组件的位置,将不同位置的附件加载到对应的组件上]
     * @return Result
     * @description 获取文件列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result list(String docType, String docCode, String pageLocation) {
        Result result = new Result();
        try {
            List<FileDto> list = fileService.createFileList(docType, docCode, pageLocation);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(list);
            result.setResult_name("获取文件成功!");
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("获取文件失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * @param fileId 文件id
     * @return Result
     * @description 获取文件列表
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Result delete(String fileId) {
        Result result = new Result();
        String optName = UserUtil.getCurrentUserName();
        try {
            FileDto fileDto = fileService.getFile(fileId);
            if (fileDto != null) {
                fileDto = fileService.fileDelete(fileDto, optName);
            }
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(fileDto);
            result.setResult_name("删除文件成功!");
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("删除文件失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * @param request      HttpServletRequest
     * @param docType      业务类型
     * @param docCode      业务编码
     * @param pageLocation 文件在页面的位置
     * @param oldFileId    原文件ID-用于删除原文件
     * @param reason       替换原因-用于记录日志
     * @return Result
     * @description 文件替换
     */
    @RequestMapping("/replace")
    @ResponseBody
    public Result replace(HttpServletRequest request, String docType, String docCode, String pageLocation, String oldFileId, String reason, String oldFileName) {
        Result result = new Result();
        String optName = UserUtil.getCurrentUserName();
        try {
            FileDto fileDto = fileService.getFile(oldFileId);
            if (fileDto != null) {
            	fileDto.setLogicopt(FileOpt.REPLACE);
                fileService.updateFile(fileDto);
            }
            Integer optId = new Integer(UserUtil.getCurrentUserId());
            try {
                // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
                CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                        request.getSession().getServletContext());
                // 检查form中是否有enctype="multipart/form-data"
                if (multipartResolver.isMultipart(request)) {
                    // 将request变成多部分request
                    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                    // 获取multiRequest 中所有的文件名
                    Iterator<?> iter = multiRequest.getFileNames();
                    while (iter.hasNext()) {
                        // 一次遍历所有文件
                        MultipartFile multipartFile = multiRequest.getFile(iter.next().toString());
                        if (multipartFile != null) {
                            String originalFilename = multipartFile.getOriginalFilename();
                            String extName = "";
                            if(originalFilename.contains(".")){
                                extName  = originalFilename.substring(originalFilename.lastIndexOf("."));
                            }
                            String rootDisk = PathKit.getWebRootDisk();
                            System.out.println(rootDisk);
                            String fullPath = YunkuConf.UPLOAD_ROOT + "/" + docType + "/" + docCode + "/";
                            String path = rootDisk + "/" + fullPath;
                            String nowFileName = System.currentTimeMillis() + extName;
                            File file = new File(path);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            // 上传
                            File fileNow = new File(path + nowFileName);
                            multipartFile.transferTo(fileNow);
                            FileDto newFile = fileService.fileUpload(fullPath.replaceFirst(YunkuConf.UPLOAD_ROOT, "") + originalFilename, fileNow.getAbsolutePath(), docType, docCode, pageLocation, optName, optId);
                            List<FileDto> newFiles = fileService.createFileList(docType, docCode, pageLocation);
                            result.setSuccess(true);
                            result.setResult_code(Constants.S);
                            if(CollectionUtils.isEmpty(newFiles)){
                                result.setResult_data(newFile);
                            }else{
                                result.setResult_data(newFiles.get(0));
                            }
                            result.setResult_name("替换文件成功!");
                        }
                    }
                }
            } catch (Exception e) {
                result.setResult_code(Constants.R);
                result.setSuccess(false);
                result.setResult_data(e);
                result.setResult_name("替换文件失败!" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("替换文件失败!" + e.getMessage());
        }
        
        // 保存系统日志
        fileService.saveSysLog(docCode, reason, result.getResult_code(), IPUtils.getIpAddr(request), oldFileId);
        
        return result;
    }

    /**
     * 上传替换提醒功能
     * @param message
     * @param shareUsers
     * @param type
     * @return
     */
	@RequestMapping(value = "remind", method = RequestMethod.POST)
	@ResponseBody
	public Result share(String message, String shareUsers, String type) {
		Result result = new Result();
		try {
			MessageBack messageBack = fileService.remindPerson(message, shareUsers, type);
			if(messageBack != null){
				result.setSuccess(true);
				result.setResult_data(messageBack);
				result.setResult_code(Constants.S);
				result.setResult_name(messageBack.getMessage());
			}else{
				result.setSuccess(false);
				result.setResult_code(Constants.R);
				result.setResult_name(messageBack.getMessage());
			}
			result.setResult_data(messageBack);
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			result.setResult_name("信息推送失败!" + e.getMessage());
			logger.error("信息推送失败!" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        FileDto fileDto = fileService.getFile("10014");
        fileDto.setPrevieqr3d("setPrevieqr3d");
        fileDto.setDownloadqr3d("setDownloadqr3d");
        fileDto.setDownloadqr3d("setDownloadqr3d");
        fileDto.setDownload3d("setDownloadqr3d");
        fileDto.setPagelocation("file");
        fileService.updateFile(fileDto);
        return "This is test";
    }

    /**
     * 获取连接:下载和预览
     * @param type
     * @param path
     * @return
     */
    @RequestMapping("/getUrl")
    @ResponseBody
    public String getUrl(String type, String path) {
	    String url = null;
	    try{
            String fullPath = path.replaceFirst(YunkuConf.UPLOAD_ROOT, "");
            if("preview".equals(type)){// 预览
                LinkDto preview = fileService.filePreviewLink(fullPath);
                if(preview != null){
                    url = preview.getLink();
                }
            }
            if("download".equals(type)){// 下载
                LinkDto download = fileService.filePreviewLink(fullPath);
                if(download != null){
                    url = download.getLink();
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return url;
    }
}
