package com.goukuai.srv;

import java.util.ArrayList;
import java.util.List;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.BaseData;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.helper.EntFileManagerHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
 * @description 云端存储文件操作服务类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public class YunkuFileSrv {
	private static volatile YunkuFileSrv instance = null;
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
	 * @description 文件分块上传
	 * @param fullPath  文件完整路径
	 * @param optName   创建人名称, 如果指定了op_id, 就不需要op_name
	 * @param optId     创建人ID, 个人库默认是库拥有人ID, 如果创建人不是云库用户, 可以用op_name代替
	 * @param localFile 本地文件
	 * @param overWrite 是否覆盖同名文件
	 * @return FileDto
	 * @throws Exception
	 */
	public FileDto uploadByBlock(String fullPath, String localFile, String optName, Integer optId, boolean overWrite)
			throws Exception {
		DebugConfig.DEBUG = true;
		FileDto fileDto = null;
		try {
			FileInfo file = EntFileManagerHelper.getInstance().uploadByBlock(fullPath, optName, optId, localFile,
					overWrite);
			// fileDto = new FileDto();
			// 每次下载完成后,再查询一遍,获取文件更多的信息 TODO 或者有其它更好的操作建议
			fileDto = this.getFileInfo(fullPath);
			if (fileDto != null) {
				fileDto.setUploadserver(file.uploadServer);
				/*
				 * fileDto.setHash(file.hash); fileDto.setFilehash(file.fileHash);
				 * fileDto.setFilename(file.filename); fileDto.setFullpath(file.fullpath);
				 * fileDto.setFilesize(file.fileSize); fileDto.setCreate_member_name(optName);
				 * fileDto.setLast_member_name(optName); fileDto.setCreate_dateline(new
				 * Date().getTime()); fileDto.setLast_dateline(new Date().getTime());
				 */
			}
		} catch (YunkuException e) {
			ReturnResult result = e.getReturnResult();
			if (result != null) {
				this.parseError(result);
			}
		}
		return fileDto;
	}

	public FileDto uploadByBlock(String fullPath, String localFile, String optName, Integer optId) throws Exception {
		return this.uploadByBlock(fullPath, localFile, optName, optId, true);
	}

	/**
	 * @description 获取文件列表
	 * @param fullpath 路径, 空字符串表示根目录
	 * @param order    排序
	 * @param tag      返回指定标签的文件
	 * @param start    起始下标, 分页显示
	 * @param size     返回文件/文件夹数量限制
	 * @param dirOnly  只返回文件夹
	 * @return List<FileDto>
	 * @throws Exception 获取失败则抛出异常
	 */
	public List<FileDto> getFileList(String fullpath, String order, String tag, int start, int size, boolean dirOnly)
			throws Exception {
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().getFileList(fullpath, order, tag, start, size,
				dirOnly);
		List<FileDto> files = new ArrayList<FileDto>();
		if (result.isOK()) {
			// 成功的结果
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
			// 解析body
		} else {
			this.parseError(result);
		}
		return files;
	}

	/**
	 * @description 获取文件列表
	 * @param fullpath 路径, 空字符串表示根目录
	 * @return List<FileDto>
	 * @throws Exception
	 */
	public List<FileDto> getFileList(String fullpath) throws Exception {
		return this.getFileList(fullpath, null, null, 1, 100, false);
	}

	/**
	 * @description 获取文件列表
	 * @param fullpath 路径, 空字符串表示根目录
	 * @param order    排序
	 * @return List<FileDto>
	 * @throws Exception
	 */
	public List<FileDto> getFileList(String fullpath, String order) throws Exception {
		return this.getFileList("", null, null, 1, 100, false);
	}

	/**
	 * @description 获取根目录文件列表
	 * @return List<FileDto>
	 * @throws Exception
	 */
	public List<FileDto> getFileList() throws Exception {
		return this.getFileList("", null, null, 1, 100, false);
	}

	/**
	 * @description 获取文件信息
	 * @param fullPath 文件路径
	 * @return FileDto
	 * @throws Exception
	 */
	public FileDto getFileInfo(String fullPath) throws Exception {
		FileDto fileDto = null;
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().getFileInfo(fullPath, EntFileManager.NetType.DEFAULT,
				true);
		if (result.isOK()) {
			// 成功的结果
			String body = result.getBody();
			if (StringUtils.isNotBlank(body)) {
				fileDto = JSON.parseObject(body, FileDto.class);
			}
		} else {
			this.parseError(result);
		}
		return fileDto;
	}

	/**
	 * @description 删除文件
	 * @param fullPath 文件路径
	 * @param optName  操作人
	 * @return FileDto 删除前的文件
	 * @throws Exception
	 */
	public FileDto deleteFile(String fullPath, String optName) throws Exception {
		FileDto fileDto = this.getFileInfo(fullPath);
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().del(fullPath, optName);
		if (result.isOK()) {
			// 成功的结果
			String body = result.getBody();
			System.out.println(body);
			return fileDto;
		} else {
			this.parseError(result);
		}
		return null;
	}

	/**
	 * @description 获取文件链接
	 * @param fullPath 文件路径
	 * @param authType 权限类型
	 * @return LinkDto
	 * @throws Exception
	 */
	private LinkDto getLink(String fullPath, AuthType authType) throws Exception {
		if(YunkuConf.IS_INGORE_FULLPATH) {
			if(StringUtils.isNotBlank(fullPath)) {
				if(fullPath.length() > 1) {
					char c = fullPath.charAt(0);
					if(c == '\\' || c == '/') {
						fullPath = fullPath.substring(1, fullPath.length());
					}
				}
			}
		}
		LinkDto linkDto = null;
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().link(fullPath, 0, authType, null);
		if (result.isOK()) {
			// 成功的结果
			String body = result.getBody();
			if (StringUtils.isNotBlank(body)) {
				linkDto = JSON.parseObject(body, LinkDto.class);
			}
		} else {
			this.parseError(result);
		}
		return linkDto;
	}

	/**
	 * @description 获取文件预览链接,此链接可以预览,并且带登录功能,有效期3天,带水印
	 * @param fullPath 文件在云库上的全路径
	 * @return LinkDto
	 * @throws Exception
	 */
	public LinkDto getPreviewWindow(String fullPath) throws Exception {
		return this.getLink(fullPath, EntFileManager.AuthType.PREVIEW);
	}

	/**
	 * @description 获取文件下载链接,此链接下载与预览,并且带登录功能,有时效日期,有效期3天
	 * @param fullPath 文件在云库上的全路径
	 * @return LinkDto
	 * @throws Exception
	 */
	public LinkDto getDownloadWindow(String fullPath) throws Exception {
		return this.getLink(fullPath, EntFileManager.AuthType.DOWNLOAD);
	}

	/**
	 * @description 获取文件预览URL(带水印)
	 * @param fullPath       文件在云库上的全路径
	 * @param showWaterRemark 是否带水印
	 * @param memberName     成员名
	 * @return String
	 * @throws Exception
	 */
	private String getPreviewUrl(String fullPath, boolean showWaterRemark, String memberName) throws Exception {
		String url = null;
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().previewUrl(fullPath, showWaterRemark, memberName);
		if (result.isOK()) {
			// 成功的结果
			String body = result.getBody();
			if (StringUtils.isNotBlank(body)) {
				JSONObject jsonObject = JSON.parseObject(body);
				url = jsonObject.getString("url");
			}
		} else {
			this.parseError(result);
		}
		return url;
	}

	/**
	 * @description 获取文件预览URL(带水印),是一个直接展示链接
	 * @param fullPath   文件在云库上的全路径,如:pics/Love.jpg
	 * @param memberName 成员名
	 * @return String
	 * @throws Exception
	 */
	public String getPreviewUrl(String fullPath, String memberName) throws Exception {
		return this.getPreviewUrl(fullPath, true, memberName);
	}

	/**
	 * @description 通过文件路径获取下载地址,是一个直接下载链接
	 * @param fullPath 文件在云库上的全路径,如:pics/Love.jpg
	 * @return List<String>
	 * @throws Exception
	 */
	public List<String> getDownloadUrlByFullPath(String fullPath) throws Exception {
		List<String> list = null;
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().getDownloadUrlByFullpath(fullPath, false,
				EntFileManager.NetType.DEFAULT);
		if (result.isOK()) {
			// 成功的结果
			String body = result.getBody();
			if (StringUtils.isNotBlank(body)) {
				JSONObject jsonObject = JSON.parseObject(body);
				Object urls = jsonObject.get("urls");
				list = JSON.parseArray(JSON.toJSONString(urls), String.class);
			}
		} else {
			this.parseError(result);
		}
		return list;
	}

	/**
	 * @description 通过文件唯一标识获取下载地址,是一个直接下载链接
	 * @param hash 文件哈希值,文件唯一标志
	 * @return List<String>
	 * @throws Exception
	 */
	public List<String> getDownloadUrlByHash(String hash) throws Exception {
		List<String> list = null;
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().getDownloadUrlByHash(hash, false,
				EntFileManager.NetType.DEFAULT);
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
			this.parseError(result);
		}
		return list;
	}

	/**
	 * @description WEB直接上传文件 (支持50MB以上文件的上传),获取文件要提交的表单地址
	 * @return List<String>
	 * @throws Exception
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
			this.parseError(result);
		}
		return list;
	}
	
	/**
	 * @description 文件搜索
	 * @param keyWords 关键字
	 * @param path 路径
	 * @param start 开始
	 * @param size 大小
	 * @param scopes 范围
	 * @return List<FileDto>
	 * @throws Exception
	 */
	public List<FileDto> searchFile(String keyWords, String path, int start, int size, ScopeType... scopes) throws Exception {
		DebugConfig.DEBUG = true;
		ReturnResult result = EntFileManagerHelper.getInstance().search(keyWords, path, start, size, scopes);
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
		}
		return files;
	}
	
	private void parseError(ReturnResult result) {
		// 解析result中的内容
        BaseData data = BaseData.create(result.getBody());
        if (data != null) {
            // 如果可解析，则返回错误信息和错误号
            throw new RuntimeException(data.getErrorCode() + ":" + data.getErrorMsg());
        }
	}
}
