package com.goukuai.constant;

import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;

/**
 * @description 云端存储常量配置类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public interface YunkuConf{
	Prop prop = PropKit.use("yunkuconf.properties");
	public static final String API_HOST = prop.get("yunku.api.apihost");
	/**
	 * 多语言环境, zh-CN表示中文, en-US表示英文, 英文
	 */
	public static final String LANGUAGE_EN_US = prop.get("yunku.api.enUs");
	/**
	 * 多语言环境, zh-CN表示中文, en-US表示英文, 中文
	 */
	public static final String LANGUAGE_ZH_CN = prop.get("yunku.api.zhCh");
	/**
	 * 企业管理后台"授权管理"中获取的client id
	 */
	public static final String CLIENT_ID = prop.get("yunku.api.clientId");
	/**
	 * 企业管理后台"授权管理"中获取的client secret
	 */
	public static final String CLIENT_SECRET = prop.get("yunku.api.clientSecret");

	/**
	 * 需要通过 EntLibManager中 bind 获取库授权获取org client id
	 */
	public static final String ORG_CLIENT_ID = prop.get("yunku.api.orgClientId");

	/**
	 * 需要通过 EntLibManager中 bind 获取库授权获取org client secret
	 */
	public static final String ORG_CLIENT_SECRET = prop.get("yunku.api.orgClientSecret");

	/**
	 * 企业在合作方系统中的唯一ID
	 */
	public static final String OUT_ID = prop.get("yunku.api.outId");

	/**
	 * 用户名
	 */
	public static final String ADMIN = prop.get("yunku.api.admin");
	
	/**
	 * 用户名
	 */
	public static final String OP_NAME = prop.get("yunku.api.opName");

	/**
	 * 密码
	 */
	public static final String PASSWORD = prop.get("yunku.api.password");
	/**
	 * 操作文件返回状态描述
	 */
	public static final String UPLOAD_SUCCESS = prop.get("yunku.return.uploadSuccess");
	public static final String UPLOAD_FAIL = prop.get("yunku.return.uploadFail");
	public static final String DELETE_SUCCESS = prop.get("yunku.return.deleteSuccess");
	public static final String DELETE_FAIL = prop.get("yunku.return.deleteFail");
	public static final String MODIFY_SUCCESS = prop.get("yunku.return.modifySuccess");
	public static final String MODIFY_FAIL = prop.get("yunku.return.modifyFail");
	public static final String QUERY_SUCCESS = prop.get("yunku.return.querySuccess");
	public static final String QUERY_FAIL = prop.get("yunku.return.queryFail");
	public static final String GET_SUCCESS = prop.get("yunku.return.getSuccess");
	public static final String GET_FAIL = prop.get("yunku.return.getFail");
	public static final String NET_IO = prop.get("yunku.return.NetIo");
	/**
	 * 上传根路径
	 */
	public static final String UPLOAD_ROOT = prop.get("yunku.fullpath.uploadRoot");
}
