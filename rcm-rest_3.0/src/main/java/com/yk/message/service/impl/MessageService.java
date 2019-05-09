package com.yk.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goukuai.dto.FileDto;
import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import com.yk.common.IBaseMongo;
import com.yk.exception.BusinessException;
import com.yk.message.dao.IMessageMapper;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import com.yk.power.dao.IUserMapper;
import com.yk.rcm.file.service.IFileService;
import common.Constants;
import common.PageAssistant;
import common.commonMethod;
import fnd.UserDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.DateUtil;
import util.UserUtil;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/1/25.
 */
@Service
@Transactional
public class MessageService implements IMessageService {
    @Resource
    IMessageMapper messageMapper;
    @Resource
    IUserMapper userMapper;

    @Override
    public List<Message> list(String procInstId, Long parentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("procInstId", procInstId);
        params.put("parentId", parentId);
        return messageMapper.selectMessageList(params);
    }

    @Override
    public Message get(Long messageId) {
        return messageMapper.selectMessageById(messageId);
    }

    @Override
    public Message save(Message message) {
        message.setMessageDate(DateUtil.getCurrentDate());
        // 设置创建人和回复人相关信息
        String createdBy = message.getCreatedBy();
        if(StringUtils.isNotBlank(createdBy)){
            List<HashMap<String,Object>> about = messageMapper.selectUserAbout(createdBy);
            if(CollectionUtils.isNotEmpty(about)){
                HashMap<String, Object> user = about.get(0);
                String dept = "";
                String position = "";
                if(user.get("ORGNAME") != null && !"null".equalsIgnoreCase(String.valueOf(user.get("ORGNAME")))){
                    dept = String.valueOf(user.get("ORGNAME"));
                    if(user.get("DEPTNAME") != null && !"null".equalsIgnoreCase(String.valueOf(user.get("DEPTNAME")))){
                        dept += "-" + String.valueOf(user.get("DEPTNAME"));
                    }
                }
                if(user.get("JOBNAME") != null && "null".equalsIgnoreCase(String.valueOf(user.get("JOBNAME")))){
                    position = String.valueOf(user.get("JOBNAME"));
                }
                message.setCreatedDept(dept);
                message.setCreatedPosition(position);
            }
        }
        String repliedBy = message.getRepliedBy();
        if(StringUtils.isNotBlank(repliedBy)){
            List<HashMap<String,Object>> about = messageMapper.selectUserAbout(repliedBy);
            if(CollectionUtils.isNotEmpty(about)){
                HashMap<String, Object> user = about.get(0);
                String dept = "";
                String position = "";
                if(user.get("ORGNAME") != null && !"null".equalsIgnoreCase(String.valueOf(user.get("ORGNAME")))){
                    dept = String.valueOf(user.get("ORGNAME"));
                    if(user.get("DEPTNAME") != null && !"null".equalsIgnoreCase(String.valueOf(user.get("DEPTNAME")))){
                        dept += "-" + String.valueOf(user.get("DEPTNAME"));
                    }
                }
                if(user.get("JOBNAME") != null && "null".equalsIgnoreCase(String.valueOf(user.get("JOBNAME")))){
                    position = String.valueOf(user.get("JOBNAME"));
                }
                message.setRepliedDept(dept);
                message.setRepliedPosition(position);
            }
        }
        messageMapper.insertMessage(message);
        return message;
    }

    @Override
    public Message update(Message message) {
        messageMapper.updateMessage(message);
        return message;
    }

    @Override
    public Message delete(Long messageId) {
        Message message = messageMapper.selectMessageById(messageId);
        if(message.getMessageFile() != null){
            FileDto fileDto = fileService.getFile(String.valueOf(message.getMessageFile()));
            fileService.deleteFile(fileDto);
        }
        messageMapper.deleteMessage(messageId);
        return message;
    }

    @Override
    public List<Message> getMessageTree(String procInstId, Long parentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("procInstId", procInstId);
        params.put("parentId",parentId);
        // 获取根节点
        List<Message> roots = messageMapper.selectMessageList(params);
        List<Message> messages = new ArrayList<Message>();
        if (CollectionUtils.isEmpty(roots)) {
            return messages;
        }
        // 遍历迭代
        for (Message root : roots) {
            List<Message> leaves = this.getMessageTree(procInstId, root.getMessageId());
            if (CollectionUtils.isNotEmpty(leaves)) {
                root.setChildren(leaves);
            }
            messages.add(root);
        }
        return messages;
    }

