package com.yk.rcm.file.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LogDto;
import com.goukuai.kit.FileKit;
import com.goukuai.kit.PathKit;
import com.yk.rcm.file.constant.FileOpt;
import com.yk.rcm.file.dao.ILogMapper;
import common.Constants;
import common.Result;
import fnd.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.common.FileController;
import com.yk.rcm.file.service.IFileService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import util.Util;

import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/cloud")
public class YunkuController {
    @Resource
    private IFileService fileService;

    @RequestMapping("/upload")
    @ResponseBody
    public Result upload(HttpServletRequest request, String docType, String docCode) {
        Result result = new Result();
        // TODO 测试阶段,假设docType为cloud,docCode为1008611
        docType = "cloud";
        docCode = "CL008611";
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
                        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
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
                        FileDto fileDto = fileService.fileUpload(fullPath.replace(YunkuConf.UPLOAD_ROOT, "") + originalFilename, fileNow.getAbsolutePath(), docType, docCode, "我就是喜欢吃辣椒", 10086);
                        if (fileNow.exists() && file.canRead()) {
                            FileKit.delete(fileNow);
                        }
                        result.setSuccess(true);
                        result.setResult_code(Constants.S);
                        result.setResult_data(fileDto);
                        result.setResult_name("上传文件成功!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("上传文件失败!" + e.getMessage());
        }
        return result;
    }
}
