package com.yk.message.service;

import com.alibaba.fastjson.JSONObject;
import com.yk.message.entity.Message;
import common.PageAssistant;
import ws.msg.client.MessageBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/1/25.
 */
public interface IMessageService {
    /**
     * 留言列表
     * @param procInstId 流程实例ID
     * @param parentId 父留言ID,不传查询所有
     * @return List<Message>
     */
    List<Message> list(String procInstId, Long parentId);

    /**
     * 获取留言
     *
     * @param messageId 留言ID
     * @return Message
     */
    Message get(Long messageId);

    /**
     * 保存留言
     *
     * @param message 留言实体
     */
    Message save(Message message);

    /**
     * 更新留言
     *
     * @param message 留言实体
     */
    Message update(Message message);

    /**
     * 删除留言
     *
     * @param messageId 留言ID
     */
    Message delete(Long messageId);

    /**
     * 获取留言组织树
     *
     * @param procInstId 流程实例
     * @param parentId   父留言ID
     * @return List<Message>
     */
    List<Message> getMessageTree(String procInstId, Long parentId);

    /**
     * 递归删除留言及其下所有的叶子留言
     *
     * @param messageId 留言ID
     * @return List<Message> 留言信息
     */
    List<Message> recursionDelete(Long messageId);

    /**
     * 获取一个留言下的所有叶子留言
     * @param parentId 父留言ID
     * @return List<Message>
     */
    List<Message> getMessageChildren(Long parentId);

    /**
     * 更新是否已读标志
     * @param messageId 留言ID
     * @return Message
     */
    Message updateRead(Long messageId);

    /**
     * 更新是否推送标志
     * @param messageId 留言ID
     * @return Message
     */
    Message updatePush(Long messageId);

    /**
     * 根据创建人查找本人和其他人留言
     * @param procInstId
     * @param createdBy
     * @param notIncludeCreatedBy
     * @return
     */
    List<Message> findMessages(String procInstId, String createdBy, boolean notIncludeCreatedBy);

    /**
     * 根据创建人查找本人和其他人留言
     * @param procInstId
     * @param createdBy
     * @return
     */
    Map<String, List<Message>> findMessages(String procInstId, String createdBy);

    /**
     * 根据创建人查找本人和其他人留言
     * @param procInstId
     * @param createdBy
     * @return
     */
    List<JSONObject> messages(String procInstId, String createdBy);

	List<List<JSONObject>> queryMessagesList(String procInstId, Long parentId, String _query_params_);

    /**
     * 分享留言
     * @param messageId
     * @param shareUsers
     * @return
     */
    MessageBack shareMessage(Long messageId, String shareUsers, String type);

    /**
     * 通过用户uuid获取用户信息
     * @param uuid
     * @return
     */
    HashMap<String, Object> getUserByUuid(String uuid);

    /**
     * 留言列表-分页查询
     * @param pageAssistant
     */
    void queryMessagesListPage(PageAssistant pageAssistant);

    /**
     * 获取当前登录人的留言列表
     * @return List<Message>
     */
    List<Message> getMessageNotify();

    /**
     * 查询@用户
     * @param page
     */
    void queryViaUsers(PageAssistant page);

    /**
     * 获取附件类型
     * @param message_business_id
     * @param message_type
     * @return
     */
    List<Map> getAttachmentType(String message_business_id, String message_type);

    /**
     * 向同一个主题的留言推送留言信息
     * @param message
     */
    void shareMessageToSameSubject(Message message);

    /**
     * 获取过程附件的位置序列号
     * @param docType 附件类型
     * @param docCode 附件业务ID
     * @param tempUuid 临时UUID，这里用创建人的UUID
     * @return 序列号
     */
    int getPageLocationSequenceNumber(String docType, String docCode, String tempUuid);

    /**
     * 从留言中获取知会人
     * @param message 留言
     * @return 知会人，格式用户id，多个以“,”分隔
     */
    String getNotifyUsers(Message message);

    /**
     * 查询项目
     * @param type
     * @param id
     * @return
     */
    HashMap<String, Object> getProject(String type, String id);
}