    @Override
    public List<Message> recursionDelete(Long messageId) {
        Message message = this.get(messageId);
        List<Message> messages = this.getMessageTree(message.getProcInstId(), message.getMessageId());
        messageMapper.deleteMessageRootAndChildren(messageId);
        return messages;
    }

    @Override
    public List<Message> getMessageChildren(Long parentId) {
        return messageMapper.selectMessageChildren(parentId);
    }

    @Override
    public Message updateRead(Long messageId) {
        Message message = this.get(messageId);
        message.setReadFlag("Y");
        this.update(message);
        return message;
    }

    @Override
    public Message updatePush(Long messageId) {
        Message message = this.get(messageId);
        message.setPushFlag("Y");
        this.update(message);
        return message;
    }

    @Override
    public List<Message> findMessages(String procInstId, String createdBy, boolean notIncludeCreatedBy) {
        return messageMapper.selectMessages(procInstId, createdBy, notIncludeCreatedBy);
    }

    @Override
    public Map<String, List<Message>> findMessages(String procInstId, String createdBy) {
        Map<String, List<Message>> map = new HashMap<String, List<Message>>();
        map.put("left", this.findMessages(procInstId, createdBy, false));
        map.put("right", this.findMessages(procInstId, createdBy, true));
        return map;
    }

