package message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.message.dao.IMessageMapper;
import com.yk.message.entity.Message;
import com.yk.message.service.IMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ws.todo.utils.JaXmlBeanUtil;

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
        List<Message> list = messageService.getMessageTree("1008611", new Long(0));
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

    @Test
    public void testList(){
        List<Message> list = messageService.findMessages("1008611", "0001N6100000000HADK0", true);
        for(Message message : list){

        }
        System.out.println(list.size());
        list = messageService.findMessages("1008611", "0001N6100000000HADK0", false);
        for(Message message : list){

        }
        System.out.println(list.size());
    }

    @Test
    public void testJSONObjects(){
        List<JSONObject> list = messageService.messages("1008611", "0001N6100000000HADK0");
        for(JSONObject message : list){
             System.out.println(message);
        }
        System.out.println(list.size());
    }

    @Test
    public void testMessageNotify(){
        List<Message> list = messageService.getMessageNotify();
        System.out.println(list.size());
    }

    @Test
    public void testAttachmentType(){
        messageService.getAttachmentType("5c46d1b85049b5283c49a7e3","formalReview");
    }

    @Test
    public void testAttachmentType2(){
        messageService.getAttachmentType("5cd007417ad1b42bb860fb81","preReview");
    }

    @Test
    public void testDecode(){
        System.out.println(JaXmlBeanUtil.encodeScriptUrl("10558"));
        System.out.println(JaXmlBeanUtil.decodeScriptUrl("MTA0NTA="));
    }

    @Test
    public void testVia(){
        Message message = messageService.get(new Long(10461));
        messageService.shareMessageToSameSubject(message);
    }

    @Test
    public void testVia2(){
        String m1 = "formalReview", b1 = "5cdf743eddd03432b87e9774";
        String m2 = "bulletin", b2 = "5cd394f4ddd034106b5cc84e";
        String m3 = "preReview", b3 = "5cbd21b9ddd03403775404aa";
        JSONObject js = messageService.getInvestmentAndLaw(b3, m3);
        System.out.println(JSON.toJSONString(js));
    }

    @Test
    public void  encodeScriptUrl(){
        System.out.println(JaXmlBeanUtil.encodeScriptUrl("10866"));
    }
}
