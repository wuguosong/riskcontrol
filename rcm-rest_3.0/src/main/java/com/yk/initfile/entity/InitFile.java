package com.yk.initfile.entity;

import com.alibaba.fastjson.JSON;

/**
 * Created by Administrator on 2019/5/30 0030.
 */
public class InitFile {
    private boolean server;// 是否存在于服务上
    private boolean cloud;// 是否存在于云库上
    private boolean synchronize;// 两者是否同步
    private String nameServer;// 服务上的文件名
    private String pathServer;// 服务上的文件路径
    private String nameCloud;// 云库上的文件名
    private String pathCloud;// 云库上的文件路径
    private String location;// 页面位置
    private String type;// 类型
    private String code;// 主键
    private String version;// 版本
    private Integer sequence;// 排序
    private String name;// 模块描述
    private String table;// 查询的表

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isCloud() {
        return cloud;
    }

    public void setCloud(boolean cloud) {
        this.cloud = cloud;
    }

    public boolean isSynchronize() {
        return synchronize;
    }

    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getPathServer() {
        return pathServer;
    }

    public void setPathServer(String pathServer) {
        this.pathServer = pathServer;
    }

    public String getNameCloud() {
        return nameCloud;
    }

    public void setNameCloud(String nameCloud) {
        this.nameCloud = nameCloud;
    }

    public String getPathCloud() {
        return pathCloud;
    }

    public void setPathCloud(String pathCloud) {
        this.pathCloud = pathCloud;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}