package com.yk.message.entity;


import com.alibaba.fastjson.JSON;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by LiPan on 2019/1/25.
 */
public class Message{
    private Long messageId;// 留言id
    private String messageType;// 留言类型(正式评审、其他评审...)
    private String procInstId;// 流程实例id
    private Long parentId;// 为0时，为根节点
    private Long originalId;// 原始留言id
    private String createdBy;// 留言或者回复人id
    private String createdName;// 留言或者回复人
    private String createdDept;// 创建人部门
    private String createdPosition;// 创建人职位
    private String repliedBy;// 被回复人id
    private String repliedName;// 被回复人
    private String repliedDept;// 被回复人部门
    private String repliedPosition;// 被回复人职位
    private String messageTitle;// 留言主题
    private String messageContent;// 留言或者回复内容
    private Timestamp messageDate;// 留言或者回复日期
    private String pushFlag;// 推送成功标志(Y/N)
    private String readFlag;// 已读标志(Y/N)
    private String bsUniRelated;// 业务唯一关联信息(备用)
    private String viaUsers;// @的用户
    private Long messagePriority;// 留言优先级
    private String messageFile;// 留言过程附件ID
    private String messageFileType;// 附件类型
    private String messageScreenType;// 消息分屏类型
    private String attriText01;// 备用字段1
    private String attriText02;// 备用字段2
    private String attriText03;// 备用字段3
    private String attriText04;// 备用字段4

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedName() {
        return createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }

    public String getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(String repliedBy) {
        this.repliedBy = repliedBy;
    }

    public String getRepliedName() {
        return repliedName;
    }

    public void setRepliedName(String repliedName) {
        this.repliedName = repliedName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Timestamp getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Timestamp messageDate) {
        this.messageDate = messageDate;
    }

    public String getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(String pushFlag) {
        this.pushFlag = pushFlag;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getBsUniRelated() {
        return bsUniRelated;
    }

    public void setBsUniRelated(String bsUniRelated) {
        this.bsUniRelated = bsUniRelated;
    }

    public String getAttriText01() {
        return attriText01;
    }

    public void setAttriText01(String attriText01) {
        this.attriText01 = attriText01;
    }

    public String getAttriText02() {
        return attriText02;
    }

    public void setAttriText02(String attriText02) {
        this.attriText02 = attriText02;
    }

    public String getAttriText03() {
        return attriText03;
    }

    public void setAttriText03(String attriText03) {
        this.attriText03 = attriText03;
    }

    public String getAttriText04() {
        return attriText04;
    }

    public void setAttriText04(String attriText04) {
        this.attriText04 = attriText04;
    }


    public String getViaUsers() {
        return viaUsers;
    }

    public void setViaUsers(String viaUsers) {
        this.viaUsers = viaUsers;
    }

    @Override

    public String toString() {
        return JSON.toJSONString(this);
    }

    private List<Message> children = new ArrayList<Message>();// 子留言

    public List<Message> getChildren() {
        return children;
    }
    public void setChildren(List<Message> children) {
        this.children = children;
    }

    public String getCreatedDept() {
        return createdDept;
    }

    public void setCreatedDept(String createdDept) {
        this.createdDept = createdDept;
    }

    public String getCreatedPosition() {
        return createdPosition;
    }

    public void setCreatedPosition(String createdPosition) {
        this.createdPosition = createdPosition;
    }

    public String getRepliedDept() {
        return repliedDept;
    }

    public void setRepliedDept(String repliedDept) {
        this.repliedDept = repliedDept;
    }

    public String getRepliedPosition() {
        return repliedPosition;
    }

    public void setRepliedPosition(String repliedPosition) {
        this.repliedPosition = repliedPosition;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public Long getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(Long messagePriority) {
        this.messagePriority = messagePriority;
    }

    public String getMessageFile() {
        return messageFile;
    }

    public void setMessageFile(String messageFile) {
        this.messageFile = messageFile;
    }

    public String getMessageFileType() {
        return messageFileType;
    }

    public void setMessageFileType(String messageFileType) {
        this.messageFileType = messageFileType;
    }

    public String getMessageScreenType() {
        return messageScreenType;
    }

    public void setMessageScreenType(String messageScreenType) {
        this.messageScreenType = messageScreenType;
    }
}

