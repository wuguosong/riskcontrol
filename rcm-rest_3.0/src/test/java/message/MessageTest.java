package message;

import com.yk.message.dao.IMessageMapper;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by mnipa on 2019/1/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class MessageTest {
    @Resource
    private IMessageMapper messageMapper;

    @Resource
    private IMessageService messageService;

    @org.junit.Test
    public void testTree() {
        List<Message> list = messageService.getMessageTree(1008611L, new Long(0));
        System.out.println(list.size());
        for (Message message : list) {
            System.out.println(message);
        }
    }

    @org.junit.Test
    public void testGet(){
        Message message = messageMapper.selectMessageById(10091L);
        System.out.println(message);
    }

    @org.junit.Test
    public void testUpdate(){
        Message message = messageMapper.selectMessageById(10085L);
        message.setMessageContent("我是Admin!");
        messageMapper.updateMessage(message);
    }

    @org.junit.Test
    public void testDelete(){
        messageMapper.deleteMessage(10091L);
    }

    @org.junit.Test
    public void testRecursionDelete(){
        List<Message> list = messageService.recursionDelete(10079L);
        for (Message message : list){
            System.out.println(message);
        }
    }

    @Test
    public void testChildren(){
        List<Message> list = messageService.getMessageChildren(10080L);
        System.out.println(list.size());
    }

    @Test
    public void testPush(){
        Message message = messageService.updatePush(10109L);
        System.out.println(message);
    }

    @Test
    public void testRead(){
        Message message = messageService.updateRead(10109L);
        System.out.println(message);
    }
}
