package com.goukuai.srv;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.BaseData;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.helper.EntFileManagerHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gokuai.base.DebugConfig;
import com.gokuai.base.ReturnResult;
import com.yunkuent.sdk.ConfigHelper;
import com.yunkuent.sdk.EntFileManager;
import com.yunkuent.sdk.ScopeType;
import com.yunkuent.sdk.EntFileManager.AuthType;
import com.yunkuent.sdk.data.FileInfo;
import com.yunkuent.sdk.data.YunkuException;

/**
 * @author LiPan[wjsxhclj@sina.com]
 * @description 云端存储文件操作服务类
 * @date 2019/01/09
 */
public class YunkuFileSrv {
    private static volatile YunkuFileSrv instance = null;
    private static final Logger log = LoggerFactory.getLogger(YunkuFileSrv.class);

    static {
        ConfigHelper configHelper = new ConfigHelper();
        configHelper.apiHost(YunkuConf.API_HOST);
        configHelper.retry(5);
        configHelper.language(YunkuConf.LANGUAGE_ZH_CN);
        configHelper.uploadOpName(YunkuConf.OP_NAME);
        configHelper.config();
    }

    public static YunkuFileSrv getInstance() {
        if (instance == null) {
            synchronized (YunkuFileSrv.class) {
                if (instance == null) {
                    instance = new YunkuFileSrv();
                }
            }
        }
        return instance;
    }

    /**
     * @param fullPath  文件完整路径
     * @param optName   创建人名称, 如果指定了op_id, 就不需要op_name
     * @param optId     创建人ID, 个人库默认是库拥有人ID, 如果创建人不是云库用户, 可以用op_name代替
     * @param localFile 本地文件
     * @param overWrite 是否覆盖同名文件
     * @return FileDto
     * @throws Exception
     * @description 文件分块上传
     */
    public FileDto uploadByBlock(String fullPath, String localFile, String optName, Integer optId, boolean overWrite) throws Exception {
        DebugConfig.DEBUG = true;
        FileDto fileDto = null;
        try {
            FileInfo file = EntFileManagerHelper.getInstance().uploadByBlock(YunkuConf.UPLOAD_ROOT + fullPath, optName, optId, localFile, overWrite);
            // fileDto = new FileDto();
            // 每次下载完成后,再查询一遍,获取文件更多的信息 TODO 或者有其它更好的操作建议
            fileDto = this.getFileInfo(fullPath);
            if (fileDto != null) {
                fileDto.setUploadserver(localFile);// 保存rcm服务器文件路径
                fileDto.setHash(file.hash);
                fileDto.setFilehash(file.fileHash);
                // 设置原始文件名
                String rcmFileName = null;
                int index = fullPath.lastIndexOf("\\");
                if(index > 0){
                    rcmFileName = fullPath.substring(index + 1, fullPath.length());
                }else{
                    index = fullPath.lastIndexOf("/");
                    if(index > 0){
                        rcmFileName = fullPath.substring(index + 1, fullPath.length());
                    }
                }
                fileDto.setRcmfilename(rcmFileName);
                if(StringUtils.isNotBlank(file.filename)){
                    fileDto.setFilename(file.filename.replace("\\", "").replace("/", ""));
                }
                fileDto.setFullpath(file.fullpath);
                fileDto.setFilesize(file.fileSize);
                fileDto.setCreate_member_name(optName);
                fileDto.setLast_member_name(optName);
                fileDto.setCreate_dateline(new Date().getTime());
                fileDto.setLast_dateline(new Date().getTime());
            }
        } catch (YunkuException e) {
            e.printStackTrace();
            log.error("Upload file[" + fullPath + "] failed!");
            ReturnResult result = e.getReturnResult();
            if (result != null) {
                this.parseError(result);
            }
        }finally {
            try{
                File local = new File(localFile);
                if(local.exists()){
                    local.delete();
                }
            }catch (Exception e){
                LoggerFactory.getLogger(YunkuFileSrv.class).error(e.getMessage());
            }
        }
        return fileDto;
    }

    public FileDto uploadByBlock(String fullPath, String localFile, String optName, Integer optId) throws Exception {
        return this.uploadByBlock(fullPath, localFile, optName, optId, false);
    }

