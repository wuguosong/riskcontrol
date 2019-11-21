package com.yk.announce.entity;

import com.alibaba.fastjson.JSON;

import java.sql.Timestamp;

/**
 * @author lipan92
 * @version 1.0
 * @calssName Announce
 * @description 公告
 * @date 2019/11/21 0021 14:30
 **/
public class Announce {
    private Long announceId;// 主键ID
    private String title;// 标题
    private String comments;// 内容
    private String status;// 状态
    private String createBy;// 创建人
    private String updateBy;// 更新人
    private Timestamp createTime;// 创建时间
    private Timestamp updateTime;// 更新时间

    public Long getAnnounceId() {
        return announceId;
    }

    public void setAnnounceId(Long announceId) {
        this.announceId = announceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
