package com.yk.message.dao;

import com.yk.common.BaseMapper;
import com.yk.message.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by LiPan on 2019/1/25.
 */
@Repository
public interface IMessageMapper extends BaseMapper {

    /**
     * 查询留言列表
     *
     * @return List<Message>
     */
    List<Message> selectMessageList(@Param("procInstId") Long procInstId, @Param("parentId") Long parentId);

    /**
     * 通过ID查询留言
     *
     * @param messageId 留言ID
     * @return Message
     */
    Message selectMessageById(@Param("messageId") Long messageId);

    /**
     * 插入留言信息
     *
     * @param message 留言实体
     */
    void insertMessage(@Param("message") Message message);

    /**
     * 更新留言信息
     *
     * @param message 留言实体
     */
    void updateMessage(@Param("message") Message message);

    /**
     * 删除留言信息
     *
     * @param messageId 留言ID
     */
    void deleteMessage(@Param("messageId") Long messageId);

    /**
     * 删除留言及其叶子留言
     *
     * @param messageId 留言ID
     */
    void deleteMessageRootAndChildren(@Param("messageId") Long messageId);

    /**
     * 查询一个留言下的所有叶子留言
     *
     * @param parentId 父留言ID
     * @return List<Message>
     */
    List<Message> selectMessageChildren(@Param("parentId") Long parentId);
}
