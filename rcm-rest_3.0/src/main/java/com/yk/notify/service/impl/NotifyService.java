package com.yk.notify.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import com.yk.exception.BusinessException;
import com.yk.message.dao.IMessageMapper;
import com.yk.notify.dao.INotifyMapper;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import com.yk.power.dao.IUserMapper;
import com.yk.sign.dao.ISignMapper;
import common.PageAssistant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.DateUtil;
import util.UserUtil;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;
import ws.todo.client.TodoClient;
import ws.todo.entity.TodoBack;
import ws.todo.entity.TodoInfo;
import ws.todo.utils.JaXmlBeanUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/27.
 */
@Service
@Transactional
public class NotifyService implements INotifyService {
    @Resource
    private INotifyMapper notifyMapper;
    @Resource
    private IUserMapper userMapper;
    @Resource
    private Notify notify;
    @Resource
    private ISignMapper signMapper;
    @Resource
    private IMessageMapper messageMapper;
    // 配置文件
    private Prop prop = PropKit.use("wsdl_conf.properties");
    @Override
    public List<Notify> list(String business_module, String business_id) {
        return notifyMapper.selectNotifies(business_module, business_id);
    }

    @Override
    public Notify get(String notify_id) {
        if (StringUtils.isBlank(notify_id)) {
            throw new BusinessException("NOTIFY_ID NOT NULL!");
        }
        return notifyMapper.selectNotifyById(notify_id);
    }

    @Override
    public void update(Notify notify) {
        notifyMapper.modifyNotify(notify);
    }

    @Override
    public void save(Notify notify) {
        notifyMapper.insertNotify(notify);
    }

    @Override
    public void delete(String notify_id) {
        if (StringUtils.isBlank(notify_id)) {
            throw new BusinessException("NOTIFY_ID NOT NULL!");
        }
        notifyMapper.removeNotify(notify_id);
    }

    @Override
    public List<Notify> save(String business_module, String business_id, String notifies_user) {
        // notifyMapper.removeNotifyMultipleParameters(business_module, business_id);
        if(StringUtils.isNotEmpty(notifies_user)){
            String[] notifiesUser = notifies_user.split(",");
            Map<String, Object> params = new HashMap<String, Object>();
            String notifyCreated = UserUtil.getCurrentUserUuid();
            String notifyCreatedName = UserUtil.getCurrentUserName();
            String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
            String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
            String url = prefix + business_id + "/" + suffix;
            for(String notifyUser : notifiesUser){
                params.put("UUID", notifyUser);
                Map<String, Object> user = userMapper.selectAUser(params);
                if(user != null){
                    notify.setNotifyUser(notifyUser);
                    notify.setNotifyUserName(String.valueOf(user.get("NAME")));
                    notify.setNotifyCreated(notifyCreated);
                    notify.setNotifyCreatedName(notifyCreatedName);
                    notify.setBusinessModule(business_module);
                    notify.setBusinessId(business_id);
                    notify.setPortalStatus(Notify.STATUS_0);
                    notify.setMessageStatus(Notify.STATUS_0);
                    notify.setNotifyStatus(Notify.STATUS_0);
                    notify.setNotifyUrl(url);
                    notify.setNotifyComments(null);
                    notify.setAssociateId(null);
                    notifyMapper.insertNotify(notify);
                }
            }
            return notifyMapper.selectNotifies(business_module, business_id);
        }
        return new ArrayList<Notify>();
    }


