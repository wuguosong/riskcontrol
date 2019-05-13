package notify;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.DateUtil;
import ws.todo.client.TodoClient;
import ws.todo.entity.*;
import ws.todo.utils.JaXmlBeanUtil;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class NotifyTest {
    private TodoClient todoClient = TodoClient.getInstance();
    private TodoInfo todoInfo = new TodoInfo();
    @Before
    public void sendBefore(){
        // 设置代办信息
        todoInfo.setBusinessId("5cbd21b9ddd03403775404aa");
        String prefix = TodoClient.PREFIX_BIDDING_URL + "5cbd21b9ddd03403775404aa/";
        String suffix = JaXmlBeanUtil.encodeScriptUrl(TodoClient.SUFFIX_URL);
        String url = prefix + suffix;
        System.out.println(url);
        todoInfo.setUrl(url);
        todoInfo.setCreatedTime(DateUtil.getDateToString(DateUtil.getCurrentDate(), DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
        todoInfo.setOwner("0001N6100000000HADK0");
        todoInfo.setTitle("代办测试");
    }

    @Test
    public void sendTodo_ToDo(){
        TodoBack todoBack = todoClient.sendTodo_ToDo(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }


    @Test
    public void sendTodo_ToDo2Done(){
        TodoBack todoBack = todoClient.sendTodo_ToDo2Done(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }

    @Test
    public void sendTodo_ToRead(){
        TodoBack todoBack = todoClient.sendTodo_ToRead(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }

    @Test
    public void sendTodo_ToRead2Done(){
        TodoBack todoBack = todoClient.sendTodo_ToRead2Done(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }

    @Test
    public void sendTodo_ToDoDel(){
        TodoBack todoBack = todoClient.sendTodo_ToDoDel(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }

    @Test
    public void sendTodo_ToReadDel(){
        TodoBack todoBack = todoClient.sendTodo_ToReadDel(todoInfo);
        System.out.println(JSON.toJSONString(todoBack));
    }
}
