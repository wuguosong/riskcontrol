package com.yk.notify.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import com.yk.exception.BusinessException;
import com.yk.message.dao.IMessageMapper;
import com.yk.message.entity.Message;
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
    public List<Notify> list(String business_module, String business_id, String notifyType) {
        return notifyMapper.selectNotifies(business_module, business_id, notifyType);
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
        // 如果待阅已经被点击，待阅->已阅，需要同步到统一代办平台
        if (Notify.STATUS_2.equals(notify.getNotifyStatus())) {
            TodoClient todoClient = TodoClient.getInstance();
            List<HashMap<String, Object>> projects = messageMapper.selectProjectByTypeAndId(notify.getBusinessModule(), notify.getBusinessId());
            if (CollectionUtils.isEmpty(projects)) {
                throw new BusinessException("只会人同步统一代办失败：相关业务数据为空！");
            }
            Map<String, Object> project = projects.get(0);
            String title = String.valueOf(project.get("title"));
            String sender = String.valueOf(project.get("sender"));
            String createdTime = DateUtil.getDateToString(DateUtil.getCurrentDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
            TodoInfo todoInfo = new TodoInfo(notify.getBusinessId(), title, createdTime, notify.getNotifyUrl(), notify.getNotifyUser(), sender);
            TodoBack todoBack = todoClient.sendTodo_ToRead2Done(todoInfo);
            if (TodoBack.CODE_SUCCESS.equals(todoBack.getCode())) {
                notify.setPortalStatus(Notify.STATUS_2);
                notifyMapper.modifyNotify(notify);
            }
        } else {
            notifyMapper.modifyNotify(notify);
        }
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
    public List<Notify> save(String business_module, String business_id, String notifies_user, String notifyType, String associateId, String notifyComments, boolean isCheck) {
        if (StringUtils.isNotEmpty(notifies_user)) {
            String[] notifiesUser = notifies_user.split(",");
            if (isCheck) {
                // 检测保存的用户是否已经存在
                List<Notify> list = notifyMapper.selectNotifies(business_module, business_id, notifyType);
                JSONObject js = new JSONObject();
                for (Notify notify : list) {
                    js.put(notify.getNotifyUser(), notify);
                }
                int i = 0;
                for (String notifyUser : notifiesUser) {
                    if (js.get(notifyUser) != null) {
                        notifiesUser[i] = "";
                        i++;
                    }
                }
            }
            Map<String, Object> params = new HashMap<String, Object>();
            String notifyCreated = UserUtil.getCurrentUserUuid();
            String notifyCreatedName = UserUtil.getCurrentUserName();
            String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
            String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
            String url = prefix + business_id + "/" + suffix;
            for (String notifyUser : notifiesUser) {
                if (StringUtils.isNotBlank(notifyUser)) {
                    params.put("UUID", notifyUser);
                    Map<String, Object> user = userMapper.selectAUser(params);
                    if (user != null) {
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
                        notify.setNotifyComments(notifyComments);
                        notify.setAssociateId(associateId);
                        notify.setNotifyType(notifyType);
                        notifyMapper.insertNotify(notify);
                    }
                }
            }
            return notifyMapper.selectNotifies(business_module, business_id, notifyType);
        }
        return new ArrayList<Notify>();
    }

    // notify 功能调用
    @Override
    public List<Notify> save(String business_module, String business_id, String notifies_user, boolean isCheck) {
        return this.save(business_module, business_id, notifies_user, Notify.TYPE_NOTIFY, null, null, true);
    }

    @Override
    public List<Notify> sendToPortal(String business_module, String business_id, boolean isTodo, boolean isRead, String notifyType) {
        List<Notify> notifies = notifyMapper.selectNotifies(business_module, business_id, notifyType);
        if (CollectionUtils.isNotEmpty(notifies)) {
            boolean portalOpen = prop.getBoolean("notify.send.portal.open", false);
            boolean todoOpen = prop.getBoolean("notify.send.portal.todo.open", false);
            boolean readOpen = prop.getBoolean("notify.send.portal.read.open", false);
            if (portalOpen) {
                TodoClient todoClient = TodoClient.getInstance();
                List<HashMap<String, Object>> projects = messageMapper.selectProjectByTypeAndId(business_module, business_id);
                if (CollectionUtils.isEmpty(projects)) {
                    throw new BusinessException("只会人同步统一代办失败：相关业务数据为空！");
                }
                Map<String, Object> project = projects.get(0);
                String title = String.valueOf(project.get("title"));
                String sender = String.valueOf(project.get("sender"));
                String createdTime = DateUtil.getDateToString(DateUtil.getCurrentDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
                String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
                String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
                String url = prefix + business_id + "/" + suffix;
                // 构造代办信息
                TodoInfo todoInfo = new TodoInfo(business_id, title, createdTime, url, "", sender);
                for (Notify notify : notifies) {
                    if (Notify.STATUS_0.equalsIgnoreCase(notify.getPortalStatus())) {
                        todoInfo.setOwner(notify.getNotifyUser());
                        if (isTodo && todoOpen) {// 代办同步
                            TodoBack todoBack = todoClient.sendTodo_ToDo(todoInfo);
                            if (todoBack != null) {// 更新
                                if (TodoBack.CODE_SUCCESS.equalsIgnoreCase(todoBack.getCode())) {
                                    notify.setPortalStatus(Notify.STATUS_1);// 已发送
                                    notifyMapper.modifyNotify(notify);
                                }
                            }
                        }
                        if (isRead && readOpen) {// 待阅同步
                            TodoBack todoBack = todoClient.sendTodo_ToRead(todoInfo);
                            if (todoBack != null) {
                                if (todoBack != null) {// 更新
                                    if (TodoBack.CODE_SUCCESS.equalsIgnoreCase(todoBack.getCode())) {
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
    public List<Notify> sendToMessage(String business_module, String business_id, String notifyType) {
        List<Notify> notifies = notifyMapper.selectNotifies(business_module, business_id, notifyType);
        if (CollectionUtils.isNotEmpty(notifies)) {
            boolean messageOpen = prop.getBoolean("notify.send.message.open", false);
            boolean dtOpen = prop.getBoolean("notify.send.message.dt.open", false);
            if (messageOpen) {
                List<HashMap<String, Object>> projects = messageMapper.selectProjectByTypeAndId(business_module, business_id);
                if (CollectionUtils.isEmpty(projects)) {
                    throw new BusinessException("只会人同步统一代办失败：相关业务数据为空！");
                }
                Map<String, Object> project = projects.get(0);
                String title = String.valueOf(project.get("title"));
                String prefix = prop.get("agency.wsdl.prefix.url." + business_module);
                String suffix = JaXmlBeanUtil.encodeScriptUrl(prop.get("agency.wsdl.suffix.url"));
                String url = prefix + business_id + "/" + suffix;
                StringBuffer userSb = new StringBuffer();
                Map<String, Object> params = new HashMap<String, Object>();
                for (Notify notify : notifies) {
                    if (Notify.STATUS_0.equalsIgnoreCase(notify.getMessageStatus())) {
                        params.put("UUID", notify.getNotifyUser());
                        Map<String, Object> user = userMapper.selectAUser(params);
                        if (user != null) {
                            userSb.append(String.valueOf(user.get("ACCOUNT")));
                            userSb.append(",");
                        }
                    }
                }
                if (StringUtils.isNotBlank(userSb)) {
                    MessageClient messageClient = new MessageClient();
                    MessageBack messageBack = messageClient.sendDtLink(null, userSb.substring(0, userSb.lastIndexOf(",")).replace(",", "|"), url, title, url, title);
                    if (messageBack != null) {
                        if (messageBack.getCode() == 0) {
                            for (Notify notify : notifies) {
                                if (Notify.STATUS_0.equalsIgnoreCase(notify.getMessageStatus())) {
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
        for (JSONObject js : list) {
            if (Notify.STATUS_1.equalsIgnoreCase(js.getString("NOTIFY_STATUS"))) {
                myReadingList.add(js);
            } else if (Notify.STATUS_2.equalsIgnoreCase(js.getString("NOTIFY_STATUS"))) {
                myReadList.add(js);
            }
        }
        jsonObject.put("myReadingList", myReadingList);
        jsonObject.put("myReadList", myReadList);
        jsonObject.put("myReadingCount", myReadingList.size());
        jsonObject.put("myReadCount", myReadList.size());
        this.initAnchorPoint(jsonObject);
        return jsonObject;
    }

    @Override
    public void queryNotifyInfoPage(PageAssistant page) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        if (null != page.getParamMap() && page.getParamMap().size() > 0) {
            params.putAll(page.getParamMap());
        }
        params.put("curUserUuid", UserUtil.getCurrentUserUuid());
        List<Map<String, Object>> list = notifyMapper.selectNotifyInfoPage(params);
        this.initAnchorPoint(list);
        page.setList(list);
    }

    @Override
    public List<Notify> delete(String business_module, String business_id, String notify_user, String notifyType) {
        JSONObject jsonObject = JSON.parseObject(notify_user, JSONObject.class);
        String curId = UserUtil.getCurrentUserUuid();
        notifyMapper.removeNotifyByJson(business_module, business_id, jsonObject.getString("VALUE"), curId, notifyType);
        List<Notify> list = notifyMapper.selectNotifies(business_module, business_id, notifyType);
        return list;
    }

    /**
     * 初始化页面锚点信息
     *
     * @param pageList
     */
    private void initAnchorPoint(List<Map<String, Object>> pageList) {
        if (CollectionUtils.isNotEmpty(pageList)) {
            for (Map<String, Object> objectMap : pageList) {
                JSONObject objectMapJs = JSON.parseObject(JSON.toJSONString(objectMap), JSONObject.class);
                objectMapJs = this.judgeJsonObject(objectMapJs, pageList.indexOf(objectMap), pageList.size());
                Map<String, Object> retMap = JSON.parseObject(JSON.toJSONString(objectMapJs), HashMap.class);
                objectMap.putAll(retMap);
            }
        }
    }

    /**
     * 初始化页面锚点信息
     *
     * @param jsonObject
     */
    private void initAnchorPoint(JSONObject jsonObject) {
        List<JSONObject> myReadingList = jsonObject.getJSONArray("myReadingList").toJavaList(JSONObject.class);
        if (CollectionUtils.isNotEmpty(myReadingList)) {
            for (JSONObject object : myReadingList) {
                this.judgeJsonObject(object, myReadingList.indexOf(object), myReadingList.size());
            }
        }
        List<JSONObject> myReadList = jsonObject.getJSONArray("myReadList").toJavaList(JSONObject.class);
        if (CollectionUtils.isNotEmpty(myReadList)) {
            for (JSONObject object : myReadList) {
                this.judgeJsonObject(object, myReadingList.indexOf(object), myReadList.size());
            }
        }
    }

    /**
     * 判断
     *
     * @param jsonObject
     * @param idx        索引位置
     */
    private JSONObject judgeJsonObject(JSONObject jsonObject, int idx, int all) {
        jsonObject.put("AnchorPointPageTab", jsonObject.getString("NOTIFY_TYPE"));
        if (Notify.TYPE_MESSAGE.equals(jsonObject.getString("NOTIFY_TYPE"))) {
            String messageId = jsonObject.getString("ASSOCIATE_ID");
            if (StringUtils.isNotBlank(messageId)) {
                Message message = messageMapper.selectMessageById(new Long(messageId));
                Long parentId = message.getParentId();
                if(new Long(0).compareTo(parentId) == -1){
                    Message parent = messageMapper.selectMessageById(parentId);
                    jsonObject.put("AnchorPointMessageSubject", parent.getMessageId());// 主题id
                }else{
                    jsonObject.put("AnchorPointMessageSubject", messageId);// 主题id
                }
                if (message != null) {
                    jsonObject.put("AnchorPointMessageTab", message.getMessageScreenType());
                    jsonObject.put("AnchorPointMessageId", messageId);
                }
            }
            jsonObject.put("AnchorPointMessageIdx", idx);
            jsonObject.put("AnchorPointMessagePageNo", this.getPageNo(idx, all));
        }
        return jsonObject;
    }

    /**
     * 获取页面
     *
     * @param idx
     * @param all
     * @return
     */
    private int getPageNo(int idx, int all) {
        idx += 1;
        int pageNo = 1;
        int page = 5;// 默认为5
        if (all < page) {
            return pageNo;
        }
        if (all % page == 0) {
            pageNo = all / page;
        } else {
            pageNo = all / page + 1;
        }
        return pageNo;
    }
}
