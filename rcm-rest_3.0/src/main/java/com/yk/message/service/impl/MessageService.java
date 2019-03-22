package com.yk.message.service.impl;

import com.yk.message.dao.IMessageMapper;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiPan on 2019/1/25.
 */
@Service
@Transactional
public class MessageService implements IMessageService{
    @Resource
    IMessageMapper messageMapper;

    @Override
    public List<Message> list(String procInstId, Long parentId) {
        return messageMapper.selectMessageList(procInstId, parentId);
    }

    @Override
    public Message get(Long messageId) {
        return messageMapper.selectMessageById(messageId);
    }

    @Override
    public Message save(Message message) {
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
        messageMapper.deleteMessage(messageId);
        return message;
    }

    @Override
    public List<Message> getMessageTree(String procInstId, Long parentId) {
        // 获取根节点
        List<Message> roots = messageMapper.selectMessageList(procInstId, parentId);
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
}
