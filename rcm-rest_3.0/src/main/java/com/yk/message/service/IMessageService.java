package com.yk.message.service;

import com.yk.message.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    List<Message> list(Long procInstId, Long parentId);

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
    List<Message> getMessageTree(Long procInstId, Long parentId);

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
}
