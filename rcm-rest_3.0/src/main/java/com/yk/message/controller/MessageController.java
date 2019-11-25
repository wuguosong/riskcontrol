package com.yk.message.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import util.UserUtil;
import ws.msg.client.MessageBack;
import ws.msg.client.MessageClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/1/25.
 */
@Controller
@RequestMapping("message")
public class MessageController {
    private Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Resource
    private IMessageService messageService;
    @Resource
    private INotifyService notifyService;

    /**
     * 将留言信息以树的方式展示,每次只根据父节点查询其以下的所有子节点
     *
     * @param procInstId 流程实例ID
     * @param parentId   父节点ID
     * @return Result
     */
    @RequestMapping(value = "tree", method = RequestMethod.POST)
    @ResponseBody
    public Result tree(String procInstId, String parentId) {
        Result result = new Result();
        try {
            List<Message> list = messageService.getMessageTree(procInstId, new Long(parentId));
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(list);
            result.setResult_name("获取留言树成功!");
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            e.printStackTrace();
            result.setResult_name("获取留言树失败!" + e.getMessage());
            logger.error("获取留言树失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取一个留言下的所有叶子留言
     *
     * @param parentId 父留言ID
     * @return
     */
    @RequestMapping(value = "leaves", method = RequestMethod.POST)
    @ResponseBody
    public Result leaves(String parentId) {
        Result result = new Result();
        try {
            List<Message> list = messageService.getMessageChildren(new Long(parentId));
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(list);
            result.setResult_name("获取留言叶子成功!");
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("获取留言叶子失败!" + e.getMessage());
            e.printStackTrace();
            logger.error("获取留言叶子失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * 保存
     *
     * @param message 留言信息实体
     * @return Result
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result addMessage(Message message) {
        Result result = new Result();
        message.setCreatedBy(UserUtil.getCurrentUserUuid());
        message.setCreatedName(UserUtil.getCurrentUserName());
        message.setPushFlag("N");
        message.setReadFlag("N");
        try {
            message = messageService.save(message);
            String users = messageService.getNotifyUsers(message);// 同一个主题下的所有用户，同时作为已经发送过的集合校验容器
            // 推送钉钉消息：向同一个主题的其他人发送钉钉
            //this.executeMessage(message, users);
            // 推送待阅消息：向同一个主题的其他人发送待阅
            //this.executePortal(message, users);
            // 向特定的业务人员推送钉钉和待阅，如果特定人员已经包含在前面的推送中，则不再推送。
            //this.executeBusinessMessageAndPortal(message, users);// 向特定的业务人员发送待阅（同时包含钉钉消息）
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(message);
            result.setResult_name("保存留言成功!");
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            e.printStackTrace();
            result.setResult_name("保存留言失败!" + e.getMessage());
            logger.error("保存留言失败!" + e.getMessage());
        }
        return result;
    }


    /**
     * 更新
     *
     * @param message 留言信息实体
     * @return Result
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateMessage(Message message) {
        Result result = new Result();
        try {
            if (message != null) {
                messageService.update(message);
                message = messageService.get(message.getMessageId());
                result.setSuccess(true);
                result.setResult_code(Constants.S);
                result.setResult_data(message);
                result.setResult_name("更新留言成功!");
            }
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("更新留言失败!" + e.getMessage());
            e.printStackTrace();
            logger.error("更新留言失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取
     *
     * @param messageId 留言ID
     * @return Result
     */
    @RequestMapping(value = "get", method = RequestMethod.POST)
    @ResponseBody
    public Result getMessage(Long messageId) {
        Result result = new Result();
        if (messageId != null) {
            try {
                Message message = messageService.get(messageId);
                result.setSuccess(true);
                result.setResult_code(Constants.S);
                result.setResult_data(message);
                result.setResult_name("获取留言成功!");
            } catch (Exception e) {
                result.setResult_code(Constants.R);
                result.setSuccess(false);
                result.setResult_data(e);
                result.setResult_name("获取留言失败!" + e.getMessage());
                e.printStackTrace();
                logger.error("获取留言失败!" + e.getMessage());
            }
        } else {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_name("没有获取到留言ID!");
            logger.error("没有获取到留言ID!");
        }
        return result;
    }

    /**
     * 删除
     *
     * @param messageId 留言ID
     * @return Result
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteMessage(Long messageId) {
        Result result = new Result();
        try {
            Message message = messageService.delete(messageId);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(message);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name(e.getMessage());
            logger.error("删除留言失败!" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新是否已读
     *
     * @param messageId 留言ID
     * @return Result
     */
    @RequestMapping(value = "read", method = RequestMethod.POST)
    @ResponseBody
    public Result updateRead(Long messageId) {
        Result result = new Result();
        try {
            Message message = messageService.updateRead(messageId);
            result.setSuccess(true);
            result.setResult_code(Constants.S);
            result.setResult_data(message);
            result.setResult_name("更新已阅成功!");
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("更新已阅失败!" + e.getMessage());
            logger.error("更新已阅失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param procInstId
     * @return
     */
    @RequestMapping(value = "findMessages", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<Message>> findMessages(String procInstId) {
        Map<String, List<Message>> map = null;
        try {
            map = messageService.findMessages(procInstId, UserUtil.getCurrentUserUuid());
            logger.info("获取信息成功!");
        } catch (Exception e) {
            logger.error("更新已阅失败!" + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param procInstId
     * @return
     */
    @RequestMapping(value = "messages", method = RequestMethod.POST)
    @ResponseBody
    public List<JSONObject> messages(String procInstId) {
        List<JSONObject> messages = null;
        try {
            messages = messageService.messages(procInstId, UserUtil.getCurrentUserUuid());
            logger.info("获取信息成功!");
        } catch (Exception e) {
            logger.error("更新已阅失败!" + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * @param procInstId
     * @return
     */
    @RequestMapping(value = "queryMessagesList", method = RequestMethod.POST)
    @ResponseBody
    public List<List<JSONObject>> queryMessagesList(String procInstId, String parentId, String queryParams) {
        List<List<JSONObject>> messages = null;
        try {
            messages = messageService.queryMessagesList(procInstId, new Long(parentId), queryParams);
            logger.info("获取信息成功!");
        } catch (Exception e) {
            logger.error("获取信息失败!" + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    @RequestMapping(value = "queryMessagesListPage", method = RequestMethod.POST)
    @ResponseBody
    public Result queryMessagesListPage(String page, String queryParams) {
        Result result = new Result();
        try {
            PageAssistant pageAssistant = new PageAssistant(page);
            if (StringUtils.isNotBlank(queryParams)) {
                Map _paramsMap = JSON.parseObject(queryParams, HashMap.class);
                Map<String, Object> paramMap = pageAssistant.getParamMap();
                if (paramMap == null) {
                    paramMap = new HashMap<String, Object>();
                }
                paramMap.putAll(_paramsMap);
                pageAssistant.setParamMap(paramMap);
            }
            System.out.println("***********_query_params_********" + queryParams);
            messageService.queryMessagesListPage(pageAssistant);
            result.setResult_data(pageAssistant);
            logger.info("获取信息成功!");
        } catch (Exception e) {
            logger.error("获取信息失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 信息分享
     *
     * @param messageId
     * @param shareUsers
     * @return
     */
    @RequestMapping(value = "share", method = RequestMethod.POST)
    @ResponseBody
    public Result share(Long messageId, String shareUsers, String type) {
        Result result = new Result();
        try {
            MessageBack messageBack = messageService.shareMessage(messageId, shareUsers, type);
            if (messageBack != null) {
                result.setSuccess(true);
                result.setResult_data(messageBack);
                result.setResult_code(Constants.S);
                result.setResult_name(messageBack.getMessage());
            } else {
                result.setSuccess(false);
                result.setResult_code(Constants.R);
                result.setResult_name(messageBack.getMessage());
            }
            result.setResult_data(messageBack);
        } catch (Exception e) {
            result.setResult_code(Constants.R);
            result.setSuccess(false);
            result.setResult_data(e);
            result.setResult_name("分享信息失败!" + e.getMessage());
            logger.error("分享信息失败!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getUserInfo(String uuid) {
        HashMap<String, Object> userInfo = null;
        try {
            userInfo = messageService.getUserByUuid(uuid);
        } catch (Exception e) {
            logger.error("分享信息失败!" + e.getMessage());
            e.printStackTrace();
        }
        return userInfo;
    }

    @RequestMapping(value = "getMessageNotify", method = RequestMethod.POST)
    @ResponseBody
    public List<Message> getMessageNotify() {
        return messageService.getMessageNotify();
    }

    /**
     * 用户选择框，分页查询用户
     *
     * @param request
     * @return
     */
    @RequestMapping("/queryViaUsers")
    @ResponseBody
    public Result queryViaUsers(HttpServletRequest request, String message_business_id, String message_type) {
        Result result = new Result();
        PageAssistant page = new PageAssistant(request.getParameter("page"));
        Map<String, Object> paramMap = page.getParamMap();
        paramMap.put("messageBusinessId", message_business_id);
        paramMap.put("messageType", message_type);
        page.setParamMap(paramMap);
        messageService.queryViaUsers(page);
        result.setResult_data(page);
        return result;
    }

    /**
     * 获取附件类型
     *
     * @param message_business_id 留言业务ID
     * @param message_type        留言类型
     * @return List<Map>
     */
    @RequestMapping("/getAttachmentType")
    @ResponseBody
    public List<Map> getAttachmentType(String message_business_id, String message_type) {
        return messageService.getAttachmentType(message_business_id, message_type);
    }

    /**
     * 获取附件位置序列号
     *
     * @param docType      附件类型
     * @param docCode      附件业务Id
     * @param pageLocation 附件位置
     * @return int 附件序列号
     */
    @RequestMapping("/getPageLocationSequenceNumber")
    @ResponseBody
    public int getPageLocationSequenceNumber(String docType, String docCode, String pageLocation) {
        int pageLocationSequenceNumber = messageService.getPageLocationSequenceNumber(docType, docCode, pageLocation);
        return pageLocationSequenceNumber;
    }

    /**
     * 获取项目
     *
     * @param type 项目类型
     * @param id   项目id
     * @return 项目信息
     */
    @RequestMapping("/getProject")
    @ResponseBody
    public HashMap<String, Object> getProject(String type, String id) {
        HashMap<String, Object> map = null;
        try {
            map = messageService.getProject(type, id);
        } catch (Exception e) {
            LoggerFactory.getLogger(MessageController.class).error(e.getMessage());
        }
        return map;
    }


    /**
     * @param dictCode
     * @return
     */
    @RequestMapping("/findDictItemByDictCode")
    @ResponseBody
    public List<Map<String, Object>> findDictItemByDictCode(String dictCode) {
        List<Map<String, Object>> list = null;
        try {
            list = messageService.findDictItemByDictCode(dictCode);
        } catch (Exception e) {
            LoggerFactory.getLogger(MessageController.class).error(e.getMessage());
        }
        return list;
    }

    /**
     * 获取留言在审批过程中的开启权限
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return Task
     */
    @RequestMapping("/getMessageOpenAuthorityInApproval")
    @ResponseBody
    public JSONObject getMessageOpenAuthorityInApproval(String processKey, String businessKey) {
        JSONObject result = new JSONObject();
        try {
            List<JSONObject> tasks = messageService.getMessageOpenAuthorityInApproval(processKey, businessKey);
            result.put("data", tasks);
            result.put("success", true);
        } catch (Exception e) {
            result.put("data", null);
            result.put("success", false);
            LoggerFactory.getLogger(MessageController.class).error(e.getMessage());
        }
        return result;
    }

    /**
     * 执行业务模块需求
     *
     * @param message
     * @param checkUsers
     */
    private void executeBusinessMessageAndPortal(Message message, String checkUsers) {
        String curUserId = UserUtil.getCurrentUserUuid();
        if (StringUtils.isBlank(checkUsers)) {
            checkUsers = curUserId;
        }else{
            StringBuffer sb = new StringBuffer(checkUsers);
            sb.append(",");
            sb.append(curUserId);
            checkUsers = sb.toString();
        }
        boolean open = messageService.getOpenBusinessExecute();
        if (open) {
            // 获取业务单据
            JSONObject mongoData = messageService.getBusinessMongoDbData(message.getProcInstId(), message.getMessageType());
            if (mongoData != null) {
                // 正式评审
                if (Constants.PROCESS_KEY_FormalAssessment.equalsIgnoreCase(message.getMessageType())) {
                    // 项目归属负责人
                    JSONObject companyHeader = mongoData.getJSONObject("companyHeader");
                    String companyHeaderValue = "";
                    if (companyHeader != null) {
                        companyHeaderValue = companyHeader.getString("VALUE");
                    }
                    // 获取投资经理和项目归属负责人
                    if ("review".equals(message.getMessageScreenType())) {
                        // 投资经理
                        String investmentManagerValue = "";
                        JSONObject investmentManager = mongoData.getJSONObject("investmentManager");
                        if (investmentManager != null) {
                            investmentManagerValue = investmentManager.getString("VALUE");
                        }
                        if (StringUtils.isNotBlank(investmentManagerValue)) {
                            if (!checkUsers.contains(investmentManagerValue)) {
                                this.executeMessage(message, investmentManagerValue);
                                this.executePortal(message, investmentManagerValue);
                            }
                        }
                        // 项目归属负责人
                        if (StringUtils.isNotBlank(companyHeaderValue)) {
                            if (!checkUsers.contains(companyHeaderValue)) {
                                this.executeMessage(message, companyHeaderValue);
                                this.executePortal(message, companyHeaderValue);
                            }
                        }
                    }
                    // 获取基层法务和项目归属负责人
                    if ("legal".equals(message.getMessageScreenType())) {
                        // 基层法务
                        String grassrootsLegalStaffValue = "";
                        JSONObject grassrootsLegalStaff = mongoData.getJSONObject("grassrootsLegalStaff");
                        if (grassrootsLegalStaff != null) {
                            grassrootsLegalStaffValue = grassrootsLegalStaff.getString("VALUE");
                        }
                        if (StringUtils.isNotBlank(grassrootsLegalStaffValue)) {
                            if (!checkUsers.contains(grassrootsLegalStaffValue)) {
                                this.executeMessage(message, grassrootsLegalStaffValue);
                                this.executePortal(message, grassrootsLegalStaffValue);
                            }
                        }
                        // 项目归属负责人
                        if (StringUtils.isNotBlank(companyHeaderValue)) {
                            if (!checkUsers.contains(companyHeaderValue)) {
                                this.executeMessage(message, companyHeaderValue);
                                this.executePortal(message, companyHeaderValue);
                            }
                        }
                    }
                    // 投标评审
                } else if (Constants.PROCESS_KEY_PREREVIEW.equalsIgnoreCase(message.getMessageType())) {
                    // 项目归属负责人
                    JSONObject companyHeader = mongoData.getJSONObject("companyHeader");
                    String companyHeaderValue = "";
                    if (companyHeader != null) {
                        companyHeaderValue = companyHeader.getString("VALUE");
                    }
                    // 获取投资经理和项目归属负责人
                    if ("review".equals(message.getMessageScreenType())) {
                        // 投资经理
                        JSONObject investmentManager = mongoData.getJSONObject("investmentManager");
                        String investmentManagerValue = "";
                        if (investmentManager != null) {
                            investmentManagerValue = investmentManager.getString("VALUE");
                        }
                        if (StringUtils.isNotBlank(investmentManagerValue)) {
                            if (!checkUsers.contains(investmentManagerValue)) {
                                this.executeMessage(message, investmentManagerValue);
                                this.executePortal(message, investmentManagerValue);
                            }
                        }
                        // 项目归属负责人
                        if (StringUtils.isNotBlank(companyHeaderValue)) {
                            if (!checkUsers.contains(companyHeaderValue)) {
                                this.executeMessage(message, companyHeaderValue);
                                this.executePortal(message, companyHeaderValue);
                            }
                        }
                    }
                    // 获取基层法务和项目归属负责人
                    if ("legal".equals(message.getMessageScreenType())) {
                        // 基层法务
                        JSONObject grassrootsLegalStaff = mongoData.getJSONObject("grassrootsLegalStaff");
                        String grassrootsLegalStaffValue = "";
                        if (grassrootsLegalStaff != null) {
                            grassrootsLegalStaffValue = grassrootsLegalStaff.getString("VALUE");
                        }
                        if (StringUtils.isNotBlank(grassrootsLegalStaffValue)) {
                            if (!checkUsers.contains(grassrootsLegalStaffValue)) {
                                this.executeMessage(message, grassrootsLegalStaffValue);
                                this.executePortal(message, grassrootsLegalStaffValue);
                            }
                        }
                        // 项目归属负责人
                        if (StringUtils.isNotBlank(companyHeaderValue)) {
                            if (!checkUsers.contains(companyHeaderValue)) {
                                this.executeMessage(message, companyHeaderValue);
                                this.executePortal(message, companyHeaderValue);
                            }
                        }
                    }
                    // 其它评审
                } else if (Constants.PROCESS_KEY_BULLETIN.equalsIgnoreCase(message.getMessageType())) {
                    JSONObject unitPerson = mongoData.getJSONObject("unitPerson");
                    String unitPersonValue = "";
                    if (unitPerson != null) {
                        unitPersonValue = unitPerson.getString("VALUE");
                    }
                    JSONObject applyUser = mongoData.getJSONObject("applyUser");
                    String applyUserValue = "";
                    if (applyUser != null) {
                        applyUserValue = applyUser.getString("VALUE");
                    }
                    // 获取单位负责人和申请人
                    if ("review".equals(message.getMessageScreenType())) {
                        if (StringUtils.isNotBlank(unitPersonValue)) {
                            if (!checkUsers.contains(unitPersonValue)) {
                                this.executeMessage(message, unitPersonValue);
                                this.executePortal(message, unitPersonValue);
                            }
                        }
                        if (StringUtils.isNotBlank(applyUserValue)) {
                            if (!checkUsers.contains(applyUserValue)) {
                                this.executeMessage(message, applyUserValue);
                                this.executePortal(message, applyUserValue);
                            }
                        }
                    }
                    // 获取单位负责人和申请人
                    if ("legal".equals(message.getMessageScreenType())) {
                        if (StringUtils.isNotBlank(unitPersonValue)) {
                            if (!checkUsers.contains(unitPersonValue)) {
                                this.executeMessage(message, unitPersonValue);
                                this.executePortal(message, unitPersonValue);
                            }
                        }
                        if (StringUtils.isNotBlank(applyUserValue)) {
                            if (!checkUsers.contains(applyUserValue)) {
                                this.executeMessage(message, applyUserValue);
                                this.executePortal(message, applyUserValue);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行发送-待阅
     *
     * @param message
     * @param user
     */
    private void executePortal(Message message, String user) {
        if (StringUtils.isNotBlank(user)) {
            // 生成风控系统的待阅（根据user生成多个知会记录）
            notifyService.save(message.getMessageType(), message.getProcInstId(), user, Notify.TYPE_MESSAGE, String.valueOf(message.getMessageId()), message.getMessageContent(),
                    false);
            // 向统一代办平台发送待阅（根据类型个业务id查询多个知会记录，获取其知会人，进行一次性同步）
            notifyService.sendToPortal(message.getMessageType(), message.getProcInstId(), true, false, Notify.TYPE_MESSAGE);
        }
    }

    /**
     * 执行发送-钉钉
     *
     * @param message
     * @param user
     */
    private void executeMessage(Message message, String user) {
        if (StringUtils.isNotBlank(user)) {
            // 一次性向多个用户推送
            messageService.shareMessage(message.getParentId() == 0 ? message.getMessageId() : message.getParentId(), user, MessageClient._DT);
        }
    }
}
