package ws.agency.client;

import com.alibaba.fastjson.JSON;
import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;

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

    public String agencyList(todo_list agencyList){

        return upToDoService.todoList(JSON.toJSONString(agencyList));
    }
}
