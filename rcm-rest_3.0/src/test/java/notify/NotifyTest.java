package notify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ws.agency.client.TodoClient;
import ws.agency.client.TodoList;
import ws.agency.entity.Header;
import ws.agency.entity.To_Do_Info;
import ws.agency.entity.To_Do_List;
import ws.agency.utils.JaXmlBeanUtil;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class NotifyTest {
    private To_Do_List to_do_list;
    @Before
    public void allTestBefore(){
        to_do_list = new To_Do_List();
        Header header = new Header();
        header.setSyscode("T0094");
        to_do_list.setHeader(header);
        List<To_Do_Info> to_do_infoList = new ArrayList<To_Do_Info>();
        To_Do_Info to_do_info = new To_Do_Info();
        to_do_info.setAgreeurl("");
        to_do_info.setCreated("");
        to_do_info.setDenyurl("");
        to_do_info.setDepart("");
        to_do_info.setMobileUrl("");
        to_do_info.setOwner("");
        to_do_info.setPriority("");
        to_do_info.setRecord_status("");
        to_do_info.setSender("");
        to_do_info.setStatus("");
        to_do_info.setTitle("");
        to_do_info.setType("");
        to_do_info.setUniid("");
        to_do_info.setUrl("");
        to_do_infoList.add(to_do_info);
        To_Do_Info to_do_info2 = new To_Do_Info();
        to_do_info2.setAgreeurl("");
        to_do_info2.setCreated("");
        to_do_info2.setDenyurl("");
        to_do_info2.setDepart("");
        to_do_info2.setMobileUrl("");
        to_do_info2.setOwner("");
        to_do_info2.setPriority("");
        to_do_info2.setRecord_status("");
        to_do_info2.setSender("");
        to_do_info2.setStatus("");
        to_do_info2.setTitle("");
        to_do_info2.setType("");
        to_do_info2.setUniid("");
        to_do_info2.setUrl("");
        to_do_infoList.add(to_do_info2);
        to_do_list.setToDoInfoList(to_do_infoList);
    }

    @Test
    public void testToDoList(){
    }


    @Test
    public void testXmlToBean(){
    }

    @Test
    public void testBeanToXml(){
       String xml = JaXmlBeanUtil.object2Xml(to_do_list);
       System.out.println(xml);
    }
}
