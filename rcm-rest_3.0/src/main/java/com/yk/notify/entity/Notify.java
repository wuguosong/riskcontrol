package com.yk.notify.entity;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * 知会实体类
 * Created by LiPan on 2019/3/27.
 */
@Component
public class Notify {
    private Long notifyId;
    private String businessModule;
    private String businessId;
    private String associateId;
    private String notifyUser;
    private Timestamp notifyDate;
    private String notifyStatus;
    private String notifyComments;
    private String notifyUserName;
    private String notifyCreated;
    private String notifyCreatedName;

    public Long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public String getBusinessModule() {
        return businessModule;
    }

    public void setBusinessModule(String businessModule) {
        this.businessModule = businessModule;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getAssociateId() {
        return associateId;
    }

    public void setAssociateId(String associateId) {
        this.associateId = associateId;
    }

    public String getNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(String notifyUser) {
        this.notifyUser = notifyUser;
    }

    public Timestamp getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Timestamp notifyDate) {
        this.notifyDate = notifyDate;
    }

    public String getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(String notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public String getNotifyComments() {
        return notifyComments;
    }

    public void setNotifyComments(String notifyComments) {
        this.notifyComments = notifyComments;
    }

    public String getNotifyUserName() {
        return notifyUserName;
    }

    public void setNotifyUserName(String notifyUserName) {
        this.notifyUserName = notifyUserName;
    }

    public String getNotifyCreated() {
        return notifyCreated;
    }

    public void setNotifyCreated(String notifyCreated) {
        this.notifyCreated = notifyCreated;
    }

    public String getNotifyCreatedName() {
        return notifyCreatedName;
    }

    public void setNotifyCreatedName(String notifyCreatedName) {
        this.notifyCreatedName = notifyCreatedName;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