    /**
     * @param fullpath 路径, 空字符串表示根目录
     * @param order    排序
     * @param tag      返回指定标签的文件
     * @param start    起始下标, 分页显示
     * @param size     返回文件/文件夹数量限制
     * @param dirOnly  只返回文件夹
     * @return List<FileDto>
     * @throws Exception 获取失败则抛出异常
     * @description 获取文件列表
     */
    public List<FileDto> getFileList(String fullpath, String order, String tag, int start, int size, boolean dirOnly) throws Exception {
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().getFileList(YunkuConf.UPLOAD_ROOT + fullpath, order, tag, start, size, dirOnly);
        List<FileDto> files = new ArrayList<FileDto>();
        if (result.isOK()) {
            // 解析body
            String body = result.getBody();
            if (StringUtils.isNoneBlank(body)) {
                JSONObject bodyJsonObj = JSON.parseObject(body);
                String listStr = JSON.toJSONString(bodyJsonObj.get("list"));
                if (StringUtils.isNotBlank(listStr)) {
                    JSONArray jsonArry = JSON.parseArray(listStr);
                    if (CollectionUtils.isNotEmpty(jsonArry)) {
                        for (Object json : jsonArry) {
                            String jsonStr = JSON.toJSONString(json);
                            FileDto filDto = JSON.parseObject(jsonStr, FileDto.class);
                            files.add(filDto);
                        }
                    }
                }
            }
        } else {
            log.error("Get file list[" + fullpath + "] failed!");
            this.parseError(result);
        }
        return files;
    }

    /**
     * @param fullpath 路径, 空字符串表示根目录
     * @return List<FileDto>
     * @throws Exception
     * @description 获取文件列表
     */
    public List<FileDto> getFileList(String fullpath) throws Exception {
        return this.getFileList(fullpath, null, null, 0, 100, false);
    }

    /**
     * @param fullpath 路径, 空字符串表示根目录
     * @param order    排序
     * @return List<FileDto>
     * @throws Exception
     * @description 获取文件列表
     */
    public List<FileDto> getFileList(String fullpath, String order) throws Exception {
        return this.getFileList(fullpath, order, null, 0, 100, false);
    }

    /**
     * @param fullpath 路径, 空字符串表示根目录
     * @param order    排序
     * @param dirOnly  是否只是目录
     * @return List<FileDto>
     * @throws Exception
     * @description 获取文件列表
     */
    public List<FileDto> getFileList(String fullpath, String order, boolean dirOnly) throws Exception {
        return this.getFileList(fullpath, order, null, 0, 100, dirOnly);
    }

    /**
     * @param fullpath 路径, 空字符串表示根目录
     * @param dirOnly  是否只是目录
     * @return List<FileDto>
     * @throws Exception
     * @description 获取文件列表
     */
    public List<FileDto> getFileList(String fullpath, boolean dirOnly) throws Exception {
        return this.getFileList(fullpath, null, null, 0, 100, dirOnly);
    }

    /**
     * @return List<FileDto>
     * @throws Exception
     * @description 获取根目录文件列表
     */
    public List<FileDto> getFileList() throws Exception {
        return this.getFileList("", null, null, 0, 100, false);
    }

