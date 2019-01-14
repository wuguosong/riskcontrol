package com.goukuai.dto;

import com.alibaba.fastjson.JSON;
/**
 * @description 云端存储文件日志类
 * @author LiPan[wjsxhclj@sina.com]
 * @date 2019/01/09
 */
public class LogDto {
	private Long logId;// 日志ID
	private String hash;// 哈希
	private String fileHash;// 文件哈希
	private String createdBy;// 创建人
	private String updatedBy;// 更新人
	private Long createdDate;// 创建时间
	private Long updatedDate;// 更新时间
	private String docType;// 业务单据类型
	private String docCode;// 业务单据编号
	private String fileOpt;// 文件操作

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocCode() {
		return docCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}

	public String getFileOpt() {
		return fileOpt;
	}

	public void setFileOpt(String fileOpt) {
		this.fileOpt = fileOpt;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
