package com.goukuai.dto;

import com.alibaba.fastjson.JSON;

/**
 * @description 云端存储文件实体类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public class FileDto {
	private Long fileid;// 文件在业务系统中的ID
	private String hash;// 文件唯一标识
	private Integer dir;// 是否文件夹, 1是, 0否
	private String fullpath;// 文件路径
	private String filename;// 文件名称
	private String filehash;// 文件内容hash, 如果是文件夹, 则为空
	private Long filesize;// 文件大小, 如果是文件夹, 则为0
	private String create_member_name;// 文件创建人名称
	private Long create_dateline;// 文件创建时间戳
	private String last_member_name;// 文件最后修改人名称
	private Long last_dateline;// 文件最后修改时间戳
	private String property;// {"tag": "标签", "op_name": "操作人名称", "permisson": 文件权限}
	private String uploadserver;
	private String uri;// 下载URL
	private String preview;// 预览URL
	private String thumbnail;// 缩略图
	private String tag;
	private String do_type;// 业务单据类型
	private String do_code;// 业务单据编号

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Integer getDir() {
		return dir;
	}

	public void setDir(Integer dir) {
		this.dir = dir;
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilehash() {
		return filehash;
	}

	public void setFilehash(String filehash) {
		this.filehash = filehash;
	}

	public Long getFilesize() {
		return filesize;
	}

	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}

	public String getCreate_member_name() {
		return create_member_name;
	}

	public void setCreate_member_name(String create_member_name) {
		this.create_member_name = create_member_name;
	}

	public Long getCreate_dateline() {
		return create_dateline;
	}

	public void setCreate_dateline(Long create_dateline) {
		this.create_dateline = create_dateline;
	}

	public String getLast_member_name() {
		return last_member_name;
	}

	public void setLast_member_name(String last_member_name) {
		this.last_member_name = last_member_name;
	}

	public Long getLast_dateline() {
		return last_dateline;
	}

	public void setLast_dateline(Long last_dateline) {
		this.last_dateline = last_dateline;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getUploadserver() {
		return uploadserver;
	}

	public void setUploadserver(String uploadserver) {
		this.uploadserver = uploadserver;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getDo_type() {
		return do_type;
	}

	public Long getFileid() {
		return fileid;
	}

	public void setFileid(Long fileid) {
		this.fileid = fileid;
	}

	public void setDo_type(String do_type) {
		this.do_type = do_type;
	}

	public String getDo_code() {
		return do_code;
	}

	public void setDo_code(String do_code) {
		this.do_code = do_code;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