    @Override
    public List<JSONObject> messages(String procInstId, String createdBy) {
        List<Message> messages = this.findMessages(procInstId, null, false);
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        for (Message message : messages) {
            JSONObject jsonObject = JSON.parseObject(message.toString());
            if (createdBy.equals(message.getCreatedBy())) {
                jsonObject.put("position", "left");
            } else {
                jsonObject.put("position", "right");
            }
            jsonObject.put("messageDate",
                    DateUtil.getOracleDateToString(message.getMessageDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    @Override
    public List<List<JSONObject>> queryMessagesList(String procInstId, Long parentId, String _query_params_) {
        List<List<JSONObject>> jsonObjects = new ArrayList<List<JSONObject>>();
        // 查询所有的根节点
        Map<String, Object> _paramsMap = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(_query_params_)){
            _paramsMap = JSON.parseObject(_query_params_, HashMap.class);
        }
        _paramsMap.put("procInstId", procInstId);
        _paramsMap.put("parentId", parentId);
        List<Message> messages = messageMapper.selectMessageList(_paramsMap);
        for (Message message : messages) {
            this.setMessageFileType(message);
            List<JSONObject> jsonObjectLeaves = new ArrayList<JSONObject>();
            // this.setViaUsers(message);
            JSONObject jsonObject = JSON.parseObject(message.toString());
            jsonObject.put("position", "left");
            jsonObject.put("messageDate",
                    DateUtil.getOracleDateToString(message.getMessageDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
            // 将根节点装入聊天组中
            this.setViaUsers(jsonObject);
            this.setMessageFileInfo(jsonObject);
            jsonObjectLeaves.add(jsonObject);
            // 把根节点下面的叶子节点全部都查出来
            List<Message> leaves = messageMapper.selectLeavesMessageList(procInstId, message.getMessageId());
            for (Message leave : leaves) {
                this.setMessageFileType(leave);
                // this.setViaUsers(leave);
                JSONObject leaveObject = JSON.parseObject(leave.toString());
                if (leave.getCreatedBy().equals(message.getCreatedBy())) {
                    leaveObject.put("position", "left");
                } else {
                    leaveObject.put("position", "right");
                }
                leaveObject.put("messageDate",
                        DateUtil.getOracleDateToString(leave.getMessageDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
                this.setViaUsers(leaveObject);
                this.setMessageFileInfo(leaveObject);
                jsonObjectLeaves.add(leaveObject);
            }

            jsonObjects.add(jsonObjectLeaves);
        }
        return jsonObjects;
    }

    private void setMessageFileType(Message message){
        if(StringUtils.isBlank(message.getMessageFileType())){
            message.setMessageFileType("-1");
        }
    }

    private void setViaUsers(Message message) {
        String viaUsers = message.getViaUsers();
        if (StringUtils.isNotBlank(viaUsers)) {
            String[] viaUsersArray = viaUsers.split(",");
            if (ArrayUtils.isNotEmpty(viaUsersArray)) {
                Map<String, Object> params = new HashMap<String, Object>();
                StringBuilder sb = new StringBuilder();
                for (String viaUser : viaUsersArray) {
                    params.put("UUID", viaUser);
                    Map<String, Object> user = userMapper.selectAUser(params);
                    if (user != null) {
                        sb.append("@" + user.get("NAME") + " ");
                    }
                }
                if (StringUtils.isNotBlank(sb)) {
                    message.setViaUsers(sb.toString());
                }
            }
        }
    }

    private void setViaUsers(JSONObject jsonObject) {
        String viaUsers = jsonObject.getString("viaUsers");
        if (StringUtils.isNotBlank(viaUsers)) {
            String[] viaUsersArray = viaUsers.split(",");
            if (ArrayUtils.isNotEmpty(viaUsersArray)) {
                Map<String, Object> params = new HashMap<String, Object>();
                List<JSONObject> viaUsersList = new ArrayList<JSONObject>();
                for (String viaUser : viaUsersArray) {
                    params.put("UUID", viaUser);
                    Map<String, Object> user = userMapper.selectAUser(params);
                    if (user != null) {
                        JSONObject jb = new JSONObject();
                        jb.put("viaId", viaUser);
                        jb.put("viaName", user.get("NAME"));
                        jb.put("via", "@" + user.get("NAME"));
                        viaUsersList.add(jb);
                    }
                }
                jsonObject.put("viaArray", viaUsersList);
            }
        }
    }

    /**
     * 留言分享给指定用户，发送url到钉钉
     *
     * @param messageId
     * @param shareUsers
     * @return String url
     */
    @Override
    public MessageBack shareMessage(Long messageId, String shareUsers, String type) {
        if(StringUtils.isBlank(type)){
            type = MessageClient._DT;
        }
        if (messageId == null) {
            throw new BusinessException("共享失败，留言Id为空！");
        }
        if (StringUtils.isBlank(shareUsers)) {
            throw new BusinessException("共享失败，要共享的用户为空！");
        }
        // 分割用户
        String[] shareUserArray = shareUsers.split(",");
        if (ArrayUtils.isEmpty(shareUserArray)) {
            throw new BusinessException("共享失败，要共享的用户为空！");
        }
        StringBuffer accounts = new StringBuffer();
        StringBuffer emails = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        for (String shareUser : shareUserArray) {
            params.put("UUID", shareUser);
            Map<String, Object> user = userMapper.selectAUser(params);
            if (user != null) {
                accounts.append(String.valueOf(user.get("ACCOUNT")));
                accounts.append(",");
                emails.append(String.valueOf(user.get("EMAIL")));
                emails.append(",");
            }
        }
        MessageClient client = new MessageClient();
        MessageBack messageBack = null;
        UserDto userDto = UserUtil.getCurrentUser();
        // 钉钉
        if(MessageClient._DT.equalsIgnoreCase(type)){
            messageBack = client.sendDtLink(null, accounts.substring(0, accounts.lastIndexOf(",")).replace(",", "|"), client._URL + messageId, client._TITLE, client._URL + messageId, client._CONTENT + client._URL + messageId);
        }
        // 邮件
        if(MessageClient._EMAIL.equalsIgnoreCase(type)){
            messageBack = client.sendEmail(userDto.getEmail(), emails.substring(0, emails.lastIndexOf(",")).replace(",", "|"), null, client._TITLE, client._CONTENT + client._URL + messageId);
        }
        // 短信
        if(MessageClient._SMS.equalsIgnoreCase(type)){
            String target = userDto.getContact1();
            if(StringUtils.isBlank(target)){
                target = userDto.getContact2();
            }
            messageBack = client.sendSms(target, client._CONTENT + client._URL + messageId, null);
        }
        // 微信
        if(MessageClient._WX.equalsIgnoreCase(type)){
            messageBack = client.sendWxText(null, accounts.substring(0, accounts.lastIndexOf(",")).replace(",", "|"), "", (short)0, client._CONTENT + client._URL + messageId);
        }
        // 如果同步成功，将返回的凭证存入，此凭证可用于查询消息发送状态
        if (messageBack != null && 0 == messageBack.getCode()) {
            Message message = this.get(messageId);
            if (message != null) {
                message.setAttriText01(message.getAttriText01());
                this.update(message);
            }
            System.out.println(JSON.toJSONString(messageBack));
        }
        return messageBack;
    }

    @Override
    public HashMap<String, Object> getUserByUuid(String uuid) {
        return messageMapper.selectUserInfoByUuid(uuid);
    }

    @Override
    public void queryMessagesListPage(PageAssistant pageAssistant) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", pageAssistant);
        if (pageAssistant.getParamMap() != null) {
            params.putAll(pageAssistant.getParamMap());
        }
        List<List<JSONObject>> jsonObjects = new ArrayList<List<JSONObject>>();
        // 查询所有的根节点
        List<Message> messages = messageMapper.selectMessageListPage(params);
        for (Message message : messages) {
            this.setMessageFileType(message);
            List<JSONObject> jsonObjectLeaves = new ArrayList<JSONObject>();
            JSONObject jsonObject = JSON.parseObject(message.toString());
            jsonObject.put("position", "left");
            jsonObject.put("messageDate",
                    DateUtil.getOracleDateToString(message.getMessageDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
            // 将根节点装入聊天组中
            this.setViaUsers(jsonObject);
            this.setMessageFileInfo(jsonObject);
            jsonObjectLeaves.add(jsonObject);
            // 把根节点下面的叶子节点全部都查出来
            List<Message> leaves = messageMapper.selectLeavesMessageList(String.valueOf(params.get("procInstId")), message.getMessageId());
            for (Message leave : leaves) {
                this.setMessageFileType(leave);
                JSONObject leaveObject = JSON.parseObject(leave.toString());
                if (leave.getCreatedBy().equals(message.getCreatedBy())) {
                    leaveObject.put("position", "left");
                } else {
                    leaveObject.put("position", "right");
                }
                leaveObject.put("messageDate",
                        DateUtil.getOracleDateToString(leave.getMessageDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
                this.setViaUsers(leaveObject);
                this.setMessageFileInfo(leaveObject);
                jsonObjectLeaves.add(leaveObject);
            }
            jsonObjects.add(jsonObjectLeaves);
        }
        pageAssistant.setList(jsonObjects);
    }

    @Override
    public List<Message> getMessageNotify() {
        String curUuid = UserUtil.getCurrentUserUuid();
        return messageMapper.selectMessageNotify(curUuid);
    }

    @Override
    public void queryViaUsers(PageAssistant page) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        if(page.getParamMap() != null){
            params.putAll(page.getParamMap());
        }
        String orderBy = page.getOrderBy();
        if(orderBy == null){
            orderBy = " account ";
        }
        params.put("orderBy", orderBy);
        List<Map<String, Object>> list = messageMapper.selectUserByCondition(params);
        page.setList(list);
    }

    @Resource
    private IFileService fileService;
    private void setMessageFileInfo(JSONObject message){
        if(message != null){
            if(message.getLong("messageFile") != null){
                Long fileId = message.getLong("messageFile");
                FileDto fileDto = fileService.getFile(String.valueOf(fileId));
                if(fileDto != null){
                    try{
                        List<FileDto> list = fileService.createFileList(fileDto.getDoctype(), fileDto.getDoccode(), fileDto.getPagelocation());
                        if(CollectionUtils.isNotEmpty(list)){
                            message.put("fileDto", list.get(0));
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
    }

    @Resource
    private IBaseMongo baseMongo;
    @Override
    public List<Map> getAttachmentType(String message_business_id, String message_type) {
        List<Map> list = new ArrayList<Map>();
        Map choice = new HashMap();
        choice.put("ITEM_CODE", "-1");
        choice.put("ITEM_NAME", "");
        list.add(0, choice);
        String serviceCode = "";
        String projectModelName = "";
        String functionType = "";
        Map<String, Object> mongo = null;
        if(Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(message_type)){
            functionType = "正式评审";
            mongo = this.baseMongo.queryById(message_business_id, Constants.RCM_FORMALASSESSMENT_INFO);
        }else if(Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(message_type)){
            functionType = "预评审";
            mongo = baseMongo.queryById(message_business_id, Constants.RCM_PRE_INFO);
        }else{
            return list;
        }
        if(mongo == null){
            return list;
        }
        JSONObject mongoJs = JSON.parseObject(JSON.toJSONString(mongo), JSONObject.class);
        JSONObject applyJs = mongoJs.getJSONObject("apply");
        if(applyJs != null){
            JSONArray  pmJss = applyJs.getJSONArray("projectModel");
            if(CollectionUtils.isEmpty(pmJss)){
                return list;
            }
            JSONObject pmJs = pmJss.getJSONObject(0);
            projectModelName = pmJs.getString("VALUE");
            JSONArray stJss = applyJs.getJSONArray("serviceType");
            if(CollectionUtils.isEmpty(stJss)){
                return list;
            }
            JSONObject stJs = stJss.getJSONObject(0);
            serviceCode = stJs.getString("KEY");
        }else{
            return list;
        }
        commonMethod cm = new commonMethod();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serviceCode", serviceCode);
        jsonObject.put("projectModelName", projectModelName);
        jsonObject.put("functionType", functionType);
        list = cm.getAttachmentType(JSON.toJSONString(jsonObject));
        choice = new HashMap();
        choice.put("ITEM_CODE", "-1");
        choice.put("ITEM_NAME", "");
        list.add(0, choice);
        return list;
    }
}
