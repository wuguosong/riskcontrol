package ws.agency.client;

import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import ws.agency.entity.Header;
import ws.agency.entity.To_Do_Info;
import ws.agency.entity.To_Do_List;
import ws.agency.utils.JaXmlBeanUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/4/25 0025.
 */
public class TodoClient {
    private Prop prop = PropKit.use("wsdl_conf.properties");
    private String SYS_CODE = prop.get("agency.ws.sys.code");
    private String environment = prop.get("agency.wsdl.environment");
    private IUpToDoService upToDoService = null;
    public TodoClient() {
        upToDoService = new UpToDoServiceImplService().getTodoPort();
    }

    public String sendTodoList(List<To_Do_Info> to_do_infoList){
        To_Do_List to_do_list = new To_Do_List();
        Header header = new Header();
        header.setSyscode(SYS_CODE);
        to_do_list.setHeader(header);
        String xml = JaXmlBeanUtil.object2Xml(to_do_infoList);
        return upToDoService.todoList(xml);
    }
}
