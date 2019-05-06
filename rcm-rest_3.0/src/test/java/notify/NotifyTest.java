package notify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ws.agency.client.TodoClient;
import ws.agency.client.todo_list;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class NotifyTest {

    @Test
    public void testToDoList(){
        TodoClient todoClient = new TodoClient();
        todo_list todoList = new todo_list();
        todo_list.header header = new todo_list.header();
        header.setSyscode("");
        todoList.setHeader(header);
        List<todo_list.todo_info> todoInfoList = new ArrayList<todo_list.todo_info>();
        todo_list.todo_info todoInfo = new todo_list.todo_info();
        todoInfo.setDenyurl("");
        todoInfo.setCreated("");
        todoInfo.setAgreeurl("");
        todoInfo.setDepart("");
        todoInfo.setMobileUrl("");
        todoInfo.setOwner("");
        todoInfo.setPriority("");
        todoInfo.setRecord_status("");
        todoInfo.setSender("");
        todoInfo.setStatus("");
        todoInfo.setTitle("");
        todoInfo.setType("");
        todoInfo.setUniid("");
        todoList.setTodolist(todoInfoList);
        String msg = todoClient.sendTodoList(todoList);
        System.out.println(msg);
    }
}
