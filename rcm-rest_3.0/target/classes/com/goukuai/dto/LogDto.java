package com.goukuai.dto;

import com.alibaba.fastjson.JSON;

/**
 * @description 云端存储文件日志类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public class LogDto {
	private Long logid;// 日志ID
	private String hash;// 哈希
	private String filehash;// 文件哈希
	private String fullpath;// 文件路径
	private String filename;// 文件名称
	private String createdby;// 创建人
	private String updatedby;// 更新人
	private Long createddate;// 创建时间
	private Long updateddate;// 更新时间
	private String doctype;// 业务单据类型
	private String doccode;// 业务单据编号
	private String fileopt;// 文件操作

	public Long getLogid() {
		return logid;
	}

	public void setLogid(Long logid) {
		this.logid = logid;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getFilehash() {
		return filehash;
	}

	public void setFilehash(String filehash) {
		this.filehash = filehash;
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

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	public Long getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Long createddate) {
		this.createddate = createddate;
	}

	public Long getUpdateddate() {
		return updateddate;
	}

	public void setUpdateddate(Long updateddate) {
		this.updateddate = updateddate;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDoccode() {
		return doccode;
	}

	public void setDoccode(String doccode) {
		this.doccode = doccode;
	}

	public String getFileopt() {
		return fileopt;
	}

	public void setFileopt(String fileopt) {
		this.fileopt = fileopt;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
