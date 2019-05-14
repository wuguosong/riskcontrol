package ws.todo.client;

import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import com.yk.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ws.todo.entity.*;
import ws.todo.utils.JaXmlBeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/25 0025.
 */
public class TodoClient {
    // 客户端
    private TodoClient(){}
    private volatile static TodoClient todoClient;
    // 状态
    private static final String TO_DO = "1";
    private static final String DONE = "2";
    private static final String TO_READ = "3";
    private static final String READ = "4";
    private static final String DELETE = "5";
    // 配置
    private static final Prop prop = PropKit.use("wsdl_conf.properties");
    private final String SYS_CODE = prop.get("agency.ws.sys.code");
    private final String environment = prop.get("agency.wsdl.environment");
    // url
    public static final String SUFFIX_URL = prop.get("agency.wsdl.suffix.url");
    public static final String PREFIX_FORMAL_URL = prop.get("agency.wsdl.prefix.url.formalReview");
    public static final String PREFIX_OTHERS_URL = prop.get("agency.wsdl.prefix.url.bulletin");
    public static final String PREFIX_BIDDING_URL = prop.get("agency.wsdl.prefix.url.preReview");
    // 服务
    private final IUpToDoService upToDoService = new UpToDoServiceImplService().getTodoPort();
    public static TodoClient getInstance(){
        if(todoClient == null){
            synchronized (TodoClient.class){
                if(todoClient == null){
                    todoClient = new TodoClient();
                }
            }
        }
        return todoClient;
    }

    private To_Do_Result sendTodoList(List<To_Do_Info> to_do_infoList) {
        To_Do_List to_do_list = new To_Do_List();
        Header header = new Header();
        header.setSyscode(SYS_CODE);
        to_do_list.setHeader(header);
        to_do_list.setToDoInfoList(to_do_infoList);
        String toXml = JaXmlBeanUtil.object2Xml(to_do_list);
        String baXml = upToDoService.todoList(toXml);
        return JaXmlBeanUtil.xml2Object(baXml, To_Do_Result.class);
    }

    /**
     * 流程同步列表
     *
     * @param todoInfoList
     * @return
     */
    public TodoBack senTodoList(List<TodoInfo> todoInfoList) {
        if(CollectionUtils.isEmpty(todoInfoList)){
            throw new BusinessException("代办列表不能为空！");
        }
        for(TodoInfo todo : todoInfoList){
            if(StringUtils.isBlank(todo.getBusinessId())){
                throw new BusinessException("代办业务ID不能空！");
            }
            if(StringUtils.isBlank(todo.getTitle())){
                throw new BusinessException("代办标题不能为空！");
            }
            if(StringUtils.isBlank(todo.getCreatedTime())){
                throw new BusinessException("代办时间不能为空！");
            }
            if(StringUtils.isBlank(todo.getUrl())){
                throw new BusinessException("代办URL不能为空！");
            }
            if(StringUtils.isBlank(todo.getType())){
                throw new BusinessException("代办事件类型不能为空！");
            }
            if(StringUtils.isBlank(todo.getStatus())){
                throw new BusinessException("代办任务类型不能为空！");
            }
            if(StringUtils.isBlank(todo.getOwner())){
                throw new BusinessException("代办处理人不能为空！");
            }
        }
        List<To_Do_Info> to_do_infoList = new ArrayList<To_Do_Info>();
        for (TodoInfo todoInfo : todoInfoList) {
            To_Do_Info to_do_info = new To_Do_Info();
            to_do_info.setAgree_url(todoInfo.getAgreeUrl());
            to_do_info.setCreated(todoInfo.getCreatedTime());
            to_do_info.setDeny_url(todoInfo.getDenyUrl());
            to_do_info.setDepart(todoInfo.getDepart());
            to_do_info.setMobileUrl(todoInfo.getMobileUrl());
            to_do_info.setOwner(todoInfo.getOwner());
            to_do_info.setPriority(todoInfo.getPriority());
            to_do_info.setRecord_status(todoInfo.getRecordStatus());
            to_do_info.setSender(todoInfo.getSender());
            to_do_info.setStatus(todoInfo.getStatus());
            to_do_info.setTitle(todoInfo.getTitle());
            to_do_info.setType(todoInfo.getType());
            to_do_info.setUniid(todoInfo.getBusinessId());
            to_do_info.setUrl(todoInfo.getUrl());
            to_do_infoList.add(to_do_info);
        }
        To_Do_Result toDoResult = this.sendTodoList(to_do_infoList);
        if (toDoResult == null) {
            throw new BusinessException("代办返回结果异常！");
        }
        TodoBack todoBack = new TodoBack();
        todoBack.setCode(toDoResult.getCode());
        todoBack.setMessage(toDoResult.getMessage());
        todoBack.setSysCode(toDoResult.getSyscode());
        todoBack.setSysInstanceId(toDoResult.getSysinstanceid());
        return todoBack;
    }

    /**
     * 流程同步
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo(TodoInfo todoInfo) {
        List<TodoInfo> list = new ArrayList<TodoInfo>();
        list.add(todoInfo);
        return this.senTodoList(list);
    }

    /**
     * 流程同步-代办
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToDo(TodoInfo todoInfo) {
        todoInfo.setType(TO_DO);
        todoInfo.setStatus(TO_DO);
        return this.sendTodo(todoInfo);
    }

    /**
     * 流程同步-待阅
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToRead(TodoInfo todoInfo) {
        todoInfo.setType(TO_READ);
        todoInfo.setStatus(TO_READ);
        return this.sendTodo(todoInfo);
    }

    /**
     * 流程同步-删除代办
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToDoDel(TodoInfo todoInfo) {
        todoInfo.setType(TO_DO);
        todoInfo.setStatus(DELETE);
        return this.sendTodo(todoInfo);
    }

    /**
     * 流程同步-删除待阅
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToReadDel(TodoInfo todoInfo) {
        todoInfo.setType(TO_READ);
        todoInfo.setStatus(DELETE);
        return this.sendTodo(todoInfo);
    }

    /**
     * 流程同步-代办转已办
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToDo2Done(TodoInfo todoInfo) {
        todoInfo.setType(TO_DO);
        todoInfo.setStatus(DONE);
        return this.sendTodo(todoInfo);
    }


    /**
     * 流程同步-待阅转已阅
     *
     * @param todoInfo
     * @return
     */
    public TodoBack sendTodo_ToRead2Done(TodoInfo todoInfo) {
        todoInfo.setType(TO_READ);
        todoInfo.setStatus(READ);
        return sendTodo(todoInfo);
    }
}
