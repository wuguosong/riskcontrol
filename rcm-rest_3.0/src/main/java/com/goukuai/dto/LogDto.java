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
	private String fileHash;// 文件哈希
	private String fullpath;// 文件路径
	private String filename;// 文件名称
	private String createdBy;// 创建人
	private String updatedBy;// 更新人
	private Long createdDate;// 创建时间
	private Long updatedDate;// 更新时间
	private String doctype;// 业务单据类型
	private String doccode;// 业务单据编号
	private String fileopt;// 文件操作

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

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

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Long updatedDate) {
		this.updatedDate = updatedDate;
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