    /**
     * @param fullPath 文件路径
     * @return FileDto
     * @throws Exception
     * @description 获取文件信息
     */
    public FileDto getFileInfo(String fullPath) throws Exception {
        FileDto fileDto = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().getFileInfo(YunkuConf.UPLOAD_ROOT + fullPath, EntFileManager.NetType.DEFAULT, true);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                fileDto = JSON.parseObject(body, FileDto.class);
            }
        } else {
            log.error("Get file[" + fullPath + "] failed!");
            this.parseError(result);
        }
        return fileDto;
    }

    /**
     * @param fullPath 文件路径
     * @param optName  操作人
     * @return FileDto 删除前的文件
     * @throws Exception
     * @description 删除文件
     */
    public FileDto deleteFile(String fullPath, String optName) throws Exception {
        FileDto fileDto = this.getFileInfo(fullPath);
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().del(YunkuConf.UPLOAD_ROOT + fullPath, optName);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            log.info("Delete file[" + fullPath + "] successful!" + body);
            return fileDto;
        } else {
            log.error("Delete file[" + fullPath + "] failed!");
            this.parseError(result);
        }
        return null;
    }

    /**
     * @param fullPath 文件路径
     * @param authType 权限类型
     * @return LinkDto
     * @throws Exception
     * @description 获取文件链接
     */
    private LinkDto getLink(String fullPath, AuthType authType) throws Exception {
        LinkDto linkDto = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().link(YunkuConf.UPLOAD_ROOT + fullPath, 0, authType, null);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                linkDto = JSON.parseObject(body, LinkDto.class);
            }
        } else {
            log.error("Get link[" + fullPath + "] failed!");
            this.parseError(result);
        }
        return linkDto;
    }

    /**
     * @param fullPath 文件在云库上的全路径
     * @return LinkDto
     * @throws Exception
     * @description 获取文件预览链接, 此链接可以预览, 并且带登录功能, 有效期3天, 带水印
     */
    public LinkDto getPreviewWindow(String fullPath) throws Exception {
        return this.getLink(fullPath, AuthType.PREVIEW);
    }

    /**
     * @param fullPath 文件在云库上的全路径
     * @return LinkDto
     * @throws Exception
     * @description 获取文件下载链接, 此链接下载与预览, 并且带登录功能, 有时效日期, 有效期3天
     */
    public LinkDto getDownloadWindow(String fullPath) throws Exception {
        return this.getLink(fullPath, AuthType.DOWNLOAD);
    }

    /**
     * @param fullPath        文件在云库上的全路径
     * @param showWaterRemark 是否带水印
     * @param memberName      成员名
     * @return String
     * @throws Exception
     * @description 获取文件预览URL(带水印)
     */
    private String getPreviewUrl(String fullPath, boolean showWaterRemark, String memberName) throws Exception {
        String url = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().previewUrl(YunkuConf.UPLOAD_ROOT + fullPath, showWaterRemark, memberName);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                JSONObject jsonObject = JSON.parseObject(body);
                url = jsonObject.getString("url");
            }
        } else {
            log.error("Get preview url[" + fullPath + "] failed!");
            this.parseError(result);
        }
        return url;
    }

    /**
     * @param fullPath   文件在云库上的全路径,如:pics/Love.jpg
     * @param memberName 成员名
     * @return String
     * @throws Exception
     * @description 获取文件预览URL(带水印), 是一个直接展示链接
     */
    public String getPreviewUrl(String fullPath, String memberName) throws Exception {
        return this.getPreviewUrl(fullPath, true, memberName);
    }

    /**
     * @param fullPath 文件在云库上的全路径,如:pics/Love.jpg
     * @return List<String>
     * @throws Exception
     * @description 通过文件路径获取下载地址, 是一个直接下载链接
     */
    public List<String> getDownloadUrlByFullPath(String fullPath) throws Exception {
        List<String> list = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().getDownloadUrlByFullpath(YunkuConf.UPLOAD_ROOT + fullPath, false, EntFileManager.NetType.DEFAULT);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                JSONObject jsonObject = JSON.parseObject(body);
                Object urls = jsonObject.get("urls");
                list = JSON.parseArray(JSON.toJSONString(urls), String.class);
            }
        } else {
            log.error("Get download url by full path[" + fullPath + "] failed!");
            this.parseError(result);
        }
        return list;
    }

    /**
     * @param hash 文件哈希值,文件唯一标志
     * @return List<String>
     * @throws Exception
     * @description 通过文件唯一标识获取下载地址, 是一个直接下载链接
     */
    public List<String> getDownloadUrlByHash(String hash) throws Exception {
        List<String> list = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().getDownloadUrlByHash(hash, false, EntFileManager.NetType.DEFAULT);
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                // IDE提示修正了此段代码
            }
            JSONObject jsonObject = JSON.parseObject(body);
            Object urls = jsonObject.get("urls");
            list = JSON.parseArray(JSON.toJSONString(urls), String.class);
        } else {
            log.error("Get download url by hash[" + hash + "] failed!");
            this.parseError(result);
        }
        return list;
    }

    /**
     * @return List<String>
     * @throws Exception
     * @description WEB直接上传文件 (支持50MB以上文件的上传),获取文件要提交的表单地址
     */
    public List<String> getUploadServer() throws Exception {
        List<String> list = null;
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().getUploadServers();
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            if (StringUtils.isNotBlank(body)) {
                JSONObject jsonObject = JSON.parseObject(body);
                Object m_uploads = jsonObject.get("m-upload");
                list = JSON.parseArray(JSON.toJSONString(m_uploads), String.class);
            }
        } else {
            log.error("Get uploadserver failed!");
            this.parseError(result);
        }
        return list;
    }

    /**
     * @param keyWords 关键字
     * @param path     路径
     * @param start    开始
     * @param size     大小
     * @param scopes   范围
     * @return List<FileDto>
     * @throws Exception
     * @description 文件搜索
     */
    public List<FileDto> searchFile(String keyWords, String path, int start, int size, ScopeType... scopes) throws Exception {
        DebugConfig.DEBUG = true;
        ReturnResult result = EntFileManagerHelper.getInstance().search(keyWords, YunkuConf.UPLOAD_ROOT + path, start, size, scopes);
        List<FileDto> files = new ArrayList<FileDto>();
        if (result.isOK()) {
            // 成功的结果
            String body = result.getBody();
            // 解析body
            if (StringUtils.isNotBlank(body)) {
                JSONObject bodyJsonObj = JSON.parseObject(body);
                String listStr = JSON.toJSONString(bodyJsonObj.get("list"));
                if (StringUtils.isNotBlank(listStr)) {
                    JSONArray jsonArry = JSON.parseArray(listStr);
                    for (Object json : jsonArry) {
                        String jsonStr = JSON.toJSONString(json);
                        FileDto filDto = JSON.parseObject(jsonStr, FileDto.class);
                        files.add(filDto);
                    }
                }
            }
        } else {
            this.parseError(result);
            log.error("Search file failed!");
        }
        return files;
    }

    private void parseError(ReturnResult result) {
        // 解析result中的内容
        BaseData data = BaseData.create(result.getBody());
        if (data != null) {
            // 如果可解析，则返回错误信息和错误号
            log.error(data.getErrorCode() + ":" + data.getErrorMsg());
            throw new RuntimeException(data.getErrorCode() + ":" + data.getErrorMsg());
        }
    }
}
