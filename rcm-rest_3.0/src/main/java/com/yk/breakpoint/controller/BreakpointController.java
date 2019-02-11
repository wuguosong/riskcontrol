package com.yk.breakpoint.controller;

import com.alibaba.fastjson.JSON;
import com.goukuai.kit.PathKit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import util.DateUtil;
import util.RedisUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by LiPan on 2019/1/30.
 */
@Controller
public class BreakpointController {
    private final Logger logger = LoggerFactory.getLogger(BreakpointController.class);

    /**
     * 获取进度条信息
     *
     * @param fileName
     * @return String
     */
    @RequestMapping(value = "v1/breakpoint/progress", method = RequestMethod.POST)
    @ResponseBody
    public String progress(String fileName) {
        logger.info("v1/breakpoint/progress<<");
        String progressLine =  RedisUtil.get("progressLine_" + fileName);
        if (StringUtils.isBlank(progressLine)) {
            progressLine = "0";
        }
        logger.info("fileName:" + fileName + " progress:" + progressLine);
        logger.info("v1/breakpoint/progress>>");
        return progressLine;
    }

    /**
     * 检测分片信息
     *
     * @param fileName     文件名称
     * @param progressLine 上传进度条
     * @param fileMd5      文件MD5
     * @param chunk        分片
     * @param chunkSize    分片大小
     * @return Map<String, Object>
     */
    @RequestMapping(value = "v1/breakpoint/check", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> check(String fileName, String progressLine, String fileMd5, String chunk, String chunkSize) {
        logger.info("v1/breakpoint/check<<");
        String savePath = PathKit.getWebRootDisk() + "/upload";
        Map<String, Object> json = new HashMap<String, Object>();
        String lastUploadTime = DateUtil.getDateToString(new Date(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
        if (StringUtils.isBlank( RedisUtil.get("fileName_" + fileName))) {
            // 存储文件唯一MD5标识,格式{"fileName_<fileName>":<fileMd5>}
            RedisUtil.set("fileName_" + fileName, fileMd5);
        }
        RedisUtil.set("lastUploadTime_" + fileName, lastUploadTime);
        RedisUtil.set("progressLine_" + fileName, progressLine);
        // 获取分区文件
        File checkFile = new File(savePath + "/" + RedisUtil.get("fileName_" + fileName) + "/" + chunk);
        // 判断该分区文件是否存在且完整
        if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            json.put("exist", true);
        } else {
            json.put("exist", false);
        }
        logger.info(progressLine + "% 校验结果:" + JSON.toJSONString(json));
        logger.info("v1/breakpoint/check>>");
        return json;
    }

    /**
     * 上传成功后合并分区文件
     *
     * @param fileName 文件名称
     * @param fileMd5  文件唯一标识
     * @return Map<String, Object>
     */
    @RequestMapping(value = "v1/breakpoint/merge", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> merge(String fileName, String fileMd5) {
        Map<String, Object> json = new HashMap<String, Object>();
        logger.info("v1/breakpoint/merge<<进入");
        logger.info("参数信息:" + "{fileName:" + fileName + ",fileMd5:" + fileMd5 + "}");
        String basePath = PathKit.getWebRootDisk() + "/upload";
        // 合并文件
        try {
            // 读取目录里的所有文件
            File f = new File(basePath + "/" + RedisUtil.get("fileName_" + fileName));
            // 获取分片数组
            File[] fileArray = f.listFiles();
            // 转成集合，便于排序
            List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
            Collections.sort(fileList, new Comparator<File>() {

                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                        return -1;
                    }
                    return 1;
                }
            });
            // 截取文件名的后缀名
            int pointIndex = fileName.lastIndexOf(".");
            // 后缀名
            String suffix = fileName.substring(pointIndex);
            // 合并后的文件
            File outputFile = new File(basePath + "/" + fileName);
            // 创建文件
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
            FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
            // 合并
            for (File file : fileList) {
                FileChannel inChannel = new FileInputStream(file).getChannel();
                try {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                } finally {
                    inChannel.close();
                }
                // 删除分片
                file.delete();
            }
            try {
                outChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
            // 清除文件夹
            File tempFile = new File(basePath + "/" + RedisUtil.get("fileName_" + fileName));
            if (tempFile.exists() && tempFile.isDirectory()) {
                tempFile.delete();
            }
            // 将文件的最后上传时间和生成的文件名返回
            json.put("lastUploadTime", RedisUtil.get("lastUploadTime_" + fileName));
            json.put("pathFileName", RedisUtil.get("fileName_" + fileName) + suffix);
            // 清除文件相关缓存信息
            RedisUtil.del("progressLine_" + fileName);
            RedisUtil.del("lastUploadTime_" + fileName);
            RedisUtil.del("fileName_" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.info("v1/breakpoint/merge>>退出");
        return json;
    }

    /**
     * 保存上传的文件
     *
     * @param request          HttpServletRequest
     *                         <p>注:以下参数为表单自动携带</p>
     * @param id               文件ID
     * @param fileMd5          文件唯一标识
     * @param chunk            文件分片
     * @param name             文件名称
     * @param type             类型
     * @param lastModifiedDate 最后修改时间
     * @param size             文件大小
     */
    @RequestMapping(value = "v1/breakpoint/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(HttpServletRequest request, String id, String fileMd5, String chunk, String name, String type, String lastModifiedDate, String size) {
        logger.info("v1/breakpoint/save<<进入");
        logger.info("************FormData*************");
        logger.info("id:" + id);
        logger.info("fileMd5:" + fileMd5);
        logger.info("chunk:" + chunk);
        logger.info("name:" + name);
        logger.info("type:" + type);
        logger.info("lastModifiedDate:" + lastModifiedDate);
        logger.info("size:" + size);
        logger.info("************FormData*************");
        try {
            // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            // 检查form中是否有enctype="multipart/form-data"
            if (multipartResolver.isMultipart(request)) {
                // 将request变成多部分request
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                // 获取multiRequest 中所有的文件名
                Iterator<?> iterator = multiRequest.getFileNames();
                while (iterator.hasNext()) {
                    // 一次遍历所有文件
                    MultipartFile multipartFile = multiRequest.getFile(iterator.next().toString());
                    if (multipartFile != null) {
                        String fileName = multipartFile.getOriginalFilename();
                        String rootDisk = PathKit.getWebRootDisk();
                        String tempPath = "upload" + "/" + RedisUtil.get("fileName_" + name);
                        String tempDiskPath = rootDisk + "/" + tempPath + "/";
                        File file = new File(tempDiskPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        // 如果chunk为null,说明文件没有分区
                        if (StringUtils.isBlank(chunk)) {
                            chunk = fileName;
                        }
                        // 上传
                        File chunkFile = new File(tempDiskPath + chunk);
                        multipartFile.transferTo(chunkFile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.info("v1/breakpoint/save>>退出");
        return fileMd5;
    }
    /**
     * 技术要求:
     * redis缓存
     * io文件管道
     <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.9.0</version>
     </dependency>
     <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.4</version>
     </dependency>
     <dependency>
         <groupId>org.springframework.data</groupId>
         <artifactId>spring-data-redis</artifactId>
         <version>1.7.1.RELEASE</version>
     </dependency>
     */
}
