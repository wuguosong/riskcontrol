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
    public static final String STATUS_0 = "0";// 初始
    public static final String STATUS_1 = "1";// 待阅/代办已发送/消息已发送
    public static final String STATUS_2 = "2";// 已阅
    public static final String TYPE_MESSAGE = "MESSAGE";// 留言
    public static final String TYPE_NOTIFY = "NOTIFY";// 知会人

    private Long notifyId;// 只会Id
    private String notifyType;// 知会类型
    private String businessModule;// 业务模块
    private String businessId;// 业务ID
    private String associateId;// 关联ID
    private String notifyUser;// 知会人
    private Timestamp notifyDate;// 只会日期
    private String portalStatus;// 同步到统一代办的状态
    private String messageStatus;// 同步到统一消息平台的状态
    private String notifyComments;// 只会意见
    private String notifyUrl;// 只会url
    private String notifyStatus;// 只会状态
    private String notifyUserName;// 只会人名
    private String notifyCreated;// 只会发起人
    private String notifyCreatedName;// 只会发起人名

    public Long getNotifyId() {
        return notifyId;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
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

    public String getPortalStatus() {
        return portalStatus;
    }

    public void setPortalStatus(String portalStatus) {
        this.portalStatus = portalStatus;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(String notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