    @Override
    public List<Notify> sendToPortal(String business_module, String business_id, boolean isTodo, boolean isRead) {
        List<Notify> notifies = notifyMapper.selectNotifies(business_module, business_id);
        if(CollectionUtils.isNotEmpty(notifies)){
            boolean portalOpen = prop.getBoolean("notify.send.portal.open", false);
            boolean todoOpen = prop.getBoolean("notify.send.portal.todo.open", false);
            boolean readOpen = prop.getBoolean("notify.send.portal.read.open", false);
            if(portalOpen){
                TodoClient todoClient = TodoClient.getInstance();
                List<Map<String, Object>> projects = messageMapper.selectProjectByTypeAndId(business_module, business_id);
                if(CollectionUtils.isEmpty(projects)){
                    throw new BusinessException("只会人同步统一代办失败：相关业务数据为空！");
                }
                Map<String, Object> project = projects.get(0);
                String title = String.valueOf(project.get("title"));
                String sender = String.valueOf(project.get("sender"));;
                String createdTime = DateUtil.getDateToString(DateUtil.getCurrentDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
                String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
                String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
                String url = prefix + business_id + "/" + suffix;
                // 构造代办信息
                TodoInfo todoInfo = new TodoInfo();
                todoInfo.setTitle(title);
                todoInfo.setUrl(url);
                todoInfo.setSender(sender);
                todoInfo.setCreatedTime(createdTime);
                todoInfo.setBusinessId(business_id);
                for(Notify notify : notifies){
                    if(Notify.STATUS_0.equalsIgnoreCase(notify.getPortalStatus())){
                        todoInfo.setOwner(notify.getNotifyUser());
                        if(isTodo && todoOpen){// 代办同步
                            TodoBack todoBack = todoClient.sendTodo_ToDo(todoInfo);
                            if(todoBack != null){// 更新
                                if(TodoBack.CODE_SUCCESS.equalsIgnoreCase(todoBack.getCode())){
                                    notify.setPortalStatus(Notify.STATUS_1);// 已发送
                                    notifyMapper.modifyNotify(notify);
                                }
                            }
                        }
                        if(isRead && readOpen){// 待阅同步
                            TodoBack todoBack = todoClient.sendTodo_ToRead(todoInfo);
                            if(todoBack != null){
                                if(todoBack != null){// 更新
                                    if(TodoBack.CODE_SUCCESS.equalsIgnoreCase(todoBack.getCode())){
                                        notify.setPortalStatus(Notify.STATUS_1);// 已发送
                                        notify.setNotifyStatus(Notify.STATUS_1);// 待阅
                                        notifyMapper.modifyNotify(notify);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return notifies;
    }

    @Override
    public List<Notify> sendToMessage(String business_module, String business_id) {
        List<Notify> notifies = notifyMapper.selectNotifies(business_module, business_id);
        if(CollectionUtils.isNotEmpty(notifies)){
            boolean messageOpen = prop.getBoolean("notify.send.message.open", false);
            boolean dtOpen = prop.getBoolean("notify.send.message.dt.open", false);
            if(messageOpen){
                List<Map<String, Object>> projects = messageMapper.selectProjectByTypeAndId(business_module, business_id);
                if(CollectionUtils.isEmpty(projects)){
                    throw new BusinessException("只会人同步统一代办失败：相关业务数据为空！");
                }
                Map<String, Object> project = projects.get(0);
                String title = String.valueOf(project.get("title"));
                String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
                String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
                String url = prefix + business_id + "/" + suffix;
                StringBuffer userSb = new StringBuffer();
                Map<String, Object> params = new HashMap<String, Object>();
                for(Notify notify : notifies){
                    if(Notify.STATUS_0.equalsIgnoreCase(notify.getMessageStatus())){
                        params.put("UUID", notify.getNotifyUser());
                        Map<String, Object> user = userMapper.selectAUser(params);
                        if (user != null) {
                            userSb.append(String.valueOf(user.get("ACCOUNT")));
                            userSb.append(",");
                        }
                    }
                }
                if(StringUtils.isNotBlank(userSb)){
                    MessageClient messageClient = new MessageClient();
                    MessageBack messageBack = messageClient.sendDtLink(null, userSb.substring(0, userSb.lastIndexOf(",")).replace(",", "|"), url, title, url, title);
                    if(messageBack != null){
                        if(messageBack.getCode() == 0){
                            for(Notify notify : notifies){
                                if(Notify.STATUS_0.equalsIgnoreCase(notify.getMessageStatus())){
                                    notify.setMessageStatus(Notify.STATUS_1);// 已发送
                                    notifyMapper.modifyNotify(notify);
                                }
                            }
                        }
                    }
                }
            }
        }
        return notifies;
    }

    @Override
    public JSONObject queryNotifyInfo() {
        String curUserUuid = UserUtil.getCurrentUserUuid();
        List<JSONObject> list = notifyMapper.selectNotifyInfo(curUserUuid);
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> myReadingList = new ArrayList<JSONObject>();
        List<JSONObject> myReadList = new ArrayList<JSONObject>();
        for(JSONObject js : list){
            if(Notify.STATUS_1.equalsIgnoreCase(js.getString("NOTIFY_STATUS"))){
                myReadingList.add(js);
            }else if(Notify.STATUS_2.equalsIgnoreCase(js.getString("NOTIFY_STATUS"))){
                myReadList.add(js);
            }
        }
        jsonObject.put("myReadingList", myReadingList);
        jsonObject.put("myReadList", myReadList);
        jsonObject.put("myReadingCount", myReadingList.size());
        jsonObject.put("myReadCount", myReadList.size());
        return jsonObject;
    }

    @Override
    public void queryNotifyInfoPage(PageAssistant page) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        if(null != page.getParamMap() && page.getParamMap().size() > 0){
            params.putAll(page.getParamMap());
        }
        params.put("curUserUuid", UserUtil.getCurrentUserUuid());
        List<Map<String, Object>> list = notifyMapper.selectNotifyInfoPage(params);
        page.setList(list);
    }

    @Override
    public List<Notify> delete(String business_module, String business_id, String notify_user) {
        JSONObject jsonObject = JSON.parseObject(notify_user, JSONObject.class);
        String curId = UserUtil.getCurrentUserUuid();
        notifyMapper.removeNotifyByJson(business_module, business_id, jsonObject.getString("VALUE"), curId);
        List<Notify> list = notifyMapper.selectNotifies(business_module, business_id);
        return list;
    }
}
