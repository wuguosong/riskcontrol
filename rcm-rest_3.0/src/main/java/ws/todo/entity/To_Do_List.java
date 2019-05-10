package ws.todo.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 代办列表
 */
@XmlRootElement(name="todo_list")
public class To_Do_List {
    private Header header;
    private List<To_Do_Info> toDoInfoList;

    public Header getHeader() {
        return header;
    }

    @XmlElement(name = "header")
    public void setHeader(Header header) {
        this.header = header;
    }

    // @XmlElementWrapper(name = "todo_info")
    @XmlElement(name = "todo_info")
    public List<To_Do_Info> getToDoInfoList() {
        return toDoInfoList;
    }


    public void setToDoInfoList(List<To_Do_Info> toDoInfoList) {
        this.toDoInfoList = toDoInfoList;
    }
}
