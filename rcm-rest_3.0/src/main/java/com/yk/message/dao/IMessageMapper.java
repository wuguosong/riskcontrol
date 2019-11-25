package com.yk.message.dao;

import com.alibaba.fastjson.JSONObject;
import com.goukuai.dto.FileDto;
import com.yk.common.BaseMapper;
import com.yk.message.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    List<Message> selectMessageList(Map<String, Object> params);

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
    void insertMessage(Message message);

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

    List<Message> selectMessages(@Param("procInstId") String procInstId, @Param("createdBy") String createdBy, @Param("notIncludeCreatedBy") boolean notIncludeCreatedBy);

    List<Message> selectLeavesMessageList(@Param("procInstId") String procInstId, @Param("messageId") Long messageId);

    HashMap<String, Object> selectUserInfoByUuid(@Param("uuid") String uuid);

    List<Message> selectMessageListPage(Map<String, Object> params);

    List<Message> selectMessageNotify(@Param("curUuid") String curUuid);

    List<Map<String, Object>> selectUserByCondition(Map<String, Object> params);

    List<HashMap<String, Object>> selectUserAbout(@Param("uuid") String uuid);

    List<HashMap<String, Object>> selectProjectByTypeAndId(@Param("type") String type, @Param("id") String id);

    /**
     * 查询留言过程附件的位置序列号
     *
     * @param params 查询条件
     * @return List<FileDto> 文件实体列表
     */
    List<FileDto> selectPageLocationSequenceNumber(Map<String, Object> params);

    /**
     * 查询@用户列表
     *
     * @return
     */
    List<JSONObject> selectViaUsers();

    List<Map<String, Object>> findDictItemByDictCode(@Param("dictCode")String dictCode);
}
