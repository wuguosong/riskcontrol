package com.yk.initfile.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 上会附件信息
 */
public class MeetingFile {
    private String id;// id
    private String name;// 名称
    private String dataTable;// 主表名
    private String fileTable;// 上会附件表名
    private Integer status;// 状态
    private List<JSONObject> files;// 上会文件
    private Integer sequence;// 序号
    private String url;// 链接url

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    public String getFileTable() {
        return fileTable;
    }

    public void setFileTable(String fileTable) {
        this.fileTable = fileTable;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<JSONObject> getFiles() {
        return files;
    }

    public void setFiles(List<JSONObject> files) {
        this.files = files;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
